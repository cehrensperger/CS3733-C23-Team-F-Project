package edu.wpi.FlashyFrogs.ORM;

import static java.time.temporal.ChronoUnit.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.time.Instant;
import java.util.Date;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class NodeTest {
  private Session session; // The session to be used for DB connection
  /** Sets up the database connection we will use */
  @BeforeAll
  public static void setupDBConnection() {
    DBConnection.CONNECTION.connect(); // Connect to the DB
  }

  /** Tears down the database connection we used */
  @AfterAll
  public static void teardownDBConnection() {
    DBConnection.CONNECTION.disconnect();
  }

  // Creates iteration of Node
  Node testNode = new Node("Test", "Building", Node.Floor.L2, 0, 1);

  /** Sets up the session to be used for DB connection */
  @BeforeEach
  public void setupSession() {
    session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
  }

  /** Cleans up the DB tables and closes the test session */
  @AfterEach
  public void cleanupDatabase() {
    // If the prior test is open
    try {
      Session priorSession = DBConnection.CONNECTION.getSessionFactory().getCurrentSession();
      if (priorSession != null && priorSession.isOpen()) {

        // If the transaction is still active
        if (priorSession.getTransaction().isActive()) {
          priorSession.getTransaction().rollback(); // Roll it back
        }

        priorSession.close(); // Close it, so we can create new ones
      }
    } catch (HibernateException ignored) {
    }

    Transaction cleanupTransaction =
        session.beginTransaction(); // Create a transaction to cleanup with
    session.createMutationQuery("DELETE FROM Move").executeUpdate(); // Delete moves
    session.createMutationQuery("DELETE FROM LocationName ").executeUpdate(); // Delete locations
    session.createMutationQuery("DELETE FROM Node").executeUpdate(); // Delete nodes
    cleanupTransaction.commit(); // Commit the cleanup transaction

    session.close(); // Close the session
  }

  /** Reset testNode after each test */
  @BeforeEach
  @AfterEach
  public void resetTestNode() {
    Node testNode = new Node("Test", "Building", Node.Floor.L2, 0, 1);
  }

  /** Tests if the equals in Node.java correctly compares two Node objects */
  @Test
  public void testEquals() {
    Node node2 = new Node("Test", "0", Node.Floor.ONE, 1, 0);
    Node node3 = new Node("Another Test", "0", Node.Floor.ONE, 1, 0);
    assertEquals(testNode, node2);
    assertNotEquals(testNode, node3);
  }

  /** Checks to see if toString makes a string in the same format specified in Node.java */
  @Test
  public void testToString() {
    String stringId = testNode.toString();
    assertEquals(stringId, testNode.getId());
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    Node testNode2 = new Node("NewID", "Building", Node.Floor.L2, 100, 100000);
    assertNotEquals(testNode2.hashCode(), testNode.hashCode());
  }

  /** Tests setter for xCoord */
  @Test
  void setXCoord() {
    testNode.setXCoord(1);
    assertEquals(1, testNode.getXCoord());
  }

  /** Tests setter for yCoord */
  @Test
  void setYCoord() {
    testNode.setYCoord(1);
    assertEquals(1, testNode.getYCoord());
  }

  /** Tests setter for floor */
  @Test
  void setFloor() {
    testNode.setFloor(Node.Floor.L1);
    assertEquals(Node.Floor.L1, testNode.getFloor());
  }

  /** Tests setter for building */
  @Test
  void setBuilding() {
    testNode.setBuilding("New Building");
    assertEquals("New Building", testNode.getBuilding());
  }

  /** If a node isn't in the database, querying for it should return null */
  @Test
  public void nodeNotPersistedTest() {
    assertTrue(testNode.getCurrentLocation().isEmpty()); // Assert the test node has a null location
    assertTrue(
        testNode.getCurrentLocation(session).isEmpty()); // Assert the test node has a null location
  }

  /** Tests that a node with no mapping in move does not return a location */
  @Test
  public void nodeNotMappedTest() {
    Node testNode = new Node("a", "b", Node.Floor.L1, 0, 0); // Random node

    // Commit the node
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction
    session.persist(testNode); // Persist the node
    commitTransaction.commit(); // Commit

    assertTrue(testNode.getCurrentLocation().isEmpty());
    assertTrue(testNode.getCurrentLocation(session).isEmpty());
  }

  /**
   * Test for a case where a node is only mapped in the future. In this case, getting the location
   * should return null
   */
  @Test
  public void mappingOnlyInFutureTest() {
    Node testNode = new Node("b", "a", Node.Floor.L1, 15, 20); // Random node
    LocationName location = new LocationName("a", LocationName.LocationType.SERV, "b"); // Loc
    Move testMove =
        new Move(testNode, location, Date.from(Instant.now().plus(1, DAYS))); // Date ahead

    // Commit the node
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction
    session.persist(testNode); // Persist the node
    session.persist(location);
    session.persist(testMove);
    commitTransaction.commit(); // Commit

    assertTrue(testNode.getCurrentLocation().isEmpty()); // Assert the location is null
    assertTrue(testNode.getCurrentLocation(session).isEmpty()); // Assert the location is null
  }

  /** Tests that if the correct location is remapped, null is returned */
  @Test
  public void locationRemappedTest() {
    Node thisNode = new Node("n", "g", Node.Floor.L2, 99, 100); // Random node
    Node otherNode = new Node("i", "g", Node.Floor.THREE, 10000, 99); // Bad node
    LocationName theLocation = new LocationName("a", LocationName.LocationType.SERV, "b");
    Move oldMove = new Move(thisNode, theLocation, Date.from(Instant.ofEpochSecond(1))); // Old move
    Move newMove =
        new Move(otherNode, theLocation, Date.from(Instant.ofEpochSecond(2))); // New move

    Transaction commitTransaction = session.beginTransaction(); // Session to commit these
    session.persist(thisNode);
    session.persist(otherNode);
    session.persist(theLocation);
    session.persist(oldMove);
    session.persist(newMove);
    commitTransaction.commit(); // Commit

    assertTrue(thisNode.getCurrentLocation().isEmpty()); // Assert the location is null
    assertTrue(thisNode.getCurrentLocation(session).isEmpty()); // Assert the location is null
  }

  /**
   * Tests that if the correct location is remapped but there is another candidate location (older),
   * null is returned
   */
  @Test
  public void locationRemappedFallbackTest() {
    Node thisNode = new Node("p", "i", Node.Floor.L1, 99, 0); // Random node
    Node otherNode = new Node("h", "i", Node.Floor.L2, 0, 99); // Bad node
    LocationName theLocation = new LocationName("a", LocationName.LocationType.SERV, "b");
    LocationName badLocation = new LocationName("b", LocationName.LocationType.INFO, "b");
    Move fallbackMove =
        new Move(thisNode, badLocation, Date.from(Instant.ofEpochSecond(10))); // Old
    Move oldMove =
        new Move(thisNode, theLocation, Date.from(Instant.ofEpochSecond(22))); // Old move
    Move newMove =
        new Move(otherNode, theLocation, Date.from(Instant.ofEpochSecond(100))); // New move

    Transaction commitTransaction = session.beginTransaction(); // Session to commit these
    session.persist(thisNode);
    session.persist(otherNode);
    session.persist(theLocation);
    session.persist(badLocation);
    session.persist(fallbackMove);
    session.persist(oldMove);
    session.persist(newMove);
    commitTransaction.commit(); // Commit

    assertTrue(thisNode.getCurrentLocation().isEmpty()); // Assert the location is null
  }

  /** Tests that a simple mapping (one location, one node) works as expected */
  @Test
  public void simpleMappingTest() {
    Node node = new Node("node", "building", Node.Floor.L2, 0, 0);
    LocationName location = new LocationName("location", LocationName.LocationType.ELEV, "l");
    Move move = new Move(node, location, new Date()); // Simple location mapping for right now

    // Commit everything
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction to commit with
    session.persist(node);
    session.persist(location);
    session.persist(move);
    commitTransaction.commit(); // Commit the transaction

    assertEquals(
        location,
        node.getCurrentLocation().stream().findFirst().get()); // Assert the location is valid
    assertEquals(
        location,
        node.getCurrentLocation(session).stream()
            .findFirst()
            .get()); // Assert the location is valid
    assertEquals(1, node.getCurrentLocation().size());
  }

  /** Tests that old mappings of this node -> a location are ignored */
  @Test
  public void ignoresOldNodeMappingTest() {
    Node node = new Node("n", "b", Node.Floor.L2, 500, 5000); // node
    LocationName currentLocation = new LocationName("loc", LocationName.LocationType.CONF, "l");
    LocationName midLocation = new LocationName("loca", LocationName.LocationType.ELEV, "");
    LocationName oldLocation = new LocationName("l", LocationName.LocationType.ELEV, "");
    Move currentMove = new Move(node, currentLocation, new Date()); // Current move
    Move midMove =
        new Move(node, midLocation, Date.from(Instant.ofEpochSecond(10000))); // Older move
    Move oldMove = new Move(node, oldLocation, Date.from(Instant.ofEpochSecond(100))); // Old move

    // Commit everything
    Transaction commitTransaction = session.beginTransaction(); // Commit transaction
    session.persist(node);
    session.persist(currentLocation);
    session.persist(midLocation);
    session.persist(oldLocation);
    session.persist(currentMove);
    session.persist(midMove);
    session.persist(oldMove);
    commitTransaction.commit(); // Commit everything

    assertEquals(
        currentLocation,
        node.getCurrentLocation().stream()
            .findFirst()
            .get()); // Assert the correct location is gotten

    assertEquals(1, node.getCurrentLocation().size());
  }

  /** Tests that old mappings of the current location -> a node are ignored */
  @Test
  public void ignoresOldLocationMappingTest() {
    Node theNode = new Node("theNode", "building", Node.Floor.TWO, 500, 0); // Node
    LocationName location = new LocationName("l", LocationName.LocationType.CONF, "l");
    Move currentMove = new Move(theNode, location, new Date());
    Node anotherNode = new Node("old", "b", Node.Floor.THREE, 5, 50);
    Move oldMove = new Move(anotherNode, location, Date.from(Instant.ofEpochSecond(1))); // Old move

    // Commit everything to the DB
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction
    session.persist(theNode);
    session.persist(location);
    session.persist(currentMove);
    session.persist(anotherNode);
    session.persist(oldMove);
    commitTransaction.commit(); // Commit everything

    assertEquals(
        location,
        theNode.getCurrentLocation().stream()
            .findFirst()
            .get()); // Check that the location is correct
    assertEquals(
        location,
        theNode.getCurrentLocation(session).stream()
            .findFirst()
            .get()); // Check that the location is correct
    assertEquals(1, theNode.getCurrentLocation().size());
  }

  /** Tests for a case where the node is remapped in the future, ignores the future locations */
  @Test
  public void nodeRemappedInFuture() {
    Node node = new Node("n", "b", Node.Floor.THREE, 0, 0); // Create the node
    LocationName currentLocation = new LocationName("curr", LocationName.LocationType.CONF, "cur");
    LocationName futureLocation = new LocationName("fut", LocationName.LocationType.ELEV, "f");
    LocationName furtherFut = new LocationName("ff", LocationName.LocationType.SERV, "");
    Move currentNode = new Move(node, currentLocation, new Date()); // Move for right now
    Move futureMove =
        new Move(node, futureLocation, Date.from(Instant.now().plus(10, HOURS))); // Future
    Move moreFuture =
        new Move(node, furtherFut, Date.from(Instant.now().plus(99, DAYS))); // More future

    // Commit transaction
    Transaction commitTransaction =
        session.beginTransaction(); // Create a transaction to commit with
    session.persist(node);
    session.persist(currentLocation);
    session.persist(futureLocation);
    session.persist(furtherFut);
    session.persist(currentNode);
    session.persist(futureMove);
    session.persist(moreFuture);
    commitTransaction.commit(); // Commit the transaction

    assertEquals(
        currentLocation,
        node.getCurrentLocation().stream().findFirst().get()); // Assert the location is right
    assertEquals(
        currentLocation,
        node.getCurrentLocation(session).stream()
            .findFirst()
            .get()); // Assert the location is right
    assertEquals(1, node.getCurrentLocation().size());
  }

  /** Test for a case where the location is remapped in the future */
  @Test
  public void locationRemappedInFutureTest() {
    LocationName location = new LocationName("m", LocationName.LocationType.CONF, "");
    Node currentNode = new Node("N", "B", Node.Floor.L2, 0, 0);
    Node futureNode = new Node("", "", Node.Floor.L2, 50, 50);
    Node furtherNode = new Node("11", "BB", Node.Floor.L2, 5000, 5000);
    Node furthestNode = new Node("5565", "BB", Node.Floor.THREE, 50, 0);
    Move currentMove = new Move(currentNode, location, new Date()); // Move for right now
    Move futureMove =
        new Move(futureNode, location, Date.from(Instant.now().plus(100, MINUTES))); // Future
    Move moreFuture =
        new Move(furtherNode, location, Date.from(Instant.now().plus(99, DAYS))); // More future
    Move furthestFuture =
        new Move(furtherNode, location, Date.from(Instant.now().plus(711, DAYS))); // Furthest

    // Commit transaction
    Transaction commitTransaction =
        session.beginTransaction(); // Create a transaction to commit with
    session.persist(location);
    session.persist(currentNode);
    session.persist(futureNode);
    session.persist(furtherNode);
    session.persist(furthestNode);
    session.persist(currentMove);
    session.persist(futureMove);
    session.persist(moreFuture);
    session.persist(furthestFuture);
    commitTransaction.commit(); // Commit the transaction

    assertEquals(
        location,
        currentNode.getCurrentLocation().stream()
            .findFirst()
            .get()); // Assert the location is right
    assertEquals(
        location,
        currentNode.getCurrentLocation(session).stream()
            .findFirst()
            .get()); // Assert the location is right
    assertEquals(1, currentNode.getCurrentLocation().size());
  }

  /**
   * Test that puts it all together - has old nodes for the location, old locations for the node,
   * future locations for the node, and future nodes for the location
   */
  @Test
  public void mostRecentNotInFutureTest() {
    Node correctNode = new Node("correct", "b", Node.Floor.L2, 0, 0);
    LocationName correctLocation = new LocationName("correct", LocationName.LocationType.CONF, "c");
    Node futureNode = new Node("fut", "b", Node.Floor.THREE, 50, 50);
    LocationName futureLocation = new LocationName("future", LocationName.LocationType.SERV, "f");
    Node oldNode = new Node("old", "b", Node.Floor.TWO, 5, 10);
    LocationName oldLocation = new LocationName("old", LocationName.LocationType.SERV, "o");
    Move currentMove = new Move(correctNode, correctLocation, new Date());
    Move futureNodeToLocation =
        new Move(futureNode, correctLocation, Date.from(Instant.now().plus(1, DAYS)));
    Move currentNodeToFutureLocation =
        new Move(correctNode, futureLocation, Date.from(Instant.now().plus(366, DAYS)));
    Move oldNodeToLocation =
        new Move(oldNode, correctLocation, Date.from(Instant.ofEpochSecond(10)));
    Move currentNodeToOldLocation =
        new Move(correctNode, oldLocation, Date.from(Instant.ofEpochSecond(50)));

    // Commit transaction
    Transaction commitTransaction =
        session.beginTransaction(); // Create a transaction to commit with
    session.persist(correctNode);
    session.persist(correctLocation);
    session.persist(futureNode);
    session.persist(futureLocation);
    session.persist(oldNode);
    session.persist(oldLocation);
    session.persist(currentMove);
    session.persist(futureNodeToLocation);
    session.persist(currentNodeToFutureLocation);
    session.persist(oldNodeToLocation);
    session.persist(currentNodeToOldLocation);
    commitTransaction.commit(); // Commit the transaction

    assertEquals(
        correctLocation,
        correctNode.getCurrentLocation().stream()
            .findFirst()
            .get()); // Assert the location is right
    assertEquals(
        correctLocation,
        correctNode.getCurrentLocation(session).stream()
            .findFirst()
            .get()); // Assert the location is right
    assertEquals(1, correctNode.getCurrentLocation().size());
  }

  @Test
  public void twoLocationsAtOnce() {
    Node node = new Node("n", "b", Node.Floor.THREE, 0, 0); // Create the node
    LocationName currentLocation1 =
        new LocationName("curr1", LocationName.LocationType.CONF, "cur1");
    LocationName currentLocation2 =
        new LocationName("curr2", LocationName.LocationType.ELEV, "cur2");
    // LocationName furtherFut = new LocationName("ff", LocationName.LocationType.SERV, "");
    Move currentNode1 =
        new Move(node, currentLocation1, Date.from(Instant.now())); // Move for right now
    Move currentNode2 = new Move(node, currentLocation2, Date.from(Instant.now()));

    Transaction commitTransaction = session.beginTransaction();
    session.persist(node);
    session.persist(currentLocation1);
    session.persist(currentLocation2);
    session.persist(currentNode1);
    session.persist(currentNode2);
    commitTransaction.commit();

    assertEquals(2, node.getCurrentLocation(session).size());
  }
}
