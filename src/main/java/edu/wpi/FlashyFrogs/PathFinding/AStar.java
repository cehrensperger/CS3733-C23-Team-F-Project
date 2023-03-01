package edu.wpi.FlashyFrogs.PathFinding;

import static edu.wpi.FlashyFrogs.PathFinding.PathFinder.getNeighbors;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.time.Instant;
import java.util.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.hibernate.Session;

public class AStar implements IFindPath {
  /**
   * Private method to find the path between two locations.
   *
   * @param start the start node
   * @param end the end node
   * @return the path (as a list) between the two nodes, or null if it could not find a path
   */
  @SneakyThrows
  public List<Node> findPath(
      @NonNull Node start,
      @NonNull Node end,
      @NonNull Boolean accessible,
      @NonNull Session session) {

    if (start.getId().equals(null) || end.getId().equals(null)) throw new Exception();

    PriorityQueue<PathFinder.NodeWrapper> openList =
        new PriorityQueue<>(); // create priority queue for nodes to search
    List<PathFinder.NodeWrapper> closedList =
        new LinkedList<>(); // create list for nodes that have been visited

    openList.add(new PathFinder.NodeWrapper(start, null)); // add start node to open list

    NODE_CHECK:
    while (!openList.isEmpty()) { // while open list is not empty
      PathFinder.NodeWrapper q = openList.poll(); // get node with the lowest estimated cost

      for (PathFinder.NodeWrapper closed : closedList) {
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
        PathFinder.NodeWrapper child =
            new PathFinder.NodeWrapper(node, q); // create node wrapper out of current node
        if (q.node.getFloor() != child.node.getFloor()) {
          List<LocationName> locs =
              child.node.getCurrentLocation(session, Date.from(Instant.now()));
          if (locs.size() > 0
              && locs.get(0).getLocationType().equals(LocationName.LocationType.ELEV)) {
            child.g = q.g + 50; // cost for elevator
          } else if (locs.size() > 0
              && locs.get(0).getLocationType().equals(LocationName.LocationType.STAI)) {
            if (accessible) continue;
            child.g = q.g + 100; // cost for stairs
          }
        } else {
          child.g = q.g + euclideanDistance(child.node, q.node); // calculate distance from start
        }
        child.h =
            euclideanDistance(child.node, end); // calculate the lowest possible distance to end
        child.f = child.g + child.h;

        for (PathFinder.NodeWrapper open :
            openList) { // check if node is on open list with a lower cost
          if (open.node.equals(child.node) && open.f < child.f) {
            continue NODE_LOOP;
          }
        }

        for (PathFinder.NodeWrapper closed :
            closedList) { // check is node is on closed list ith lower cost
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
  public static double euclideanDistance(@NonNull Node node1, @NonNull Node node2) {
    int x1 = node1.getXCoord();
    int x2 = node2.getXCoord();
    int y1 = node1.getYCoord();
    int y2 = node2.getYCoord();
    int dx = x1 - x2;
    int dy = y1 - y2;
    return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
  }
}
