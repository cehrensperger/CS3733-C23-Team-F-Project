package edu.wpi.FlashyFrogs.MapEditor;

import static java.lang.Math.abs;
import static java.lang.Math.round;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.*;
import edu.wpi.FlashyFrogs.ORM.Alert;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import edu.wpi.FlashyFrogs.Sound;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
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
import javafx.scene.shape.Rectangle;
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
  @FXML private AnchorPane mapPane;
  @FXML private MFXButton backButton;
  private MapController mapController; // Controller for the map
  @FXML private TableView<LocationName> locationTable; // Attribute for the location table
  @FXML private CheckBox checkBox;
  @FXML private DatePicker viewingDate;
  @FXML private Cursor cursor;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;

  boolean hDone = false;
  TimerTask task;
  Timer timer;

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
  private Text locationDragText;

  /** Initializes the map editor, adds the map onto it */

  // -----------------------------------------------------------------------
  /**
   * Adds a number of milliseconds to a date returning a new object. The original date object is
   * unchanged.
   *
   * @param date the date, not null
   * @param amount the amount to add, may be negative
   * @return the new date object with the amount added
   * @throws IllegalArgumentException if the date is null
   */
  public static Date addMilliseconds(Date date, int amount) {
    return add(date, Calendar.MILLISECOND, amount);
  }

  // -----------------------------------------------------------------------
  /**
   * Adds to a date returning a new object. The original date object is unchanged.
   *
   * @param date the date, not null
   * @param calendarField the calendar field to add to
   * @param amount the amount to add, may be negative
   * @return the new date object with the amount added
   * @throws IllegalArgumentException if the date is null
   */
  public static Date add(Date date, int calendarField, int amount) {
    if (date == null) {
      Sound.ERROR.play();
      throw new IllegalArgumentException("The date must not be null");
    }
    Calendar c = Calendar.getInstance();
    c.setTime(date);
    c.add(calendarField, amount);
    return c.getTime();
  }

  @SneakyThrows
  @FXML
  private void initialize() {
    nodeToDrag.setOnMouseEntered(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            Fapp.getPrimaryStage().getScene().setCursor(Cursor.OPEN_HAND);
            root.setOnMousePressed(
                new EventHandler<MouseEvent>() {
                  @Override
                  public void handle(MouseEvent event) {
                    Fapp.getPrimaryStage().getScene().setCursor(Cursor.CLOSED_HAND);
                  }
                });
            root.setOnMouseReleased(
                new EventHandler<MouseEvent>() {
                  @Override
                  public void handle(MouseEvent event) {
                    Fapp.getPrimaryStage().getScene().setCursor(Cursor.OPEN_HAND);
                  }
                });
          }
        });

    nodeToDrag.setOnMouseExited(
        new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
            Fapp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
            root.setOnMousePressed(p -> {});
            root.setOnMouseReleased(p -> {});
            event.consume();
          }
        });

    viewingDate.setValue(LocalDate.now());

    duplicateCircle = new Circle(5);
    duplicateCircle.setFill(Color.RED);
    duplicateCircle.setVisible(false);

    locationDragText = new Text("");
    locationDragText.setVisible(false);

    mapPane.getChildren().add(duplicateCircle);
    mapPane.getChildren().add(locationDragText);

    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    h7.setVisible(false);
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

          row.setOnMouseDragged(event -> event.setDragDetect(true));

          row.setOnMouseEntered(
              event -> {
                if (!mapController.getLocs().contains(row.getItem())) {
                  Fapp.getPrimaryStage().getScene().setCursor(Cursor.OPEN_HAND);

                  row.setOnMouseExited(
                      event5 -> {
                        Fapp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
                        row.setOnMousePressed(p -> {});
                        row.setOnMouseReleased(p -> {});
                        nodeToDrag.setOnMousePressed(p -> {});
                        nodeToDrag.setOnMouseReleased(p -> {});
                      });

                  row.setOnMousePressed(
                      event14 -> Fapp.getPrimaryStage().getScene().setCursor(Cursor.CLOSED_HAND));

                  row.setOnMouseReleased(
                      event13 -> Fapp.getPrimaryStage().getScene().setCursor(Cursor.OPEN_HAND));

                  row.setOnDragDetected(
                      dragEvent -> {
                        timer = new Timer(true);
                        Dragboard dragboard = row.startDragAndDrop(TransferMode.COPY);
                        dragboard.setDragView(ResourceDictionary.TRANSPARENT_IMAGE.resource);
                        ClipboardContent clipboardContent = new ClipboardContent();
                        String longName = row.getItem().getLongName();
                        clipboardContent.putString(longName);
                        dragboard.setContent(clipboardContent);
                        locationDragText.setText(longName);
                        mapPane.setOnDragOver(p -> {});
                        mapPane.setOnDragDropped(p -> {});

                        root.setOnDragOver(
                            event12 -> {
                              locationDragText.setVisible(true);
                              locationDragText.setX(event12.getX() - 250);
                              locationDragText.setY(event12.getY());

                              double xPos = event12.getX() - mapPane.getLayoutX();
                              double yPos = event12.getY() - mapPane.getLayoutY();

                              double kp = 0.01;
                              double errorX = xPos - (mapPane.getWidth() / 2);
                              double errorY = yPos - (mapPane.getHeight() / 2);
                              double[] effortX = {0};
                              double[] effortY = {0};

                              if (abs(errorX) > 0.95 * (mapPane.getWidth() / 2)) {
                                effortX[0] = errorX * kp;
                              }
                              if (abs(errorY) > 0.95 * (mapPane.getHeight() / 2)) {
                                effortY[0] = errorY * kp;
                              }

                              if (task != null) task.cancel();
                              task =
                                  new TimerTask() {
                                    @Override
                                    public void run() {
                                      Platform.runLater(
                                          () ->
                                              mapController
                                                  .getGesturePane()
                                                  .translateBy(
                                                      new Dimension2D(effortX[0], effortY[0])));
                                    }
                                  };

                              timer.scheduleAtFixedRate(task, 0, 5);
                            });

                        root.setOnDragDone(
                            event1 -> {
                              locationDragText.setVisible(false);
                              if (timer != null) timer.cancel();
                              if (task != null) task.cancel();
                            });

                        for (Node node : mapController.getNodeToCircleMap().keySet()) {
                          Circle circle = mapController.getNodeToCircleMap().get(node);

                          circle.setOnDragOver(
                              event18 -> {
                                event18.acceptTransferModes(TransferMode.COPY);
                                // "#F6BD38" - Hospital Yellow
                                circle.setFill(Paint.valueOf("#F6BD38"));
                              });

                          circle.setOnDragExited(
                              event16 -> circle.setFill(Paint.valueOf(Color.BLACK.toString())));

                          circle.setOnDragDropped(
                              event17 -> {
                                Session session = mapController.getMapSession();
                                LocationName locationName =
                                    session.find(
                                        LocationName.class,
                                        dragboard.getContent(DataFormat.PLAIN_TEXT));

                                Date date =
                                    Date.from(
                                        viewingDate
                                            .getValue()
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant());

                                // Move newMove = new Move(node, locationName, date);
                                Node fromNode = locationName.getCurrentNode(date);
                                Move newMove = new Move(node, locationName, date);
                                session.flush();
                                if (newMove.getLocation().getCurrentNode(date) != null) {
                                  FXMLLoader newLoad =
                                      new FXMLLoader(
                                          Fapp.class.getResource(
                                              "MapEditor/EquipmentTransferConfirmationPopOver.fxml"));
                                  PopOver popOver = null; // create the popover
                                  AtomicReference<PopOver> equipmentPopOver =
                                      new AtomicReference<>();
                                  try {
                                    popOver = new PopOver(newLoad.load());
                                  } catch (IOException e) {
                                    throw new RuntimeException(e);
                                  }

                                  EquipmentTransferConfirmationPopOverController controller =
                                      newLoad.getController();
                                  PopOver finalPopOver = popOver;
                                  controller
                                      .getNoButton()
                                      .setOnAction(
                                          e -> {
                                            finalPopOver.hide();
                                          });

                                  controller
                                      .getYesButton()
                                      .setOnAction(
                                          e -> {
                                            finalPopOver.hide();

                                            FXMLLoader newNewLoader =
                                                new FXMLLoader(
                                                    Fapp.class.getResource(
                                                        "MapEditor/EquipmentTransportPopOver.fxml"));

                                            try {
                                              equipmentPopOver.set(
                                                  new PopOver(newNewLoader.load()));
                                              EquipmentTransportPopOverController
                                                  equipmentTransportPopOverController =
                                                      newNewLoader.getController();
                                              equipmentPopOver.get().detach();
                                              equipmentTransportPopOverController
                                                  .getTo()
                                                  .setValue(locationName);
                                              equipmentTransportPopOverController.setFromNode(
                                                  fromNode);
                                              equipmentTransportPopOverController
                                                  .getFrom()
                                                  .setText("From Node: " + fromNode.toString());
                                              equipmentTransportPopOverController
                                                  .getSubmitButton()
                                                  .setOnAction(
                                                      ev -> {
                                                        try {
                                                          // check
                                                          if (equipmentTransportPopOverController
                                                                  .getEquipment()
                                                                  .getText()
                                                                  .equals("")
                                                              || equipmentTransportPopOverController
                                                                  .getTo()
                                                                  .getValue()
                                                                  .toString()
                                                                  .equals("")
                                                              || equipmentTransportPopOverController
                                                                  .getDate()
                                                                  .getValue()
                                                                  .toString()
                                                                  .equals("")
                                                              || equipmentTransportPopOverController
                                                                  .getDescription()
                                                                  .getText()
                                                                  .equals("")) {
                                                            throw new NullPointerException();
                                                          }

                                                          Date dateNeeded =
                                                              Date.from(
                                                                  equipmentTransportPopOverController
                                                                      .getDate()
                                                                      .getValue()
                                                                      .atStartOfDay(
                                                                          ZoneId.systemDefault())
                                                                      .toInstant());

                                                          EquipmentTransport equipmentTransport =
                                                              new EquipmentTransport(
                                                                  CurrentUserEntity.CURRENT_USER
                                                                      .getCurrentUser(),
                                                                  dateNeeded,
                                                                  Date.from(Instant.now()),
                                                                  equipmentTransportPopOverController
                                                                      .getUrgency()
                                                                      .getValue(),
                                                                  equipmentTransportPopOverController
                                                                      .getTo()
                                                                      .getValue(),
                                                                  // what is this?
                                                                  fromNode,
                                                                  equipmentTransportPopOverController
                                                                      .getEquipment()
                                                                      .getText(),
                                                                  equipmentTransportPopOverController
                                                                      .getDescription()
                                                                      .getText());

                                                          try {
                                                            session.persist(equipmentTransport);
                                                            equipmentPopOver.get().hide();
                                                            // toastAnimation();
                                                            Sound.SUBMITTED.play();
                                                          } catch (RollbackException exception) {
                                                            Sound.ERROR.play();
                                                          }
                                                        } catch (ArrayIndexOutOfBoundsException
                                                            | NullPointerException exception) {
                                                          Sound.ERROR.play();
                                                        }
                                                      });
                                              equipmentPopOver.get().show(mapPane);
                                            } catch (IOException ex) {
                                              throw new RuntimeException(ex);
                                            }
                                          });

                                  popOver.detach(); // Detach the pop-up, so it's not stuck to the
                                  // button

                                  popOver.show(mapPane); // display the popover

                                  //                                      popOver
                                  //                                          .showingProperty()
                                  //                                          .addListener(
                                  //                                              (observable,
                                  // oldValue, newValue) -> {
                                  //                                                if
                                  // (!newValue) {
                                  //                                                  try {
                                  //
                                  // controller.handleSubmit(
                                  //
                                  // mapController.getMapSession());
                                  //                                                  } catch
                                  // (IOException e) {
                                  //                                                    throw
                                  // new RuntimeException(e);
                                  //                                                  }
                                  //                                                }
                                  //                                              });
                                }

                                session.persist(newMove);

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(newMove.getMoveDate());
                                calendar.add(Calendar.DAY_OF_YEAR, 7);
                                Date weekLater = calendar.getTime();

                                calendar = Calendar.getInstance();
                                calendar.setTime(newMove.getMoveDate());
                                calendar.add(Calendar.DAY_OF_YEAR, -7);
                                Date weekEarlier = calendar.getTime();

                                Alert alert =
                                    new Alert(
                                        weekEarlier,
                                        weekLater,
                                        CurrentUserEntity.CURRENT_USER.getCurrentUser(),
                                        "Recent Move!",
                                        newMove.getLocation().getLongName()
                                            + " has moved! Check the move visualizer for more details.",
                                        CurrentUserEntity.CURRENT_USER
                                            .getCurrentUser()
                                            .getDepartment(),
                                        Alert.Severity.MILD);
                                session.persist(alert);

                                session.flush();
                                mapController.redraw();
                              });
                        }
                        dragEvent.consume();
                      });
                } else {
                  row.setOnMouseExited(p -> {});

                  row.setOnMousePressed(p -> {});

                  row.setOnMouseReleased(p -> {});

                  row.setOnDragDetected(p -> {});
                }

                event.consume();
              });
          // Add a listener to show the pop-up
          row.setOnMouseClicked(
              (mouseEvent) -> {
                // If the pop over exists and is either not focused or we are showing a
                // new
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
                      locationTable.getItems().remove(oldName);
                      mapController.removeLocationName(oldName);
                      // Remove the old name
                      tablePopOver.getAndSet(null).hide(); // Remove the pop-over
                    },
                    // Set the original saved row number to be the new location name
                    (oldLocation, newLocation) -> {
                      updateLocationInTable(
                          oldLocation, newLocation); // Update the location in the table
                      tablePopOver.getAndSet(null).hide(); // Hide the pop-over
                    },
                    false);

                tablePopOver.get().show(row); // Show the pop-over on the row
                mouseEvent.consume();
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

              mouseEvent.consume();
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
              mouseEvent.consume();
            });

    // back button no longer exists

    // Handle quick-draw stuff in terms of moving the mouse drags a node around
    //    Platform.runLater(
    //        () ->
    //            backButton
    //                .getScene()
    //                .addEventFilter(
    //                    MouseEvent.MOUSE_MOVED,
    //                    (mouseEvent) -> {
    //                      // System.out.println(root.getHeight());
    //                      // If quick draw is enabled
    //                      if (quickDrawActive) {
    //                        // Set the circles position
    //                        currentQuickDrawCircle.relocate(
    //                            mouseEvent.getSceneX(), mouseEvent.getSceneY() - 27);
    //                      }
    //                      mouseEvent.consume();
    //                    }));

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
                        mapController.redraw(); // Redraw on update
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
          timer = new Timer(true);
          Dragboard dragboard = nodeToDrag.startDragAndDrop(TransferMode.COPY);
          dragboard.setDragView(ResourceDictionary.TRANSPARENT_IMAGE.resource);
          ClipboardContent clipboardContent = new ClipboardContent();
          clipboardContent.putString("fjbwef");
          dragboard.setContent(clipboardContent);

          mapPane.setOnDragEntered(
              e -> {
                e.acceptTransferModes(TransferMode.ANY);
                duplicateCircle.setVisible(true);
                event.consume();
              });
          mapPane.setOnDragExited(
              e -> {
                if (task != null) task.cancel();
                duplicateCircle.setVisible(false);
                event.consume();
              });
          root.setOnDragDone(
              e -> {
                if (timer != null) timer.cancel();
                if (task != null) task.cancel();
                duplicateCircle.setVisible(false);
                event.consume();
              });
          mapPane.setOnDragOver(
              e -> {
                /* data is dragged over the target */
                /* accept it only if it is not dragged from the same node
                 * and if it has a string data */
                if (e.getGestureSource() != mapPane
                    &&
                    // image to represent the node?
                    e.getDragboard().hasString()) {
                  /* allow for both copying and moving, whatever user chooses */
                  e.acceptTransferModes(TransferMode.COPY);
                  GesturePane gesturePane = mapController.getGesturePane();
                  double scale = gesturePane.getCurrentScale();
                  duplicateCircle.setOpacity(1);
                  duplicateCircle.setRadius(5 * scale);
                  duplicateCircle.setVisible(true);
                  duplicateCircle.setFill(Paint.valueOf("012D5A"));
                  duplicateCircle.setCenterX(e.getX());
                  duplicateCircle.setCenterY(e.getY());
                  double kp = 0.01;
                  double errorX = e.getX() - (mapPane.getWidth() / 2);
                  double errorY = e.getY() - (mapPane.getHeight() / 2);
                  double[] effortX = {0};
                  double[] effortY = {0};

                  if (abs(errorX) > 0.9 * (mapPane.getWidth() / 2)) {
                    effortX[0] = errorX * kp;
                  }
                  if (abs(errorY) > 0.9 * (mapPane.getHeight() / 2)) {
                    effortY[0] = errorY * kp;
                  }

                  if (task != null) task.cancel();

                  task =
                      new TimerTask() {
                        @Override
                        public void run() {
                          Platform.runLater(
                              () ->
                                  mapController
                                      .getGesturePane()
                                      .translateBy(new Dimension2D(effortX[0], effortY[0])));
                        }
                      };

                  timer.scheduleAtFixedRate(task, 0, 5);
                  // X bounds
                  if (e.getX() < 0) {
                    duplicateCircle.setVisible(false);
                  } else if (e.getX() > mapPane.getWidth()) {
                    duplicateCircle.setVisible(false);
                  }

                  // Y bounds
                  if (e.getY() < 0) {
                    duplicateCircle.setVisible(false);
                  } else if (e.getY() > mapPane.getHeight()) {
                    duplicateCircle.setVisible(false);
                  }
                }

                e.consume();
              });
          event.consume();
        });

    nodeToDrag.setOnMouseDragged(
        event -> {
          event.consume();
          event.setDragDetect(true);
        });

    duplicateCircle.setOnDragDropped(
        event -> {
          if (task != null) task.cancel();
          GesturePane gesturePane = mapController.getGesturePane();
          double scale = gesturePane.getCurrentScale();
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

          mapController.getMapSession().persist(newNode);
          mapController.getMapSession().flush();
          mapController.addNode(newNode, false);

          duplicateCircle.setVisible(false);
          event.consume();
        });

    viewingDate
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setDate(
                  add(
                      Date.from(newValue.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                      Calendar.MILLISECOND,
                      1));
              mapController.redraw();
            });
    setBoxCreation();
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
        (oldNode) -> {
          popOver.hide();
          mapController.redraw();
        }, // On delete we do nothing but hide
        (oldNode, newNode) -> {
          mapController.addNode(newNode, false);
          popOver.hide();
        }, // On create new one, process it
        (oldLocation) -> {
          mapController.removeLocationName(oldLocation);
          mapController.redraw();
        },
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
    actionEvent.consume();
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
    if (task != null) task.cancel();
    if (timer != null) timer.cancel();
    mapController.exit();
  }

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      h7.setVisible(true);
      hDone = true;
    } else {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      h7.setVisible(false);
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
          timer = new Timer(true);
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
          event.consume();
        });

    // On drag
    circle.setOnMouseDragged(
        (event) -> {
          event.consume();
          // If a drag is in not progress
          if (!dragInProgress) {
            return; // Don't do anything
          }

          // Calculate the differential on the positions
          int xDiff = (int) Math.round(event.getX()) - node.getXCoord();
          int yDiff = (int) Math.round(event.getY()) - node.getYCoord();

          // get the 4 outside most nodes
          Node lowest = selectedNodes.get(0);
          Node highest = selectedNodes.get(0);
          Node left = selectedNodes.get(0);
          Node right = selectedNodes.get(0);
          for (Node selectedNode : selectedNodes) {
            if (selectedNode.getXCoord() < left.getXCoord()) left = selectedNode;
            if (selectedNode.getXCoord() > right.getXCoord()) right = selectedNode;
            if (selectedNode.getYCoord() > lowest.getYCoord()) lowest = selectedNode;
            if (selectedNode.getYCoord() < highest.getYCoord()) highest = selectedNode;
          }

          double mapCenterX =
              (mapController.getGesturePane().getCurrentX() * -1)
                  + ((mapController.getGesturePane().getWidth()
                          / mapController.getGesturePane().getCurrentScaleX())
                      / 2);

          double mapCenterY =
              (mapController.getGesturePane().getCurrentY() * -1)
                  + ((mapController.getGesturePane().getHeight()
                          / mapController.getGesturePane().getCurrentScaleY())
                      / 2);

          double kp = 0.01;
          double leftErrorX =
              mapController.getNodeToCircleMap().get(left).getCenterX() - mapCenterX;
          double rightErrorX =
              mapController.getNodeToCircleMap().get(right).getCenterX() - mapCenterX;
          double highErrorY =
              mapController.getNodeToCircleMap().get(highest).getCenterY() - mapCenterY;
          double lowErrorY =
              mapController.getNodeToCircleMap().get(lowest).getCenterY() - mapCenterY;

          double[] effortX = {0};
          double[] effortY = {0};

          if (abs(leftErrorX)
              > 0.9
                  * (mapPane.getWidth() / mapController.getGesturePane().getCurrentScaleX() / 2)) {
            effortX[0] = leftErrorX * kp;
          } else if (abs(rightErrorX)
              > 0.9
                  * (mapPane.getWidth() / mapController.getGesturePane().getCurrentScaleX() / 2)) {
            effortX[0] = rightErrorX * kp;
          }
          if (abs(highErrorY)
              > 0.9
                  * (mapPane.getHeight() / mapController.getGesturePane().getCurrentScaleY() / 2)) {
            effortY[0] = highErrorY * kp;
          } else if (abs(lowErrorY)
              > 0.9
                  * (mapPane.getHeight() / mapController.getGesturePane().getCurrentScaleY() / 2)) {
            effortY[0] = lowErrorY * kp;
          }

          if (task != null) {
            task.cancel();
          }

          Node finalRight = right;
          Node finalLeft = left;
          Node finalHighest = highest;
          Node finalLowest = lowest;
          task =
              new TimerTask() {
                @Override
                public void run() {
                  Platform.runLater(
                      () -> {
                        // First, validate bounds
                        if (mapController.getNodeToCircleMap().get(finalRight).getCenterX()
                                + effortX[0]
                            > mapController.getMapWidth()) return;
                        if (mapController.getNodeToCircleMap().get(finalLeft).getCenterX()
                                + effortX[0]
                            < 0) return;
                        if (mapController.getNodeToCircleMap().get(finalLowest).getCenterY()
                                + effortY[0]
                            > mapController.getMapHeight()) return;
                        if (mapController.getNodeToCircleMap().get(finalHighest).getCenterY()
                                + effortY[0]
                            < 0) return;

                        mapController
                            .getGesturePane()
                            .translateBy(new Dimension2D(round(effortX[0]), round(effortY[0])));

                        for (Node node : selectedNodes) {
                          moveCircleToPosition(
                              mapController.getNodeToCircleMap().get(node),
                              node,
                              (int)
                                  round(
                                      mapController.getNodeToCircleMap().get(node).getCenterX()
                                          + effortX[0]),
                              (int)
                                  round(
                                      mapController.getNodeToCircleMap().get(node).getCenterY()
                                          + effortY[0]));
                        }
                      });
                }
              };

          timer.scheduleAtFixedRate(task, 0, 5);

          event.consume();

          // First, validate the bounds
          if (finalRight.getXCoord() + xDiff > mapController.getMapWidth()) return;
          if (finalLeft.getXCoord() + xDiff < 0) return;
          if (finalLowest.getYCoord() + yDiff > mapController.getMapHeight()) return;
          if (finalHighest.getYCoord() + yDiff < 0) return;

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
          if (task != null) task.cancel();
          if (timer != null) timer.cancel();

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

          event.consume();
        });

    // handles both left and right click since I couldn't get
    // setOnContextMenuRequested to fire at the correct time
    circle.setOnMouseClicked(
        (event) -> {

          // need to check to make sure for some reason
          if (event.isConsumed()) {
            return;
          }

          // MouseButton.SECONDARY == right click
          // TODO: make this work with other ways of doing right click (ctrl + left-click)
          if (event.getButton() == MouseButton.SECONDARY) {

            // Connor's notes
            // if circle is not in selected nodes, it is outside the selection, don't have to worry
            // about weirdness
            // node and circle represent the same thing
            // If circle is not in currently selected nodes, deselect other selected nodes, add this
            // one and show Ian's popup
            // If selected nodes is empty, show Ian's popup on circle
            // If circle is in selected nodes, and it is length 1, do above
            // If circle is in selected nodes, and it is more than length 1, do new thing
            //
            // If we are dragging or quick draw is on
            if (event.isConsumed() || dragInProgress || quickDrawActive) {
              return; // Don't do anything!
            }
            // If we're no longer hovering and the pop over exists, delete it. We will
            // either create a new one
            // or, keep it deleted
            clearNodePopOver();
            if (selectedNodes.contains(node) && selectedNodes.size() > 1) {

              // Bulk right click has occurred
              // create a popup at the mouse position *in the gesture pane*

              // TODO: make this look a lot better....
              FXMLLoader contextMenuLoader =
                  new FXMLLoader(getClass().getResource("GroupSelectionContextMenu.fxml"));
              try {
                // Try creating the pop-over
                circlePopOver = new PopOver(contextMenuLoader.load());
              } catch (IOException e) {
                throw new RuntimeException(e); // If it fails, throw an exception
              }

              // don't let the user drag the popup around
              circlePopOver.setDetachable(false);

              // load the popup controller
              GroupSelectionContextMenuController controller =
                  contextMenuLoader.getController(); // Get the controller to use

              // set the button action handling methods so we don't have to pass state to the
              // controller I guess
              controller.setOnAutoAlign(
                  e -> {
                    // TODO: set alignVertical based on standard deviation????
                    double[] xVals = new double[selectedNodes.size()];
                    double[] yVals = new double[selectedNodes.size()];
                    for (int i = 0; i < selectedNodes.size(); i++) {
                      xVals[i] = selectedNodes.get(i).getXCoord();
                      yVals[i] = selectedNodes.get(i).getYCoord();
                    }
                    double xStdDev = calculateSD(xVals);
                    double yStdDev = calculateSD(yVals);
                    tryAutoAlign(
                        (int) circle.getCenterX(), (int) circle.getCenterY(), yStdDev >= xStdDev);
                  });

              controller.setOnDeleteLocations(
                  event1 -> {
                    for (Node n : selectedNodes) {
                      for (LocationName loc : mapController.getNodeToLocationNameMap().get(n)) {
                        mapController.removeLocationName(loc);
                      }
                    }
                  });

              controller.setOnDeleteNodes(
                  event12 -> mapController.deleteNodes(selectedNodes, checkBox.isSelected()));
              circlePopOver.show(circle); // Show the pop-over

              // Disable the gesture pane (this causes clunkyness when you click on the page after
              // using the pop-up)
              mapController.getGesturePane().setGestureEnabled(false);

              // On close of the pop-up
              circlePopOver.setOnHidden(
                  // Re-enable map gestures
                  (popCloseEvent) -> mapController.getGesturePane().setGestureEnabled(true));

              event.consume();
            } else {

              // If right-clicked circle is outside the current selection
              // clear the other selection and do the thing for one node
              if (!selectedNodes.contains(node)
                  || (selectedNodes.size() == 1 && selectedNodes.contains(node))) {

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
              }
            }

          } else {
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
            if (selectedNodes.contains(node)) {
              selectedNodes.remove(node);
            } else {
              selectedNodes.add(node);
            }
          }

          event.consume();
        });
  }

  /**
   * @param x X coordinate of the circle you are aligning to.
   * @param y Y coordinate of the circle you are aligning to.
   * @param alignVertical whether to align vertically or horizontally.
   */
  private void tryAutoAlign(int x, int y, boolean alignVertical) {

    Collection<Node> nodes =
        selectedNodes.stream().toList(); // Collection of nodes, so that we can remove them

    for (Node node : nodes) {

      if (alignVertical) {
        if (x != node.getXCoord()) {

          if (mapController
                  .getMapSession()
                  .find(Node.class, createNodeID(node.getFloor(), x, node.getYCoord()))
              != null) {
            Sound.ERROR.play();
            throw new IllegalArgumentException("Duplicate position detected!");
          }
        }
      } else {
        if (y != node.getYCoord()) {
          if (mapController
                  .getMapSession()
                  .find(Node.class, createNodeID(node.getFloor(), node.getXCoord(), y))
              != null) {
            Sound.ERROR.play();
            throw new IllegalArgumentException("Duplicate position detected!");
          }
        }
      }
    }

    selectedNodes.clear();
    // Now actually do the move
    for (Node node : nodes) {
      if (alignVertical) {
        String newID = createNodeID(node.getFloor(), x, node.getYCoord()); // Get the ID

        // Create a query to move the node in the DB
        mapController
            .getMapSession()
            .createMutationQuery(
                "UPDATE Node n SET n.id = :newID, n.xCoord = "
                    + ":newXCoord, n.yCoord = :newYCoord WHERE n.id = :oldID")
            .setParameter("newID", newID)
            .setParameter("newXCoord", x)
            .setParameter("newYCoord", node.getYCoord())
            .setParameter("oldID", node.getId())
            .executeUpdate();

        Node newNode = mapController.getMapSession().find(Node.class, newID); // Get the new node

        mapController.moveNode(node, newNode); // Process the node change

        selectedNodes.add(newNode); // Re-add this to the selected
      } else {
        String newID = createNodeID(node.getFloor(), node.getXCoord(), y); // Get the ID

        // Create a query to move the node in the DB
        mapController
            .getMapSession()
            .createMutationQuery(
                "UPDATE Node n SET n.id = :newID, n.xCoord = "
                    + ":newXCoord, n.yCoord = :newYCoord WHERE n.id = :oldID")
            .setParameter("newID", newID)
            .setParameter("newXCoord", node.getXCoord())
            .setParameter("newYCoord", y)
            .setParameter("oldID", node.getId())
            .executeUpdate();

        Node newNode = mapController.getMapSession().find(Node.class, newID); // Get the new node

        mapController.moveNode(node, newNode); // Process the node change

        selectedNodes.add(newNode); // Re-add this to the selected
      }
    }
  }

  /**
   * Tries doing a bulk move on the selected nodes, moving them to the provided offset in the DB and
   * visually. Throws an exception if any node duplicates position. Saves no changes in that case
   *
   * @param xDiff the x-delta
   * @param yDiff the y-delta
   */
  private void tryCommitBulkMove(int xDiff, int yDiff) {
    // If there's no delta, do nothing
    if (xDiff == 0 && yDiff == 0) {
      return;
    }

    Node lowest = selectedNodes.get(0);
    Node highest = selectedNodes.get(0);
    Node left = selectedNodes.get(0);
    Node right = selectedNodes.get(0);

    Collection<Node> nodes =
        selectedNodes.stream().toList(); // Collection of nodes, so that we can remove them

    // For each selected node
    for (Node node : nodes) {
      if (mapController
              .getMapSession()
              .createQuery("FROM Node WHERE id = :id", Node.class)
              .setParameter(
                  "id",
                  createNodeID(node.getFloor(), node.getXCoord() + xDiff, node.getYCoord() + yDiff))
              .uniqueResult()
          != null) {
        Sound.ERROR.play();
        throw new IllegalArgumentException("Duplicate position detected!");
      }

      if (node.getXCoord() < left.getXCoord()) left = node;
      else if (node.getXCoord() > right.getXCoord()) right = node;
      else if (node.getYCoord() > lowest.getYCoord()) lowest = node;
      else if (node.getYCoord() < highest.getYCoord()) highest = node;
    }

    selectedNodes.clear(); // Clear the selected nodes. Do this all at once for efficiency

    if (mapController.getNodeToCircleMap().get(left).getCenterX() < 20) {
      xDiff = 50 - left.getXCoord();
    }
    if (mapController.getNodeToLocationBox().get(highest).getLayoutY() < 20) {
      yDiff = 50 - highest.getYCoord();
    }
    if (mapController.getNodeToLocationBox().get(lowest).getLayoutY()
            + mapController.getNodeToLocationBox().get(lowest).getHeight()
        > mapController.getCurrentDrawingPane().getHeight() - 20) {
      yDiff =
          (int)
              (round(
                      (mapController.getCurrentDrawingPane().getHeight()
                              - mapController.getNodeToLocationBox().get(lowest).getHeight())
                          - 100)
                  - lowest.getYCoord());
    }
    if (mapController.getNodeToLocationBox().get(right).getLayoutX()
            + mapController.getNodeToLocationBox().get(right).getWidth()
        > mapController.getCurrentDrawingPane().getWidth() - 20) {
      xDiff =
          (int)
              (round(
                      (mapController.getCurrentDrawingPane().getWidth()
                              - -mapController.getNodeToLocationBox().get(right).getWidth())
                          - 200)
                  - right.getXCoord());
    }

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

  // https://www.programiz.com/java-programming/examples/standard-deviation
  private static double calculateSD(double[] numArray) {
    double sum = 0.0, standardDeviation = 0.0;
    int length = numArray.length;

    for (double num : numArray) {
      sum += num;
    }

    double mean = sum / length;

    for (double num : numArray) {
      standardDeviation += Math.pow(num - mean, 2);
    }

    return Math.sqrt(standardDeviation / length);
  }

  private void setBoxCreation() {
    mapController
        .getCurrentDrawingPane()
        .setOnMousePressed(
            event -> {
              if (!event.isSecondaryButtonDown()) {
                if (quickDrawActive) return; // Don't do anything if quick draw is one

                Timer timer = new Timer(true);
                mapController.getGesturePane().setGestureEnabled(false);
                double startX = event.getX();
                double startY = event.getY();
                Rectangle rect = new Rectangle(startX, startY, 0, 0);
                mapController.getCurrentDrawingPane().getChildren().add(rect);
                rect.setFill(Paint.valueOf("012D5A"));
                rect.setOpacity(0.3);

                mapController
                    .getCurrentDrawingPane()
                    .setOnMouseDragged(
                        e -> {
                          if (!e.isConsumed()) {
                            double width = e.getX() - startX;
                            double height = e.getY() - startY;
                            double kp = 0.2;
                            double errorX;
                            double errorY;

                            if (e.getX() >= mapController.getGesturePane().getCurrentX() * -1
                                && e.getX()
                                    <= (mapController.getGesturePane().getWidth()
                                            / mapController.getGesturePane().getCurrentScaleX()
                                        - mapController.getGesturePane().getCurrentX())) {
                              if (width < 0) {
                                rect.setX(e.getX());
                              }
                              rect.setWidth(abs(width));
                            }
                            if (e.getX()
                                < (mapController.getGesturePane().getCurrentX() * -1)
                                    + 50.0 / mapController.getGesturePane().getCurrentScaleX()) {
                              errorX =
                                  e.getX()
                                      - ((mapController.getGesturePane().getCurrentX() * -1)
                                          + 50.0
                                              / mapController.getGesturePane().getCurrentScaleX());
                            } else if (e.getX()
                                > ((mapController.getGesturePane().getWidth()
                                            / mapController.getGesturePane().getCurrentScaleX())
                                        - mapController.getGesturePane().getCurrentX())
                                    - 50.0 / mapController.getGesturePane().getCurrentScaleX()) {
                              errorX =
                                  e.getX()
                                      - (((mapController.getGesturePane().getWidth()
                                                  / mapController
                                                      .getGesturePane()
                                                      .getCurrentScaleX())
                                              - mapController.getGesturePane().getCurrentX())
                                          - 50.0
                                              / mapController.getGesturePane().getCurrentScaleX());
                            } else {
                              errorX = 0;
                            }

                            if (e.getY() >= mapController.getGesturePane().getCurrentY() * -1
                                && e.getY()
                                    <= (mapController.getGesturePane().getHeight()
                                            / mapController.getGesturePane().getCurrentScaleY()
                                        - mapController.getGesturePane().getCurrentY())) {
                              if (height < 0) {
                                rect.setY(e.getY());
                              }
                              rect.setHeight(abs(height));
                            }
                            if (e.getY()
                                < (mapController.getGesturePane().getCurrentY() * -1)
                                    + 50.0 / mapController.getGesturePane().getCurrentScaleY()) {
                              errorY =
                                  e.getY()
                                      - ((mapController.getGesturePane().getCurrentY() * -1)
                                          + 50.0
                                              / mapController.getGesturePane().getCurrentScaleX());
                            } else if (e.getY()
                                > ((mapController.getGesturePane().getHeight()
                                            / mapController.getGesturePane().getCurrentScaleY())
                                        - mapController.getGesturePane().getCurrentY())
                                    - 50.0 / mapController.getGesturePane().getCurrentScaleY()) {
                              errorY =
                                  e.getY()
                                      - (((mapController.getGesturePane().getHeight()
                                                  / mapController
                                                      .getGesturePane()
                                                      .getCurrentScaleY())
                                              - mapController.getGesturePane().getCurrentY())
                                          - 50.0
                                              / mapController.getGesturePane().getCurrentScaleY());
                            } else {
                              errorY = 0;
                            }
                            double[] finalErrorX = {errorX};
                            double[] finalErrorY = {errorY};
                            double[] width2 = {width};
                            double[] height2 = {height};

                            if (task != null) task.cancel();

                            task =
                                new TimerTask() {
                                  @Override
                                  public void run() {
                                    Platform.runLater(
                                        () ->
                                            mapController
                                                .getGesturePane()
                                                .translateBy(
                                                    new Dimension2D(
                                                        finalErrorX[0] * kp, finalErrorY[0] * kp)));

                                    if ((mapController.getGesturePane().getWidth()
                                                    / mapController
                                                        .getGesturePane()
                                                        .getCurrentScaleX()
                                                - mapController.getGesturePane().getCurrentX())
                                            < mapController.getCurrentDrawingPane().getWidth()
                                        && mapController.getGesturePane().getCurrentX() * -1 > 0) {

                                      width2[0] += finalErrorX[0] * kp;
                                      if (width < 0) {
                                        Platform.runLater(
                                            () -> rect.setX(rect.getX() + finalErrorX[0] * kp));
                                      }
                                      Platform.runLater(() -> rect.setWidth(abs(width2[0])));
                                    }

                                    if ((mapController.getGesturePane().getHeight()
                                                    / mapController
                                                        .getGesturePane()
                                                        .getCurrentScaleY()
                                                - mapController.getGesturePane().getCurrentY())
                                            < mapController.getCurrentDrawingPane().getHeight()
                                        && mapController.getGesturePane().getCurrentY() * -1 > 0) {
                                      height2[0] += finalErrorY[0] * kp;
                                      if (height < 0) {
                                        Platform.runLater(
                                            () -> rect.setY(rect.getY() + finalErrorY[0] * kp));
                                      }
                                      Platform.runLater(() -> rect.setHeight(abs(height2[0])));
                                    }
                                  }
                                };

                            timer.scheduleAtFixedRate(task, 0, 5);
                          }
                        });

                mapController
                    .getCurrentDrawingPane()
                    .setOnMouseReleased(
                        e -> {
                          if (quickDrawActive) return;

                          if (!e.isConsumed()) {
                            if (!e.isShiftDown() && !(e.getButton() == MouseButton.SECONDARY)) {
                              selectedNodes.clear();
                            }

                            if (task != null) task.cancel();
                            timer.cancel();

                            for (Node node : mapController.getNodeToCircleMap().keySet()) {
                              if (rect.contains(new Point2D(node.getXCoord(), node.getYCoord()))) {
                                selectedNodes.add(node);
                              }
                            }
                            mapController.getCurrentDrawingPane().getChildren().remove(rect);
                            mapController.getGesturePane().setGestureEnabled(true);
                          }
                        });
              }
            });
  }
}
