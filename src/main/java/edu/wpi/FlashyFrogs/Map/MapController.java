package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.NonNull;
import net.kurobako.gesturefx.GesturePane;
import org.hibernate.Session;

/**
 * Controller for the Map of the hospital. Provides utilities to make it useful for classes that may
 * want to use this. Listens for additions/removals of nodes/edges/locations in order to
 * automatically display changes to the map
 */
public class MapController {
  @FXML private GesturePane gesturePane; // Gesture pane, used to zoom to given locations
  @FXML private Group group; // Group that will be used as display in the gesture pane
  private Pane currentDrawingPane; // The current drawing pane to use to draw nodes/edges

  @NonNull private final MapEntity mapEntity = new MapEntity(); // The entity the map will use

  /**
   * Sets the node creation function
   *
   * @param function the function to set the node creation function to. May be null
   */
  public void setNodeCreation(BiConsumer<Node, Circle> function) {
    mapEntity.setNodeCreation(function);
  }

  /**
   * Zooms the map to the given coordinates
   *
   * @param x the x-coordinate to zoom to
   * @param y the y-coordinate to zoom to
   */
  public void zoomToCoordinates(int x, int y) {
    gesturePane.centreOn(new Point2D(x, y));
  }

  /**
   * Adds a node to the map, including drawing it on the map and calling the associated callback.
   * NOTE: Assumes that the Node has no edges, and thus does not draw them
   *
   * @param node the node to draw on the map
   */
  public void addNode(@NonNull Node node) {
    Circle circleToDraw = new Circle(node.getXCoord(), node.getYCoord(), 5, Color.BLACK);
    currentDrawingPane.getChildren().add(circleToDraw); // Draw the circle

    mapEntity.addNode(node, circleToDraw); // Add the circle to the entity
  }

  /**
   * Manages the deletion of a node, including removing the visual representation of it, removing it
   * from the current node mapping, and deleting any edges that are associated with it. Will throw
   * an exception if the node isn't mapped
   *
   * @param node the node to delete
   */
  public void deleteNode(@NonNull Node node) {
    currentDrawingPane
        .getChildren()
        .remove(getNodeToCircleMap().get(node)); // Remove the nodes circle

    // For each edge
    for (Edge edge : getEdgeToLineMap().keySet()) {
      // Check if it relates to the node we're deleting
      if (edge.getNode1().equals(node) || edge.getNode2().equals(node)) {
        currentDrawingPane
            .getChildren()
            .remove(getEdgeToLineMap().get(edge)); // Remove the edge visually
      }
    }

    mapEntity.removeNode(node); // Remove the node, this also handles removing the edges
  }

  /**
   * Replaces one node with another. Deletes the old node and replaces it with the new one. This
   * involves deleting the visual representation and creating a new one. This may be called on nodes
   * that may not belong to this floor, they will automatically be filtered
   *
   * @param oldNode the node to replace
   * @param newNode the new node
   */
  public void moveNode(@NonNull Node oldNode, @NonNull Node newNode) {
    deleteNode(oldNode); // Completely delete the old node

    if (newNode
        .getFloor()
        .equals(this.getFloor())) { // If the floors are equal (we should draw this)
      addNode(newNode); // Add the new node

      // For each edge in the edges on this floor and associated with this node
      List<Edge> newEdges =
          getMapSession()
              .createQuery(
                  "FROM Edge "
                      + "WHERE node1.floor = :thisFloor AND node2.floor = :thisFloor AND "
                      + "(node1 = :thisNode OR node2 = :thisNode)",
                  Edge.class)
              .setParameter("thisFloor", this.getFloor())
              .setParameter("thisNode", newNode)
              .getResultList();

      newEdges.forEach(this::addEdge); // Add them
    }
  }

  /**
   * Processing adding an edge to the map, including persistence and lines. Assumes the edge is
   * valid
   *
   * @param edge the edge to draw
   */
  public void addEdge(@NonNull Edge edge) {
    Line lineToDraw =
        new Line(
            edge.getNode1().getXCoord(),
            edge.getNode1().getYCoord(),
            edge.getNode2().getXCoord(),
            edge.getNode2().getYCoord());
    currentDrawingPane.getChildren().add(lineToDraw); // Add the line

    mapEntity.addEdge(edge, lineToDraw); // Add the line to the entity
  }

