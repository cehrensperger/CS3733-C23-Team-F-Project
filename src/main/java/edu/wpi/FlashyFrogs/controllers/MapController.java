package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import java.util.HashMap;
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
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Controller for the Map of the hospital. Provides utilities to make it useful for classes that may
 * want to use this. Listens for additions/removals of nodes/edges/locations in order to
 * automatically display changes to the map
 */
public class MapController {
  @FXML private Group group; // Group that will be used as display in the gesture pane
  private Node.Floor floor =
      null; // The floor to display on the map. Starts as null, e.g., empty map, no floor
  private @NonNull final Map<Edge, Line> edges =
      new HashMap<>(); // Map relating edge to the line it is represented by
  private @NonNull final Map<Node, Circle> nodes =
      new HashMap<>(); // Map relating node to the circle it is represented by

  // Function that will be called when a new node is drawn. This is the place to add actions onto
  // the node  (e.g.,
  // on drag, on click, on hover, etc)
  @Setter private BiConsumer<Node, Circle> nodeCreation;

  /**
   * Adds a node (in the JavaFX sense, NOT the ORM/map sense) to the map. This node will be cleared
   * when the map has a floor change
   *
   * @param node the node to draw. A node can be essentially any sort of scene item. This should
   *     have positioning already set so it renders correctly
   */
  public void addNode(@NonNull javafx.scene.Node node) {
    group.getChildren().add(node);
  }

  /**
   * Removes a node (in the JavaFX sense, NOT the ORM/map sense) from the map
   *
   * @param node the node to remove. A node can be essentially any sort of scene item
   * @throws IllegalArgumentException if the provided node does not exist in the map
   */
  public void removeNode(@NonNull javafx.scene.Node node) {
    if (!group.getChildren().remove(node)) {
      throw new IllegalArgumentException("Provided node does not exist in the Map!");
    }
  }

  /**
   * Redraws the entire map, from scratch. This is an EXPENSIVE operation, so should be avoided when
   * possible. This involves DB fetches to fetch data from the floor
   */
  private void redraw() {
    edges.clear(); // Clear the edges
    nodes.clear(); // Clear the nodes

    // Clear the gesture pane
    group.getChildren().clear();

    // If we have a floor to draw
    if (floor != null) {
      // Fetch the map image from the resource dictionary, and then drop it at the root
      ImageView imageView =
          new ImageView(ResourceDictionary.valueOf(floor.name()).resource); // image
      imageView.relocate(0, 0); // Relocate to 0, 0

      // Add the image to the group
      group.getChildren().add(imageView);

      // Create a pane to draw the nodes in
      Pane pane = new Pane(); // Create it
      group.getChildren().add(pane); // Add it to the group

      Session mapUpdateSession =
          DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session

      Transaction readTransaction =
          mapUpdateSession.beginTransaction(); // Begin a transaction for reading

      // Get the list of nodes
      List<Node> nodes =
          mapUpdateSession
              .createQuery("FROM Node n WHERE n.floor = :floor", Node.class)
              .setParameter("floor", floor)
              .getResultList();

      // Get the list of edges
      List<Edge> edges =
          mapUpdateSession
              .createQuery(
                  "FROM Edge WHERE node1.floor = :floor OR node2.floor = :floor", Edge.class)
              .setParameter("floor", floor)
              .getResultList();

      readTransaction.commit(); // Commit the update transaction, now that we're done reading

      // For each node in the nodes to draw
      for (Node node : nodes) {
        Circle circleToDraw = new Circle(node.getXCoord(), node.getYCoord(), 5, Color.BLACK);
        pane.getChildren().add(circleToDraw); // Draw the circle
        this.nodes.put(node, circleToDraw);

        // If the node function is valid
        if (nodeCreation != null) {
          // Call it on what was provided
          nodeCreation.accept(node, circleToDraw);
        }
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
        this.edges.put(edge, lineToDraw);
      }

      mapUpdateSession.close(); // Close the session
    }
  }

  /**
   * Changes the floor that the map is displaying
   *
   * @param floor the new floor to display. Must not be null
   */
  public void setFloor(@NonNull Node.Floor floor) {
    this.floor = floor; // Update the floor
    redraw(); // Force a redraw/re-fetch from scratch
  }
}
