package com.agentdid127.resourcepack;

import java.io.IOException;
import javafx.application.Application;

public class GUIApplication {

  public static void main(String[] args) throws IOException {
    if (args.length > 0 && args[0].equals("nogui")) {
      GUIRunner.run(args, System.out);
      return;
    }

    Application.launch(GUI.class, args);
  }
}
