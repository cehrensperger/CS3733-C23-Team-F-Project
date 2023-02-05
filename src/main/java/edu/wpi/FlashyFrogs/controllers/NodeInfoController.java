package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import lombok.NonNull;

/** Controller for the node info */
public class NodeInfoController {
  @FXML private MFXTextField nodeIDField;
  @FXML private MFXTextField xCoordinateField;
  @FXML private MFXTextField yCoordinateField;
  @FXML private MFXTextField floorField;
  @FXML private MFXTextField locationNameField;
  @FXML private MFXTextField locationTypeField;
  @FXML private MFXTextField shortNameField;

  private Node node; // The node that info is being displayed on

  /**
   * Sets the node that the pop-up will use, including updating fields to use it
   *
   * @param node the node to set to
   */
  public void setNode(@NonNull Node node) {
    // Set node text
    nodeIDField.setText(node.getId());
    xCoordinateField.setText(Integer.toString(node.getXCoord()));
    yCoordinateField.setText(Integer.toString(node.getYCoord()));
    floorField.setText(node.getFloor().name());

    LocationName location = node.getCurrentLocation(); // Get the location for the node
    if (location != null) { // If the location exists
      // Set its fields
      locationNameField.setText(location.getLongName());
      locationTypeField.setText(location.getLocationType().name());
      shortNameField.setText(location.getShortName());
    }
  }
}
