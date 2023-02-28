package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.Set;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

@GeneratedExclusion
public class FloorSelectorController {

  private Property<Node.Floor> floorProperty = null;

  @FXML Button button1;
  @FXML Button button2;
  @FXML Button button3;
  @FXML Button buttonL1;
  @FXML Button buttonL2;

  @FXML
  public void initialize() {}

  /**
   * Sets the allowed floors for the selector. Does nothing if the allowed floors is null or empty
   *
   * @param allowedFloors the floors allowed on the selector
   */
  void setAllowedFloors(Set<Node.Floor> allowedFloors) {
    if (allowedFloors == null || allowedFloors.size() == 0)
      return; // Exit in the exit conditions (above)

    if (!allowedFloors.contains(Node.Floor.ONE)) {
      button1.setVisible(false);
    }

    if (!allowedFloors.contains(Node.Floor.TWO)) {
      button2.setVisible(false);
    }

    if (!allowedFloors.contains(Node.Floor.THREE)) {
      button3.setVisible(false);
    }

    if (!allowedFloors.contains(Node.Floor.L1)) {
      buttonL1.setVisible(false);
    }

    if (!allowedFloors.contains(Node.Floor.L2)) {
      buttonL2.setVisible(false);
    }
  }

  @FXML
  public void changeFloor(ActionEvent event) {
    String floorLevel = event.getSource().toString().split("'")[1].substring(6);

    floorProperty.setValue(Node.Floor.getEnum(floorLevel));
  }

  public void setFloorProperty(Property<Node.Floor> floorProperty) {
    this.floorProperty = floorProperty;
  }
}
