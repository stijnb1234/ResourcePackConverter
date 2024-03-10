package com.agentdid127.resourcepack;

import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import joptsimple.ValueConverter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Command Line Option Flags.
 */
public class Options {
    public static final OptionParser PARSER = new OptionParser();

    public static final OptionSpec<Void> HELP = Options.PARSER.acceptsAll(Arrays.asList("?", "h", "help"), "Print this message.").forHelp();

    public static final OptionSpec<Path> INPUT_DIR = Options.PARSER.acceptsAll(Arrays.asList("i", "input", "input-dir"), "Input directory for the packs").withRequiredArg().withValuesConvertedBy(new PathConverter()).defaultsTo(Paths.get("./"));

    public static final OptionSpec<Boolean> DEBUG = Options.PARSER.accepts("debug", "Displays other output").withRequiredArg().ofType(Boolean.class).defaultsTo(true);

    public static final OptionSpec<String> TO = Options.PARSER.accepts("to", "Updates to version").withRequiredArg().ofType(String.class).defaultsTo("1.13");

    public static final ArgumentAcceptingOptionSpec<String> FROM = Options.PARSER.accepts("from", "Updates from version").withRequiredArg().ofType(String.class).defaultsTo("1.12");

    public static final ArgumentAcceptingOptionSpec<String> LIGHT = Options.PARSER.accepts("light", "Updates from version").withRequiredArg().ofType(String.class).defaultsTo("none");

    public static final OptionSpec<Void> MINIFY = Options.PARSER.accepts("minify", "Minify the json files.");

    public static final OptionSpec<Void> UNSTABLE = Options.PARSER.accepts("unstable", "Use Unstable Features.");

    public static class PathConverter implements ValueConverter<Path> {
        @Override
        public Path convert(String s) {
            return Paths.get(s);
        } // convert

        @Override
        public Class<? extends Path> valueType() {
            return Path.class;
        } // valueType

        @Override
        public String valuePattern() {
            return "*";
        } // valuePattern
    } // PathConverter
}