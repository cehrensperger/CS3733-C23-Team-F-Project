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
   * converts a location name to a Node by looking for the latest Node that has the given location
   * name. Returns null if no result could be found
   *
   * @param name the location name to query for
   * @param session the session to run the query on
   * @return the found node, or null
   */
  private Node locationToNode(@NonNull LocationName name, @NonNull Session session) {
    // Create a query that selects the first node from the move where the location is the location
    // orders by descending date (first at top) and limits it to one, so we only get one.
    // Then casts to a Node, sets the parameter location (to prevent injection)
    // Get unique result will either return the result, or null if there was none
    return session
        .createQuery(
            """
                        SELECT node
                        FROM Move
                        where location = :location
                        ORDER BY moveDate DESC
                        LIMIT 1""",
            Node.class)
        .setParameter("location", name)
        .uniqueResult();
  }

  /**
   * Returns the location associated with a given Node, or null if none could be found
   *
   * @param node the node to lookup
   * @param session the session to use in the lookup
   * @return the location that was found
   */
  private LocationName nodeToLocation(@NonNull Node node, @NonNull Session session) {
    // Create a query that selects the first location from the move where the location is the
    // location, orders
    // by descending date (first at top) and limits to one, so we only get one.
    // Then casts to LocationName, sets the parameter location (to prevent injection)
    // Gets a unique result, which will return either the singular result found or null if there was
    // none
    return session
        .createQuery(
            """
                                          SELECT LocationName
                                          FROM Move
                                          WHERE Node = :node
                                          ORDER BY moveDate DESC
                                          LIMIT 1
                                          """,
            LocationName.class)
        .setParameter("node", node)
        .uniqueResult();
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
      Node startNode = locationToNode(longNameToLocation(start, session), session);
      Node endNode = locationToNode(longNameToLocation(end, session), session);

      // Find the path with A*
      path = aStar(startNode, endNode, session);
    } catch (
        NullPointerException
            error) { // Catch failures, so we can close the transaction no matter what
      // End the transaction
      transaction.rollback();

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
    PriorityQueue<NodeWrapper> openList = new PriorityQueue<>();
    List<NodeWrapper> closedlist = new LinkedList<>();

    openList.add(new NodeWrapper(start, null));

    // while open list is not empty
    while (!openList.isEmpty()) {
      NodeWrapper q = openList.poll();

      if (q.node.equals(end)) {
        List<Node> path = new LinkedList<>();
        path.add(q.node);
        while (!q.node.equals(start)) {
          q = q.parent;
          path.add(q.node);
        }
        Collections.reverse(path);
        return path;
      }

      NODE_LOOP:
      for (Node node : getNeighbors(q.node, session)) {
        NodeWrapper child = new NodeWrapper(node, q);
        child.g = q.g + euclideanDistance(child.node, q.node);
        child.h = euclideanDistance(child.node, end);
        child.f = child.g + child.h;

        for (NodeWrapper open : openList) {
          if (open.node.equals(child.node) && open.f < child.f) {
            continue NODE_LOOP;
          }
        }

        for (NodeWrapper closed : closedlist) {
          if (closed.node.equals(child.node) && closed.f < child.f) {
            continue NODE_LOOP;
          }
        }
        openList.add(child);
      }
      closedlist.add(q);
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

    @Override
    public int compareTo(NodeWrapper nodeWrapper) {
      return Double.compare(f, nodeWrapper.f);
    }
  }
}
