package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
   * @param start the start node to find
   * @param end the end node to find
   * @return the path (as a list) between the two locations, or null if it could not find a path
   * @throws NullPointerException if the path between the two locations couldn't be found
   */
  public List<Node> findPath(@NonNull LocationName start, @NonNull LocationName end) {
    Session session = sessionFactory.openSession();
    // Query location names and return nodes to send to aStar function
    Node startNode = locationToNode(start, session);
    Node endNode = locationToNode(end, session);

    List<Node> path = aStar(startNode, endNode, session);

    session.close();

    return path;
  }

  private List<Node> aStar(@NonNull Node start, @NonNull Node end, @NonNull Session session) {
    PriorityQueue<NodeWrapper> openList = new PriorityQueue<>();
    List<NodeWrapper> closedlist = new LinkedList<>();

    openList.add(new NodeWrapper(start, null, 0));

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

  private double euclideanDistance(@NonNull Node node1, @NonNull Node node2) {
    int x1 = node1.getXCoord();
    int x2 = node2.getXCoord();
    int y1 = node1.getYCoord();
    int y2 = node2.getYCoord();
    int dx = x1 - x2;
    int dy = y1 - y2;
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }

  private class NodeWrapper implements Comparable<NodeWrapper> {
    Node node;
    NodeWrapper parent;
    double g;
    double h;
    double f;

    NodeWrapper(@NonNull Node node, NodeWrapper parent) {
      this.node = node;
      this.parent = parent;
    }

    NodeWrapper(@NonNull Node node, NodeWrapper parent, double cost) {
      this.node = node;
      this.parent = parent;
      this.f = cost;
    }

    @Override
    public int compareTo(NodeWrapper nodeWrapper) {
      if (f == nodeWrapper.f) return 0;
      else if (f > nodeWrapper.f) return 1;
      else return -1;
    }
  }
}
