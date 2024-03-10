package com.agentdid127.resourcepack;

import com.agentdid127.converter.core.PluginLoader;
import com.agentdid127.converter.iface.Application;
import com.agentdid127.converter.iface.IPluginLoader;
import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.backwards.BackwardsPackConverter;
import com.agentdid127.resourcepack.forwards.ForwardsPackConverter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.RPPlugin;
import com.agentdid127.resourcepack.library.RPPluginVersionSetter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import joptsimple.OptionSet;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Common Converter Application Utility
 */
public class CommonTool implements Application {

    IPluginLoader<RPPlugin> pluginLoader;

    public static CommonTool instance;

  /**
   * Constructs a usable CommonTool instance.
   */
  public CommonTool() {
        instance = this;
    } // CommonTool


  /**
   * Runs a basic conversion
   * @param optionSet Main Option and arguments.
   * @param out System output.
   * @param error System Error output
   * @throws IOException If something fails when dealing with files.
   */
    public static void run(OptionSet optionSet, PrintStream out, PrintStream error) throws IOException {
        // Generate a new CommonTool
        new CommonTool();

        //Print help if needed
        if (optionSet.has(Options.HELP)) {
            Options.PARSER.printHelpOn(System.out);
            return;
        } // if

        // Gather options data.
        String from = optionSet.valueOf(Options.FROM);
        String to = optionSet.valueOf(Options.TO);
        String light = optionSet.valueOf(Options.LIGHT);
        boolean minify = optionSet.has(Options.MINIFY);
        boolean unstable = optionSet.has(Options.UNSTABLE);

        // Load Gson Data
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.disableHtmlEscaping().create();

        // Output Handling
        Logger.setErrorStream(error);
        Logger.setStream(out);

        // Create plugins data
        Path pluginsPath = Paths.get("./plugins/");
        if (!pluginsPath.toFile().exists()) {
            pluginsPath.toFile().mkdirs();
        } // if

        // Load plugins, with allowed shared libaries.
        PluginLoader<RPPlugin> pluginLoader = new PluginLoader(pluginsPath.toFile(), RPPlugin.class,
            Arrays.asList("com.agentdid127.resourcepack.library", "com.agentdid127.converter",
                "com.agentdid127.resourcepack.forwards", "com.agentdid127.resourcepack.backwards"));

        CommonTool.instance.pluginLoader = pluginLoader;
        pluginLoader.loadPlugins();


        // Determine Main base converter.
        PackConverter packConverter;
        if (Util.getVersionProtocol(gson, from) > Util.getVersionProtocol(gson, to)) {
             packConverter = new BackwardsPackConverter(from, to, light, minify, optionSet.valueOf(Options.INPUT_DIR),
                 optionSet.valueOf(Options.DEBUG), unstable);
        } else {
            packConverter = new ForwardsPackConverter(from, to, light, minify, optionSet.valueOf(Options.INPUT_DIR),
                optionSet.valueOf(Options.DEBUG), unstable);
        } // if

        // Load plugins
        for (RPPlugin value : pluginLoader.getPlugins().values()) {
            value.setApplication(CommonTool.instance);
            RPPluginVersionSetter.setData(value, from, to, packConverter);
            Logger.error(value.getFrom() + " " + value.getTo());
            Logger.error(value.getPackConverter());
            value.onInit();

            Logger.error(value.getRunners().size());

            // Register all converters for the plugin.
            for (RPConverter converter : value.getRunners()) {
                packConverter.registerConverter(converter);
            } // for
        } // for

        // Run a Conversion for the current directory.
        // TODO: Allow for an input file with -i
        packConverter.runDir(optionSet.valueOf(Options.INPUT_DIR));

        // unload all plugins when complete.
        for (RPPlugin value : pluginLoader.getPlugins().values()) {
            value.onUnload();
        } // for
    } // run

  /**
   * Gets the Plugin Loader
   * @return
   */
  @Override
    public IPluginLoader getPluginLoader() {
        return this.pluginLoader;
    } // getPluginLoader
} // CommonTool