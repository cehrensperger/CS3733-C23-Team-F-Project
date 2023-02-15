package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.PathFinding.PathfindingController;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class NextFloorPopupController {
  @FXML private Label message;
  private PathfindingController pathfindingController;

  public void initialize() {}

  @FXML
  private void handleYesButton() throws IOException {
    pathfindingController.setFloor(message.getText());
  }

  public void setMessage(String text) {
    message.setText(text);
  }

  public void setPathfindingController(PathfindingController pathfindingController) {
    this.pathfindingController = pathfindingController;
  }
}
