package com.agentdid127.resourcepack;

import java.io.IOException;
import javafx.application.Application;

/**
 * Main GUI Application Instance.
 */
public class GUIApplication {

  /**
   * Main application entry method.
   * @param args Command-line arguments.
   * @throws IOException if the system fails to output correctly.
   */
  public static void main(String[] args) throws IOException {
    // If we specifically ask for a command-line interface, allow for it.
    if (args.length > 0 && args[0].equals("nogui")) {
      GUIRunner.run(args, System.out, System.err);
      return;
    } // if

    // Run the Graphical User Interface.
    Application.launch(GUI.class, args);
  } // main
} // GUIApplication
