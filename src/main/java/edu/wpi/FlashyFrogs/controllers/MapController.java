package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.MapEntity;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import javafx.fxml.FXML;
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
  @FXML private GesturePane gesturePane; // Gesture pane
  @FXML private Group group; // Group that will be used as display in the gesture pane

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
      Pane pane = new Pane(); // Create it
      group.getChildren().add(pane); // Add it to the group

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
        Circle circleToDraw = new Circle(node.getXCoord(), node.getYCoord(), 5, Color.BLACK);
        pane.getChildren().add(circleToDraw); // Draw the circle

        mapEntity.addNode(node, circleToDraw); // Add the circle to the entity
      }

      for (Edge edge : edges) {
        // Create the line from one node to the next
        Line lineToDraw =
            new Line(
                edge.getNode1().getXCoord(),
                edge.getNode1().getYCoord(),
                edge.getNode2().getXCoord(),
                edge.getNode2().getYCoord());
        pane.getChildren().add(lineToDraw); // Add the line

        mapEntity.addEdge(edge, lineToDraw); // Add the line to the entity
      }

      gesturePane.centreOn(
          new javafx.geometry.Point2D(nodes.get(0).getXCoord(), nodes.get(0).getYCoord()));
    }
  }

  /**
   * Changes the floor that the map is displaying
   *
   * @param floor the new floor to display. Must not be null
   */
  public void setFloor(@NonNull Node.Floor floor) {
    this.mapEntity.setMapFloor(floor); // Set the floor in the entity
    redraw(); // Force a redraw/re-fetch from scratch
  }

  /** Saves changes to the map */
  public void saveChanges() {
    this.mapEntity.commitMapChanges(); // Commit the map changes
  }

  /** Cancels changes to the map */
  public void cancelChanges() {
    this.mapEntity.rollbackMapChanges(); // Abort the changes
    this.redraw(); // Redraw the map, as the map will now be in a different staet
  }

  /** Shuts down the map controller, ending the session it uses */
  public void exit() {
    this.mapEntity.closeMap(); // Close the map
  }
}
