package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.Map.MapController;
import java.util.Collection;
import java.util.LinkedList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import lombok.Getter;
import lombok.NonNull;

/** Abstract class representing a map item */
abstract class MapItem {
  @NonNull @Getter
  private final Collection<Path> relevantPaths; // Paths that are relevant to this map item

  /** Constructor, sets the relevant paths for this */
  MapItem() {
    this.relevantPaths = new LinkedList<>();
  }

  /**
   * Method that should get the string representation of the map item
   *
   * @return the string representation of the map item
   */
  @NonNull
  abstract String getMapItemString();

  /**
   * Gets the number of uses of the map item
   *
   * @return the number of uses of the map item
   */
  int getNumUses() {
    return relevantPaths.size();
  }

  /**
   * Gets the floor for the given map element. May be null if the map doesn't have a concrete
   * backing
   *
   * @return the floor for the given map element
   */
  abstract edu.wpi.FlashyFrogs.ORM.Node.Floor getMapFloor();

  /**
   * Gets the map coordinates for this map item
   *
   * @return the coordinates for this map item
   */
  abstract Point2D getMapCoordinates();

  /**
   * Gets the backing of this as a JavaFX node in the map controller. May be null if this isn't on
   * the floor the map controller is showing
   *
   * @param mapController the map controller
   * @return the JavaFX backing of this node
   */
  abstract Shape getMapBacking(@NonNull MapController mapController);

  /**
   * Hash code, should compare by the map object
   *
   * @return the hash code of the map object
   */
  @Override
  public abstract int hashCode();

  /**
   * Equals, compares two map items based on the map item this represents
   *
   * @param obj the object to compare this to
   * @return true if the two objects are equal
   */
  @Override
  public abstract boolean equals(Object obj);
}
