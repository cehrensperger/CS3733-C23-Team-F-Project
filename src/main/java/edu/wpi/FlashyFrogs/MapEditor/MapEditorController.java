package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
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
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
@GeneratedExclusion
public class MapEditorController implements IController {
  @FXML private AnchorPane root; // Root pane, used to position the quickdraw circle
  @FXML private Button quickDraw;
  @FXML private Button addLocation;
  @FXML private Button addEdge;
  @FXML private Text h41;
  @FXML private AnchorPane mapPane;
  @FXML private Button backButton;
  private MapController mapController; // Controller for the map
  @FXML private TableView<LocationName> locationTable; // Attribute for the location table
  @FXML private CheckBox checkBox;
  @FXML private DatePicker viewingDate;

  @FXML Text h1;
  @FXML Text h2;

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
  private boolean quickDrawActive = false; // Whether quick draw is currently enabled
  private Node lastQuickDrawNode = null; // Last node for the quickdraw chain
  private Circle currentQuickDrawCircle; // Current circle for quickdraw
  private Line currentQuickDrawLine; // Current line for quickdraw

  @FXML private Circle nodeToDrag;
  private Circle duplicateCircle;

  /** Initializes the map editor, adds the map onto it */
  @SneakyThrows
  @FXML
  private void initialize() {
      viewingDate.setValue(LocalDate.now());

    duplicateCircle = new Circle(5);
    duplicateCircle.setFill(Color.RED);
    duplicateCircle.setVisible(false);
    mapPane.getChildren().add(duplicateCircle);

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

    javafx.scene.Node map = mapLoader.load(); // Load the map
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

    mapController.setEdgeCreation(
        (edge, line) -> {
          line.toBack(); // Move the line to the back, for visual reasons
        });

    mapController.setLocationCreation(
        (node, location, name) -> {
          name.setMouseTransparent(true); // Set this to not intercept mouse events
        });

    mapController
        .getCurrentDrawingPane()
        .setOnMouseClicked(
            (mouseEvent) -> {
              // If quick draw is active, and this hasn't been consumed, place this node
              if (quickDrawActive
                  && !mouseEvent.isConsumed()
                  && !mouseEvent.getSource().getClass().equals(Circle.class)) {
                // Round the X and Y
                int roundedX = (int) Math.round(mouseEvent.getX());
                int roundedY = (int) Math.round(mouseEvent.getY());

                // Place this node
                Node newNode =
                    new Node(
                        createNodeID(
                            mapController.getMapFloorProperty().getValue(), roundedX, roundedY),
                        "",
                        mapController.getMapFloorProperty().getValue(),
                        roundedX,
                        roundedY);
                mapController.getMapSession().persist(newNode); // Save the new node
                mapController.addNode(newNode, false); // Add the node onto the map

                quickDrawHandleNodeClick(newNode); // Handle the node click
              }
            });

    mapController
        .getCurrentDrawingPane()
        .addEventFilter(
            MouseEvent.MOUSE_MOVED,
            (mouseEvent) -> {
              // If quick-draw is enabled, we're on the gesture pane, and we have a source
              // node
              if (quickDrawActive
                  && lastQuickDrawNode != null) { // If we have a source node for the line

                // If the current line exists, just update it
                if (currentQuickDrawLine != null) {
                  currentQuickDrawLine.setEndX(mouseEvent.getX());
                  currentQuickDrawLine.setEndY(mouseEvent.getY());
                } else { // Otherwise
                  currentQuickDrawLine =
                      new Line(
                          lastQuickDrawNode.getXCoord(),
                          lastQuickDrawNode.getYCoord(),
                          mouseEvent.getX(),
                          mouseEvent.getY());
                  currentQuickDrawLine.setMouseTransparent(true);
                  mapController
                      .getCurrentDrawingPane()
                      .getChildren()
                      .add(currentQuickDrawLine); // Add the line
                }
              }
            });

    mapController
        .getCurrentDrawingPane()
        .addEventFilter(
            MouseEvent.MOUSE_EXITED,
            (mouseEvent) -> {
              // If we have a line and aren't in the page, delete the current quick draw line
              if (currentQuickDrawLine != null) { // If the current quick draw line exists
                // Delete the current quick draw line
                mapController.getCurrentDrawingPane().getChildren().remove(currentQuickDrawLine);
                currentQuickDrawLine = null;
              }
            });

    // Handle quick-draw stuff in terms of moving the mouse drags a node around
    Platform.runLater(
        () ->
            backButton
                .getScene()
                .addEventFilter(
                    MouseEvent.MOUSE_MOVED,
                    (mouseEvent) -> {
                      // System.out.println(root.getHeight());
                      // If quick draw is enabled
                      if (quickDrawActive) {
                        // Set the circles position
                        currentQuickDrawCircle.relocate(
                            mouseEvent.getSceneX(), mouseEvent.getSceneY() - 27);
                      }
                    }));

    // Set the button handler
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
                            (node) -> {
                              // For each node, delete it
                              mapController.deleteNode(
                                  node, checkBox.isSelected()); // Delete the node
                              mapController
                                  .getMapSession()
                                  .createMutationQuery("DELETE FROM " + "Node WHERE id = :id")
                                  .setParameter("id", node.getId())
                                  .executeUpdate();
                            }); // Delete all selected nodes
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
                      } else if (event.getCode().equals(KeyCode.ESCAPE) && quickDrawActive) {
                        toggleQuickDraw(null); // Toggle quick draw
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
                // Double-check to make sure this hasn't been removed
                if (mapController.getNodeToCircleMap().containsKey(oldNode)) {
                  // Clear the effect
                  mapController.getNodeToCircleMap().get(oldNode).setFill(Color.BLACK);
                }
              }
            });

    // Set the node creation processor
    mapController.setNodeCreation(this::nodeCreation);

    // On floor change
    mapController
        .getMapFloorProperty()
        .addListener(
            (property) -> {
              selectedNodes.clear(); // Clear the selected nodes
            });

    nodeToDrag.setOnDragDetected(
        event -> {
          Dragboard dragboard = nodeToDrag.startDragAndDrop(TransferMode.COPY);
          dragboard.setDragView(ResourceDictionary.DRAG_SVG.resource);
          ClipboardContent clipboardContent = new ClipboardContent();
          clipboardContent.putString("fjbwef");
          dragboard.setContent(clipboardContent);
        });

    nodeToDrag.setOnMouseDragged(event -> event.setDragDetect(true));

    mapPane.setOnDragOver(
        event -> {
          /* data is dragged over the target */
          /* accept it only if it is not dragged from the same node
           * and if it has a string data */
          if (event.getGestureSource() != mapPane
              &&
              // image to represent the node?
              event.getDragboard().hasString()) {
            /* allow for both copying and moving, whatever user chooses */
            event.acceptTransferModes(TransferMode.COPY);
            GesturePane gesturePane = mapController.getGesturePane();
            double scale = gesturePane.getCurrentScale();
            duplicateCircle.setRadius(5 * scale);
            duplicateCircle.setVisible(true);
            duplicateCircle.setFill(Paint.valueOf("012DFA"));
            duplicateCircle.setCenterX(event.getX());
            duplicateCircle.setCenterY(event.getY());

            // X bounds
            if (event.getX() < 0) {
              duplicateCircle.setCenterX(0);
              duplicateCircle.setCenterY(event.getY());
            } else if (event.getX() > mapPane.getWidth()) {
              duplicateCircle.setCenterX(mapPane.getWidth());
              duplicateCircle.setCenterY(event.getY());
            }

            // Y bounds
            if (event.getY() < 0) {
              duplicateCircle.setCenterY(0);
              duplicateCircle.setCenterX(event.getX());
            } else if (event.getY() > mapPane.getHeight()) {
              duplicateCircle.setCenterY(mapPane.getHeight());
              duplicateCircle.setCenterX(event.getX());
            }
          }

          event.consume();
        });

    mapPane.setOnDragDropped(
        event -> {
          GesturePane gesturePane = mapController.getGesturePane();
          double scale = gesturePane.getCurrentScale();
          // System.out.println("translate X: " + gesturePane.getCurrentX());
          // System.out.println("translate Y: " + gesturePane.getCurrentY());
          // System.out.println("scale factor: " + scale);

          // System.out.println(
          //  "actual X: " + (event.getX() * scale) + gesturePane.getCurrentX() * -1);
          // System.out.println(
          //  "actual Y: " + (event.getY() * scale) + gesturePane.getCurrentY() * -1);
          double x = (duplicateCircle.getCenterX() / scale) + gesturePane.getCurrentX() * -1;
          double y = (duplicateCircle.getCenterY() / scale) + gesturePane.getCurrentY() * -1;
          int roundedX = (int) Math.round(x);
          int roundedY = (int) Math.round(y);
          Node newNode =
              new Node(
                  createNodeID(mapController.getMapFloorProperty().getValue(), roundedX, roundedY),
                  "",
                  floorProperty.getValue(),
                  roundedX,
                  roundedY);
          mapController.addNode(newNode, false);

          // make sure the circle is within bounds
          if (x > 0 && x < mapPane.getWidth() && y > 0 && y < mapPane.getHeight()) {
            // System.out.println("out of bounds");
          }
          duplicateCircle.setVisible(false);
        });

    viewingDate
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setDate(
                  Date.from(newValue.atStartOfDay(ZoneId.of("America/Montreal")).toInstant()));
              mapController.redraw();
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
        new Node("", "", mapController.getMapFloorProperty().getValue(), 0, 0),
        mapController.getMapSession(), // Get the map session
        (oldNode) -> popOver.hide(), // On delete we do nothing but hide
        (oldNode, newNode) -> {
          mapController.addNode(newNode, false);
          popOver.hide();
        }, // On create new one, process it
        (oldLocation) -> mapController.removeLocationName(oldLocation),
        (oldLocation, newLocation, node) ->
            mapController.updateLocationName(
                oldLocation, newLocation, node), // No location processing, no locations
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

  public void onClose() {
    mapController.exit();
  }

  public void handleQuickDraw() {}

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h41.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
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
    // On hover, add an outline to the circle
    circle
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // If quick draw is enabled
              if (quickDrawActive) {
                if (newValue) {
                  circle.setFill(Color.YELLOW); // Set a hover
                } else {
                  circle.setFill(Color.BLACK); // Disable
                }
              }
            });

    // Set the on-click processor
    circle.setOnDragDetected(
        (event) -> {
          // If quick draw is enabled
          if (quickDrawActive) {
            return; // Do nothing
          }

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

          // Check to make sure that each dragged node is in bounds
          for (Node selectedNode : selectedNodes) {
            // Check the bounds
            if (selectedNode.getXCoord() + xDiff < 0
                || selectedNode.getXCoord() + xDiff > mapController.getMapWidth()
                || selectedNode.getYCoord() + yDiff < 0
                || selectedNode.getYCoord() + yDiff > mapController.getMapHeight()) {
              return;
            }
          }

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

          // If quick draw is active
          if (quickDrawActive) {
            quickDrawHandleNodeClick(node); // handle it
            return; // Don't do any selection stuff!
          }

          // If shift is not down
          if (!event.isShiftDown()) {
            selectedNodes.clear(); // Clear
          }

          // Otherwise, add this
          selectedNodes.add(node);
        });

    // On right-click (context menu)
    circle.setOnContextMenuRequested(
        (event) -> {
          // If we are dragging or quick draw is on
          if (event.isConsumed() || dragInProgress || quickDrawActive) {
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

                mapController.deleteNode(oldNode, false); // On delete, delete
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

  /**
   * Method that enables/disables QuickDraw functionality
   *
   * @param actionEvent the event triggering this
   */
  @FXML
  private void toggleQuickDraw(MouseEvent actionEvent) {
    selectedNodes
        .clear(); // Clear selected nodes. If was enabled, should be empty. Otherwise, must be
    quickDrawActive = !quickDrawActive; // Toggle quickdraw status

    // Figure out what to do with the circle
    if (quickDrawActive) {
      // If it's enabled, create
      currentQuickDrawCircle = new Circle(5, Color.BLACK);
      currentQuickDrawCircle.setOpacity(.25); // Set it to be slightly transparent
      currentQuickDrawCircle.relocate(actionEvent.getSceneX(), actionEvent.getSceneY() - 27);
      root.getChildren().add(currentQuickDrawCircle); // And add
    } else {
      // If disabled, delete it
      root.getChildren().remove(currentQuickDrawCircle);
      currentQuickDrawCircle = null; // Clear it
      lastQuickDrawNode = null; // Clear the last node

      // If the quick draw line exists
      if (currentQuickDrawLine != null) {
        // Delete it
        mapController.getCurrentDrawingPane().getChildren().remove(currentQuickDrawLine);
        currentQuickDrawLine = null;
      }
      // No need to clear the last edge, already gone (off the map editor)
    }
  }

  /**
   * Handles clicking a node in quickdraw mode. Handles creating an edge (if the edge is unique and
   * necessary)
   *
   * @param clickedNode the clicked node
   */
  private void quickDrawHandleNodeClick(@NonNull Node clickedNode) {
    // If we have a node to create an edge from
    if (lastQuickDrawNode != null) {
      // Create the edge
      Edge edge = new Edge(lastQuickDrawNode, clickedNode);

      // Check to make sure this edge is unique
      if (mapController.getMapSession().find(Edge.class, edge) == null) {
        mapController.getMapSession().persist(edge); // Save the edge

        mapController.addEdge(edge); // Draw the edge
      }

      // Update the last node to this
      lastQuickDrawNode = clickedNode;

      // If the line exists, update it
      if (currentQuickDrawLine != null) {
        currentQuickDrawLine.setStartX(lastQuickDrawNode.getXCoord());
        currentQuickDrawLine.setStartY(lastQuickDrawNode.getYCoord());
      }
    } else {
      // Update the last node to this
      lastQuickDrawNode = clickedNode;
    }
  }

  /**
   * Callback to close the map editor and show the move visualizer
   *
   * @param actionEvent the event triggering this
   */
  public void showMoveVisualizer(ActionEvent actionEvent) {
    onClose(); // Handle the exit
    Fapp.setScene("MoveVisualizer", "MoveVisualizer");
  }
}
