package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;

/** Abstract class representing a map item */
abstract class MapItem {
  @NonNull @Getter
  private final Collection<Path> relevantPaths; // Paths that are relevant to this map item

  /**
   * Constructor, sets the relevant paths for this
   * @param relevantPaths the relevant paths to save this
   */
  MapItem(@NonNull Collection<Path> relevantPaths) {
    this.relevantPaths = relevantPaths;
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
}
