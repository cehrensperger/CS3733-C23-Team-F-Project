package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Node;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import lombok.NonNull;

class NodeMapItem extends MapItem {
  @NonNull private final Node node; // Node this represents

  /**
   * Constructor, sets the relevant paths for this
   *
   * @param node the node this uses
   */
  NodeMapItem(@NonNull Node node) {
    this.node = node; // Save the node
  }

  /**
   * Gets the string representing a map item
   *
   * @return the map item as a string
   */
  @Override
  @NonNull
  String getMapItemString() {
    return node.toString();
  }

  @Override
  @NonNull
  String type() {
    return "Node";
  }

  /**
   * Gets the floor for the node
   *
   * @return the floor for the node
   */
  @Override
  Node.Floor getMapFloor() {
    return node.getFloor();
  }

  /**
   * Gets the map coordinates for the node
   *
   * @return the coordinates of the node
   */
  @Override
  Point2D getMapCoordinates() {
    // Return the nodes coordinates
    return new Point2D(node.getXCoord(), node.getYCoord());
  }

  @Override
  Shape getMapBacking(@NonNull MapController mapController) {
    return mapController.getNodeToCircleMap().get(this.node);
  }

  /**
   * Returns the hash code of the map item
   *
   * @return the hash code of the map item
   */
  @Override
  public int hashCode() {
    return this.node.hashCode();
  }

  /**
   * @param obj the object to compare this to
   * @return true if the items are true
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true; // Short-circuit on memory equal
    if (obj == null) return false; // Null means not equal
    if (obj.getClass() != getClass()) return false; // If the classes aren't equal false

    NodeMapItem other = (NodeMapItem) obj; // Cast

    return this.node.equals(other.node); // Diverte to nodes being equal
  }
}
