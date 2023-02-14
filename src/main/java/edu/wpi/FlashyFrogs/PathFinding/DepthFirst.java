package edu.wpi.FlashyFrogs.PathFinding;

import static edu.wpi.FlashyFrogs.PathFinding.PathFinder.getNeighbors;

import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.hibernate.Session;

public class DepthFirst implements IFindPath {
  public List<Node> findPath(@NonNull Node start, @NonNull Node end, @NonNull Session session) {
    Stack<Node> stack = new Stack<>(); // create stack
    List<Node> visited = new LinkedList<>(); // create list for nodes that have been visited
    stack.push(start); // push start node to stack
    visited.add(
        start); // mark start node as visited: so the while loop won't bother executing if start
    // node is destination node
    while (!visited.contains(end) && !stack.isEmpty()) {
      List<Node> neighbors =
          getNeighbors(stack.peek(), session).stream().collect(Collectors.toList());
      boolean noUnvisitedVerticesReachable = true;
      for (Node node : neighbors) {
        if (!visited.contains(node)) { // if the node wasn't already visited
          noUnvisitedVerticesReachable = false;
          stack.push(node);
          visited.add(node);
          break;
        }
      }
      if (noUnvisitedVerticesReachable) {
        stack.pop();
      }
    }
    if (!visited.contains(end)) { // we ended because stack empty, not because we found a path
      return null;
    } else {
      return stack.stream().collect(Collectors.toList());
    }
  }
}
