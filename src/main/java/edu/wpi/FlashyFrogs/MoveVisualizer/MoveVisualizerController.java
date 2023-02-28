package edu.wpi.FlashyFrogs.MoveVisualizer;

import edu.wpi.FlashyFrogs.Accounts.LoginController;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.AStar;
import edu.wpi.FlashyFrogs.PathVisualizer.AbstractPathVisualizerController;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.io.File;
import java.io.FileInputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;
import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;

/** Controller for the announcement visualizer */
public class MoveVisualizerController extends AbstractPathVisualizerController
    implements IController {
  public Pane errtoast;
  public Rectangle errcheck2;
  public Rectangle errcheck1;
  @FXML private VBox directionsBox;
  @FXML private BorderPane borderPane;
  @FXML private Text adminMessage; // Admin message text
  @FXML private SearchableComboBox<LocationName> leftLocationBox; // Left location search box
  @FXML private SearchableComboBox<LocationName> rightLocationBox; // Right location search box
  @FXML private Text rightLocation; // Right arrow location, actual
  @FXML private Text leftLocation; // Left arrow location, actual
  @FXML private TextField headerText; // Text for the header, entry
  @FXML private TextField textText; // The text to show on the map
  @FXML private Text noLocationText; // No location error text
  @FXML private FilteredTableView<Move> moveTable; // Table for the moves
  @FXML private FilteredTableColumn<Move, Node> nodeColumn; // Node column
  @FXML private FilteredTableColumn<Move, LocationName> locationColumn; // Location column
  @FXML private FilteredTableColumn<Move, Date> dateColumn; // Date column
  @FXML private AnchorPane mapPane; // Map pane for map display
  private final Collection<javafx.scene.Node> nodes =
      new LinkedList<>(); // Collection of nodes that are on the map

  // Static place to keep the timer, so that it can be canceled when the admin enters the move
  // visualizer
  private static PauseTransition backToVisualizerTimer = null;

  /** Sets up the move visualizer, including all tables, and the map */
  @SneakyThrows
  @FXML
  protected void initialize() {
    // Cancel the timer if it exists
    if (backToVisualizerTimer != null) {
      // Stop the visualizer
      backToVisualizerTimer.stop();
      backToVisualizerTimer = null; // Cancel the timer
    }

    // Binds the admin message to the admin message entry
    adminMessage
        .textProperty()
        .bindBidirectional(
            headerText.textProperty(),
            new StringConverter<>() {
              @Override
              public String toString(String object) {
                // if the text is empty, use sample text
                if (object.isEmpty()) {
                  return "Header Text"; // Sample text
                } else {
                  return object; // Otherwise, return the object
                }
              }

              @Override
              public String fromString(String string) {
                return null; // This is not supported, so it does nothing
              }
            });

    // Bind the left location text to the location
    leftLocation
        .textProperty()
        .bindBidirectional(
            leftLocationBox.valueProperty(),
            new StringConverter<>() {
              // Converter based on the location
              @Override
              public String toString(LocationName object) {
                if (object != null) {
                  return object
                      .toString(); // If it's not null, return just the string of the location
                } else {
                  return "Left Location"; // Otherwise, default text
                }
              }

              @Override
              public LocationName fromString(String string) {
                return null; // This is not supported, so it does nothign
              }
            });

    // Bind the right location to the right text
    rightLocation
        .textProperty()
        .bindBidirectional(
            rightLocationBox.valueProperty(),
            new StringConverter<>() {
              // Converter based on the location
              @Override
              public String toString(LocationName object) {
                if (object != null) {
                  return object
                      .toString(); // If it's not null, return just the string of the location
                } else {
                  return "Right Location"; // Otherwise, default text
                }
              }

              @Override
              public LocationName fromString(String string) {
                return null; // This is not supported, so it does nothing
              }
            });

    // Query the location names
    ObservableList<LocationName> locationNames =
        FXCollections.observableList(
            mapController
                .getMapSession()
                .createQuery("FROM LocationName", LocationName.class)
                .getResultList());

    // Set the boxes to contain them
    leftLocationBox.setItems(locationNames);
    rightLocationBox.setItems(locationNames);

    // Give the columns their filters
    PopupFilter<Move, Node> nodeFilter = new PopupStringFilter<>(nodeColumn); // Node filter
    nodeColumn.setOnFilterAction((event) -> nodeFilter.showPopup()); // Node filter action
    PopupFilter<Move, LocationName> locationFilter =
        new PopupStringFilter<>(locationColumn); // Loc filter
    locationColumn.setOnFilterAction((event) -> locationFilter.showPopup()); // Loc filter action
    PopupFilter<Move, Date> dateFilter = new PopupStringFilter<>(dateColumn); // Date filter
    dateColumn.setOnFilterAction((event) -> dateFilter.showPopup()); // Date filter action

    // Set the columns to not be order-able anymore
    nodeColumn.setReorderable(false);
    locationColumn.setReorderable(false);
    dateColumn.setReorderable(false);

    mapPane.getChildren().add(0, map);

    // Anchor it to take up the whole map pane
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);

    // Set the value factories for the columns
    nodeColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getNode()));
    locationColumn.setCellValueFactory(
        row -> new SimpleObjectProperty<>(row.getValue().getLocation()));
    dateColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getMoveDate()));

    // Get the moves
    List<Move> moves =
        mapController.getMapSession().createQuery("FROM Move", Move.class).getResultList();
    moveTable.setItems(FXCollections.observableList(moves)); // set the items in the table

    pathFinder.setAlgorithm(new AStar()); // Select A* as what we will use

    // Add a listener to the selected table to regenerate the path
    moveTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              noLocationText.setVisible(false); // Reset the no location text

              unColorFloor();

              // If something is selected
              if (newValue != null) {
                // Generate the path. Start at the node the location was at 1ms before the move
                // End at the node in the move

                // Get the old node for the location, 1ms before
                Node oldLocation =
                    newValue
                        .getLocation()
                        .getCurrentNode(
                            mapController.getMapSession(),
                            Date.from(newValue.getMoveDate().toInstant().minusMillis(1)));

                // If the location isn't null
                if (oldLocation != null) {
                  // Get and draw the path
                  currentPath = pathFinder.findPath(oldLocation, newValue.getNode(), false);

                  // Set the floor
                  mapController.getMapFloorProperty().setValue(oldLocation.getFloor());

                  drawTable(new Date()); // Draw the table
                } else {
                  errortoastAnimation();
                  noLocationText.setVisible(true);
                }
              }

              colorFloor(); // Redraw
            });

    mapController
        .getMapFloorProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              clearNodes(null); // Clear the nodes on floor change
            });

    super.initialize(); // Call the supers initialize method

    // The box with the directions should pop-out when its hovered
    directionsBox
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              TranslateTransition transition =
                  new TranslateTransition(Duration.seconds(.5), directionsBox);
              if (newValue) {
                transition.setToY(-299);
              } else {
                transition.setToY(0);
              }

              transition.play();
            });
  }

  /** Help, shows the help menu for the visualizer */
  @Override
  public void help() {}

  /**
   * Adds text to the map
   *
   * @param actionEvent the event triggering this
   */
  @FXML
  private void addText(ActionEvent actionEvent) {
    // Don't do anything if the text is empty
    if (textText.getText().isEmpty()) {
      return;
    }

    Text text = new Text(textText.getText()); // Create the text
    text.setStyle("-fx-font-size: 250"); // Increase font size
    text.setTextOrigin(VPos.TOP); // Make sure that the text origin is the top left for coordinates

    // Cap the width at half the screen, however less is okay
    text.setWrappingWidth(Math.min(mapController.getMapWidth() / 4, text.getWrappingWidth()));

    handleAddNode(text); // Add the text
  }

  /**
   * Adds an image to the map
   *
   * @param actionEvent the event triggering this
   */
  @SneakyThrows
  @FXML
  private void addImage(ActionEvent actionEvent) {
    // Create the image file chooser
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Picture File"); // Set the text

    // Give it the extensions it will use
    fileChooser
        .getExtensionFilters()
        .addAll(new FileChooser.ExtensionFilter("Picture", "*.jpg", "*.png", "*.gif", "*.jpeg"));

    // Open the file chooser, get the file
    File file = fileChooser.showOpenDialog(textText.getScene().getWindow());

    // If the file is null, don't do anything
    if (file == null) {
      return; // Exit
    }

    // Create the image
    ImageView image = new ImageView(new Image(new FileInputStream(file)));

    handleAddNode(image); // Add the image
  }

  /**
   * Handles adding a node to the UI, setting actions and stuff for that node
   *
   * @param node the node
   */
  private void handleAddNode(@NonNull javafx.scene.Node node) {
    AnchorPane root = new AnchorPane(); // Anchor pane for the root

    root.getChildren().add(node); // Add the root to the pane

    // Add teh anchors
    AnchorPane.setTopAnchor(root, 0.0);
    AnchorPane.setBottomAnchor(root, 0.0);
    AnchorPane.setRightAnchor(root, 0.0);
    AnchorPane.setLeftAnchor(root, 0.0);

    // Resize circle
    Circle resizeCircle = new Circle(50.0);
    root.getChildren().add(resizeCircle);

    AnchorPane.setBottomAnchor(resizeCircle, -15.0);
    AnchorPane.setRightAnchor(resizeCircle, -15.0);

    mapController.getCurrentDrawingPane().getChildren().add(root);

    // Get the center of the thing
    Point2D targetPoint = mapController.getGesturePane().targetPointAtViewportCentre();

    // Set the coords
    root.setLayoutX(targetPoint.getX());
    root.setLayoutY(targetPoint.getY());

    nodes.add(root); // Add the node to the collection to be able to clear

    // Starting positions for the mouse and node
    double[] originalPosition = new double[2];
    double[] mousePosition = new double[2];
    boolean[] dragEnabled = new boolean[1]; // Whether the drag is enabled

    // Function that validates and executes new X layout and scale
    BiConsumer<Double, Double> updateX =
        (layoutX, scale) -> {
          double adjustmentFactor = ((scale * root.getWidth() - root.getWidth()) / 2);
          double left = layoutX - adjustmentFactor;
          double width = scale * root.getWidth();

          // Because scale can be negative, we need to compute the actual bounds
          // based on the max left and right
          double actualLeft = Math.min(left, left + width);
          double actualRight = Math.max(left, left + width);

          // Compute the old adjustment factor, in case we need to "go back"
          double oldAdjustmentFactor = (root.getScaleX() * root.getWidth() - root.getWidth()) / 2;
          double oldWidth = root.getScaleX() * root.getWidth();

          // It's valid if it's positive but not outside
          if (width <= mapController.getMapWidth()) {
            if (actualLeft >= 0 && actualRight <= mapController.getMapWidth()) {
              // Set the scale and layout
              root.setLayoutX(layoutX);
              root.setScaleX(scale);
            } else if (actualLeft < 0) {
              // If it's out in X on the left, go to relative 0
              if (root.getScaleX() >= 0) {
                root.setLayoutX(oldAdjustmentFactor);
              } else {
                // handle the case of the scale being negative, we need to account for the
                // adjustment
                // putting the top left on the wrong side
                root.setLayoutX(oldAdjustmentFactor - oldWidth);
              }
            } else {
              // On the other side, go to the right
              if (root.getScaleX() >= 0) {
                root.setLayoutX(mapController.getMapWidth() - oldWidth + oldAdjustmentFactor);
              } else {
                // handle the case of the scale being negative, we need to account for the
                // adjustment
                // putting the top left on the wrong side
                root.setLayoutX(mapController.getMapWidth() + oldAdjustmentFactor);
              }
            }
          }
        };

    // Function that validates and executes new Y layout and scale
    BiConsumer<Double, Double> updateY =
        (layoutY, scale) -> {
          double adjustmentFactor = ((scale * root.getHeight() - root.getHeight()) / 2);
          double top = layoutY - adjustmentFactor;
          double height = scale * root.getHeight();

          // Because scale can be negative, we need to compute the actual bounds
          // based on the max top and bottom
          double actualTop = Math.min(top, top + height);
          double actualBottom = Math.max(top, top + height);

          // Compute the old adjustment factor, in case we need to "go back"
          double oldAdjustmentFactor = (root.getScaleY() * root.getHeight() - root.getHeight()) / 2;
          double oldHeight = root.getScaleY() * root.getHeight();

          // It's valid if it's positive but not outside (and the height isn't off)
          if (height <= mapController.getMapHeight()) {
            if (actualTop >= 0 && actualBottom <= mapController.getMapHeight()) {
              // Set the scale and layout
              root.setLayoutY(layoutY);
              root.setScaleY(scale);
            } else if (actualTop < 0) {
              // If it's out in Y on the top, go to 0 (scaled)
              if (root.getScaleY() >= 0) {
                root.setLayoutY(oldAdjustmentFactor);
              } else {
                // handle the case of scale being negative, where the adjustment factor
                // puts the layout on the wrong side
                root.setLayoutY(oldAdjustmentFactor - oldHeight);
              }
            } else {
              // On the other side, go to the right
              if (root.getScaleY() >= 0) {
                root.setLayoutY(mapController.getMapHeight() - oldHeight + oldAdjustmentFactor);
              } else {
                //
                root.setLayoutY(mapController.getMapHeight() + oldAdjustmentFactor);
              }
            }
          }
        };

    node.setOnDragDetected(
        (event) -> {
          // Avoid intercepting from the circle
          if (event.isConsumed()) {
            return;
          }

          // Save starting info
          originalPosition[0] = root.getLayoutX();
          originalPosition[1] = root.getLayoutY();
          mousePosition[0] = event.getScreenX();
          mousePosition[1] = event.getScreenY();

          dragEnabled[0] = true; // Enable
        });

    // On drag
    node.setOnMouseDragged(
        (event) -> {
          if (event.isConsumed() || !dragEnabled[0]) {
            return;
          }

          // Disable the gesture pane (this fixes some weirdness)
          this.mapController.getGesturePane().setGestureEnabled(false);

          // Divide by scale, so that the mouse distance is actually reflected
          // in the (potentially zoomed in/out) page
          // New x and y coords, original + the mouse delta. Since this is layout based, this is
          // separate from the top
          // -left based calculate done below!
          double newX =
              originalPosition[0]
                  + (event.getScreenX() - mousePosition[0])
                      / mapController.getGesturePane().getCurrentScale();
          double newY =
              originalPosition[1]
                  + (event.getScreenY() - mousePosition[1])
                      / mapController.getGesturePane().getCurrentScale();

          // Update the coordinates if they are in bounds
          updateX.accept(newX, root.getScaleX());
          updateY.accept(newY, root.getScaleY());
        });

    // On drag stop (this represents that?)
    node.setOnMouseReleased(
        (event) -> {
          if (event.isConsumed()) {
            return;
          }

          dragEnabled[0] = false; // Disable dragging

          // Re-enable gestures
          mapController.getGesturePane().setGestureEnabled(true);
        });

    // Enable the context menu to delete the nodes
    root.setOnContextMenuRequested(
        (event) -> {
          Button deleteButton = new Button("Delete"); // Delete button
          deleteButton.getStyleClass().addAll("redOutlineNoSetSize");
          deleteButton.setDefaultButton(true); // Default is this, so that space deletes

          PopOver popOver = new PopOver(deleteButton); // Create the pop-over to use

          deleteButton.setOnAction(
              (deleteEvent) -> {
                mapController.getCurrentDrawingPane().getChildren().remove(root); // Delete the node
                nodes.remove(root); // Delete the node from the collection
                popOver.hide(); // hide the pop over
              });

          popOver.show(root);
        });

    // Drag start coordinates
    double[] dragStart = new double[2];
    double[] startScale = new double[2]; // Starting scales
    double[] startPosition = new double[2]; // Starting position
    boolean[] dragInProgress =
        new boolean[1]; // Whether a drag is in progress, don't allow resize until progress

    // On drag detected
    resizeCircle.setOnDragDetected(
        (event) -> {
          event.consume(); // Consume the event
          mapController.getGesturePane().setGestureEnabled(false); // Disable gestures

          // Save starting coords
          dragStart[0] = event.getScreenX();
          dragStart[1] = event.getScreenY();
          startScale[0] = root.getScaleX();
          startScale[1] = root.getScaleY();
          dragInProgress[0] = true; // Enable drag
          startPosition[0] =
              root.getLayoutX()
                  - ((root.getScaleX() * root.getWidth() - root.getWidth())
                      / 2); // Top-left x, for top left updates. Accounts for any current scaling
          startPosition[1] =
              root.getLayoutY()
                  - ((root.getScaleY() * root.getHeight() - root.getHeight())
                      / 2); // Top-left y, for top left updates. Accounts for any current scaling
        });

    // When a drag is in progress
    resizeCircle.setOnMouseDragged(
        (event) -> {
          if (dragInProgress[0]) {
            event.consume(); // Consume the event

            // Calculate the scales. This math is determined by solving the equalities for scale
            // newHeight = currentHeight + dragDistance
            // newHeight = absHeight * scale.
            // currentHeight = absHeight * currentScale
            // This whole thing is necessary because JavaFX doesn't recalculate bounds on scale
            double newScaleX =
                startScale[0]
                    + (((event.getScreenX() - dragStart[0])
                            / mapController.getGesturePane().getCurrentScale())
                        / root.getWidth());
            double newScaleY =
                startScale[1]
                    + (((event.getScreenY() - dragStart[1])
                            / mapController.getGesturePane().getCurrentScale())
                        / root.getHeight());

            // Calculate the shift, essentially the difference in dimension generated by the scaling
            double shiftX = ((newScaleX - 1) * root.getWidth()) / 2;
            double shiftY = ((newScaleY - 1) * root.getHeight()) / 2;

            // Update the position and scale if they are good
            updateX.accept(startPosition[0] + shiftX, newScaleX);
            updateY.accept(startPosition[1] + shiftY, newScaleY);
          }
        });

    // On drag over
    resizeCircle.setOnMouseReleased(
        (event) -> {
          mapController.getGesturePane().setGestureEnabled(true); // Re-enable gestures
          dragInProgress[0] = false; // Reset drag to no longer be in progress
        });
  }

  /**
   * Callback that clears the nodes from the map
   *
   * @param actionEvent the event triggering this
   */
  @FXML
  private void clearNodes(ActionEvent actionEvent) {
    // For each node
    for (javafx.scene.Node node : nodes) {
      mapController.getCurrentDrawingPane().getChildren().remove(node); // Remove it
    }
  }

  /**
   * Callback for the enter kiosk button
   *
   * @param actionEvent the event triggering this
   */
  @FXML
  private void enterKioskMode(ActionEvent actionEvent) {
    // First, make the border pain (messages + map) fullscreen by anchoring it to the root
    AnchorPane.setTopAnchor(borderPane, 0.0);
    AnchorPane.setLeftAnchor(borderPane, 0.0);
    AnchorPane.setBottomAnchor(borderPane, 0.0);
    AnchorPane.setRightAnchor(borderPane, 0.0);

    // Toggle the map controls (no floor/SR interaction)
    mapController.toggleMapControls();

    // For each node on the map, make it mouse transparent so that it's not editable
    // (viewers shouldn't be able to move/delete this stuff)
    for (javafx.scene.Node node : nodes) {
      node.setMouseTransparent(true);
    }

    Fapp.logOutWithoutSceneChange(); // Log the user out without going to the login screen

    // Set the key handler for the border pane, if the user presses escape
    borderPane
        .getScene()
        .addEventFilter(
            KeyEvent.KEY_PRESSED,
            (keyEvent) -> {
              Fapp.setLastKeyPressTime(Instant.now());

              // We only care about escape
              if (keyEvent.getCode().equals(KeyCode.ESCAPE)) {
                // Go to the login page
                Fapp.setScene("Accounts", "Login");
              }
            });

    // Use a pause transition so that it only does the thing on the home screen
    backToVisualizerTimer = new PauseTransition(Duration.seconds(1));

    // Set the transition to close on completion
    backToVisualizerTimer.setOnFinished(
        (event) -> {
          // If they are on the login screen and the last press was more than 10 seconds
          // ago
          if (Fapp.getIController().getClass() == LoginController.class
              && Fapp.getLastKeyPressTime().plus(10, ChronoUnit.SECONDS).isBefore(Instant.now())) {
            Fapp.setRoot(borderPane); // Set the root to be this
          }

          // Redo the transitions
          backToVisualizerTimer.play();
        });

    backToVisualizerTimer.play(); // Start the timer
  }

  public void errortoastAnimation() {
    errtoast.getTransforms().clear();
    errtoast.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translate1.setByX(-320);
    translate1.setAutoReverse(true);
    errcheck1.setFill(Color.web("#012D5A"));
    errcheck2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck1, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck2, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(320);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
  }
}
