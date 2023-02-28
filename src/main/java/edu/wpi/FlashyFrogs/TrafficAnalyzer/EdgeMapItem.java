package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.ORM.Edge;
import lombok.NonNull;

import java.util.Collection;

/**
 * Map item containing an edge
 */
class EdgeMapItem extends MapItem {
    @NonNull
    final Edge edge; // Edge this map item uses

    public EdgeMapItem(@NonNull Edge edge, @NonNull Collection<Path> paths) {
        super(paths); // Save the paths
        this.edge = edge; // Save the edge
    }
    @Override
    @NonNull
    String getMapItemString() {
        return null;
    }
}
