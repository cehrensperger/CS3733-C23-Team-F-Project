package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ORM.locationName;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.SessionFactory;

import java.util.List;

@AllArgsConstructor
public class PathFinder {
    @NonNull
    final private SessionFactory sessionFactory;

    /**
     * Public method to find the path between two locations.
     *
     * @param start the start node to find
     * @param end the end node to find
     * @return the path (as a list) between the two locations, or null if it could not find a path
     * @throws NullPointerException if the path between the two locations couldn't be found
     */
    public List<Edge> findPath(@NonNull locationName start, @NonNull locationName end) {
        // Query location names and return nodes to send to aStar function
        return null;
    }

    private List<Edge> aStar(@NonNull Node start, @NonNull Node end) {

        //
        return null;
    }

    private double euclideanDistance(@NonNull int x1, @NonNull int x2, @NonNull int y1, @NonNull int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }
}
