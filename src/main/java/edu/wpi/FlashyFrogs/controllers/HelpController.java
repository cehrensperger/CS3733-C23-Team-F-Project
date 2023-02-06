package edu.wpi.FlashyFrogs.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HelpController {

  @FXML private Text e1;
  @FXML private Text e2;
  @FXML private Text e3;
  @FXML private Text e4;
  @FXML private Text e5;
  @FXML private Text e6;
  @FXML private Text e7;
  @FXML private Text e8;
  @FXML private Text e9;
  @FXML private Text e10;
  @FXML private Text e11;
  @FXML private Text e12;
  @FXML private Text e13;

  @FXML
  public void handleQHome() throws IOException {
    /*e1.setText(
         "Service Requests Page - Allows the user to select which service request form they would like to fill out. Also allows the user to access a list of previously submitted service request forms.");
    */
    e2.setText("Map Data Editor - Allows the user to edit nodes on teh hospital maps");
    e3.setText("");
  }
}
