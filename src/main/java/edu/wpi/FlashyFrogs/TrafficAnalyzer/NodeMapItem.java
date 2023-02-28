package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.Collection;
import lombok.NonNull;

class NodeMapItem extends MapItem {
  @NonNull private final Node node; // Node this represents

  /**
   * Constructor, sets the relevant paths for this
   *
   * @param node the node this uses
   * @param relevantPaths the relevant paths to save this
   */
  NodeMapItem(@NonNull Node node, @NonNull Collection<Path> relevantPaths) {
    super(relevantPaths); // Save the paths
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
}
