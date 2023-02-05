package edu.wpi.FlashyFrogs.controllers;

import javafx.fxml.FXML;
import org.w3c.dom.Text;

public class HelpController {

  @FXML private Text t1;
  @FXML private Text t2;
  @FXML private Text t3;
  @FXML private Text t4;
  @FXML private Text t5;
  @FXML private Text t6;
  @FXML private Text t7;
  @FXML private Text t8;
  @FXML private Text t9;
  @FXML private Text t10;
  @FXML private Text t11;
  @FXML private Text t12;
  @FXML private Text t13;

  @FXML
  public void handleQ() {
    t1.setTextContent(
        "Service Requests Page - Allows the user to select which service request form they would like to fill out. Also allows the user to access a list of previously submitted service request forms.");
    t2.setTextContent("Map Data Editor - Allows the user to edit nodes on teh hospital maps");
    // text3.setTextContent("");
  }
}
