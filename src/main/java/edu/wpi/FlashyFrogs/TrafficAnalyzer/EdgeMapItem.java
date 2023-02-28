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

  public EdgeMapItem(@NonNull Edge edge) {
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
    // Return the coordinates for the start of the edge
    return new Point2D(edge.getNode1().getXCoord(), edge.getNode2().getYCoord());
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
