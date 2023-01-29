package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.SessionFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

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
    public List<Edge> findPath(@NonNull LocationName start, @NonNull LocationName end) {
        // Query location names and return nodes to send to aStar function
        return null;
    }

    private List<Edge> aStar(@NonNull Node start, @NonNull Node end) {
        PriorityQueue<NodeWrapper> openList = new PriorityQueue<>();
        List<NodeWrapper> closedlist = new LinkedList<>();

        openList.add(new NodeWrapper(start, 0));

        // while open list is not empty
        while (!openList.isEmpty()) {
            NodeWrapper q = openList.poll();
            // generate children of q and add them to openList
            // for each child
            // if goal then stop
            // else compute g and h
            // set f to g + h
            // if node is already on the open list with lower f then skip
            // if node is on closed list with lower f then skip else add to open list
            closedlist.add(q);
        }
        return null;
    }

    private double euclideanDistance(@NonNull int x1, @NonNull int x2, @NonNull int y1, @NonNull int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    private class NodeWrapper implements Comparable<NodeWrapper> {
        Node node;
        Node parent;
        double g;
        double h;
        double f;

        NodeWrapper(@NonNull Node node, @NonNull double cost) {
            this.node = node;
            this.parent = null;
            this.f = cost;
        }

        NodeWrapper(@NonNull Node startNode, @NonNull Node endNode) {
            node = endNode;
            cost = euclideanDistance(startNode.getXCoord(), endNode.getYCoord(), endNode.getYCoord(), endNode.getYCoord());
        }

        @Override
        public int compareTo(NodeWrapper nodeWrapper) {
            if(f==nodeWrapper.f)
                return 0;
            else if(f>nodeWrapper.f)
                return 1;
            else
                return -1;
        }
    }
}
