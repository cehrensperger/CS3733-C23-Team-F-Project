package edu.wpi.FlashyFrogs.PathFinding;

import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.NonNull;
import org.hibernate.Session;

public class BreadthFirst implements IFindPath {
  public List<Node> findPath(@NonNull Node start, @NonNull Node end, @NonNull Session session) {
    List<PathFinder.NodeWrapper> queue = new LinkedList<>();
    List<Node> nodeQueue = new LinkedList<>();
    List<Node> visited = new LinkedList<>();
    PathFinder.NodeWrapper currentNodeWrapper = new PathFinder.NodeWrapper(start, null);
    queue.add(currentNodeWrapper);
    nodeQueue.add(start);
    while (!visited.contains(end) && !queue.isEmpty()) {
      List<Node> neighbors =
          PathFinder.getNeighbors(currentNodeWrapper.node, session).stream().toList();
      for (Node node : neighbors) {
        if (!visited.contains(node)
            && !nodeQueue.contains(node)
            && !node.equals(
                currentNodeWrapper
                    .node)) { // if the node wasn't already visited and isn't already queued
          queue.add(new PathFinder.NodeWrapper(node, currentNodeWrapper));
          nodeQueue.add(node);
        }
      }
      visited.add(currentNodeWrapper.node);
      queue.remove(currentNodeWrapper);
      nodeQueue.remove(currentNodeWrapper.node);
      if (!queue.isEmpty()) {
        currentNodeWrapper = queue.get(0);
      }
    }
    List<Node> path = new LinkedList<>();
    if (visited.contains(end)) {
      while (currentNodeWrapper.parent != null) {
        path.add(currentNodeWrapper.node);
        currentNodeWrapper = currentNodeWrapper.parent;
      }
      path.add(start);
      Collections.reverse(path);
      return path;
    } else {
      return null;
    }
  }
}
