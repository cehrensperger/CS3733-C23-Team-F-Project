package edu.wpi.FlashyFrogs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;

/**
 * Tests for the PathFinder class, tests to ensure that it finds a valid path, and that it always
 * finds the shortest path. Uses transactions to ensure test atomicity where required, along with
 * separate sessions for each test. Uses a separate test schema and makes no changes to the actual
 * database
 */
public class PathFinderTest {
  private static SessionFactory
      sessionFactory; // Session factory to be created before all tests have run
  private static StandardServiceRegistry
      serviceRegistry; // Service registry associated with the factory
  private Session testSession; // Session to be used for each individual test

  /**
   * Setup method to be run before all tests that creates the session factory and service registry
   */
  @BeforeAll
  public static void setupSessionFactory() {
    // Create the service registry we will use
    serviceRegistry =
        new StandardServiceRegistryBuilder()
            .configure("./edu/wpi/FlashyFrogs/hibernate.cfg.xml") // Load settings
            .build();

    // Create the session factory from that
    sessionFactory = new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
  }

  /**
   * Teardown method to be run after all tests. Cleans up the service registry and session factory
   */
  @AfterAll
  public static void closeSessionFactory() {
    sessionFactory.close(); // Close the session factory
    serviceRegistry.close(); // Close the service registry
  }

  /** Setup method, sets up the session before each test */
  @BeforeEach
  public void setupSessionAndTransaction() {
    testSession = sessionFactory.openSession(); // Open a session
  }

  /** Teardown method, closes the session */
  @AfterEach
  public void teardownSession() {
    testSession.close(); // Close the session
  }

  /** Tests for a line of nodes, ensures that the algorithm reaches the end */
  @Test
  public void testLineOfNodes() {
    // Line of nodes A->E, moving apart in Y-Coord
    Node nodeOne = new Node("a", "building", Node.Floor.L1, 0, 0);
    Node nodeTwo = new Node("b", "building", Node.Floor.L1, 0, 1);
    Edge oneToTwo = new Edge(nodeOne, nodeTwo);
    Node nodeThree = new Node("c", "building", Node.Floor.L1, 0, 2);
    Edge twoToThree = new Edge(nodeTwo, nodeThree);
    Node nodeFour = new Node("d", "building", Node.Floor.L1, 0, 3);
    Edge threeToFour = new Edge(nodeThree, nodeFour);
    Node nodeFive = new Node("e", "building", Node.Floor.L1, 0, 4);
    Edge fourToFive = new Edge(nodeFour, nodeFive);

    // Start name
    LocationName startName = new LocationName("start", LocationName.LocationType.CONF, "start");

    // End name
    LocationName endName = new LocationName("end", LocationName.LocationType.DEPT, "end");

    Move startMove = new Move(nodeOne, startName, Date.from(Instant.now()));
    Move endMove = new Move(nodeFive, endName, Date.from(Instant.now()));

    // Create a transaction to put stuff into the DB. This is because the PathFinder will only read
    // committed data
    Transaction creationTransaction = testSession.beginTransaction();

    // Save the Nodes and edges
    testSession.persist(nodeOne);
    testSession.persist(nodeTwo);
    testSession.persist(oneToTwo);
    testSession.persist(nodeThree);
    testSession.persist(twoToThree);
    testSession.persist(nodeFour);
    testSession.persist(threeToFour);
    testSession.persist(nodeFive);
    testSession.persist(fourToFive);
    testSession.persist(startName);
    testSession.persist(endName);
    testSession.persist(startMove);
    testSession.persist(endMove);

    creationTransaction.commit(); // Commit the data

    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(nodeOne);
    expectedResult.add(nodeTwo);
    expectedResult.add(nodeThree);
    expectedResult.add(nodeFour);
    expectedResult.add(nodeFive);

    PathFinder pathFinder = new PathFinder(sessionFactory);
    assertEquals(
        expectedResult, pathFinder.findPath(startName.getLongName(), endName.getLongName()));

    // Transaction to delete everything
    Transaction deleteTransaction = testSession.beginTransaction();

    // Delete everything
    testSession.remove(nodeOne);
    testSession.remove(nodeTwo);
    testSession.remove(oneToTwo);
    testSession.remove(nodeThree);
    testSession.remove(twoToThree);
    testSession.remove(nodeFour);
    testSession.remove(threeToFour);
    testSession.remove(nodeFive);
    testSession.remove(fourToFive);
    testSession.remove(startName);
    testSession.remove(endName);
    testSession.remove(startMove);
    testSession.remove(endMove);

    // Commit the deletion
    deleteTransaction.commit();
  }
}
