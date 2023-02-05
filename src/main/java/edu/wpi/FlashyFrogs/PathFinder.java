package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

@AllArgsConstructor
public class PathFinder {
  @NonNull private final SessionFactory sessionFactory;

  /**
   * Converts a String location name to the LocationName object associated with it. MAY return Null
   * if no location has that name
   *
   * @param locationName the string location name to find the LocationName object of
   * @param session the session to execute the query on
   * @return the LocationName object associated with the queried name
   */
  private LocationName longNameToLocation(@NonNull String locationName, @NonNull Session session) {
    return session.get(LocationName.class, locationName); // Query the session for the name
  }

  /**
   * @param nodes list of nodes to lookup
   * @return list of locations that were found
   */
  public List<LocationName> nodeListToLocation(@NonNull List<Node> nodes, @NonNull Session session) {
    List<LocationName> locations = new ArrayList<>();
    for (Node node : nodes) {
      locations.add(node.getCurrentLocation(session));
    }
    return locations;
  }

  /**
   * Gets the neighbors of a Node, by means of their edges
   *
   * @param node the node to get the neighbors of
   * @param session the session to use to get the results
   * @return the Set of Nodes that represent the Edges of a given Node
   */
  @NonNull
  private Set<Node> getNeighbors(@NonNull Node node, @NonNull Session session) {
    // Gets the Set of Nodes that represents the union (combination with no duplicates)
    // of Nodes that this Node starts or ends. Select the opposite end in both cases.
    // Sets the parameter, and gets the result list as well
    return new HashSet<>(
        session
            .createQuery(
                """
                                        SELECT node1
                                        FROM Edge
                                        WHERE node2 = :node
                                        UNION
                                        SELECT node2
                                        FROM Edge
                                        WHERE node1 = :node""",
                Node.class)
            .setParameter("node", node)
            .getResultList());
  }

  /**
   * Public method to find the path between two locations.
   *
   * @param start the start location to find. Will find the Node most recently associated with this
   * @param end the end location to find. Will find the Node most recently associated with this
   * @return the path (as a list) between the two locations, or null if it could not find a path
   * @throws NullPointerException if the lookup for a location (or node associated with the
   *     location) fails
   */
  public List<Node> findPath(@NonNull String start, @NonNull String end) {
    // Get the session to use for this
    Session session = sessionFactory.openSession();

    // Create a transaction so that nothing happens while reading occurs
    Transaction transaction = session.beginTransaction();

    List<Node> path;

    try {
      // Query location names and return nodes to send to aStar function
      LocationName startLocation = longNameToLocation(start, session);
      LocationName endLocation = longNameToLocation(end, session);

      Node startNode = startLocation.getCurrentNode(session);
      Node endNode = endLocation.getCurrentNode(session);

      // Find the path with A*
      path = aStar(startNode, endNode, session);
    } catch (
        NullPointerException
            error) { // Catch failures, so we can close the transaction no matter what
      if (transaction.isActive()) {
        transaction.rollback();
      }

      // Close the session
      session.close();

      throw error;
    }

    // Commit the transaction
    transaction.commit();

    // Close the session
    session.close();

    return path; // Return the path
  }

  /**
   * Private method to find the path between two locations.
   *
   * @param start the start node
   * @param end the end node
   * @return the path (as a list) between the two nodes, or null if it could not find a path
   */
  private List<Node> aStar(@NonNull Node start, @NonNull Node end, @NonNull Session session) {
    PriorityQueue<NodeWrapper> openList =
        new PriorityQueue<>(); // create priority queue for nodes to search
    List<NodeWrapper> closedList =
        new LinkedList<>(); // create list for nodes that have been visited

    openList.add(new NodeWrapper(start, null)); // add start node to open list

    NODE_CHECK:
    while (!openList.isEmpty()) { // while open list is not empty
      NodeWrapper q = openList.poll(); // get node with the lowest estimated cost

      for (NodeWrapper closed : closedList) {
        if (closed.node.equals(q.node)) {
          continue NODE_CHECK;
        }
      }

      if (q.node.equals(end)) { // if the current node is the goal
        List<Node> path = new LinkedList<>(); // create list of nodes to represent the path
        path.add(q.node); // add the end node
        while (!q.node.equals(start)) { // follow the path backwards and add nodes in reverse order
          q = q.parent;
          path.add(q.node);
        }
        Collections.reverse(path); // reverse the path so that it is in the correct order
        return path;
      }

      NODE_LOOP:
      for (Node node : getNeighbors(q.node, session)) { // get the neighbors of the current node
        NodeWrapper child = new NodeWrapper(node, q); // create node wrapper out of current node
        child.g = q.g + euclideanDistance(child.node, q.node); // calculate distance from start
        child.h =
            euclideanDistance(child.node, end); // calculate the lowest possible distance to end
        child.f = child.g + child.h;

        for (NodeWrapper open : openList) { // check if node is on open list with a lower cost
          if (open.node.equals(child.node) && open.f < child.f) {
            continue NODE_LOOP;
          }
        }

        for (NodeWrapper closed : closedList) { // check is node is on closed list ith lower cost
          if (closed.node.equals(child.node) && closed.f < child.f) {
            continue NODE_LOOP;
          }
        }
        openList.add(child);
      }
      closedList.add(q);
    }
    return null;
  }

  /**
   * Private method to find the distance between two nodes
   *
   * @param node1 first node
   * @param node2 second node
   * @return distance between the two nodes
   */
  private double euclideanDistance(@NonNull Node node1, @NonNull Node node2) {
    int x1 = node1.getXCoord();
    int x2 = node2.getXCoord();
    int y1 = node1.getYCoord();
    int y2 = node2.getYCoord();
    int dx = x1 - x2;
    int dy = y1 - y2;
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }

  private static class NodeWrapper implements Comparable<NodeWrapper> {
    Node node;
    NodeWrapper parent;
    double g;
    double h;
    double f;

    /**
     * Generates a node wrapper that contains a node with values to calculate cost
     *
     * @param node the node to wrap
     * @param parent the parent node
     */
    private NodeWrapper(@NonNull Node node, NodeWrapper parent) {
      this.node = node;
      this.parent = parent;
      this.f = 0;
      this.g = 0;
      this.h = 0;
    }

    /**
     * @param nodeWrapper the object to be compared.
     * @return the comparison of node costs
     */
    @Override
    public int compareTo(NodeWrapper nodeWrapper) {
      return Double.compare(f, nodeWrapper.f);
    }
  }
}
