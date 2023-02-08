package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.hibernate.Session;

/** Controller for the node info */
public class NodeInfoController {
  @FXML private AnchorPane locationPane;
  @FXML private MFXTextField nodeIDField;
  @FXML private MFXTextField xCoordinateField;
  @FXML private MFXTextField yCoordinateField;
  @FXML private MFXTextField floorField;

  // The node that info is being displayed on

  /**
   * Sets the node that the pop-up will use, including updating fields to use it
   *
   * @param node the node to set to
   * @param session the session to use to fetch/save data
   */
  @SneakyThrows
  public void setNode(@NonNull Node node, @NonNull Session session) {
    // Set node text
    nodeIDField.setText(node.getId());
    xCoordinateField.setText(Integer.toString(node.getXCoord()));
    yCoordinateField.setText(Integer.toString(node.getYCoord()));
    floorField.setText(node.getFloor().name());

    LocationName location = node.getCurrentLocation(session); // Get the location for the node
    if (location != null) { // If the location exists
      // Set its fields
      FXMLLoader locationNameLoader =
          new FXMLLoader(getClass().getResource("../views/LocationNameInfo.fxml"));

      // Load the file, set it to be on the location panes children
      locationPane.getChildren().add(locationNameLoader.load());

      LocationNameInfoController controller =
          locationNameLoader.getController(); // Load the controller

      // Set the location name
      controller.setLocationName(
          location,
          session,
          () -> locationPane.getChildren().clear(),
          (locationName) -> {}); // On delete clear
    }
  }
}
