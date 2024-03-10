package com.agentdid127.resourcepack;

import com.agentdid127.resourcepack.library.RPPlugin;
import joptsimple.OptionSet;

/**
 * Main Command-line version of the Resource Pack Converter.
 */
public class Main {

    /**
     * Main class. Runs program
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        OptionSet optionSet = Options.PARSER.parse(args);
        try {
            CommonTool.run(optionSet, System.out, System.err);
        } catch (Exception e) {
            e.printStackTrace();
            for (RPPlugin value : CommonTool.instance.pluginLoader.getPlugins().values()) {
                value.onUnload();
            } // for
        } // try
    } // main
} // Main