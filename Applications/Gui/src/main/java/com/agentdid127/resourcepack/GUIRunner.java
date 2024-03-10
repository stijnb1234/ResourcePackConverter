package com.agentdid127.resourcepack;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Runs the application, with any given arguments.
 */
public class GUIRunner {

  /**
   * Runs the application.
   * @param options Command Line Based Options to enable.
   * @param console Main output stream.
   * @param error Main error Stream.
   * @throws IOException If some issue happens with files.
   */
  public static void run(String[] options, PrintStream console, PrintStream error) throws IOException {
    CommonTool.run(Options.PARSER.parse(options), console, error);
  } // run

} // GUIRunner
