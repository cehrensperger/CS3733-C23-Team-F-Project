package edu.wpi.FlashyFrogs.controllers;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class TeamMemberPopupController {

  @FXML private Text text;

  /**
   * Set the text in the popup that describes the team member's major and fun fact
   *
   * @param text
   */
  void setContent(String text) {
    this.text.setText(text);
  }
}
