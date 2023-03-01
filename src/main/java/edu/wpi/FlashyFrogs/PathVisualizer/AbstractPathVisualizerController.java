package edu.wpi.FlashyFrogs.PathVisualizer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.PathFinder;
import edu.wpi.FlashyFrogs.PathFinding.PathfindingController;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.MathUtils;
import org.controlsfx.control.PopOver;

/**
 * Abstract base class that anything that visualizes a path should implement. Creates the map, and
 * the map controller, and enables classes that extend this to easily visualize paths
 */
public abstract class AbstractPathVisualizerController implements IController {
  protected int selectedIndex = -1;
  @FXML protected TableView<PathfindingController.Instruction> pathTable;
  @FXML protected TableColumn<PathfindingController.Instruction, String> pathCol;

  @FXML protected Button back;
  @FXML protected Button next;

  @NonNull protected final MapController mapController; // Map controller

  @NonNull protected final Pane map; // Pane representing the map
  protected List<Node> currentPath; // Node list, may be null
  @NonNull protected final PathFinder pathFinder; // The path finder to use
  // List of pop-overs that are the floor changers, so they can be cleared
  protected Collection<PopOver> changeFloorPopOvers = new LinkedList<>();

  /** Constructor for an abstract path visualizer, sets up the MapController and Map */
  @SneakyThrows
  public AbstractPathVisualizerController() {
    // Create the map loader
    FXMLLoader mapLoader = new FXMLLoader(Fapp.class.getResource("Map/Map.fxml"));

    map = mapLoader.load(); // Load the map

    mapController = mapLoader.getController(); // Get the controller

    pathFinder = new PathFinder(mapController.getMapSession()); // Create the path finder

    // On floor change
    mapController
        .getMapFloorProperty()
        .addListener(
            (observable1, oldValue1, newValue1) -> {
              changeFloorPopOvers.forEach(PopOver::hide); // Hide all the pop-overs
              changeFloorPopOvers.clear(); // Clear the pop-overs
              colorFloor(); // Color the floor
            });

    // By default, set nodes to not be visible
    mapController.setNodeCreation((node, circle) -> circle.setVisible(false));

    // By default, set edges to not be visible
    mapController.setEdgeCreation((edge, line) -> line.setVisible(false));

    // By default, hide hallways
    mapController.setLocationCreation(
        ((node, locationName, text) -> {
          if (locationName.getLocationType().equals(LocationName.LocationType.HALL)) {
            text.setVisible(false); // Set hallways to not be visible
          }
        }));
  }

