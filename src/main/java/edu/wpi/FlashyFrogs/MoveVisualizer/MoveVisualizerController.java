package edu.wpi.FlashyFrogs.MoveVisualizer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.AStar;
import edu.wpi.FlashyFrogs.PathVisualizer.AbstractPathVisualizerController;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

/** Controller for the announcement visualizer */
public class MoveVisualizerController extends AbstractPathVisualizerController
    implements IController {
  @FXML private TextField textText; // The text to show on the map
  @FXML private Text noLocationText;
  @FXML private TableView2<Move> moveTable; // Table for the moves
  @FXML private TableColumn2<Move, Node> nodeColumn; // Node column
  @FXML private TableColumn2<Move, LocationName> locationColumn; // Location column
  @FXML private TableColumn2<Move, Date> dateColumn; // Date column
  @FXML private AnchorPane mapPane; // Map pane for map display
  private final Collection<javafx.scene.Node> nodes =
      new LinkedList<>(); // Collection of nodes that are on the map

  /** Sets up the move visualizer, including all tables, and the map */
  @SneakyThrows
  @FXML
  private void initialize() {
    // Set the columns to not be order-able anymore
    nodeColumn.setReorderable(false);
    locationColumn.setReorderable(false);
    dateColumn.setReorderable(false);

    mapPane.getChildren().add(map);

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
                } else {
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
  }

  /** Help, shows the help menu for the visualizer */
  @Override
  public void help() {}

  /**
   * Handler for the back button, delegates to Fapp
   *
   * @param actionEvent the event triggering this
   */
  public void handleBackButton(ActionEvent actionEvent) {
    Fapp.handleBack();
  }

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
    text.setStyle("-fx-font-size: 500"); // Increase font size
    text.setTextOrigin(VPos.TOP); // Make sure that the text origin is the top left for coordinates

    // Cap the width at half the screen, however less is okay
    text.setWrappingWidth(Math.min(mapController.getMapWidth() / 2, text.getWrappingWidth()));

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
          if (newX >= 0 && newX + root.getWidth() <= mapController.getMapWidth()) {
            root.setLayoutX(newX);
          } else if (newX < 0) {
            root.setLayoutX(0); // If it's out of bounds in the X, left = 0
          } else {
            root.setLayoutX(
                mapController.getMapWidth() - root.getWidth()); // Otherwise, max that won't go out
          }

          // Do the same
          if (newY >= 0 && newY + root.getHeight() <= mapController.getMapHeight()) {
            root.setLayoutY(newY);
          } else if (newY < 0) {
            root.setLayoutY(0); // If it's out of bounds Y, top = 0
          } else {
            root.setLayoutY(
                mapController.getMapHeight()
                    - root.getHeight()); // Otherwise, Otherwise, max that won't go out
          }
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
        });

    // When a drag is in progress
    resizeCircle.setOnMouseDragged(
        (event) -> {
          if (dragInProgress[0]) {
            event.consume(); // Consume the event

            root.setScaleX((((event.getScreenX() - dragStart[0]) / dragStart[0])) + startScale[0]);
            root.setScaleY((((event.getScreenY() - dragStart[1]) / dragStart[1])) + startScale[1]);
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
}
