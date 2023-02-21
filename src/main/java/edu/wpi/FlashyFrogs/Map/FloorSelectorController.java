package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Node;
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

  @FXML
  public void changeFloor(ActionEvent event) {
    String floorLevel = event.getSource().toString().split("'")[1].substring(6);

    floorProperty.setValue(Node.Floor.getEnum(floorLevel));
  }

  public void setFloorProperty(Property<Node.Floor> floorProperty) {
    this.floorProperty = floorProperty;
  }
}
