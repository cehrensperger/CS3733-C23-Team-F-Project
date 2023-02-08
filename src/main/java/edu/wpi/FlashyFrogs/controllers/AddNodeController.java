package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class AddNodeController {
  @FXML private MFXButton cancelButton;
  @FXML private MFXButton saveButton;
  @FXML private MFXTextField xCoordField;
  @FXML private MFXTextField yCoordField;
  @FXML private MFXTextField buildingField;
  @FXML private MFXComboBox floorField;
  @FXML private Label errorMessage;

  private PopOver popOver;
  private Session session;

  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void setSession(Session session) {
    this.session = session;
  }

  /**
   * initialize the controller by filling the floor dropdown
   */
  @FXML
  private void initialize() {
    floorField.setItems(FXCollections.observableArrayList(Node.Floor.values()));
  }

  /**
   * persist the node in the database
   * @param event the event triggering this (unused)
   */
  @SneakyThrows
  @FXML
  private void saveNode(ActionEvent event) {
    try {
      if (xCoordField.getText().equals("") // if any of the fields are empty
          || yCoordField.getText().equals("")
          || buildingField.getText().equals("")
          || floorField.getText().equals("")) {
        throw new Exception(); //throw an exception
      }

      // generate the new id
      String id =
          processNodeUpdate(
              xCoordField.getText(),
              yCoordField.getText(),
              Node.Floor.valueOf(floorField.getText()));

      // if the node already exists, throw an exception
      if (session.find(Node.class, id) != null) throw new NullPointerException();

      // create the new node object
      Node node =
          new Node(
              id,
              buildingField.getText(),
              Node.Floor.valueOf(floorField.getText()),
              Integer.parseInt(xCoordField.getText()),
              Integer.parseInt(yCoordField.getText()));
      //persist the new node and close the popOver
      session.persist(node);
      popOver.hide();

      // tell the user what's wrong
    } catch (NullPointerException e) {
      errorMessage.setText("This Node already exists.");
    } catch (Exception e) {
      errorMessage.setText("Please fill all fields.");
    }
  }

  /**
   * cancel the submission and close the popOver
   * @param event the event triggering this (unused)
   */
  @FXML
  private void cancelNode(ActionEvent event) {
    popOver.hide();
  }

  /**
   * generate the node id based on xCoord, yCoord, and Floor
   * @param xCoord the x coordinate of the new node
   * @param yCoord the y coordinate of the new node
   * @param floor the floor that the new node is on
   * @return the id of the new node
   */
  private String processNodeUpdate(String xCoord, String yCoord, Node.Floor floor) {
    int xCoordInt;
    int yCoordInt;

    try {
      xCoordInt = Integer.parseInt(xCoord);
      yCoordInt = Integer.parseInt(yCoord);
    } catch (NumberFormatException error) {
      throw new IllegalArgumentException("Coordinates must be numeric!");
    }

    if (xCoordInt < 0 || xCoordInt > 9999 || yCoordInt < 0 || yCoordInt > 9999) {
      throw new IllegalArgumentException("Coordinates must be 0 -> 9999!");
    }

    // Return the formatted string
    return floor.toString() + String.format("X%04dY%04d", xCoordInt, yCoordInt);
  }
}
