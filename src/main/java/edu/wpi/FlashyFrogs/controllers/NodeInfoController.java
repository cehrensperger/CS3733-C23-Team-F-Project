package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.hibernate.Session;

/** Controller for the node info */
public class NodeInfoController {
  @FXML private Text errorText;
  @FXML private MFXButton deleteButton;
  @FXML private MFXButton saveButton;
  @FXML private AnchorPane locationPane;
  @FXML private MFXTextField nodeIDField;
  @FXML private MFXTextField xCoordinateField;
  @FXML private MFXTextField yCoordinateField;
  @FXML private MFXComboBox<Node.Floor> floorField;

  /**
   * Tries validating the x-coord, y-coord, and floor from string to value. If it works, generates a
   * string representing the new ID. Otherwise, throws an IllegalArgumentException
   *
   * @param xCoord the x-coord string
   * @param yCoord the y-coord string
   * @param floor the floor object
   * @return the string representing the new ID for the provided info
   * @throws IllegalArgumentException if the provided information does not constitute a valid node
   */
  private static String processNodeUpdate(String xCoord, String yCoord, Node.Floor floor) {
    // Integers for the coordiantes
    int xCoordInt;
    int yCoordInt;

    try {
      // Parse to integers
      xCoordInt = Integer.parseInt(xCoord); // Parse x
      yCoordInt = Integer.parseInt(yCoord); // Parse y
    } catch (NumberFormatException error) {
      // If something went wrong, throw an exception indicating as much
      throw new IllegalArgumentException("Coordinates must be numeric!");
    }

    // If the coordinates are out of bound
    if (xCoordInt < 0 || xCoordInt > 9999 || yCoordInt < 0 || yCoordInt > 9999) {
      throw new IllegalArgumentException(
          "Coordinates must be 0 -> 9999!"); // Throw an exception indicating
    }

    // Return the formatted string
    return floor.toString() + String.format("X%04dY%04d", xCoordInt, yCoordInt);
  }

  /**
   * Sets the node that the pop-up will use, including updating fields to use it
   *
   * @param node the node to set to
   * @param session the session to use to fetch/save data
   * @param onChange function to bve called when a change happens, should close the pop-over and
   *     handle any visual updates necessary
   */
  @SneakyThrows
  public void setNode(@NonNull Node node, @NonNull Session session, @NonNull Runnable onChange) {
    String[] originalID = new String[1]; // Original ID for the node
    originalID[0] = node.getId(); // Set the original ID

    // Properties of the floor
    StringProperty nodeID = new SimpleStringProperty(node.getId());
    StringProperty xCoord = new SimpleStringProperty(Integer.toString(node.getXCoord()));
    StringProperty yCoord = new SimpleStringProperty(Integer.toString(node.getYCoord()));
    ObjectProperty<Node.Floor> floor = new SimpleObjectProperty<>(node.getFloor());

    // Listener for x-coord
    xCoord.addListener(
        (observable, oldValue, newValue) -> {
          errorText.setText(""); // Clear text to start

          try {
            // Set the ID
            nodeID.setValue(processNodeUpdate(xCoord.get(), yCoord.get(), floor.get()));
          } catch (IllegalArgumentException error) {
            errorText.setText(error.getMessage()); // Show error
          }
        });

    // Listener for Y-Coord
    yCoord.addListener(
        ((observable, oldValue, newValue) -> {
          errorText.setText(""); // Clear text to start

          try {
            // Set the ID
            nodeID.setValue(processNodeUpdate(xCoord.get(), yCoord.get(), floor.get()));
          } catch (IllegalArgumentException error) {
            errorText.setText(error.getMessage()); // Show error
          }
        }));

    // Set the floor items
    floorField.setItems(FXCollections.observableArrayList(Node.Floor.values()));
    floorField.setText(floor.get().toString()); // Set the text manually because MFX is mean

    // Bind the fields
    nodeIDField.textProperty().bindBidirectional(nodeID);
    xCoordinateField.textProperty().bindBidirectional(xCoord); // Bind x-coord
    yCoordinateField.textProperty().bindBidirectional(yCoord); // Bind y-coord
    floorField.valueProperty().bindBidirectional(floor); // Bind floor

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
          (locationName) -> {},
          false); // On delete clear
    }

    // Set the callback for the delete button
    deleteButton.setOnAction(
        (event) -> {
          // Create a query that deletes the node with the set ID
          session
              .createMutationQuery("DELETE FROM Node WHERE id = :originalID")
              .setParameter("originalID", originalID[0])
              .executeUpdate();

          session.flush(); // Force updates
          onChange.run(); // Run the delete callback
        });

    // Set the callback for the save button
    saveButton.setOnAction(
        event -> {
          errorText.setText(""); // Clear the error text

          try {
            nodeID.set(
                processNodeUpdate(xCoord.get(), yCoord.get(), floor.get())); // Try updating the ID
          } catch (IllegalArgumentException error) {
            errorText.setText(error.getMessage()); // Display the error
            return; // Short-circuit
          }

          // Check to make sure that the node is unique
          if (!nodeID.get().equals(originalID[0])
              && session
                      .createQuery("FROM Node WHERE id = :newID", Node.class)
                      .setParameter("newID", nodeID.get())
                      .uniqueResult()
                  != null) {
            errorText.setText("A node with that ID already exists! No changes saved.");
            return; // Short-circuit, don't run the update
          }

          // Parse the integers
          int xCoordInt = Integer.parseInt(xCoord.get()); // X-Coordinate
          int yCoordInt = Integer.parseInt(yCoord.get()); // Y-Coordinate

          // Run a query that updates the location name to be the new values, searching by the long
          // name PK
          session
              .createMutationQuery(
                  "UPDATE Node SET "
                      + "id = :newID, xCoord = :newXCoord, yCoord = :newYCoord, floor = :newFloor "
                      + "WHERE id = :originalID")
              .setParameter("newID", nodeID.get())
              .setParameter("newXCoord", xCoordInt)
              .setParameter("newYCoord", yCoordInt)
              .setParameter("newFloor", floor.get())
              .setParameter("originalID", originalID[0])
              .executeUpdate();

          session.flush(); // Force things to persist

          // Get the new node based on the above
          originalID[0] = nodeID.get(); // Update the name

          Node newNode =
              session
                  .createQuery("FROM Node WHERE id = :newID", Node.class)
                  .setParameter("newID", nodeID.get())
                  .getSingleResult();
          session.refresh(newNode); // Update the new location object

          onChange.run(); // Do the redraw
        });
  }
}
