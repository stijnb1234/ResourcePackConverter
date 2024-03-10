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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main GUI for the Resource Pack Converter
 */
public class GUI extends Application {

  Stage stage;
  Scene scene;
  VBox root;
  HBox header;

  Label startVersionLabel;
  ComboBox<String> startVersion;

  Label endVersionLabel;
  ComboBox<String> endVersion;

  CheckBox minifyBox;

  Button startButton;

  TextArea mainOutput;

  private String outputString = "";

  private String[] supportedVersions;

  private final Gson gson;

  private PrintStream out;

  /**
   * Constructs a new GUI
   */
  public GUI() {

    this.gson = new GsonBuilder().disableHtmlEscaping().create();
    this.root = new VBox();

    this.header = new HBox();

    this.startVersionLabel = new Label("Start Version");
    this.startVersion = new ComboBox<>();

    this.endVersionLabel = new Label("End Version");
    this.endVersion = new ComboBox<>();

    this.minifyBox = new CheckBox("Minify");
    this.startButton = new Button("Convert!");

    this.mainOutput = new TextArea(this.outputString);
    this.mainOutput.setEditable(false);

  } // GUI

  /**
   * Initializes the GUI.
   */
  @Override
  public void init() {

    this.setupVersions();
    ObservableList<String> versions = FXCollections.observableArrayList();

    for (String s : this.supportedVersions) {
      versions.add(s);
    } // for

    this.startVersion.setItems(versions);
    this.endVersion.setItems(versions);

    this.startVersion.setValue(this.supportedVersions[0]);
    this.endVersion.setValue(this.supportedVersions[this.supportedVersions.length - 1]);

    this.minifyBox.setSelected(false);

    this.startButton.setOnAction((e) -> this.run());

    this.header.getChildren().addAll(this.startVersionLabel, this.startVersion, this.endVersionLabel, this.endVersion,
        this.minifyBox,
        this.startButton);
    this.root.getChildren().addAll(this.header, this.mainOutput);
  } // init

  /**
   * Starts up the GUI.
   *
   * @param stage Main stage.
   */
  @Override
  public void start(Stage stage) {

    this.stage = stage;
    scene = new Scene(this.root);

    stage.setTitle("Resource Pack Converter");
    stage.setScene(this.scene);
    stage.setOnCloseRequest(event -> Platform.exit());
    stage.sizeToScene();
    stage.setResizable(true);
    stage.show();
  } // start

  /**
   * Sets up the supported versions.
   */
  private void setupVersions() {
    this.supportedVersions = Util.getSupportedVersions(this.gson);
  } // setupVersions

  /**
   * Runs the converter.
   */
  private void run() {
    this.outputString = "";
    this.mainOutput.setText(this.outputString);
    this.out = this.redirectSystemStreams();

    String from = this.startVersion.getValue();
    String to = this.endVersion.getValue();

    String minify = this.minifyBox.isSelected() ? " --minify" : "";
    String args = "--from " + from + " --to " + to + minify;
    Thread t = new Thread(() -> {
      try {
        GUIRunner.run(args.split(" "), this.out, this.out);
      } catch (IOException e) {
        this.alertError(e);
      } // try
    });
    t.start();

    while (t.isAlive()) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } // try
    } // while

  } // run

  /**
   * Takes an OutputStream and does specific opterations to allow it to appear in a GUI.
   * @return a PrintStream to output to.
   */
  private PrintStream redirectSystemStreams() {
    OutputStream out2 = new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        GUI.this.outputString += (String.valueOf((char) b));
        GUI.this.mainOutput.setText(GUI.this.outputString);
      }

      @Override
      public void write(byte[] b, int off, int len) throws IOException {
        GUI.this.outputString += (new String(b, off, len));
        GUI.this.mainOutput.setText(GUI.this.outputString);
      }

      @Override
      public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
      }
    };

    return new PrintStream(out2);
  } // redirectSystemStreams

  /**
   * Displays an error box when an error may occur.
   * @param t The error that happens.
   */
  private void alertError(Throwable t) {
    Platform.runLater(() -> {
      TextArea errorText = new TextArea(t.getMessage());
      errorText.setEditable(false);
      Alert alert = new Alert(AlertType.ERROR);
      alert.getDialogPane().setContent(errorText);
      alert.setResizable(true);
      alert.showAndWait();
    });
    t.printStackTrace(this.out);
  } // alertError
} // GUI