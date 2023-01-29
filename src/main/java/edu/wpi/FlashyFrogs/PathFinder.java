package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.PriorityQueue;

@AllArgsConstructor
public class PathFinder {
    @NonNull
    final private SessionFactory sessionFactory;

    /**
     * Converts a String location name to the LocationName object associated with it.
     * MAY return Null if no location has that name
     * @param locationName the string location name to find the LocationName object of
     * @param session the session to execute the query on
     * @return the LocationName object associated with the queried name
     */
    private LocationName longNameToLocation(@NonNull String locationName, @NonNull Session session) {
        return session.get(LocationName.class, locationName); // Query the session for the name
    }

    /**
     * converts a location name to a Node by looking for the latest Node that has the given location name.
     * Returns null if no result could be found
     * @param name the location name to query for
     * @param session the session to run the query on
     * @return the found node, or null
     */
    private Node locationToNode(@NonNull LocationName name, @NonNull Session session) {
        // Create a query that selects the first node from the move where the location is the location
        // orders by descending date (first at top) and limits it to one, so we only get one.
        // Then casts to a Node, sets the parameter location (to prevent injection)
        // Get unique result will either return the result, or null if there was none
        return session.createQuery(
                """
SELECT node
FROM Move
where location = :location
ORDER BY moveDate DESC
LIMIT 1
""",
                Node.class).setParameter("location", name).uniqueResult();
    }

    /**
     * Gets the neighbors of a Node, by means of their edges
     * @param node the node to get the neighbors of
     * @param session the session to use to get the results
     * @return the Set of Nodes that represent the Edges of a given Node
     */
    @NonNull
    private Set<Node> getNeighbors(@NonNull Node node, @NonNull Session session) {
        // Gets the Set of Nodes that represents the union (combination with no duplicates)
        // of Nodes that this Node starts or ends. Select the opposite end in both cases.
        // Sets the parameter, and gets the result list as well
        return new HashSet<>(session.createQuery("""
SELECT node1
FROM Edge
WHERE node2 = :node
UNION
SELECT node2
FROM Edge
WHERE node1 = :node
""", Node.class).setParameter("node", node).getResultList());
    }

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
