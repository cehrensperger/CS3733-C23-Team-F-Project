package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
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
  void setMapFloor(@NonNull Node.Floor mapFloor) {
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

    // If the node we're creating is valid
    if (nodeCreation != null) {
      nodeCreation.accept(node, circle); // Preform the callback on it
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
    Collection<Edge> edgesToRemove =
        edgeToLineMap.keySet().stream()
            .filter((edge) -> (edge.getNode1().equals(node) || edge.getNode2().equals(node)))
            .toList();

    // For each edge to remove
    for (Edge toRemove : edgesToRemove) {
      removeEdge(toRemove); // Delete it
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
  }

  /**
   * Removes an edge from the map. Does not handle visual changes, only deletes it from the mapping
   *
   * @param edge the edge to remove
   */
  void removeEdge(@NonNull Edge edge) {
    edgeToLineMap.remove(edge);
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
