package edu.wpi.FlashyFrogs;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

/**
 * Tests for the PathFinder class, tests to ensure that it finds a valid path, and that it always
 * finds the shortest path. Uses transactions to ensure test atomicity where required, along with
 * separate sessions for each test. Uses a separate test schema and makes no changes to the actual
 * database
 */
public class PathFinderTest {
  private Session testSession; // Session to be used for each individual test

  /**
   * Setup method to be run before all tests that creates the session factory and service registry
   */
  @BeforeAll
  public static void setupSessionFactory() {
    // Create the DB connection
    DBConnection.CONNECTION.connect();
  }

  /**
   * Teardown method to be run after all tests. Cleans up the service registry and session factory
   */
  @AfterAll
  public static void closeSessionFactory() {
    DBConnection.CONNECTION.disconnect();
  }

  /** Setup method, sets up the session before each test */
  @BeforeEach
  public void setupSession() {
    testSession = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
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
    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder

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

    Node startNode = new Node("s", "b", Node.Floor.L2, 0, 0); // Start node

    // Create the move relating the two
    Move startMove = new Move(startNode, startLocation, new Date());

    Transaction commitTransaction =
        testSession.beginTransaction(); // Create a transaction to commit with
    testSession.persist(startLocation); // Persist the start location
    testSession.persist(startNode); // Persist the start node
    testSession.persist(startMove); // Persist the start move
    commitTransaction.commit(); // Commit the changes

    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder

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

    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder

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
    Node startNode = new Node("startNode", "b", Node.Floor.THREE, 0, 0);

    // Create a move relating the start node to the start
    Move startToStartNode = new Move(startNode, startLocation, new Date());

    // Commit the names
    Transaction commitTransaction = testSession.beginTransaction(); // Transaction
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startNode);
    testSession.persist(startToStartNode);
    commitTransaction.commit(); // Commit the transaction

    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder

