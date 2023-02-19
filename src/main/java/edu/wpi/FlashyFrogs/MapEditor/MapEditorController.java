package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.controllers.FloorSelectorController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
@GeneratedExclusion
public class MapEditorController implements IController {
  public Button addEdge;
  @FXML private Text h41;
  @FXML private AnchorPane mapPane;
  @FXML private Button backButton;
  @FXML private Label floorSelector;
  private MapController mapController; // Controller for the map
  @FXML private TableView<LocationName> locationTable; // Attribute for the location table
  @FXML private MFXButton floorSelectorButton;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;

  boolean hDone = false;

  @FXML
  private TableColumn<LocationName, String> longName; // Attribute for the name column of the table

  private final ObservableList<Node> selectedNodes =
      FXCollections
          .observableArrayList(); // Collection of nodes that have been selected via click/cmd+click

  // Boolean determining the floor
  private final ObjectProperty<Node.Floor> floorProperty =
      new SimpleObjectProperty<>(Node.Floor.L1);
  private PopOver circlePopOver; // Pop over for the circles
  private boolean dragInProgress; // Whether a drag is currently in progress

  /** Initializes the map editor, adds the map onto it */
  @SneakyThrows
  @FXML
  private void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h41.setVisible(false);
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

    // Make the map controller click register clear selected nodes
    mapController
        .getGesturePane()
        .setOnMouseClicked(
            (event) -> {
              // Check to ensure the node isn't consumed
              if (!event.isConsumed()) {
                selectedNodes.clear(); // Clear the nodes
              }
            });

    // Set the delete handler
    Platform.runLater(
        () ->
            mapController
                .getGesturePane()
                .getScene()
                .setOnKeyPressed(
                    (event) -> {
                      boolean shouldConsume = true; // If it's one of what we want, consume

                      if (event.getCode().equals(KeyCode.DELETE)
                          || event
                              .getCode()
                              .equals(KeyCode.BACK_SPACE)) { // if the key is delete or backsapce
                        Collection<Node> nodesToDelete =
                            selectedNodes.stream().toList(); // Get the nodes to delete

                        selectedNodes
                            .clear(); // Clear the selected nodes. This must happen before deleting
                        // in the

                        // On node delete
                        nodesToDelete.forEach(
                            (node) -> mapController.deleteNode(node)); // Delete all selected nodes
                      } else if (event.getCode().equals(KeyCode.DOWN)) { // Reversed top-bottom JFX
                        try {
                          // Try moving up
                          tryCommitBulkMove(0, 1);
                        } catch (IllegalArgumentException ignored) {
                        } // No need to do anything on error
                      } else if (event.getCode().equals(KeyCode.UP)) { // Reversed top-bottom in JFX
                        try {
                          tryCommitBulkMove(0, -1); // See above
                        } catch (IllegalArgumentException ignored) {
                        } // See above
                      } else if (event.getCode().equals(KeyCode.LEFT)) {
                        try {
                          tryCommitBulkMove(-1, 0); // See above
                        } catch (IllegalArgumentException ignored) {
                        } // See above
                      } else if (event.getCode().equals(KeyCode.RIGHT)) {
                        try {
                          tryCommitBulkMove(1, 0); // See above
                        } catch (IllegalArgumentException ignored) {
                        } // See above
                      } else {
                        shouldConsume = false; // IF we don't want it, don't consume it
                      }

                      // If we should
                      if (shouldConsume) {
                        event.consume(); // Consume it
                      }
                    }));

    createLocationNameTable(
        mapController.getMapSession()); // Create the table using the map session

    // Add the listener for the selected nodes to update styling
    selectedNodes.addListener(
        (ListChangeListener<Node>)
            listChange -> {
              listChange.next();
              // For each added node
              for (Node newNode : listChange.getAddedSubList()) {
                mapController.getNodeToCircleMap().get(newNode).setFill(Color.YELLOW);
              }

              // For each removed node
              for (Node oldNode : listChange.getRemoved()) {
                // Clear the effect
                mapController.getNodeToCircleMap().get(oldNode).setFill(Color.BLACK);
              }
            });

    // Set the node creation processor
    mapController.setNodeCreation(this::nodeCreation);

