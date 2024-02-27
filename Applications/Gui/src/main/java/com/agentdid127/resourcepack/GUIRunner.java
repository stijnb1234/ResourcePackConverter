package com.agentdid127.resourcepack;

import java.io.IOException;
import java.io.PrintStream;

public class GUIRunner {

  public static void run(String[] options, PrintStream console) throws IOException {
    CommonTool.run(Options.PARSER.parse(options), console, console);
  }

}
