package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.controllers.FloorSelectorController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
@GeneratedExclusion
public class MapEditorController implements IController {
  @FXML private AnchorPane mapPane;
  @FXML private Button backButton;
  @FXML private Label floorSelector;
  private MapController mapController; // Controller for the map
  @FXML private TableView<LocationName> locationTable; // Attribute for the location table
  @FXML private MFXButton floorSelectorButton;

  @FXML Text h1;
  @FXML Text h2;

  boolean hDone = false;

  @FXML
  private TableColumn<LocationName, String> longName; // Attribute for the name column of the table

  ObjectProperty<Node.Floor> floorProperty = new SimpleObjectProperty<>(Node.Floor.L1);

  /** Initializes the map editor, adds the map onto it */
  @SneakyThrows
  @FXML
  private void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    longName.setCellValueFactory(new PropertyValueFactory<>("longName"));

    AtomicReference<PopOver> tablePopOver =
        new AtomicReference<>(); // The pop-over the map is using for node highlighting

    locationTable.setRowFactory(
        param -> {
          TableRow<LocationName> row = new TableRow<>(); // Create a new table row to use

          // When the user selects a row, just un-select it to avoid breaking formatting
          row.selectedProperty()
              .addListener(
                  // Add a listener that does that
                  (observable, oldValue, newValue) -> row.updateSelected(false));

          // Add a listener to show the pop-up
          row.setOnMouseClicked(
              (event) -> {
                // If the pop over exists and is either not focused or we are showing a new
                // row
                if (tablePopOver.get() != null) {
                  tablePopOver.getAndSet(null).hide(); // Hide the pop-over and clear it
                }

                // Load the location name info view
                FXMLLoader locationNameLoader =
                    new FXMLLoader(getClass().getResource("LocationNameNormal.fxml"));

                // Load the resource
                try {
                  tablePopOver.set(new PopOver(locationNameLoader.load())); // Create the pop-over
                } catch (IOException e) {
                  throw new RuntimeException(e); // If anything goes wrong, just re-throw
                }

                LocationNameInfoController controller =
                    locationNameLoader.getController(); // Get the controller

                // Set the location name to the value
                controller.setLocationName(
                    row.getItem(), // Set it to the rows item
                    mapController.getMapSession(),
                    (oldName) -> {
                      {
                        locationTable.getItems().remove(oldName);
                        mapController.removeLocationName(oldName);
                      } // Remove the old name
                      tablePopOver.get().hide(); // Remove the pop-over
                    },
                    // Set the original saved row number to be the new location name
                    (oldLocation, newLocation) -> {
                      updateLocationInTable(
                          oldLocation, newLocation); // Update the location in the table
                      tablePopOver.getAndSet(null).hide(); // Hide the pop-over
                    },
                    false);

                tablePopOver.get().show(row); // Show the pop-over on the row
              });

          return row; // Return the generated row
        });

    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(Fapp.class.getResource("Map/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    mapPane.getChildren().add(0, map); // Put the map loader into the editor box
    mapController = mapLoader.getController();

    // make the anchor pane resizable
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    createLocationNameTable(
        mapController.getMapSession()); // Create the table using the map session

    AtomicReference<PopOver> mapPopOver =
        new AtomicReference<>(); // The pop-over the map is using for node highlighting

    // Set the node creation processor
    mapController.setNodeCreation(
        (node, circle) -> {
          // Set the on-click processor

          circle.setOnMouseClicked(
              (event) -> {
                // If we're no longer hovering and the pop over exists, delete it. We will
                // either create a new one
                // or, keep it deleted
                if (mapPopOver.get() != null) {
                  mapPopOver.getAndSet(null).hide(); // Hide it and clear it
                }

                // Get the node info in FXML form
                FXMLLoader nodeInfoLoader = new FXMLLoader(getClass().getResource("NodeInfo.fxml"));

                try {
                  // Try creating the pop-over
                  mapPopOver.set(new PopOver(nodeInfoLoader.load()));
                } catch (IOException e) {
                  throw new RuntimeException(e); // If it fails, throw an exception
                }

                NodeInfoController controller =
                    nodeInfoLoader.getController(); // Get the controller to use
                controller.setNode(
                    node,
                    mapController.getMapSession(),
                    (oldNode) -> {
                      mapController.deleteNode(oldNode); // On delete, delete
                      mapPopOver.getAndSet(null).hide(); // And get rid of the pop-up
                    },
                    (oldNode, newNode) -> {
                      mapController.moveNode(oldNode, newNode); // On move move
                      mapPopOver.getAndSet(null).hide(); // And get the pop-up
                    },
                    (oldLocation) -> {
                      locationTable.getItems().remove(oldLocation);
                      mapController.removeLocationName(oldLocation);
                    },
                    (oldLocation, newLocation, locationNode) -> {
                      updateLocationInTable(oldLocation, newLocation); // Update the table
                      // Update the location node
                      mapController.updateLocationName(oldLocation, newLocation, locationNode);
                    }, // Update when locations update
                    false); // Delete on delete

                mapPopOver.get().show(circle); // Show the pop-over
              });
        });

    mapController.setFloor(Node.Floor.L1);
    floorSelector.setText("Floor " + Node.Floor.L1.name());
    mapController.setFloor(Node.Floor.L1);
    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorProperty.addListener(
        (observable, oldValue, newValue) -> {
          mapController.setFloor(newValue);
          // drawNodesAndEdges(); // Re-draw pop-ups
          floorSelector.setText("Floor " + newValue.floorNum);
        }); // Set the floor text

      mapPane.setOnDragOver(new EventHandler<DragEvent>() {
          @Override
          public void handle(DragEvent event) {
              /* data is dragged over the target */
              /* accept it only if it is not dragged from the same node
               * and if it has a string data */
              if (event.getGestureSource() != mapPane &&
                      // image to represent the node?
                      event.getDragboard().hasString()) {
                  /* allow for both copying and moving, whatever user chooses */
                  event.acceptTransferModes(TransferMode.COPY);
              }

              event.consume();
          }
      });
  }

  /**
   * Creates the location name table, first clearing existing entries, then updating them
   *
   * @param session the session to use in querying
   */
  private void createLocationNameTable(@NonNull Session session) {
    locationTable.getItems().clear(); // Clear the table

    List<LocationName> longNames =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    ObservableList<LocationName> longNamesObservableList =
        FXCollections.observableList(longNames); // Create the list

    // Add the list to the table
    locationTable.getItems().addAll(longNamesObservableList);
  }

  /**
   * Handler for the help menu for the map
   *
   * @param event unsued event
   */
  @SneakyThrows
  @FXML
  public void handleQ(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the new popOver

    HelpController help = newLoad.getController(); // get the controller
    help.handleQMapEditor(); // display the correct help text

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popOver
  }

  /**
   * Creates a pop-up for a new move to be created
   *
   * @param event the unused creation event
   */
  @FXML
  @SneakyThrows
  private void popupMove(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("AddMove.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the new popOver

    AddMoveController addMove = newLoad.getController(); // get the controllers
    addMove.setAddMove(
        () -> {
          mapController
              .redraw(); // Redraw the map, to handle the new location name -> node permutations
        });
    addMove.setPopOver(popOver); // pass the popOver
    addMove.setSession(mapController.getMapSession()); // pass the session

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popover
  }

  /**
   * Creates a pop-up to create a new location
   *
   * @param event the unused event calling this
   */
  @SneakyThrows
  @FXML
  private void popupLocation(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("LocationNameNormal.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the new popover

    LocationNameInfoController addLoc = newLoad.getController(); // get the controller
    addLoc.setDeleteButtonText("Cancel"); // change the original text of the delete button
    addLoc.setLocationName( // create a new location to pass in param
        new LocationName("", LocationName.LocationType.HALL, ""),
        mapController.getMapSession(),
        (onDelete) -> popOver.hide(),
        (oldLocation, newLocation) -> {
          popOver.hide();
          locationTable.getItems().add(0, newLocation);
        },
        true);

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popover
  }

  /**
   * Replaces the selected location name in the table with a new location name
   *
   * @param oldLocation the old location
   * @param newLocation the new location
   */
  private void updateLocationInTable(LocationName oldLocation, LocationName newLocation) {
    // Replace everywhere that the old location name is with the new location name
    locationTable
        .getItems()
        .replaceAll(
            locationName -> {
              if (locationName.equals(oldLocation)) { // Check if the location is the old one
                return newLocation; // If so, return the new one
              }

              return locationName; // Return the old name otherwise
            });
  }

  /**
   * opens the popup for adding a new node f
   *
   * @param event the event triggering this (unused)
   */
  @FXML
  @SneakyThrows
  private void popupNode(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("NodeInfo.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    NodeInfoController addNode = newLoad.getController(); // get the controller
    // Provide the blank node
    addNode.setNode(
        new Node("", "", mapController.getFloor(), 0, 0),
        mapController.getMapSession(), // Get the map session
        (oldNode) -> popOver.hide(), // On delete we do nothing but hide
        (oldNode, newNode) -> {
          mapController.addNode(newNode, false);
          popOver.hide();
        }, // On create new one, process it
        (oldLocation) -> mapController.removeLocationName(oldLocation),
        (oldLocation, newLocation, node) -> {
          mapController.updateLocationName(oldLocation, newLocation, node);
        }, // No location processing, no locations
        true); // This is a new node

    popOver.detach(); // Detatch the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node) event.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover
  }

  /**
   * Handles the user pressing the back button, asks them to confirm, signals map exit and then
   * exits
   *
   * @param actionEvent the event signaling the back press, not used
   */
  @SneakyThrows
  public void handleBackButton(ActionEvent actionEvent) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("ExitConfirmation.fxml"));

    // Create a confirm exit dialog
    Pane root = loader.load();
    PopOver popOver = new PopOver(root);

    popOver.setTitle("Confirm Exit?");

    this.backButton.setDisable(true);

    ExitConfirmationController exitController = loader.getController();

    popOver.setOnHidden((action) -> this.backButton.setDisable(false));

    exitController
        .getContinueEditing()
        .setOnAction(
            (action) -> {
              this.backButton.setDisable(false);
              popOver.hide();
            });
    exitController
        .getSave()
        .setOnAction(
            (action) -> {
              popOver.hide();
              mapController.saveChanges();
              mapController.exit();
              Fapp.handleBack();
            });

    exitController
        .getDiscard()
        .setOnAction(
            (action) -> {
              popOver.hide();
              mapController.exit();
              Fapp.handleBack();
            });

    // Set the pop-up content
    popOver.setDetached(true);
    popOver.show(locationTable.getScene().getWindow()); // And show it
  }

  /**
   * Handles a press of the cancel button, rolls back everything and then regenerates the map
   *
   * @param actionEvent the action event signaling the cancel
   */
  public void handleCancel(ActionEvent actionEvent) {
    mapController.cancelChanges(); // Cancel changes
    createLocationNameTable(mapController.getMapSession()); // Reload the table
  }

  /**
   * Handles a press of the save button, commits map changes
   *
   * @param actionEvent the event signaling the save action
   */
  public void handleSave(ActionEvent actionEvent) {
    mapController.saveChanges(); // On save just save
  }

  @FXML
  public void upFloor() {
    int floorLevel = floorProperty.getValue().ordinal() + 1;
    if (floorLevel > Node.Floor.values().length - 1) floorLevel = 0;

    floorProperty.setValue(Node.Floor.values()[floorLevel]);
  }

  @FXML
  public void downFloor() {
    int floorLevel = floorProperty.getValue().ordinal() - 1;
    if (floorLevel < 0) floorLevel = Node.Floor.values().length - 1;

    floorProperty.setValue(Node.Floor.values()[floorLevel]);
  }

  @FXML
  public void openFloorSelector(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/FloorSelectorPopUp.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    popOver.setTitle("");
    FloorSelectorController floorPopup = newLoad.getController();
    floorPopup.setFloorProperty(this.floorProperty);

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node) event.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover

    floorSelectorButton.setDisable(true);
    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                floorSelectorButton.setDisable(false);
              }
            });
  }

  public void onClose() {
    mapController.exit();
  }

  public void handleQuickDraw() {}

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      hDone = false;
    }
  }
}
