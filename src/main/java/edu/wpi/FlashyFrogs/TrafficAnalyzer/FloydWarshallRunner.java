package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.AStar;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.hibernate.Session;

/**
 * Runner for Floyd-Warshall, contains static variables for caching and variables to get the results
 */
public class FloydWarshallRunner {
  private static Map<Node, Map<Node, Double>>
      costs; // Costs of traveling from one node to another, will be modified
  private static Map<Node, Map<Node, Node>>
      nextHops; // Next hop for a given node to reach another node
  private static List<Edge> edges1;
  private static List<Edge> edges2;
  private static List<Long> counts1;
  private static List<Long> counts2;
  private static List<List<Edge>> edgeses;
  private static ThreadPoolExecutor executor;

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
                generateLists();
                executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(edgeses.size());
              }

              // Check the thread status
              if (!threadShouldTerminate) {
                reCalculateEuclideanEdgeWeights(); // Re-calculate the edge weights
              }

              // Check it again
              if (!threadShouldTerminate && executor.isTerminated()) {
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

  private static void generateLists() {
    try (Session querySession = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      edges1 =
          querySession
              .createQuery("Select e from Edge e order by e.node1.id", Edge.class)
              .getResultList();
      edges2 =
          querySession
              .createQuery("Select e from Edge e order by e.node2.id", Edge.class)
              .getResultList();
      counts1 =
          querySession
              .createQuery(
                  "Select count(e) from Edge e, Node n where e.node1 = n group by e.node1.id order by e.node1.id",
                  Long.class)
              .getResultList();
      counts2 =
          querySession
              .createQuery(
                  "Select count(e) from Edge e, Node n where e.node2 = n group by e.node2.id order by e.node2.id",
                  Long.class)
              .getResultList();

      edgeses = new ArrayList<>();

      while (edges1.size() > 0 || edges2.size() > 0) {
        List<Edge> sortedEdges = new ArrayList<>();
        if (edges1.size() > 0) {
          for (int i = 0; i < counts1.get(0); i++) {
            sortedEdges.add(edges1.remove(0));
          }
          edgeses.add(sortedEdges);
          counts1.remove(0);
        }

        if (edges2.size() > 0) {
          for (int i = 0; i < counts2.get(0); i++) {
            sortedEdges.add(edges2.remove(0));
          }
          edgeses.add(sortedEdges);
          counts2.remove(0);
        }
      }
    }
  }

  /**
   * Re-calculates costs to be the euclidean edge weights taken directly from the map. This is a
   * blocking operation and very time consuming
   */
  @SneakyThrows
  private static void reCalculateEuclideanEdgeWeights() {
    costs = new HashMap<>(edgeses.size());
    nextHops = new HashMap<>(edgeses.size());

    for (int i = 0; i < edgeses.size(); i++) {
      int finalI = i;
      executor.execute(
          () -> {
            // For each edge
            for (Edge edge : edgeses.get(finalI)) {
              // If the costs don't have the start node
              if (!costs.containsKey(edge.getNode1())) {
                // Save stuff
                costs.put(edge.getNode1(), new HashMap<>(edgeses.size())); // Save the costs
                nextHops.put(edge.getNode1(), new HashMap<>(edgeses.size())); // Save the next hops
              }

              // If the costs don't have the start node
              if (!costs.containsKey(edge.getNode2())) {
                costs.put(edge.getNode2(), new HashMap<>(edgeses.size())); // Save the costs
                nextHops.put(edge.getNode2(), new HashMap<>(edgeses.size())); // Save the next hops
              }

              double cost =
                  AStar.euclideanDistance(edge.getNode1(), edge.getNode2()); // Calculate the
              costs.get(edge.getNode1()).put(edge.getNode2(), cost); // Save costs
              costs.get(edge.getNode2()).put(edge.getNode1(), cost); // Save costs
              nextHops.get(edge.getNode1()).put(edge.getNode2(), edge.getNode2()); // Next hop
              nextHops.get(edge.getNode2()).put(edge.getNode1(), edge.getNode1()); // Next hop
            }
          });
    }
    executor.shutdown();

    try {
      executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      System.out.println("Execution Interrupted");
      throw e;
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

  /**
   * Reconstructs the path from node one to node two using the current state of Floyd Warshall
   *
   * @param nodeOne the starting node
   * @param nodeTwo the ending node
   * @return the ending node
   * @throws NullPointerException if the path couldn't be found
   */
  @NonNull
  public static List<Node> reconstructPath(@NonNull Node nodeOne, @NonNull Node nodeTwo) {
    List<Node> result = new LinkedList<>(); // Result list
    Node nextHop = nodeOne; // The next hop in the path

    // While the node isn't the target node
    while (!nextHop.equals(nodeTwo)) {
      result.add(nextHop); // Add the result to the path

      nextHop =
          nextHops.get(nextHop).get(nodeTwo); // Get the next hop on the path to the destination
    }

    return result; // Return the targeted path
  }
}