  @FXML
  protected void initialize() {
    pathTable.setVisible(false);
    next.setVisible(false);
    back.setVisible(false);

    // Set up the next button to select the next row
    next.setOnAction(event -> pathTable.getSelectionModel().selectBelowCell());

    // Set up the back button to select the previous row
    back.setOnAction(event -> pathTable.getSelectionModel().selectAboveCell());

    pathCol.setCellValueFactory(new PropertyValueFactory<>("instruction"));

    // On selection change, zoom to the right property
    pathTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.getMapFloorProperty().setValue(newValue.node.getFloor());
              mapController.zoomToCoordinates(
                  2, newValue.node.getXCoord(), newValue.node.getYCoord());
            });
  }

  /**
   * Handles coloring the screen on a given floor
   *
   * @param visible the visibility
   * @param startColor the start floor color
   * @param endColor the end floor color
   * @param lineColor the line color
   */
  @SneakyThrows
  protected void handleFloorColoring(
      boolean visible, Color startColor, Color endColor, Color lineColor) {
    // Check to make sure the path exists
    if (currentPath != null) {
      Set<Node.Floor> allowedFloors = new HashSet<>(); // Set of allowed floors we will use

      Node startNode = currentPath.get(0); // Get the start node

      // If the nodes floor is correct
      if (startNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
        // Circle for the node
        Circle nodeCircle = mapController.getNodeToCircleMap().get(startNode);
        nodeCircle.setVisible(visible); // Set the node to be visible
        nodeCircle.setFill(startColor); // Set its color
      }

      Node endNode = currentPath.get(currentPath.size() - 1); // Get the end node

      // If the nodes floor is correct
      if (endNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
        // Circle for the node
        Circle nodeCircle = mapController.getNodeToCircleMap().get(endNode);
        nodeCircle.setVisible(visible); // Set the node to be visible
        nodeCircle.setFill(endColor); // Set its color
      }

      // Add the start floor to the allowed floors
      allowedFloors.add(currentPath.get(0).getFloor());

      // For each node in the path
      for (int i = 1; i < currentPath.size(); i++) {
        // Get the two nodes
        Node thisNode = currentPath.get(i);
        Node lastNode = currentPath.get(i - 1);

        allowedFloors.add(thisNode.getFloor()); // Add this floor to the list of allowed floors

        if (!thisNode.getFloor().equals(lastNode.getFloor())
            && lastNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {

          Circle circle = mapController.getNodeToCircleMap().get(lastNode);
          circle.setFill(Color.YELLOW);
          circle.setVisible(true);
        } else if (!thisNode.getFloor().equals(lastNode.getFloor())
            && thisNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
          Circle circle = mapController.getNodeToCircleMap().get(thisNode);
          circle.setFill(Color.YELLOW);
          circle.setVisible(true);
        }

        // If both nodes are on this floor
        if (thisNode.getFloor().equals(mapController.getMapFloorProperty().getValue())
            && lastNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
          Edge edge; // The edge we will get

          edge = new Edge(thisNode, lastNode); // Try the first direction

          // If that edge is invalid
          if (!mapController.getEdgeToLineMap().containsKey(edge)) {
            edge = new Edge(lastNode, thisNode); // Try the other direction
          }

          // The line to color
          Line lineToColor = mapController.getEdgeToLineMap().get(edge);

          // Set the line color
          lineToColor.setFill(lineColor);
          lineToColor.setStroke(lineColor);
          lineToColor.setStrokeWidth(5);
          lineToColor.setVisible(visible);
        }
      }

      // Set the allowed floors
      mapController.setAllowedFloors(allowedFloors);
    } else {
      mapController.setAllowedFloors(null); // Clear the allowed floors
    }
  }

  /**
   * Colors the edges on the floor that we are on based on the currently drawn path. Handles cases
   * where the current path doesn't exist
   */
  protected void colorFloor() {
    // Handle floor coloring
    handleFloorColoring(true, Color.BLUE, Color.GREEN, Color.BLUE);
  }

  /**
   * Un-colors the edges on the floor that we are on based on the currently drawn path. Handles
   * cases where the selected path doesn't exist
   */
  protected void unColorFloor() {
    changeFloorPopOvers.forEach(PopOver::hide); // hide each pop over
    changeFloorPopOvers.clear(); // Clear the pop overs

    // Handle floor un-coloring
    handleFloorColoring(false, Color.BLACK, Color.BLACK, Color.BLACK);
  }

  /** On close, in this case closes the map */
  @Override
  public void onClose() {
    mapController.exit();
  }

  /** Method that generates table for textual path instructions */
  protected void drawTable(@NonNull Date date) {
    int continueCounter = 0;
    pathTable.setVisible(true);
    next.setVisible(true);
    back.setVisible(true);

    ObservableList<Instruction> instructions = FXCollections.observableArrayList();
    double curAngle = 0;

    pathTable.setItems(instructions);
    for (int i = 0; i < currentPath.size() - 1; i++) { // For each line in the path

      Node thisNode = currentPath.get(i);
      Node nextNode = currentPath.get(i + 1);

      double target =
          Math.atan2(
              (nextNode.getYCoord() - thisNode.getYCoord()),
              (nextNode.getXCoord() - thisNode.getXCoord()));
      double errorTheta = target - curAngle;
      curAngle = target;

      errorTheta = MathUtils.normalizeAngle(errorTheta, 0.0);

      int errorDeg = (int) Math.toDegrees(errorTheta);

      String nodeName =
          thisNode.getCurrentLocation(mapController.getMapSession(), date).stream()
              .findFirst()
              .orElse(new LocationName("", LocationName.LocationType.HALL, ""))
              .getShortName();

      if (i == 0) {
        String newFloor = "Starting at floor " + currentPath.get(i).getFloor() + ":";
        instructions.add(new Instruction(newFloor, thisNode));
      } else if (currentPath.get(i).getFloor() != currentPath.get(i - 1).getFloor()) {
        String newFloor = "Going to floor " + currentPath.get(i).getFloor() + ":";
        instructions.add(new Instruction(newFloor, thisNode));
      }

      if (nodeName.equals("")) {
        if (errorDeg < -70) {
          instructions.add(new Instruction("\t\u2190 Turn left", thisNode));
          continueCounter = 0;
        } else if ((errorDeg > -70) && (errorDeg < -45)) {
          instructions.add(new Instruction("\t\u2196 Take a slight left", thisNode));
          continueCounter = 0;
        } else if (errorDeg > 70) {
          instructions.add(new Instruction("\t\u2192 Turn right", thisNode));
          continueCounter = 0;
        } else if ((errorDeg > 45) && (errorDeg < 70)) {
          instructions.add(new Instruction("\t\u2197 Take a slight right", thisNode));
          continueCounter = 0;
        } else {
          if (continueCounter == 0) {
            instructions.add(new Instruction("\t\u2191 Continue", thisNode));
            continueCounter = continueCounter + 1;
          }
        }
      } else {
        if (errorDeg < -70) {
          instructions.add(new Instruction("\t\u2190 Turn left at " + nodeName, thisNode));
          continueCounter = 0;
        } else if ((errorDeg > -70) && (errorDeg < -45)) {
          instructions.add(new Instruction("\t\u2196 Take a slight left at " + nodeName, thisNode));
          continueCounter = 0;
        } else if (errorDeg > 70) {
          instructions.add(new Instruction("\t\u2192 Turn right at " + nodeName, thisNode));
          continueCounter = 0;
        } else if ((errorDeg > 45) && (errorDeg < 70)) {
          instructions.add(
              new Instruction("\t\u2197 Take a slight right at " + nodeName, thisNode));
          continueCounter = 0;
        } else {
          if (continueCounter == 0) {
            instructions.add(new Instruction("\t\u2191 Continue at " + nodeName, thisNode));
            continueCounter = continueCounter + 1;
          }
        }
      }
    }

    instructions.add(
        new Instruction(
            "You have arrived at your destination!",
            //                + currentPath
            //                    .get(currentPath.size() - 1)
            //                    .getCurrentLocation(mapController.getMapSession(), date)
            //                    .get(0),
            currentPath.get(currentPath.size() - 1)));
  }

  public static class Instruction {
    @Getter @Setter private String instruction;
    @Getter @Setter private Node node;

    Instruction(String instruction, Node node) {
      this.instruction = instruction;
      this.node = node;
    }
  }
}
