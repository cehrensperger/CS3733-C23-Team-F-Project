package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.hibernate.Session;

/** Controller for the node info */
public class NodeInfoController {
  @FXML private ColumnConstraints thirdColumn;
  @FXML private ColumnConstraints fourthColumn;

  @FXML private Text nodeLocationText;
  @FXML private MFXTextField buildingField;
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

    // Floor number as a string
    String floorNumber = floor.floorNum;

    // If the floor is one character
    if (floorNumber.length() == 1) {
      floorNumber = "0" + floorNumber; // Prepend a 0
    }

    // Return the formatted string
    return floorNumber + String.format("X%04dY%04d", xCoordInt, yCoordInt);
  }

  /**
   * Sets the node that the pop-up will use, including updating fields to use it
   *
   * @param node the node to set to
   * @param session the session to use to fetch/save data* handle any visual updates necessary
   * @param onNodeDelete function to run when a node is to be deleted, should accept the node and
   *     remove it
   * @param onNodeUpdate function to call when a nodes id is updated, should accept the old node
   *     object and the new one
   * @param onLocationDelete function to be called when the location is deleted, accepts the
   *     location to delete
   * @param onLocationChange function to be called when the location changes to process table
   *     updates. First parameter is the old location, second is the new location
   */
  @SneakyThrows
  public void setNode(
      @NonNull Node node,
      @NonNull Session session,
      @NonNull Consumer<Node> onNodeDelete,
      @NonNull BiConsumer<Node, Node> onNodeUpdate,
      @NonNull Consumer<LocationName> onLocationDelete,
      @NonNull BiConsumer<LocationName, LocationName> onLocationChange,
      boolean isNewNode) {
    String[] originalID = new String[1]; // Original ID for the node
    originalID[0] = node.getId(); // Set the original ID

    // Properties of the floor
    StringProperty nodeID = new SimpleStringProperty(node.getId());
    StringProperty xCoord = new SimpleStringProperty(Integer.toString(node.getXCoord()));
    StringProperty yCoord = new SimpleStringProperty(Integer.toString(node.getYCoord()));
    ObjectProperty<Node.Floor> floor = new SimpleObjectProperty<>(node.getFloor());
    StringProperty building = new SimpleStringProperty(node.getBuilding());

    // Runnable to be run when a field is changed, validates everything
    Runnable onFieldChange =
        () -> {
          errorText.setText(""); // Clear text to start

          try {
            // Set the ID
            nodeID.setValue(processNodeUpdate(xCoord.get(), yCoord.get(), floor.get()));

            // If the building is empty
            if (building.get().equals("")) {
              errorText.setText(
                  "Fill in all fields before submitting!"); // The user must fill it in before
              // submitting
            }
          } catch (IllegalArgumentException error) {
            errorText.setText(error.getMessage()); // Show error
          }
        };

    // Listener for x-coord
    xCoord.addListener((observable, oldValue, newValue) -> onFieldChange.run());

    // Listener for Y-Coord
    yCoord.addListener(((observable, oldValue, newValue) -> onFieldChange.run()));

    // Set the floor items
    floorField.setItems(FXCollections.observableArrayList(Node.Floor.values()));
    floorField.setText(floor.get().toString()); // Set the text manually because MFX is mean

    // Listen for field changes
    floorField.valueProperty().addListener((observable, oldValue, newValue) -> onFieldChange.run());

    // Listen for building
    building.addListener(((observable, oldValue, newValue) -> onFieldChange.run()));

    // Bind the fields
    nodeIDField.textProperty().bindBidirectional(nodeID); // Bind ID
    xCoordinateField.textProperty().bindBidirectional(xCoord); // Bind x-coord
    yCoordinateField.textProperty().bindBidirectional(yCoord); // Bind y-coord
    floorField.valueProperty().bindBidirectional(floor); // Bind floor
    buildingField.textProperty().bindBidirectional(building); // Bind building

    // If it's not a new node
    if (!isNewNode) {
      LocationName location =
          node.getCurrentLocation(session).stream()
              .findFirst()
              .get(); // Get the location for the node

      if (location != null) { // If the location exists

        // Set its fields
        FXMLLoader locationNameLoader =
            new FXMLLoader(getClass().getResource("LocationNameInfo.fxml"));

        // Load the file, set it to be on the location panes children
        locationPane.getChildren().add(locationNameLoader.load());

        LocationNameInfoController controller =
            locationNameLoader.getController(); // Load the controller

        // Set the location name, make sure that table updates are processed
        controller.setLocationName(
            location,
            session,
            (oldLocation) -> {
              locationPane.getChildren().clear();
              onLocationDelete.accept(oldLocation);
            },
            onLocationChange, // Handle location updates
            false); // On delete clear
      }
    } else {
      nodeLocationText.setVisible(false); // hide the location name
      locationPane.setVisible(false); // hide the location frame
      thirdColumn.setMaxWidth(0); // hide the columns
      fourthColumn.setMaxWidth(0); // hide the other one
    }

    // Set the callback for the delete button
    deleteButton.setOnAction(
        (event) -> {
          // Get the old node to run the deletion
          Node original =
              session
                  .createQuery("FROM Node WHERE id = :originalID", Node.class)
                  .setParameter("originalID", originalID[0])
                  .uniqueResult();

          // Create a query that deletes the node with the set ID
          session
              .createMutationQuery("DELETE FROM Node WHERE id = :originalID")
              .setParameter("originalID", originalID[0])
              .executeUpdate();

          session.flush(); // Force updates
          onNodeDelete.accept(original); // Run the delete callback
        });

    // Set the callback for the save button
    saveButton.setOnAction(
        event -> {
          onFieldChange.run(); // Validate the input

          if (!errorText.getText().equals("")) { // If validating created an error
            return; // Stop
          }

          // Check to make sure that the node is unique. Uses session.find to ensure
          // that hibernate doesn't do weird stuff. Only done on submit because this
          // may take significant time, so we don't want typing delays
          if (!nodeID.get().equals(originalID[0])
              && session
                      .createQuery("FROM Node WHERE id = :newID", Node.class)
                      .setParameter("newID", nodeID.get())
                      .uniqueResult()
                  != null) {
            errorText.setText("A node with that ID already exists! No changes saved.");
            return; // Short-circuit, prevent submit
          }

          // Parse the integers
          int xCoordInt = Integer.parseInt(xCoord.get()); // X-Coordinate
          int yCoordInt = Integer.parseInt(yCoord.get()); // Y-Coordinate

          Node oldNode; // Place to store the old node for the callback

          // If it's a new node
          if (isNewNode) {
            oldNode = null; // There is no old node

            // Create the new node
            Node newNode =
                new Node(nodeID.get(), building.get(), floor.get(), xCoordInt, yCoordInt);

            session.persist(newNode); // Persist it
          } else {
            // Fetch the old node
            oldNode =
                session
                    .createQuery("FROM Node WHERE id =:oldID", Node.class)
                    .setParameter("oldID", originalID[0])
                    .getSingleResult();

            // Run a query that updates the location name to be the new values, searching by the
            // long
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
          }

          session.flush(); // Force things to persist

          // Get the new node based on the above
          originalID[0] = nodeID.get(); // Update the name

          Node newNode =
              session
                  .createQuery("FROM Node WHERE id = :newID", Node.class)
                  .setParameter("newID", nodeID.get())
                  .getSingleResult();
          session.refresh(newNode); // Update the new location object

          onNodeUpdate.accept(oldNode, newNode); // Do the redraw with the new noder
        });
  }
}
