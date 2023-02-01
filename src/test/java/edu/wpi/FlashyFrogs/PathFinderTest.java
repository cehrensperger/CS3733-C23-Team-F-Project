package edu.wpi.FlashyFrogs;

import static org.junit.jupiter.api.Assertions.*;

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

  /** Teardown method, closes the session and cleans up the tables */
  @AfterEach
  public void teardownSession() {
    Transaction deleteTransaction = testSession.beginTransaction(); // Clearing transaction

    testSession.createMutationQuery("DELETE FROM Edge").executeUpdate(); // Drop edge
    testSession.createMutationQuery("DELETE FROM Move").executeUpdate(); // Drop move
    testSession.createMutationQuery("DELETE FROM LocationName").executeUpdate(); // Drop location
    testSession.createMutationQuery("DELETE FROM Node").executeUpdate(); // Drop node

    deleteTransaction.commit(); // Commit the transaction

    testSession.close(); // Close the session
  }

  /** Tests that a start location does not exist throws a NullPointerException */
  @Test
  public void startLocationDoesNotExistTest() {
    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder

    assertThrows(
        NullPointerException.class,
        () -> pathFinder.findPath("a", "b")); // Assert that a null pointer exception is thrown
  }

  /**
   * Tests that when a start location does exist but the end does not, a NullPointerException is
   * still thrown
   */
  @Test
  public void endLocationDoesNotExistTest() {
    // Start location
    LocationName startLocation = new LocationName("start", LocationName.LocationType.INFO, "s");

    Node startNode = new Node("s", "b", Node.Floor.G, 0, 0); // Start node

    // Create the move relating the two
    Move startMove = new Move(startNode, startLocation, new Date());

    Transaction commitTransaction =
        testSession.beginTransaction(); // Create a transaction to commit with
    testSession.persist(startLocation); // Persist the start location
    testSession.persist(startNode); // Persist the start node
    testSession.persist(startMove); // Persist the start move
    commitTransaction.commit(); // Commit the changes

    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder

    // Assert that the second node results in an exception
    assertThrows(NullPointerException.class, () -> pathFinder.findPath("start", "doesnotexist)"));
  }

  /**
   * Test for a location where the start location exists but the move backing it to a node does not
   * exist
   */
  @Test
  public void startMoveDoesNotExistTest() {
    LocationName startLocation = new LocationName("start", LocationName.LocationType.INFO, "s");

    LocationName endLocation = new LocationName("end", LocationName.LocationType.INFO, "e");

    // Commit the names
    Transaction commitTransaction = testSession.beginTransaction(); // Transaction
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    commitTransaction.commit(); // Commit the transaction

    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder

    // Assert that finding the path throws an exception
    assertThrows(
        NullPointerException.class,
        () -> pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /** Test for a case where the end move does not exist */
  @Test
  public void endMoveDoesNotExistTest() {
    LocationName startLocation = new LocationName("start", LocationName.LocationType.INFO, "s");

    LocationName endLocation = new LocationName("end", LocationName.LocationType.INFO, "e");

    // Create the start node
    Node startNode = new Node("startNode", "b", Node.Floor.G, 0, 0);

    // Create a move relating the start node to the start
    Move startToStartNode = new Move(startNode, startLocation, new Date());

    // Commit the names
    Transaction commitTransaction = testSession.beginTransaction(); // Transaction
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startNode);
    testSession.persist(startToStartNode);
    commitTransaction.commit(); // Commit the transaction

    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder

    // Assert that finding the path throws an exception
    assertThrows(
        NullPointerException.class,
        () -> pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /** Tests for a case where the nodes are valid, however the path to the end does not exist */
  @Test
  public void pathDoesNotExistTest() {
    // Nodes
    Node nodeOne = new Node("start", "something", Node.Floor.G, 0, 0);
    Node nodeTwo = new Node("end", "something", Node.Floor.THREE, 100, 100);

    // Locations for the nodes
    LocationName startLocation = new LocationName("a", LocationName.LocationType.SERV, "");
    LocationName endLocation = new LocationName("b", LocationName.LocationType.SERV, "");

    // Moves
    Move startLocationToNode = new Move(nodeOne, startLocation, new Date());
    Move endLocationToNode = new Move(nodeTwo, endLocation, new Date());

    // Do the inserts
    Transaction commitTransaction = testSession.beginTransaction(); // Create transaction
    testSession.persist(nodeOne);
    testSession.persist(nodeTwo);
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startLocationToNode);
    testSession.persist(endLocationToNode);
    commitTransaction.commit(); // Commit the transaction

    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder

    // Assert the path returns null
    assertNull(pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /** Tests for a line of nodes, ensures that the algorithm reaches the end */
  @Test
  public void lineOfNodesTest() {
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

    // Create the expected result list
    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(nodeOne);
    expectedResult.add(nodeTwo);
    expectedResult.add(nodeThree);
    expectedResult.add(nodeFour);
    expectedResult.add(nodeFive);

    // Find the path and validate it
    PathFinder pathFinder = new PathFinder(sessionFactory);
    assertEquals(
        expectedResult, pathFinder.findPath(startName.getLongName(), endName.getLongName()));
  }

  /**
   * Tests that when multiple possible moves are available to represent a Node from a location, the
   * most recent is picked
   */
  @Test
  public void picksCorrectMoveTest() {
    // Create the real start and end
    Node realStart = new Node("realStart", "b", Node.Floor.G, 0, 0);
    Node realEnd = new Node("realEnd", "b", Node.Floor.G, 1, 1);

    // Bad start and end
    Node badStart = new Node("badStart", "g", Node.Floor.L2, 50, 50);
    Node badEnd = new Node("badEnd", "g", Node.Floor.L2, 100, 100);

    // Another bad start and end
    Node otherBadStart = new Node("otherBadStart", "n", Node.Floor.ONE, -100, -100);
    Node otherBadEnd = new Node("otherBadEnd", "n", Node.Floor.TWO, -50, -50);

    // Create the edges
    Edge realEdge = new Edge(realStart, realEnd);
    Edge badEdgeOne = new Edge(badStart, badEnd);
    Edge badEdgeTwo = new Edge(otherBadStart, otherBadEnd);

    // Location names
    LocationName start = new LocationName("begin", LocationName.LocationType.INFO, "b");
    LocationName end = new LocationName("terminate", LocationName.LocationType.SERV, "t");

    // Allocate the moves, in order from oldest to newest
    Move badStartOne =
        new Move(otherBadStart, start, Date.from(Instant.ofEpochSecond(1))); // Old start
    Move badEndOne = new Move(otherBadEnd, start, Date.from(Instant.ofEpochSecond(5))); // Old end

    Move badStartTwo =
        new Move(badStart, start, Date.from(Instant.ofEpochSecond(1000))); // Old (newer) start
    Move badEndTwo =
        new Move(badEnd, end, Date.from(Instant.ofEpochSecond(1005))); // Old (newer) start

    Move actualStart =
        new Move(realStart, start, Date.from(Instant.ofEpochSecond(100000))); // Real start
    Move actualEnd = new Move(realEnd, end, Date.from(Instant.ofEpochSecond(100050))); // Real end

    // Commit stuff to the DB
    Transaction commitTransaction =
        testSession.beginTransaction(); // Create the transaction to use in generation
    testSession.persist(realStart);
    testSession.persist(realEnd);
    testSession.persist(badStart);
    testSession.persist(badEnd);
    testSession.persist(otherBadStart);
    testSession.persist(otherBadEnd);
    testSession.persist(realEdge);
    testSession.persist(badEdgeOne);
    testSession.persist(badEdgeTwo);
    testSession.persist(start);
    testSession.persist(end);
    testSession.persist(badStartOne);
    testSession.persist(badEndOne);
    testSession.persist(badStartTwo);
    testSession.persist(badEndTwo);
    testSession.persist(actualStart);
    testSession.persist(actualEnd);
    commitTransaction.commit(); // Commit the transaction

    // Expected result list
    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(realStart); // Real start
    expectedResult.add(realEnd); // To real end

    // Create the PathFinder
    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder
    assertEquals(
        expectedResult,
        pathFinder.findPath(start.getLongName(), end.getLongName())); // Check the path
  }

  /** Tests that paths are bidirectional, e.g., that a path from end->start works */
  @Test
  public void biDirectionalPathTest() {
    // Create the nodes
    Node startNode = new Node("s", "P", Node.Floor.L2, 0, 0);
    Node endNode = new Node("e", "P", Node.Floor.THREE, 0, 0);

    // Create the edge
    Edge wrongWayEdge = new Edge(endNode, startNode); // Create the edge

    // Locations
    LocationName startLocation = new LocationName("s", LocationName.LocationType.SERV, "s");
    LocationName endLocation = new LocationName("e", LocationName.LocationType.INFO, "e");

    // Moves
    Move startMove = new Move(startNode, startLocation, new Date());
    Move endMove = new Move(endNode, endLocation, new Date());

    // Create a transaction to put the stuff into the DB
    Transaction commitTransaction = testSession.beginTransaction(); // Create a transaction to use
    testSession.persist(startNode);
    testSession.persist(endNode);
    testSession.persist(wrongWayEdge);
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startMove);
    testSession.persist(endMove);
    commitTransaction.commit(); // Commit the transaction

    // Expected list of nodes
    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(startNode);
    expectedResult.add(endNode);

    // Assert that the path is valid
    PathFinder pathFinder = new PathFinder(sessionFactory);
    assertEquals(
        expectedResult,
        pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /**
   * Tests that the database takes the latest state for pathfinding rather than the state at its
   * creation time
   */
  @Test
  public void takesLatestDatabaseStateTest() {
    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder right away

    // Nodes
    Node start = new Node("start", "b", Node.Floor.G, 0, 0);
    Node end = new Node("end", "b", Node.Floor.G, 10, 10);

    // Edge from start to end
    Edge startToEndEdge = new Edge(start, end);

    // Location names
    LocationName startLocation = new LocationName("start", LocationName.LocationType.INFO, "s");
    LocationName endLocation = new LocationName("end", LocationName.LocationType.SERV, "e");

    // Moves
    Move startMove = new Move(start, startLocation, new Date());
    Move endMove = new Move(end, endLocation, new Date());

    // Commit to DB
    Transaction commitTransaction = testSession.beginTransaction();
    testSession.persist(start);
    testSession.persist(end);
    testSession.persist(startToEndEdge);
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startMove);
    testSession.persist(endMove);
    commitTransaction.commit(); // Commit the transaction

    // Expected path
    List<Node> expectedPath = new LinkedList<>();
    expectedPath.add(start);
    expectedPath.add(end);

    // Assert that the path matches
    assertEquals(
        expectedPath, pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /** Test that when there is a loop before the target, the algorithm still reaches the end */
  @Test
  public void loopTest() {
    // Nodes with a loop
    Node root = new Node("root", "b", Node.Floor.L2, 0, 0);
    Node one = new Node("one", "b", Node.Floor.L2, 0, 1);
    Node two = new Node("two", "b", Node.Floor.L2, 1, 1);
    Node target = new Node("target", "b", Node.Floor.L2, 0, 2);

    // Edges for the loop. Two links back to the root
    Edge rootToOne = new Edge(root, one);
    Edge oneToTwo = new Edge(one, two);
    Edge twoToRoot = new Edge(two, root);
    Edge twoToTarget = new Edge(two, target);

    // Names for start and end
    LocationName startName = new LocationName("start", LocationName.LocationType.INFO, "s");
    LocationName endName = new LocationName("end", LocationName.LocationType.INFO, "e");

    // Moves for start and end
    Move startMove = new Move(root, startName, new Date());
    Move endMove = new Move(target, endName, new Date());

    // Commit to DB
    Transaction commitTransaction = testSession.beginTransaction();
    ; // Start the transaction
    testSession.persist(root);
    testSession.persist(one);
    testSession.persist(two);
    testSession.persist(target);
    testSession.persist(rootToOne);
    testSession.persist(oneToTwo);
    testSession.persist(twoToRoot);
    testSession.persist(twoToTarget);
    testSession.persist(startName);
    testSession.persist(endName);
    testSession.persist(startMove);
    testSession.persist(endMove);
    commitTransaction.commit(); // Commit the DB

    // Expected result = root, two, target
    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(root);
    expectedResult.add(two);
    expectedResult.add(target);

    // Create the pathfinder and check the path
    PathFinder pathFinder = new PathFinder(sessionFactory);
    assertEquals(
        expectedResult, pathFinder.findPath(startName.getLongName(), endName.getLongName()));
  }

  /**
   * Tests that the algorithm will prefer a shorter distance (e.g., heuristic) over a path that is
   * technically shorter (fewer nodes)
   */
  @Test
  public void takesShorterPathTest() {
    // Root node
    Node root = new Node("root", "building", Node.Floor.L2, 0, 0);

    // Left path, very close
    Node leftRoot = new Node("leftRoot", "building", Node.Floor.L2, 1, 1);
    Node leftMiddle = new Node("leftMiddle", "building", Node.Floor.L2, 2, 2);

    // Right path, very long
    Node rightNode = new Node("right", "building", Node.Floor.L2, -50, 50);

    // Target Node
    Node endNode = new Node("end", "building", Node.Floor.L2, 3, 0);

    // Create the edges connecting these
    Edge rootToLeftRoot = new Edge(root, leftRoot);
    Edge leftRootToLeftMiddle = new Edge(leftRoot, leftMiddle);
    Edge leftToEnd = new Edge(leftMiddle, endNode);
    Edge rootToRight = new Edge(root, rightNode);
    Edge rightToEnd = new Edge(rightNode, endNode);

    // Location names
    LocationName startName = new LocationName("start", LocationName.LocationType.SERV, "st");
    LocationName endName = new LocationName("end", LocationName.LocationType.DEPT, "ed");

    // Moves relating the start to its name, and end to its name
    Move start = new Move(root, startName, Date.from(Instant.now()));
    Move end = new Move(endNode, endName, Date.from(Instant.now()));

    // Persist everything
    Transaction commitTransaction = testSession.beginTransaction(); // Get a transaction to use
    testSession.persist(root);
    testSession.persist(leftRoot);
    testSession.persist(leftMiddle);
    testSession.persist(rightNode);
    testSession.persist(endNode);
    testSession.persist(rootToLeftRoot);
    testSession.persist(leftRootToLeftMiddle);
    testSession.persist(leftToEnd);
    testSession.persist(rootToRight);
    testSession.persist(rightToEnd);
    testSession.persist(startName);
    testSession.persist(endName);
    testSession.persist(start);
    testSession.persist(end);
    commitTransaction.commit(); // Commit the commit transaction

    // Create the expected result list
    List<Node> resultList = new LinkedList<>();
    resultList.add(root);
    resultList.add(leftRoot);
    resultList.add(leftMiddle);
    resultList.add(endNode);

    PathFinder pathFinder = new PathFinder(sessionFactory); // Create the path finder to use
    assertEquals(
        resultList,
        pathFinder.findPath(startName.getLongName(), endName.getLongName())); // Find the path
  }

  /**
   * Test that the program takes a shorter path with bogus branches and more paths. Also contains
   * loops
   */
  @Test
  public void takesShorterPathWithBogusBranchesTest() {
    // Root Node
    Node root = new Node("root", "b", Node.Floor.L2, 0, 0);

    // Upper path, this is shorter
    Node upper = new Node("upper", "b", Node.Floor.L2, 0, 1);
    Edge rootToUpper = new Edge(root, upper);
    Node upperDecoy = new Node("upperDecoy", "b", Node.Floor.L2, 0, 2);
    Edge upperToUpperDecoy = new Edge(upper, upperDecoy);
    Node upper2 = new Node("upper2", "b", Node.Floor.L2, 0, 3);
    Edge upperToUpper2 = new Edge(upper, upper2);
    Node upper2Decoy = new Node("upper2Decoy", "b", Node.Floor.L2, 1, 4);
    Edge upper2ToUpper2Decoy = new Edge(upper2, upper2Decoy);
    Node target = new Node("target", "b", Node.Floor.L2, 0, 4);
    Edge upper2ToTarget = new Edge(upper2, target);

    // Upper branch that leads nowhere
    Node upperBranchTwo = new Node("upperToNowhere", "b", Node.Floor.L2, 1, 1);
    Edge rootToUpperBranch2 = new Edge(root, upperBranchTwo);

    // Upper branch that loops around
    Node longerUpperLoop = new Node("longerUpper", "b", Node.Floor.L2, -1, 1);
    Edge rootToLongerUpperLoop = new Edge(root, longerUpperLoop);
    Node longerUpperLoop2 = new Node("longerUpper2", "b", Node.Floor.L2, -1, 10);
    Edge longerUpperLoopToLongerUpperLoop2 = new Edge(longerUpperLoop, longerUpperLoop2);
    Node longerUpperLoop3 = new Node("longerUpper3", "b", Node.Floor.L2, -1, 20);
    Edge longerUpperLoop2ToLongerUpperLoop3 = new Edge(longerUpperLoop2, longerUpperLoop3);
    Node extraNode = new Node("longerUpperExtra", "b", Node.Floor.L2, 5, 50);
    Edge longerUpperLoop3ToExtraNode = new Edge(longerUpperLoop3, extraNode);
    Edge longerUpperLoop3ToTarget = new Edge(longerUpperLoop3, target);
    Edge extraNodeToRoot = new Edge(extraNode, root);

    // Left path, short but bad
    Node leftBad = new Node("left", "b", Node.Floor.L2, -50, 0);
    Edge rootToLeftBad = new Edge(leftBad, root);
    Node leftFork = new Node("leftFork", "b", Node.Floor.L2, -51, 0);
    Edge rootToLeftFork = new Edge(leftBad, leftFork);
    Edge leftBadToTarget = new Edge(leftBad, target);

    // Right path, long and bad but starts close
    Node rightBad = new Node("right", "b", Node.Floor.L2, 1, 0);
    Edge rootToRightBad = new Edge(root, rightBad);
    Node right2 = new Node("right2", "b", Node.Floor.L2, 2, 0);
    Edge rightBadToRight2 = new Edge(rightBad, right2);
    Edge right2ToExtraNode = new Edge(right2, extraNode);
    Node rightEnd = new Node("rightEnd", "b", Node.Floor.L2, 100, 0);
    Edge right2ToRightEnd = new Edge(right2, rightEnd);
    Edge rightEndToTarget = new Edge(rightEnd, target);

    // Node to nowhere
    Node bottomToNowhere = new Node("bottom", "b", Node.Floor.L2, 0, -10);
    Edge rootToBottomToNowhere = new Edge(bottomToNowhere, root);
    Node bottomToNowhere2 = new Node("bottomToNowhere2", "b", Node.Floor.L2, 1, -10);
    Edge rootToBottomToNowhere2 = new Edge(root, bottomToNowhere2);
    Node bottomChain = new Node("bottomChain1", "b", Node.Floor.L2, -1, -1);
    Edge rootToBottomChain = new Edge(root, bottomChain);
    Node bottomChain2 = new Node("bottomChain2", "b", Node.Floor.L2, -1, -10);
    Edge bottomChainToBottomChain2 = new Edge(bottomChain, bottomChain2);
    Node bottomChain3 = new Node("bottomChain3", "b", Node.Floor.L2, -1, -100);
    Edge bottomChain2ToBottomChain3 = new Edge(bottomChain2, bottomChain3);

    // Commit to DB
    Transaction commitTransaction = testSession.beginTransaction(); // Create the transaction
    testSession.persist(root);
    testSession.persist(upper);
    testSession.persist(rootToUpper);
    testSession.persist(upperDecoy);
    testSession.persist(upperToUpperDecoy);
    commitTransaction.commit();
  }
}
