package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.utils.others.TriConsumer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Entity for the map, tracks the session the map will use, callbacks for node creation, and
 * relations between things on the map and their physical representations, and enables
 * commit/rollback operations on the map
 */
class MapEntity {
  @Getter @NonNull
  private final Map<Node, Circle> nodeToCircleMap = new HashMap<>(); // Map for node to circle

  @Getter @NonNull
  private final Map<Edge, Line> edgeToLineMap = new HashMap<>(); // Map for edge to lien

  @Setter private BiConsumer<Node, Circle> nodeCreation; // Callback to be called on node creation

  @Setter private BiConsumer<Edge, Line> edgeCreation; // Callback to be called on edge creation

  @Getter @NonNull
  private final Map<LocationName, Text> locationNameToTextMap =
      new HashMap<>(); // Location name to text

  @Getter @NonNull
  private final Map<Node, Set<LocationName>> nodeToLocationNameMap =
      new HashMap<>(); // Node to location

  @Getter @NonNull
  private final Map<Node, VBox> nodeToLocationBox = new HashMap<>(); // Node to location box

  @Setter
  private TriConsumer<Node, LocationName, Text>
      locationCreation; // Callback to be called on location creation

  @Getter @NonNull
  private final Session mapSession = DBConnection.CONNECTION.getSessionFactory().openSession();

  // Transaction to allow map commit/rollback
  private Transaction mapTransaction = mapSession.beginTransaction();
  @Getter private Node.Floor mapFloor = null; // The floor the map should use

  /**
   * Sets the map floor, including clearing the nodes and edges the map uses
   *
   * @param mapFloor the new floor to set
   */
  void setMapFloor(Node.Floor mapFloor) {
    this.mapFloor = mapFloor;

    nodeToCircleMap.clear();
    edgeToLineMap.clear();
  }

  /**
   * Add a node to the map, including doing any set callbacks
   *
   * @param node the node to add
   * @param circle the circle that represents the node
   */
  void addNode(@NonNull Node node, @NonNull Circle circle) {
    nodeToCircleMap.put(node, circle); // Put the node into the map

    // Put the node into the locations map
    nodeToLocationNameMap.put(node, new HashSet<>()); // Add the set

    // Add the vbox that will hold the locations
    nodeToLocationBox.put(node, new VBox());

    // If the node we're creating is valid
    if (nodeCreation != null) {
      nodeCreation.accept(node, circle); // Preform the callback on it
    }
  }

  /**
   * Adds a location to the map, including node to location and location to text. Also adds the
   * location to the node box
   *
   * @param node the node that holds the text
   * @param location the location
   * @param text the text
   */
  void addLocation(@NonNull Node node, @NonNull LocationName location, @NonNull Text text) {
    locationNameToTextMap.put(location, text); // Add the location

    // Check to make sure we're adding a second node (and not more)
    if (nodeToLocationNameMap.get(node).size() >= 2) {
      // If so, throw an exception
      throw new IllegalStateException("Node already has two locations associated with it!");
    }

    // Otherwise, add it
    nodeToLocationNameMap.get(node).add(location);

    // Add the text to the box
    nodeToLocationBox.get(node).getChildren().add(text);

    // If the location creator exists
    if (locationCreation != null) { // If the location creator exists
      locationCreation.accept(node, location, text); // Call it
    }
  }

  /**
   * Removes a given node from the node to circle mapping. Does not deal with un-rending the circle,
   * only deletes it from the mapping. Also handles edge deletion (but not rendering of that)
   *
   * @param node the node to delete
   */
  void removeNode(@NonNull Node node) {
    nodeToCircleMap.remove(node); // Delete the node

    // Filter to only have the edges to remove using a stream
    List<Edge> edgesToRemove =
        edgeToLineMap.keySet().stream()
            .filter((edge) -> (edge.getNode1().equals(node) || edge.getNode2().equals(node)))
            .collect(Collectors.toList());

    // For each edge to remove
    for (Edge toRemove : edgesToRemove) {
      removeEdge(toRemove); // Delete it
    }

    // If there is a location name, remove it
    if (nodeToLocationNameMap.containsKey(node)) {
      // For each location name, remove it
      nodeToLocationNameMap.get(node).forEach(this::removeLocationName);

      nodeToLocationBox.remove(node); // Remove the node to location name box
    }
  }

  /**
   * Adds an edge to the map, including doing any set callbacks
   *
   * @param edge the edge to add
   * @param line the line that represents the edge
   */
  void addEdge(@NonNull Edge edge, @NonNull Line line) {
    edgeToLineMap.put(edge, line); // Put the edge into the map

    // If the edge creation is valid
    if (edgeCreation != null) {
      edgeCreation.accept(edge, line); // Call it
    }
  }

  /**
   * Removes an edge from the map. Does not handle visual changes, only deletes it from the mapping
   *
   * @param edge the edge to remove
   */
  void removeEdge(@NonNull Edge edge) {
    edgeToLineMap.remove(edge);
  }

  /**
   * Handles removing the location name from the map, including removing it from the parent box
   *
   * @param locationName the location to remove
   */
  void removeLocationName(@NonNull LocationName locationName) {
    // Find the node that contain the location name
    Node toDelete =
        nodeToLocationNameMap.keySet().stream()
            .filter((node) -> nodeToLocationNameMap.get(node).contains(locationName))
            .findFirst()
            .orElseThrow();

    // Delete it from the mapping, don't remove the location name map
    nodeToLocationNameMap.get(toDelete).remove(locationName);

    // Remove the location from its parent box
    nodeToLocationBox.get(toDelete).getChildren().remove(locationNameToTextMap.get(locationName));

    locationNameToTextMap.remove(locationName); // Remove the location name in the text mapping
  }

  /** Commits any changes that have been made using the map session */
  void commitMapChanges() {
    mapTransaction.commit(); // Commit

    mapTransaction = getMapSession().beginTransaction(); // Begin a new transaction
  }

  /** Rolls-back any changes that have been made using the map session */
  void rollbackMapChanges() {
    mapTransaction.rollback(); // Rollback

    mapTransaction = getMapSession().beginTransaction(); // Begin a new transaction
  }

  /**
   * Function that MUST be called on map close! If this is not called, the session used will NOT be
   * properly closed. This also manages rolling back the open transaction
   */
  void closeMap() {
    mapTransaction.rollback(); // Close the map transaction
    getMapSession().close(); // Close the map session
  }
}
