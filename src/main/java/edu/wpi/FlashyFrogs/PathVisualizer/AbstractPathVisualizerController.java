package edu.wpi.FlashyFrogs.PathVisualizer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.PathFinder;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;

/**
 * Abstract base class that anything that visualizes a path should implement. Creates the map, and
 * the map controller, and enables classes that extend this to easily visualize paths
 */
public abstract class AbstractPathVisualizerController implements IController {
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
      if (startNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
        // Circle for the node
        Circle nodeCircle = mapController.getNodeToCircleMap().get(startNode);
        nodeCircle.setVisible(visible); // Set the node to be visible
        nodeCircle.setFill(endColor); // Set its color
      }

      // For each node in the path
      for (int i = 1; i < currentPath.size(); i++) {
        // Get the two nodes
        Node thisNode = currentPath.get(i);
        Node lastNode = currentPath.get(i - 1);

        if (!thisNode.getFloor().equals(lastNode.getFloor())
            && lastNode.getFloor().equals(mapController.getMapFloorProperty().getValue())) {
          FXMLLoader loader =
              new FXMLLoader(
                  AbstractPathVisualizerController.class.getResource("NextFloorPopup.fxml"));
          PopOver goToNext = new PopOver(loader.load());
          goToNext.setHeaderAlwaysVisible(false); // Disable the header
          changeFloorPopOvers.add(goToNext); // Add this to the pop-overs

          // Create the controller
          NextFloorPopupController controller = loader.getController();
          controller.setPathfindingController(this); // Set its controller to be this
          controller.setDestination(thisNode); // Go to that

          Circle circle = mapController.getNodeToCircleMap().get(lastNode);
          if (circle != null) {
            circle.setFill(Paint.valueOf(Color.YELLOW.toString()));
            circle.setOpacity(1);

            goToNext.show(circle);
            goToNext.setAutoHide(false);
            goToNext.setAutoFix(false);
            goToNext.detach();
            goToNext.setX(250);
            goToNext.setY(20);
          }
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
          lineToColor.setVisible(visible);
        }
      }
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

  /**
   * Zooms the path-finder to the selected node on the floor of that node
   *
   * @param node the node to go to
   */
  void goToNode(@NonNull Node node) {
    // Go to the floor
    mapController.getMapFloorProperty().setValue(node.getFloor());

    // Go to the nodes coordinates
    mapController.zoomToCoordinates(10, node.getXCoord(), node.getYCoord());
  }
}
