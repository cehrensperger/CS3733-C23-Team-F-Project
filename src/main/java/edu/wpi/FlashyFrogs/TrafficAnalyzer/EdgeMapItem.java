package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import lombok.NonNull;

/** Map item containing an edge */
class EdgeMapItem extends MapItem {
  @NonNull final Edge edge; // Edge this map item uses

  /**
   * Creates an edge map item
   *
   * @param edge the edge
   * @param serviceRequestWeight the weight the service request should have
   */
  public EdgeMapItem(@NonNull Edge edge, double serviceRequestWeight) {
    super(serviceRequestWeight);
    this.edge = edge; // Save the edge
  }

  /**
   * Returns a string representation of this edge
   *
   * @return the string representation of this edge
   */
  @Override
  @NonNull
  String getMapItemString() {
    return edge.toString();
  }

  @NonNull
  @Override
  String type() {
    return "Edge";
  }

  /**
   * @return
   */
  @Override
  Node.Floor getMapFloor() {
    if (edge.getNode1().getFloor().equals(edge.getNode2().getFloor())) {
      return edge.getNode1().getFloor();
    }

    return null;
  }

  @Override
  Point2D getMapCoordinates() {
    // Return the average for both axes, so should be the middle of the line
    return new Point2D(
        (edge.getNode1().getXCoord() + edge.getNode2().getXCoord()) / 2.0,
        (edge.getNode1().getYCoord() + edge.getNode2().getYCoord()) / 2.0);
  }

  @Override
  Shape getMapBacking(@NonNull MapController mapController) {
    return mapController.getEdgeToLineMap().get(this.edge);
  }

  /**
   * Returns the hash code of the map item
   *
   * @return the hash code of the map item
   */
  @Override
  public int hashCode() {
    return this.edge.hashCode();
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

    EdgeMapItem other = (EdgeMapItem) obj; // Cast

    return this.edge.equals(other.edge); // Diverte to edges being equal
  }
}