    // Assert that finding the path throws an exception
    assertThrows(
        NullPointerException.class,
        () -> pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /** Tests for a case where the nodes are valid, however the path to the end does not exist */
  @Test
  public void pathDoesNotExistTest() {
    // Nodes
    Node nodeOne = new Node("start", "something", Node.Floor.TWO, 0, 0);
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

    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder

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
    PathFinder pathFinder = new PathFinder(testSession);
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
    Node realStart = new Node("realStart", "b", Node.Floor.L2, 0, 0);
    Node realEnd = new Node("realEnd", "b", Node.Floor.L1, 1, 1);

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
    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder
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
    PathFinder pathFinder = new PathFinder(testSession);
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
    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder right away

    // Nodes
    Node start = new Node("start", "b", Node.Floor.THREE, 0, 0);
    Node end = new Node("end", "b", Node.Floor.TWO, 10, 10);

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
    // Start the transaction
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
    PathFinder pathFinder = new PathFinder(testSession);
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

    PathFinder pathFinder = new PathFinder(testSession); // Create the path finder to use
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

    // Location names
    LocationName startLocation = new LocationName("start", LocationName.LocationType.INFO, "s");
    LocationName endLocation = new LocationName("end", LocationName.LocationType.INFO, "e");

    // Moves
    Move startMove = new Move(root, startLocation, new Date());
    Move endMove = new Move(target, endLocation, new Date());

    // Commit to DB
    Transaction commitTransaction = testSession.beginTransaction(); // Create the transaction
    testSession.persist(root);
    testSession.persist(upper);
    testSession.persist(rootToUpper);
    testSession.persist(upperDecoy);
    testSession.persist(upperToUpperDecoy);
    testSession.persist(upper2);
    testSession.persist(upperToUpper2);
    testSession.persist(upper2Decoy);
    testSession.persist(upper2ToUpper2Decoy);
    testSession.persist(target);
    testSession.persist(upper2ToTarget);
    testSession.persist(upperBranchTwo);
    testSession.persist(rootToUpperBranch2);
    testSession.persist(longerUpperLoop);
    testSession.persist(rootToLongerUpperLoop);
    testSession.persist(longerUpperLoop2);
    testSession.persist(longerUpperLoopToLongerUpperLoop2);
    testSession.persist(longerUpperLoop3);
    testSession.persist(longerUpperLoop3);
    testSession.persist(longerUpperLoop2ToLongerUpperLoop3);
    testSession.persist(extraNode);
    testSession.persist(longerUpperLoop3ToExtraNode);
    testSession.persist(longerUpperLoop3ToTarget);
    testSession.persist(extraNodeToRoot);
    testSession.persist(leftBad);
    testSession.persist(rootToLeftBad);
    testSession.persist(leftFork);
    testSession.persist(rootToLeftFork);
    testSession.persist(leftBadToTarget);
    testSession.persist(rightBad);
    testSession.persist(rootToRightBad);
    testSession.persist(right2);
    testSession.persist(rightBadToRight2);
    testSession.persist(right2ToExtraNode);
    testSession.persist(rightEnd);
    testSession.persist(right2ToRightEnd);
    testSession.persist(rightEndToTarget);
    testSession.persist(bottomToNowhere);
    testSession.persist(rootToBottomToNowhere);
    testSession.persist(bottomToNowhere2);
    testSession.persist(rootToBottomToNowhere2);
    testSession.persist(bottomChain);
    testSession.persist(rootToBottomChain);
    testSession.persist(bottomChain2);
    testSession.persist(bottomChainToBottomChain2);
    testSession.persist(bottomChain3);
    testSession.persist(bottomChain2ToBottomChain3);
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startMove);
    testSession.persist(endMove);
    commitTransaction.commit();

    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(root);
    expectedResult.add(upper);
    expectedResult.add(upper2);
    expectedResult.add(target);

    // Check that the path finder finds the right path
    PathFinder pathFinder = new PathFinder(testSession);
    assertEquals(
        expectedResult,
        pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /**
   * Tests a series of tests where random start and end locations are picked in the actual map (read
   * from the CSV Parser), and then the paths are validate to *make sense*
   *
   * @return dynamic tests according to the above. These use random seeded the same way every time
   *     so they are repeatable
   */
  @TestFactory
  public Stream<DynamicTest> testRandomPathsInMap() {
    // Create a random generator
    Random randomGenerator = new Random();
    randomGenerator.setSeed(1); // Seed random the same way every time

    // Read in the CSVs from the path finder
    try {
      CSVParser.readFiles(
          new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/L1Nodes.csv"),
          new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/L1Edges.csv"),
          new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/locationName.csv"),
          new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/move.csv"),
          DBConnection.CONNECTION.getSessionFactory());
    } catch (FileNotFoundException fileError) {
      fail(fileError); // If we get a file error, just fail with that as the reasoning
    }

    // Tests 1->10
    return IntStream.range(1, 2)
        .mapToObj(
            testNumber ->
                DynamicTest.dynamicTest(
                    "Map Test " + testNumber,
                    () -> {
                      Transaction transaction = testSession.beginTransaction();
                      List<LocationName> locationList =
                          testSession
                              .createQuery("FROM LocationName", LocationName.class)
                              .getResultList();

                      transaction.commit();

                      // Get a random number of nodes to skip to get a random one
                      long randomNumberToSkip = randomGenerator.nextLong(locationList.size());

                      // Get a random location
                      LocationName startLocation =
                          locationList.stream().skip(randomNumberToSkip).findFirst().orElseThrow();

                      // Get a random location
                      LocationName endLocation =
                          locationList.stream()
                              .skip(
                                  randomGenerator.nextLong(
                                      randomNumberToSkip + 1, locationList.size()))
                              .findFirst()
                              .orElseThrow();

                      // Create the path finder
                      PathFinder pathFinder = new PathFinder(testSession);

                      // Find the path between the nodes
                      List<Node> result =
                          pathFinder.findPath(
                              startLocation.getLongName(), endLocation.getLongName());

                      // Transaction to get
                      transaction = testSession.beginTransaction();

                      // Get the start location, ensure the first node is that
                      assertEquals(
                          startLocation,
                          testSession
                              .createQuery(
                                  "SELECT location FROM Move " + "WHERE node = :node",
                                  LocationName.class)
                              .setParameter("node", result.get(0))
                              .uniqueResult());

                      // Get the end location, ensure that the first node is that
                      assertEquals(
                          endLocation,
                          testSession
                              .createQuery(
                                  "SELECT location FROM Move " + "WHERE node = :node",
                                  LocationName.class)
                              .setParameter("node", result.get(result.size() - 1))
                              .uniqueResult());

                      // Now check that each node actually has a connection
                      Node lastNode = null; // Last node, start it as null
                      for (Node node : result) { // For each node
                        // If the last node isn't null, check that there's a connection between it
                        // and this node
                        if (lastNode != null) {
                          // Assert that we have an Edge connecting the provided Node in either
                          // direction
                          assertNotNull(
                              testSession
                                  .createQuery(
                                      "FROM Edge WHERE "
                                          + "(node1 = :firstNode AND node2 = :secondNode) OR "
                                          + "(node2 = :secondNode AND node1 = :firstNode)",
                                      Edge.class)
                                  .setParameter("firstNode", node)
                                  .setParameter("secondNode", lastNode));
                        }

                        lastNode = node; // Increment the node
                      }

                      transaction.commit();
                    }));
  }

  @TestFactory
  public Stream<DynamicTest> testNodeListToLocation() {
    return IntStream.range(1, 10)
        .mapToObj(
            testNumber ->
                DynamicTest.dynamicTest(
                    "Node List To Location " + testNumber,
                    () -> {
                      PathFinder pathFinder = new PathFinder(testSession); // Create the path finder

                      // Create a random generator
                      Random randomGenerator = new Random();
                      randomGenerator.setSeed(1); // Seed random the same way every time

                      List<Node> nodes = new ArrayList<>();
                      List<LocationName> locations = new ArrayList<>();

                      Transaction commitTransaction =
                          testSession.beginTransaction(); // Create the transaction
                      for (int i = 0; i < 10; i++) {
                        Node node = Utils.generateRandomNode(randomGenerator);
                        LocationName location = Utils.generateRandomLocation(randomGenerator);
                        Move move = new Move(node, location, new Date());

                        nodes.add(node);
                        locations.add(location);

                        testSession.persist(node);
                        testSession.persist(location);
                        testSession.persist(move);
                      }
                      commitTransaction.commit();

                      List<LocationName> result = pathFinder.nodeListToLocation(nodes, testSession);

                      assertEquals(locations, result);

                      teardownSession();
                      setupSession();
                    }));
  }

  /** Test for having one Node that refers to itself is ignored and results in a valid path */
  @Test
  public void SelfReferentialTest() {
    // Create the nodes
    Node startNode = new Node("a", "b", Node.Floor.L2, 0, 0);
    Node middleNode = new Node("b", "b", Node.Floor.L2, 1, 0);
    Node endNode = new Node("c", "b", Node.Floor.L2, 2, 0);

    // Edges
    Edge startToMiddle = new Edge(startNode, middleNode);
    Edge middleToItself = new Edge(middleNode, middleNode); // Self-referential
    Edge middleToEnd = new Edge(middleNode, endNode);

    // Locations
    LocationName startLocation = new LocationName("start", LocationName.LocationType.INFO, "s");
    LocationName endLocation = new LocationName("end", LocationName.LocationType.ELEV, "e");

    // Moves
    Move startMove = new Move(startNode, startLocation, new Date());
    Move endMove = new Move(endNode, endLocation, new Date());

    // Transaction to commit data
    Transaction commitTransaction = testSession.beginTransaction();
    testSession.persist(startNode);
    testSession.persist(middleNode);
    testSession.persist(endNode);
    testSession.persist(startToMiddle);
    testSession.persist(middleToItself);
    testSession.persist(middleToEnd);
    testSession.persist(startLocation);
    testSession.persist(endLocation);
    testSession.persist(startMove);
    testSession.persist(endMove);
    commitTransaction.commit();

    // Create the expected result, start, middle, end
    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(startNode);
    expectedResult.add(middleNode);
    expectedResult.add(endNode);

    // Check that the result is expected
    PathFinder pathFinder = new PathFinder(testSession);
    assertEquals(
        expectedResult,
        pathFinder.findPath(startLocation.getLongName(), endLocation.getLongName()));
  }

  /** Tests that the algorithm priorities elevators over stairs */
  @Test
  public void floorChangeTest() {
    // Line of nodes A->E, moving apart in Y-Coord
    Node start = new Node("start", "building", Node.Floor.L1, 0, 0);
    Node lowerStair = new Node("lowerStair", "building", Node.Floor.L1, 0, 1);
    Node upperStair = new Node("upperStair", "building", Node.Floor.L2, 0, 1);
    Node lowerElevator = new Node("lowerElevator", "building", Node.Floor.L1, 0, -1);
    Node upperElevator = new Node("upperElevator", "building", Node.Floor.L2, 0, -1);
    Node end = new Node("end", "building", Node.Floor.L2, 0, 0);
    Edge startToStair = new Edge(start, lowerStair);
    Edge startToElevator = new Edge(start, lowerElevator);
    Edge stairs = new Edge(lowerStair, upperStair);
    Edge elevator = new Edge(lowerElevator, upperElevator);
    Edge stairToEnd = new Edge(upperStair, end);
    Edge elevatorToEnd = new Edge(upperElevator, end);

    // Start name
    LocationName startName = new LocationName("start", LocationName.LocationType.CONF, "start");
    LocationName lowerStairName =
        new LocationName("Lower Stair", LocationName.LocationType.STAI, "lowerStair");
    LocationName upperStairName =
        new LocationName("Upper Stair", LocationName.LocationType.STAI, "upperStair");
    LocationName lowerElevatorName =
        new LocationName("Lower Elevator", LocationName.LocationType.ELEV, "lowerElevator");
    LocationName upperElevatorName =
        new LocationName("Upper Elevator", LocationName.LocationType.ELEV, "upperElevator");

    // End name
    LocationName endName = new LocationName("end", LocationName.LocationType.DEPT, "end");

    Move startMove = new Move(start, startName, Date.from(Instant.now()));
    Move lowerStairMove = new Move(lowerStair, lowerStairName, Date.from(Instant.now()));
    Move upperStairMove = new Move(upperStair, upperStairName, Date.from(Instant.now()));
    Move lowerElevatorMove = new Move(lowerElevator, lowerElevatorName, Date.from(Instant.now()));
    Move upperElevatorMove = new Move(upperElevator, upperElevatorName, Date.from(Instant.now()));
    Move endMove = new Move(end, endName, Date.from(Instant.now()));

    // Create a transaction to put stuff into the DB. This is because the PathFinder will only read
    // committed data
    Transaction creationTransaction = testSession.beginTransaction();

    // Save the Nodes and edges
    testSession.persist(start);
    testSession.persist(lowerStair);
    testSession.persist(upperStair);
    testSession.persist(lowerElevator);
    testSession.persist(upperElevator);
    testSession.persist(end);
    testSession.persist(startToStair);
    testSession.persist(startToElevator);
    testSession.persist(stairs);
    testSession.persist(elevator);
    testSession.persist(stairToEnd);
    testSession.persist(elevatorToEnd);
    testSession.persist(startName);
    testSession.persist(lowerStairName);
    testSession.persist(upperStairName);
    testSession.persist(lowerElevatorName);
    testSession.persist(upperElevatorName);
    testSession.persist(endName);
    testSession.persist(startMove);
    testSession.persist(lowerStairMove);
    testSession.persist(upperStairMove);
    testSession.persist(lowerElevatorMove);
    testSession.persist(upperElevatorMove);
    testSession.persist(endMove);

    creationTransaction.commit(); // Commit the data

    // Create the expected result list
    List<Node> expectedResult = new LinkedList<>();
    expectedResult.add(start);
    expectedResult.add(lowerElevator);
    expectedResult.add(upperElevator);
    expectedResult.add(end);

    // Find the path and validate it
    PathFinder pathFinder = new PathFinder(testSession);
    assertEquals(
        expectedResult, pathFinder.findPath(startName.getLongName(), endName.getLongName()));
  }
}
