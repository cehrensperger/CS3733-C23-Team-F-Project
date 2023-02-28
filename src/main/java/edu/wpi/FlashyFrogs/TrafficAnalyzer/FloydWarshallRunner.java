package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.AStar;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Semaphore;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hibernate.Session;

/**
 * Runner for Floyd-Warshall, contains static variables for caching and variables to get the results
 */
public class FloydWarshallRunner {
  @Getter
  private static Map<Node, Map<Node, Double>>
      costs; // Costs of traveling from one node to another, will be modified

  @Getter
  private static Map<Node, Map<Node, Node>>
      nextHops; // Next hop for a given node to reach another node

  @Getter
  private static final Semaphore reCalculationLock =
      new Semaphore(
          1); // Lock to force waiting on recalculation. Sem for different thread lock/unlock

  private static Thread
      reCalcThread; // Re-calc thread, to prevent duplicate work on many re-calc requests
  private static boolean threadShouldTerminate; // Signal that the thread should terminate
  /**
   * Method to re-calculate the costs and next hops Floyd-Warshall from scratch via Floyd-Warshall
   * and DB queries. The update is run in the background, and holds reCalculationLock while it is
   * progressing
   */
  @SneakyThrows
  public static void reCalculate() {
    // Check the thread status
    if (reCalcThread != null && reCalcThread.isAlive()) {
      threadShouldTerminate = true; // Signal that the thread should terminate
      reCalcThread.join(); // Wait for the re-calc thread to terminate

      // If we hit the race condition where the lock is freed even though we signaled to not free on
      // terminate
      reCalculationLock.tryAcquire(); // Just re-acquire it. Don't block
    } else {
      reCalculationLock.acquire(); // Lock the re-calculation lock, so that the heat map must wait
    }

    reCalcThread =
        new Thread(
            () -> {
              System.out.println("started " + Instant.now());
              // Check the thread status
              if (!threadShouldTerminate) {
                reCalculateEuclideanEdgeWeights(); // Re-calculate the edge weights
              }

              // Check it again
              if (!threadShouldTerminate) {
                doFloydWarshall(); // Run floyd-warshall
              }

              // If the thread has been signaled to end, it will be replaced, so don't release its
              // semaphore
              if (!threadShouldTerminate) {
                reCalculationLock.release(); // Unlock at the end
              }

              System.out.println("ended " + Instant.now());
            });
    reCalcThread.setDaemon(
        true); // Set this to be a daemon, no sense in it forcing the program to stay open
    threadShouldTerminate = false; // Reset thread should not terminate
    reCalcThread.start(); // Start the thread, this may run in the background
  }

  /**
   * Re-calculates costs to be the euclidean edge weights taken directly from the map. This is a
   * blocking operation and very time consuming
   */
  private static void reCalculateEuclideanEdgeWeights() {
    // First, fetch everything
    Collection<Node> nodes; // All nodes
    Collection<Edge> edges; // All edges

    // Populate those. This is much faster than doing each individual node->other nodes with its own
    // query
    try (Session querySession = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      // Get all nodes and edges
      nodes = querySession.createQuery("FROM Node", Node.class).getResultList();
      edges = querySession.createQuery("FROM Edge", Edge.class).getResultList();
    }

    // Pre-size for speed
    costs = new HashMap<>(nodes.size());
    nextHops = new HashMap<>(nodes.size());

    // For each edge
    for (Edge edge : edges) {
      // If the costs don't have the start node
      if (!costs.containsKey(edge.getNode1())) {
        // Save stuff
        costs.put(edge.getNode1(), new HashMap<>(nodes.size())); // Save the costs
        nextHops.put(edge.getNode1(), new HashMap<>(nodes.size())); // Save the next hops
      }

      // If the costs don't have the start node
      if (!costs.containsKey(edge.getNode2())) {
        costs.put(edge.getNode2(), new HashMap<>(nodes.size())); // Save the costs
        nextHops.put(edge.getNode2(), new HashMap<>(nodes.size())); // Save the next hops
      }

      double cost = AStar.euclideanDistance(edge.getNode1(), edge.getNode2()); // Calculate the
      costs.get(edge.getNode1()).put(edge.getNode2(), cost); // Save costs
      costs.get(edge.getNode2()).put(edge.getNode1(), cost); // Save costs
      nextHops.get(edge.getNode1()).put(edge.getNode2(), edge.getNode2()); // Next hop
      nextHops.get(edge.getNode2()).put(edge.getNode1(), edge.getNode1()); // Next hop
    }
  }

  /**
   * Runs Floyd-Warshall. This will modify the costs map, and completely re-generate the next hops
   * map. This is a long, blocking, time consuming operation
   */
  private static void doFloydWarshall() {
    // Run FW, basically for each node i (yes the middle one) check if k is a shortcut to get to j
    // as opposed to
    // going directly
    for (Node k : costs.keySet()) {
      for (Node i : costs.keySet()) {
        for (Node j : costs.keySet()) {
          // If the thread should terminate
          if (threadShouldTerminate) {
            return; // Just exit
          }

          // If the edge doesn't exist (i-> k or k -> j), just skip
          if (costs.get(i).get(k) == null || costs.get(k).get(j) == null) {
            continue; // Skip
          }

          // Compute the new potential cost
          double newPotentialCosts = costs.get(i).get(k) + costs.get(k).get(j);

          // If we haven't found a path i->j yet or this path is shorter
          if (costs.get(i).get(j) == null || costs.get(i).get(j) > newPotentialCosts) {
            // Save the cost
            costs.get(i).put(j, newPotentialCosts); // Save it
            nextHops.get(i).put(j, nextHops.get(i).get(k)); // i will now go to k to get to j
          }
        }
      }
    }
  }
}
