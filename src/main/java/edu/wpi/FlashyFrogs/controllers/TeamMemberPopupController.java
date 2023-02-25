package edu.wpi.FlashyFrogs.controllers;

import javafx.scene.text.Text;

public class TeamMemberPopupController {

  public Text text;

  public void setContent(String text) {
    this.text.setText(text);
  }
}
