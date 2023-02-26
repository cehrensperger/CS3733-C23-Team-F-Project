package edu.wpi.FlashyFrogs.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class TeamMemberPopupController {

  @FXML private Text text;

  void setContent(String text) {
    this.text.setText(text);
  }
}
