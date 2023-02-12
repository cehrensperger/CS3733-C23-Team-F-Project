package edu.wpi.FlashyFrogs.PathFinding;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.*;
import lombok.NonNull;
import org.hibernate.Session;

public class PathFinder {
  @NonNull private final Session session;

  private IFindPath algorithm;

  public PathFinder(Session session) {
    this.session = session;
  }

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
  public List<LocationName> nodeListToLocation(
      @NonNull List<Node> nodes, @NonNull Session session) {
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
  static Set<Node> getNeighbors(@NonNull Node node, @NonNull Session session) {
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

  public void setAlgorithm(IFindPath algorithm) {
    this.algorithm = algorithm;
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
    List<Node> path;

    // Query location names and return nodes to send to aStar function
    LocationName startLocation = longNameToLocation(start, session);
    LocationName endLocation = longNameToLocation(end, session);

    Node startNode = startLocation.getCurrentNode(session);
    Node endNode = endLocation.getCurrentNode(session);

    // Find the path with the algorithm
    return algorithm.findPath(startNode, endNode, session); // Return the path
  }

  static class NodeWrapper implements Comparable<NodeWrapper> {
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
    NodeWrapper(@NonNull Node node, NodeWrapper parent) {
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
