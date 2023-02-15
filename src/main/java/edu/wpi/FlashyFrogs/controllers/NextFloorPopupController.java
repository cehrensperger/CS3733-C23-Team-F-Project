package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.PathfindingController;
import java.io.IOException;
import javafx.fxml.FXML;
import lombok.Setter;

public class NextFloorPopupController {
  private PathfindingController pathfindingController;

  @Setter private Node.Floor floor;

  public void initialize() {}

  @FXML
  private void handleYesButton() throws IOException {
    pathfindingController.setFloor(floor);
  }

  public void setPathfindingController(PathfindingController pathfindingController) {
    this.pathfindingController = pathfindingController;
  }
}