  /**
   * Gets the map relating nodes to circles on the map
   *
   * @return the map relating node to circles on the map
   */
  @NonNull
  public Map<Node, Circle> getNodeToCircleMap() {
    return mapEntity.getNodeToCircleMap();
  }

  /**
   * Gets the map relating edges to nodes on the map
   *
   * @return the map relating edges to lines on the map
   */
  @NonNull
  public Map<Edge, Line> getEdgeToLineMap() {
    return mapEntity.getEdgeToLineMap();
  }

  /**
   * Gets the session this map is using
   *
   * @return the Hibernate session this map is using
   */
  @NonNull
  public Session getMapSession() {
    return mapEntity.getMapSession();
  }

  /**
   * Redraws the entire map, from scratch. This is an EXPENSIVE operation, so should be avoided when
   * possible. This involves DB fetches to fetch data from the floor
   */
  public void redraw() {
    // Clear the gesture pane
    group.getChildren().clear();
    currentDrawingPane = null; // Delete the reference to the drawing pane

    // If we have a floor to draw
    if (mapEntity.getMapFloor() != null) {
      // Fetch the map image from the resource dictionary, and then drop it at the root
      ImageView imageView =
          new ImageView(
              ResourceDictionary.valueOf(mapEntity.getMapFloor().name()).resource); // image
      imageView.relocate(0, 0); // Relocate to 0, 0

      // Add the image to the group
      group.getChildren().add(imageView);

      // Create a pane to draw the nodes in
      currentDrawingPane = new Pane(); // Create it
      group.getChildren().add(currentDrawingPane); // Add it to the group

      // Get the list of nodes
      List<Node> nodes =
          getMapSession()
              .createQuery("FROM Node n WHERE n.floor = :floor", Node.class)
              .setParameter("floor", mapEntity.getMapFloor())
              .getResultList();

      // Get the list of edges
      List<Edge> edges =
          getMapSession()
              .createQuery(
                  "FROM Edge WHERE node1.floor = :floor AND node2.floor = :floor", Edge.class)
              .setParameter("floor", mapEntity.getMapFloor())
              .getResultList();

      // For each node in the nodes to draw
      for (Node node : nodes) {
        addNode(node); // Add the node
      }

      // For each edge in the edges to draw
      for (Edge edge : edges) {
        // Create the edge
        addEdge(edge);
      }

      gesturePane.setMinScale(.001); // Set a scale that lets you go all the way out
    }
  }

  /**
   * Changes the floor that the map is displaying
   *
   * @param floor the new floor to display. Must not be null
   */
  public void setFloor(@NonNull Node.Floor floor) {
    Point2D currentCenter =
        this.gesturePane.targetPointAtViewportCentre(); // Get the current center

    this.mapEntity.setMapFloor(floor); // Set the floor in the entity
    redraw(); // Force a redraw/re-fetch from scratch

    this.gesturePane.centreOn(currentCenter); // Re-zoom on the old center
  }

  /** Saves changes to the map */
  public void saveChanges() {
    this.mapEntity.commitMapChanges(); // Commit the map changes
  }

  /** Cancels changes to the map */
  public void cancelChanges() {
    // Get the coordinates, so we can restore view point on cancel
    Point2D currentCenter = this.gesturePane.targetPointAtViewportCentre();

    this.mapEntity.rollbackMapChanges(); // Abort the changes
    this.redraw(); // Redraw the map, as the map will now be in a different state

    this.gesturePane.centreOn(currentCenter); // Go to the previous center
  }

  /** Shuts down the map controller, ending the session it uses */
  public void exit() {
    this.mapEntity.closeMap(); // Close the map
  }

  /**
   * Gets the floor the map is current on
   *
   * @return the floor the map is currently on, may be null
   */
  public Node.Floor getFloor() {
    return this.mapEntity.getMapFloor(); // return the floor
  }
}
