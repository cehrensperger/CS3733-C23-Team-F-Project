package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
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
public class MapEntity {
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
  public void setMapFloor(@NonNull Node.Floor mapFloor) {
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
  public void addNode(@NonNull Node node, @NonNull Circle circle) {
    nodeToCircleMap.put(node, circle); // Put the node into the map

    // If the node we're creating is valid
    if (nodeCreation != null) {
      nodeCreation.accept(node, circle); // Preform the callback on it
    }
  }

  /**
   * Adds an edge to the map, including doing any set callbacks
   *
   * @param edge the edge to add
   * @param line the line that represents the edge
   */
  public void addEdge(@NonNull Edge edge, @NonNull Line line) {
    edgeToLineMap.put(edge, line); // Put the edge into the map
  }

  /** Commits any changes that have been made using the map session */
  public void commitMapChanges() {
    mapTransaction.commit(); // Commit

    mapTransaction = getMapSession().beginTransaction(); // Begin a new transaction
  }

  /** Rolls-back any changes that have been made using the map session */
  public void rollbackMapChanges() {
    mapTransaction.rollback(); // Rollback

    mapTransaction = getMapSession().beginTransaction(); // Begin a new transaction
  }

  /**
   * Function that MUST be called on map close! If this is not called, the session used will NOT be
   * properly closed. This also manages rolling back the open transaction
   */
  public void closeMap() {
    mapTransaction.rollback(); // Close the map transaction
    getMapSession().close(); // Close the map session
  }
}
