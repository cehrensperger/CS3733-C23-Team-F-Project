package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.Node;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class FloorSelectorController {

  ObjectProperty<Node.Floor> floorProperty = new SimpleObjectProperty<>();

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

  public void setFloorProperty(ObjectProperty<Node.Floor> floorProperty) {
    this.floorProperty = floorProperty;
  }
}