    mapController.setFloor(Node.Floor.L1);
    floorSelector.setText("Floor " + Node.Floor.L1.name());
    mapController.setFloor(Node.Floor.L1);
    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorProperty.addListener(
        (observable, oldValue, newValue) -> {
          selectedNodes.clear(); // Clear the selected nodes
          mapController.setFloor(newValue);
          // drawNodesAndEdges(); // Re-draw pop-ups
          floorSelector.setText("Floor " + newValue.floorNum);
        }); // Set the floor text
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

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h41.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h41.setVisible(false);
      hDone = false;
    }
  }

  /**
   * Callback to add an edge
   *
   * @param actionEvent the callback triggering this
   */
  @SneakyThrows
  public void popupEdge(ActionEvent actionEvent) {
    // Get the fxml
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AddEdge.fxml"));

    PopOver edgePopOver = new PopOver(fxmlLoader.load()); // Create the pop over
    edgePopOver.setTitle("Add Edge");

    addEdge.setDisable(true);

    AddEdgeController addController = fxmlLoader.getController(); // Load the controller
    addController.populate(mapController.getMapSession()); // Populate the fields
    addController.setOnAdd(
        () -> {
          this.mapController.redraw(); // Redraw the map
          edgePopOver.hide();
        });

    // Add edge controller
    addController.setOnCancel(edgePopOver::hide);

    edgePopOver.setOnHidden((handler) -> addEdge.setDisable(false));

    // Show the pop-over
    edgePopOver.show(addEdge.getScene().getWindow());
  }

  /**
   * Handles moving a circle to a new position, including updating all edges. Meant for dragging, as
   * this does not update edge placement
   *
   * @param circle the circle to move
   * @param node the node associated with the circle
   * @param newX the new x-position of the circle
   * @param newY the new y-position of the circle
   */
  private void moveCircleToPosition(
      @NonNull Circle circle, @NonNull Node node, int newX, int newY) {
    // Update the positions
    circle.setCenterX(newX); // X
    circle.setCenterY(newY); // Y

    // For every edge
    for (Edge edge : mapController.getEdgeToLineMap().keySet()) {
      Line line = mapController.getEdgeToLineMap().get(edge); // Get the edge

      // If the node 1 is this
      if (edge.getNode1().equals(node)) {
        line.setStartX(circle.getCenterX()); // Update the X
        line.setStartY(circle.getCenterY()); // Update the Y
      }

      // If the node 2 is this
      if (edge.getNode2().equals(node)) {
        line.setEndX(circle.getCenterX()); // Update the X
        line.setEndY(circle.getCenterY()); // Update the Y
      }
    }

    // Update the position of the locations
    VBox locationContainer = mapController.getNodeToLocationBox().get(node); // Get the box
    locationContainer.setLayoutX(circle.getCenterX() + 2.5); // Set the X
    locationContainer.setLayoutY(circle.getCenterY() - 20); // Set the Y
  }

  /**
   * Function to be called on node creation, handles setting actions for the circles
   *
   * @param node the node
   * @param circle the circle representing the node
   */
  private void nodeCreation(@NonNull Node node, @NonNull Circle circle) {
    // Set the on-click processor
    circle.setOnDragDetected(
        (event) -> {
          // If this node isn't selected pre-drag
          if (!selectedNodes.contains(node)) {
            if (!event.isShiftDown()) { // Clear only if shift isn't down
              selectedNodes.clear(); // Clear the other nodes
            }
            selectedNodes.add(node); // Add this one
          }

          // Disable the gesture pane (obviously)
          this.mapController.getGesturePane().setGestureEnabled(false);

          // Mark that we are dragging
          dragInProgress = true;
        });

    // On drag
    circle.setOnMouseDragged(
        (event) -> {
          // If a drag is in not progress
          if (!dragInProgress) {
            return; // Don't do anything
          }

          // Calculate the differential on the positions
          int xDiff = (int) Math.round(event.getX()) - node.getXCoord();
          int yDiff = (int) Math.round(event.getY()) - node.getYCoord();

          // For each selected node
          for (Node selectedNode : selectedNodes) {
            // Get the circle
            Circle selectedCircle = mapController.getNodeToCircleMap().get(selectedNode);

            // Move it according to the diff
            moveCircleToPosition(
                selectedCircle,
                selectedNode,
                selectedNode.getXCoord() + xDiff,
                selectedNode.getYCoord() + yDiff);
          }
        });

    // On drag stop, this is the only thing that represents that for some reason
    circle.setOnMouseReleased(
        (event) -> {
          // If a drag isn't in progress (for instance simple release)
          if (!dragInProgress) {
            return; // Do nothing
          }
          // Re-enable the gesture pane
          this.mapController.getGesturePane().setGestureEnabled(true);
          dragInProgress = false; // Mark that we are no longer dragging

          // Cast the coords
          int newX = (int) Math.round(circle.getCenterX()); // Int rounded
          int newY = (int) Math.round(circle.getCenterY()); // int rounded

          // First check to make sure that the circles position is unique. If it's not we need
          // to relocate all of them
          try {
            // Do the move, this does all DB stuff
            tryCommitBulkMove(newX - node.getXCoord(), newY - node.getYCoord());
          } catch (IllegalArgumentException duplicateNode) { // Duplicate detected
            for (Node selectedNode : selectedNodes) {
              // Get the circle
              Circle selectedCircle = mapController.getNodeToCircleMap().get(selectedNode);

              // Reset the position
              moveCircleToPosition(
                  selectedCircle, selectedNode, selectedNode.getXCoord(), selectedNode.getYCoord());
            }
          }
        });

    circle.setOnMouseClicked(
        (event) -> {
          event.consume(); // Consume the event, prevent propagation to the map pane (clears this)
          // If shift is not down
          if (!event.isShiftDown()) {
            selectedNodes.clear(); // Clear
          }

          // Othewrise, add this
          selectedNodes.add(node);
        });

    // On right-click (context menu)
    circle.setOnContextMenuRequested(
        (event) -> {
          // If we are dragging
          if (event.isConsumed() || dragInProgress) {
            return; // Don't do anything!
          }
          // If we're no longer hovering and the pop over exists, delete it. We will
          // either create a new one
          // or, keep it deleted
          clearNodePopOver();

          selectedNodes
              .clear(); // Clear the selected nodes, so what is happening is perfectly clear

          // Get the node info in FXML form
          FXMLLoader nodeInfoLoader = new FXMLLoader(getClass().getResource("NodeInfo.fxml"));

          try {
            // Try creating the pop-over
            circlePopOver = new PopOver(nodeInfoLoader.load());
          } catch (IOException e) {
            throw new RuntimeException(e); // If it fails, throw an exception
          }

          NodeInfoController controller =
              nodeInfoLoader.getController(); // Get the controller to use
          controller.setNode(
              node,
              mapController.getMapSession(),
              (oldNode) -> {
                selectedNodes.remove(oldNode); // Remove the node

                mapController.deleteNode(oldNode); // On delete, delete
                clearNodePopOver();
              },
              (oldNode, newNode) -> {
                mapController.moveNode(oldNode, newNode); // On move move
                clearNodePopOver();
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

          circlePopOver.show(circle); // Show the pop-over

          // Disable the gesture pane (this causes clunkyness when you click on the page after
          // using the pop-up)
          mapController.getGesturePane().setGestureEnabled(false);

          // On close of the pop-up
          circlePopOver.setOnHidden(
              // Re-enable map gestures
              (popCloseEvent) -> mapController.getGesturePane().setGestureEnabled(true));
        });
  }

  /**
   * Tries doing a bulk move on the selected nodes, moving them to the provided offset in the DB and
   * visually. Throws an exception if any node duplicates position. Saves no changes in that case
   *
   * @param xDiff the x-delta
   * @param yDiff the y-delta
   */
  private void tryCommitBulkMove(int xDiff, int yDiff) {
    Collection<Node> nodes =
        selectedNodes.stream().toList(); // Collection of nodes, so that we can remove them

    // For each selected node
    for (Node node : nodes) {
      if (mapController
              .getMapSession()
              .find(
                  Node.class,
                  createNodeID(node.getFloor(), node.getXCoord() + xDiff, node.getYCoord() + yDiff))
          != null) {
        throw new IllegalArgumentException("Duplicate position detected!");
      }
    }

    selectedNodes.clear(); // Clear the selected nodes. Do this all at once for efficiency

    // Now actually do the move
    for (Node node : nodes) {
      if (mapController.getNodeToCircleMap().get(node) == null) {}

      String newID =
          createNodeID(
              node.getFloor(), node.getXCoord() + xDiff, node.getYCoord() + yDiff); // Get the ID

      // Create a query to move the node in the DB
      mapController
          .getMapSession()
          .createMutationQuery(
              "UPDATE Node n SET n.id = :newID, n.xCoord = "
                  + ":newXCoord, n.yCoord = :newYCoord WHERE n.id = :oldID")
          .setParameter("newID", newID)
          .setParameter("newXCoord", node.getXCoord() + xDiff)
          .setParameter("newYCoord", node.getYCoord() + yDiff)
          .setParameter("oldID", node.getId())
          .executeUpdate();

      Node newNode = mapController.getMapSession().find(Node.class, newID); // Get the new node

      mapController.moveNode(node, newNode); // Process the node change

      selectedNodes.add(newNode); // Re-add this to the selected
    }
  }

  /**
   * Creates a node ID from the provided information. Does not perform any validation
   *
   * @param floor the floor
   * @param xCoord the x-coordinate
   * @param yCoord the y-coordinate
   * @return the new ID of the node
   */
  private static String createNodeID(Node.Floor floor, int xCoord, int yCoord) {
    return floor + String.format("X%04dY%04d", xCoord, yCoord);
  }

  /**
   * Function to be called to clear the node pop-over. Handles cases when the pop-over isn't shown
   */
  private void clearNodePopOver() {
    // If it's already null
    if (circlePopOver == null) {
      return; // Do nothing
    }

    circlePopOver.hide(); // Hide it
    circlePopOver = null; // Clear it
  }
}
