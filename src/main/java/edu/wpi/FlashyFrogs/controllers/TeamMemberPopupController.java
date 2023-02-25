package edu.wpi.FlashyFrogs.controllers;

import javafx.scene.text.Text;

public class TeamMemberPopupController {

  private Text text;

  void setContent(String text) {
    this.text.setText(text);
  }
}
