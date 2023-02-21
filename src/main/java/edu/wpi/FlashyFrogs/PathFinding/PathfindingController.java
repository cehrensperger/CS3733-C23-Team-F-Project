package edu.wpi.FlashyFrogs.PathFinding;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import edu.wpi.FlashyFrogs.controllers.NextFloorPopupController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

@GeneratedExclusion
public class PathfindingController implements IController {

  @FXML private SearchableComboBox<LocationName> startingBox;
  @FXML private SearchableComboBox<LocationName> destinationBox;
  @FXML private SearchableComboBox<String> algorithmBox;
  @FXML private CheckBox accessibleBox;
  @FXML private AnchorPane mapPane;
  @FXML private MFXButton mapEditorButton;
  @FXML private DatePicker moveDatePicker;
  @FXML private TableView<Instruction> pathTable;
  @FXML private TableColumn<Instruction, String> pathCol;
  private List<Node> lastPath; // The most recently generated path

  //  @FXML private Label error;

  private MapController mapController;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;

  boolean hDone = false;

  /**
   * Initializes the path finder, sets up the floor selector, and the map including default behavior
   */
  @SneakyThrows
  public void initialize() {
    moveDatePicker.setValue(LocalDate.now());
    moveDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setDate(
                  Date.from(newValue.atStartOfDay(ZoneId.of("America/Montreal")).toInstant()));
              mapController.redraw();
            });
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    pathTable.setVisible(false);
    // set resizing behavior
    Fapp.getPrimaryStage().widthProperty().addListener((observable, oldValue, newValue) -> {});

    // load map page
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(Fapp.class.getResource("Map/Map.fxml")));

    javafx.scene.Node map = mapLoader.load(); // Load the map
    mapPane.getChildren().add(0, map); // Put the map loader into the editor box
    mapController = mapLoader.getController();

    // By default hide circles
    mapController.setNodeCreation(
        (node, circle) -> {
          circle.setOpacity(0); // hide the circle
        });

    mapController.setEdgeCreation(
        (edge, line) -> {
          line.setOpacity(0); // Hide the line
        });

    // make the anchor pane resizable
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    // don't create a new session since the map is already using one
    Session session = mapController.getMapSession();

    // get the list of all location names from the database
    List<LocationName> objects =
        session.createQuery("SELECT location FROM Move", LocationName.class).getResultList();

    // sort the locations alphabetically, algorithms already alphabetical
    objects.sort(Comparator.comparing(LocationName::getLongName));

    // make the list of algorithms
    List<String> algorithms = new LinkedList<>();
    algorithms.add("A*");
    algorithms.add("Breadth-first");
    algorithms.add("Depth-first");

    // Populate the boxes
    startingBox.setItems(FXCollections.observableList(objects));
    destinationBox.setItems(FXCollections.observableList(objects));
    algorithmBox.setItems(FXCollections.observableList(algorithms));

    algorithmBox.setValue("A*");

    // On floor change
    mapController
        .getMapFloorProperty()
        .addListener(
            (observable) -> {
              // If the path exists
              if (lastPath != null) {
                try {
                  drawPath(); // Draw it
                } catch (IOException e) {
                  // Re-throw any exceptions (thanks Java!)
                  throw new RuntimeException(e);
                }
              }
            });

    // Get whether the user is an admin
    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    // Decide what to do with the admin button based on that
    if (!isAdmin) {
      mapEditorButton.setDisable(true);
      mapEditorButton.setOpacity(0);
    } else {
      mapEditorButton.setDisable(false);
      mapEditorButton.setOpacity(1);
    }

    pathCol.setCellValueFactory(new PropertyValueFactory<>("instruction"));
  }

  /** Callback to handle the back button being pressed */
  @SneakyThrows
  @FXML
  private void handleBack() {
    Fapp.handleBack(); // Delegate to Fapp
  }

  /**
   * Hides the last drawn path on the map, in preparation for a new path being drawn. Handles case
   * where no path is drawn
   */
  private void hideLastPath() {
    pathTable.setVisible(false);
    // Check to make sure there is a path
    if (lastPath != null) {
      // Get the start node
      Node startNode = lastPath.get(0);

      // If the start node is on this floor
      if (startNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
        // Get the circle
        Circle circle = mapController.getNodeToCircleMap().get(startNode);

        circle.setOpacity(0); // Hide the circle
      }

      // Get the end node in the path
      Node endNode = lastPath.get(lastPath.size() - 1);

      // If the end node is on this floor
      if (endNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
        // Get the circle
        Circle circle = mapController.getNodeToCircleMap().get(endNode);

        circle.setOpacity(0); // Hide the circle
      }

      // Hide all the edges
      for (int i = 1; i < lastPath.size(); i++) { // For each edge
        // Get the two nodes in the edge
        Node thisNode = lastPath.get(i);

        // If we're on the right floor
        if (thisNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
          // Hide
          mapController.getNodeToCircleMap().get(thisNode).setOpacity(0);
        }

        Node previousNode = lastPath.get(i - 1);

        // If both nodes are on this floor
        if (thisNode.getFloor().equals(mapController.getMapFloorProperty().getValue())
            && previousNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
          Edge edge; // The edge to hide

          // Get the edge, try the first directino
          edge = mapController.getMapSession().find(Edge.class, new Edge(thisNode, previousNode));

          // That failing
          if (edge == null) {
            // Try the other
            edge = mapController.getMapSession().find(Edge.class, new Edge(previousNode, thisNode));
          }

          // Now get the line
          Line line = mapController.getEdgeToLineMap().get(edge);
          line.setOpacity(0); // hide the line
        }
      }
    }
  }

  /** Method that generates table for textual path instructions */
  private void drawTable() {
    pathTable.setVisible(true);

    ObservableList<Instruction> instructions = FXCollections.observableArrayList();

    double curAngle = 0;

    pathTable.setItems(instructions);
    for (int i = 1; i < lastPath.size() - 1; i++) { // For each line in the path
      Node thisNode = lastPath.get(i);
      Node nextNode = lastPath.get(i + 1);

      double target =
          Math.atan2(
              (nextNode.getYCoord() - thisNode.getYCoord()),
              (nextNode.getXCoord() - thisNode.getXCoord()));
      double errorTheta = target - curAngle;
      curAngle = target;

      int errorDeg = (int) Math.toDegrees(errorTheta);

      String nodeName =
          thisNode
              .getCurrentLocation(
                  Date.from(
                      moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
              .stream()
              .findFirst()
              .orElseThrow()
              .getShortName();

      instructions.add(new Instruction("Turn " + errorDeg + " degrees at " + nodeName));
    }
  }

  /** Method that draws a path on the map based on the last gotten path. Assumes that path exists */
  private void drawPath() throws IOException {
    // Color any edges on the map
    Node prevNode = lastPath.get(0);
    for (int i = 1; i < lastPath.size(); i++) { // For each line in the path
      Node thisNode = lastPath.get(i); // Get the node

      String nextFloor = thisNode.getFloor().floorNum;

      if (!nextFloor.equals(prevNode.getFloor().floorNum)) {
        FXMLLoader loader =
            new FXMLLoader(Fapp.class.getResource("Pathfinding/NextFloorPopup.fxml"));
        PopOver goToNext = new PopOver(loader.load());

        NextFloorPopupController controller = loader.getController();
        controller.setPathfindingController(this);
        controller.setFloor(thisNode.getFloor());

        Circle circle = mapController.getNodeToCircleMap().get(prevNode);
        if (circle != null) {
          circle.setFill(Paint.valueOf(Color.YELLOW.toString()));
          circle.setOpacity(1);

          goToNext.show(circle);
          goToNext.setAutoHide(false);
          goToNext.setAutoFix(false);
          goToNext.detach();
          goToNext.setX(250);
          goToNext.setY(20);
          goToNext.setTitle("   Your path goes to Floor " + nextFloor + ".");
        }
      }
      prevNode = thisNode;

      // If the node is on this floor
      if (thisNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {

        // Try to draw its edge. Check that what it's connected to is on this floor
        if (lastPath.get(i - 1).getFloor().equals(mapController.getMapFloorProperty().getValue())) {
          // find the edge related to each pair of nodes
          Edge edge =
              mapController
                  .getMapSession()
                  .find(Edge.class, new Edge(lastPath.get(i - 1), thisNode));

          // if it couldn't find the edge, reverse the direction and look again
          if (edge == null) {
            edge =
                mapController
                    .getMapSession()
                    .find(Edge.class, new Edge(thisNode, lastPath.get(i - 1)));
          }

          // get the line on the map associated with the edge
          Line line = mapController.getEdgeToLineMap().get(edge);

          // Set its formatting
          line.setOpacity(1);
          line.setStroke(Paint.valueOf(Color.BLUE.toString()));
          line.setStrokeWidth(5);
        }
      }
    }

    // Get the first node, to draw it
    Node firstNode = lastPath.get(0);
    if (firstNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
      Circle circle = mapController.getNodeToCircleMap().get(firstNode);

      circle.setFill(Paint.valueOf(Color.BLUE.toString()));
      circle.setOpacity(1);
    }

    // Get the ending node, to draw it
    Node lastNode = lastPath.get(lastPath.size() - 1);
    if (lastNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
      Circle circle = mapController.getNodeToCircleMap().get(lastNode);
      circle.setFill(Paint.valueOf(Color.GREEN.toString()));
      circle.setOpacity(1);
    }
  }

  /** Method that handles drawing a new path (AKA the submit button handler) */
  @SneakyThrows
  public void handleGetPath() {
    // get start and end locations from text fields
    LocationName startPath = startingBox.valueProperty().get();
    LocationName endPath = destinationBox.valueProperty().get();
    Boolean accessible = accessibleBox.isSelected();

    PathFinder pathFinder = new PathFinder(mapController.getMapSession());

    // get algorithm to use in pathfinding from algorithmBox
    if (algorithmBox.getValue() != null) {
      switch (algorithmBox.getValue()) {
        case "Breadth-first" -> pathFinder.setAlgorithm(new BreadthFirst());
        case "Depth-first" -> pathFinder.setAlgorithm(new DepthFirst());
        default -> pathFinder.setAlgorithm(new AStar());
      }
    }

    hideLastPath(); // hide the last drawn path

    // Get the new path from the PathFinder
    Node startNode =
        startPath.getCurrentNode(
            Date.from(moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    Node endNode =
        endPath.getCurrentNode(
            Date.from(moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    lastPath = pathFinder.findPath(startNode, endNode, accessible);

    // Check that we actually got a path
    if (lastPath == null) {
      // if nodes is null, that means the there was no possible path
      //      error.setTextFill(Paint.valueOf(Color.RED.toString()));
      //      error.setText("No path found");
      System.out.println("no path found");
    } else {
      setFloor(startNode.getFloor());
      drawTable();
      drawPath(); // Draw the path
    }
  }

  /** Callback to open the map editor from a button */
  @FXML
  public void openMapEditor() {
    Fapp.setScene("MapEditor", "MapEditorView");
  }

  /**
   * Callback to handle the help button being pressed
   *
   * @param event the event triggering this
   */
  @FXML
  @SneakyThrows
  public void handleQ(ActionEvent event) {
    // load the help page
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    // load a pop-over object with the help page in it
    PopOver popOver = new PopOver(newLoad.load());

    // get the controller of the help page
    HelpController help = newLoad.getController();
    // show the correct text for the path finding page specifically
    help.handleQPathFinding();

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow());
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
      h5.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      hDone = false;
    }
  }

  /**
   * Sets the floor for the map
   *
   * @param floor the new floort
   */
  public void setFloor(@NonNull Node.Floor floor) {
    mapController.getMapFloorProperty().setValue(floor);
  }

  public static class Instruction {
    @Getter @Setter private String instruction;

    Instruction(String instruction) {
      this.instruction = instruction;
    }
  }
}
