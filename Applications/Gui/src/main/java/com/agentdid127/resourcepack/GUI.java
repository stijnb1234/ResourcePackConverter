package com.agentdid127.resourcepack;

import com.agentdid127.resourcepack.library.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GUI extends Application {

  Stage stage;
  Scene scene;
  VBox root;
  HBox header;

  Label startVersionLabel;
  ComboBox<String> startVersion;

  Label endVersionLabel;
  ComboBox<String> endVersion;

  Button startButton;

  TextArea mainOutput;

  private String outputString = "";

  private String[] supportedVersions;

  private Gson gson;

  private PrintStream out;

  public GUI() {

    gson = new GsonBuilder().disableHtmlEscaping().create();
    root = new VBox();

    header = new HBox();

    startVersionLabel = new Label("Start Version");
    startVersion = new ComboBox<>();

    endVersionLabel = new Label("End Version");
    endVersion = new ComboBox<>();

    startButton = new Button("Convert!");

    mainOutput = new TextArea(outputString);
    mainOutput.setEditable(false);

  }

  @Override
  public void init() {

    setupVersions();
    ObservableList<String> versions = FXCollections.<String>observableArrayList();

    for (String s : supportedVersions) {
      versions.add(s);
    }

    startVersion.setItems(versions);
    endVersion.setItems(versions);

    startVersion.setValue(supportedVersions[0]);
    endVersion.setValue(supportedVersions[supportedVersions.length - 1]);

    startButton.setOnAction((e) -> run());

    header.getChildren().addAll(startVersionLabel, startVersion, endVersionLabel, endVersion, startButton);
    root.getChildren().addAll(header, mainOutput);
  }
  @Override
  public void start(Stage stage) throws Exception {

    this.stage = stage;
    this.scene = new Scene(root);

    stage.setTitle("Resource Pack Converter");
    stage.setScene(scene);
    stage.setOnCloseRequest(event -> Platform.exit());
    stage.sizeToScene();
    stage.setResizable(true);
    stage.show();
  }

  private void setupVersions() {
    supportedVersions = Util.getSupportedVersions(gson);
  }

  private void run() {
    outputString = "";
    mainOutput.setText(outputString);
    out = redirectSystemStreams();

    String from = startVersion.getValue();
    String to = endVersion.getValue();

    String args = "--from " + from + " --to " + to;
    Thread t = new Thread(() -> {
      try {
        GUIRunner.run(args.split(" "), out);
      } catch (IOException e) {
        alertError(e);
      }
    });
    t.start();

    while (t.isAlive()) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

  private PrintStream redirectSystemStreams() {
    OutputStream out2 = new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        outputString += (String.valueOf((char) b));
        mainOutput.setText(outputString);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        outputString += (new String(b, off, len));
        mainOutput.setText(outputString);
      }

      @Override
      public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
      }
    };

    return new PrintStream(out2);
  }

  private void alertError(Throwable t) {
    Platform.runLater(() -> {
      TextArea errorText = new TextArea(t.getMessage());
      errorText.setEditable(false);
      Alert alert = new Alert(AlertType.ERROR);
      alert.getDialogPane().setContent(errorText);
      alert.setResizable(true);
      alert.showAndWait();
    });
    t.printStackTrace(out);
  }
}