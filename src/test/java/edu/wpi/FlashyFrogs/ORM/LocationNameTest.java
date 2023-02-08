package edu.wpi.FlashyFrogs.ORM;

import static java.time.temporal.ChronoUnit.*;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.time.Instant;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class LocationNameTest {
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
    // cancel any still-running transactions
    if (session.getTransaction().isActive()) {
      session.getTransaction().rollback();
    }

    Transaction cleanupTransaction =
        session.beginTransaction(); // Create a transaction to cleanup with
    session.createMutationQuery("DELETE FROM AudioVisual").executeUpdate();
    session.createMutationQuery("DELETE FROM ComputerService").executeUpdate();
    session.createMutationQuery("DELETE FROM InternalTransport").executeUpdate();
    session.createMutationQuery("DELETE FROM Sanitation").executeUpdate();
    session.createMutationQuery("DELETE FROM Security ").executeUpdate();
    session.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
    session.createMutationQuery("DELETE FROM Move").executeUpdate(); // Delete moves
    session.createMutationQuery("DELETE FROM LocationName ").executeUpdate(); // Delete locations
    session.createMutationQuery("DELETE FROM Node").executeUpdate(); // Delete nodes
    cleanupTransaction.commit(); // Commit the cleanup transaction

    session.close(); // Close the session
  }

  // Creates iteration of LocationName
  LocationName testLocName =
      new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");

  /** Reset testLocationName after each test */
  @BeforeEach
  @AfterEach
  public void resetTestLocationName() {
    testLocName.setLongName("LongName");
    testLocName.setLocationType(LocationName.LocationType.HALL);
    testLocName.setShortName("ShortName");
  }

  /** Tests if the equals in LocationName.java correctly compares two LocationName objects */
  @Test
  void testEquals() {
    LocationName otherLocName =
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");
    assertEquals(testLocName, otherLocName);
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testLocName.hashCode();
    testLocName.setLongName("NewLongName");
    testLocName.setLocationType(LocationName.LocationType.ELEV);
    assertNotEquals(testLocName.hashCode(), originalHash);
  }

  /** Checks to see if toString makes a string in the same format specified in LocationName.java */
  @Test
  void testToString() {
    String locNameToString = testLocName.toString();
    assertEquals(locNameToString, testLocName.getLongName());
  }

  /** Tests setter for longName */
  @Test
  void setLongName() {
    String newLongName = "NewLongName";
    testLocName.setLongName(newLongName);
    assertEquals(newLongName, testLocName.getLongName());
  }

  /** Tests setter for shortName */
  @Test
  void setShortName() {
    String newShortName = "NewShortName";
    testLocName.setShortName(newShortName);
    assertEquals(newShortName, testLocName.getShortName());
  }

  /** Tests setter for locationType */
  @Test
  void setLocationType() {
    testLocName.setLocationType(LocationName.LocationType.LABS);
    assertEquals(LocationName.LocationType.LABS, testLocName.getLocationType());
  }

  /** If a location isn't in the database, querying for it should return null */
  @Test
  public void locationNotPersistedTest() {
    assertNull(testLocName.getCurrentNode()); // Assert the test loc has a null location
    assertNull(testLocName.getCurrentNode(session)); // Assert the test loc has a null location
  }

  /** Tests that a location with no mapping in move does not return a node */
  @Test
  public void locNotMappedTest() {
    LocationName testLocation = new LocationName("a", LocationName.LocationType.CONF, "b");

    // Commit the node
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction
    session.persist(testNode); // Persist the node
    commitTransaction.commit(); // Commit

    assertNull(testLocation.getCurrentNode());
    assertNull(testLocation.getCurrentNode(session));
  }

  /**
   * Test for a case where a location is only mapped in the future. In this case, getting the
   * location should return null
   */
  @Test
  public void mappingOnlyInFutureTest() {
    Node testNode = new Node("", "", Node.Floor.L2, 5, 0); // Random node
    LocationName location = new LocationName("", LocationName.LocationType.SERV, ""); // Loc
    Move testMove =
        new Move(testNode, location, Date.from(Instant.now().plus(12, HOURS))); // Date ahead

    // Commit the node
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction
    session.persist(testNode); // Persist the node
    session.persist(location);
    session.persist(testMove);
    commitTransaction.commit(); // Commit

    assertNull(location.getCurrentNode()); // Assert the location is null
    assertNull(location.getCurrentNode(session)); // Assert the location is null
  }

  /** Tests that if the correct node is remapped, null is returned */
  @Test
  public void nodeRemappedTest() {
    Node thisNode = new Node("n", "g", Node.Floor.L2, 99, 100); // Random node
    LocationName theLocation = new LocationName("a", LocationName.LocationType.SERV, "b");
    LocationName otherLocation = new LocationName("b", LocationName.LocationType.REST, "b");
    Move oldMove = new Move(thisNode, theLocation, Date.from(Instant.ofEpochSecond(1))); // Old move
    Move newMove =
        new Move(thisNode, otherLocation, Date.from(Instant.ofEpochSecond(2))); // New move

    Transaction commitTransaction = session.beginTransaction(); // Session to commit these
    session.persist(thisNode);
    session.persist(otherLocation);
    session.persist(theLocation);
    session.persist(oldMove);
    session.persist(newMove);
    commitTransaction.commit(); // Commit

    assertNull(theLocation.getCurrentNode()); // Assert the location is null
    assertNull(theLocation.getCurrentNode(session)); // Assert the location is null
  }

  /**
   * Tests that if the correct node is remapped but there is another candidate location (older),
   * null is returned
   */
  @Test
  public void nodeRemappedFallbackTest() {
    Node thisNode = new Node("a", "j", Node.Floor.L2, 9, 1); // Random node
    Node badNode = new Node("n", "h", Node.Floor.L1, 6, 59); // Bad node
    LocationName theLocation = new LocationName("bb", LocationName.LocationType.SERV, "aa");
    LocationName otherLocation = new LocationName("a", LocationName.LocationType.INFO, "aa");
    Move fallbackMove = new Move(badNode, theLocation, Date.from(Instant.ofEpochSecond(10))); // Old
    Move oldMove =
        new Move(thisNode, theLocation, Date.from(Instant.ofEpochSecond(22))); // Old move
    Move newMove =
        new Move(thisNode, otherLocation, Date.from(Instant.ofEpochSecond(100))); // New move

    Transaction commitTransaction = session.beginTransaction(); // Session to commit these
    session.persist(thisNode);
    session.persist(otherLocation);
    session.persist(theLocation);
    session.persist(badNode);
    session.persist(fallbackMove);
    session.persist(oldMove);
    session.persist(newMove);
    commitTransaction.commit(); // Commit

    assertNull(theLocation.getCurrentNode()); // Assert the location is null
    assertNull(theLocation.getCurrentNode(session)); // Assert the location is null
  }

  /** Tests that a simple mapping (one location, one node) works as expected */
  @Test
  public void simpleMappingTest() {
    Node node = new Node("anode", "abuilding", Node.Floor.ONE, 50, 50);
    LocationName location = new LocationName("aloc", LocationName.LocationType.EXIT, "ex");
    Move move = new Move(node, location, new Date()); // Simple location mapping for right now

    // Commit everything
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction to commit with
    session.persist(node);
    session.persist(location);
    session.persist(move);
    commitTransaction.commit(); // Commit the transaction

    assertEquals(node, location.getCurrentNode()); // Assert the location is valid
    assertEquals(node, location.getCurrentNode(session)); // Assert the location is valid
  }

  /** Tests that old mappings of the location -> a node are ignored */
  @Test
  public void ignoresOldLocationMappingTest() {
    Node theNode = new Node("no", "bi", Node.Floor.THREE, -50, -50); // Node
    LocationName location = new LocationName("lo", LocationName.LocationType.REST, "l");
    Move currentMove = new Move(theNode, location, new Date());
    Node anotherNode = new Node("ol", "b", Node.Floor.TWO, -5, -50);
    Move oldMove = new Move(anotherNode, location, Date.from(Instant.ofEpochSecond(1))); // Old move

    // Commit everything to the DB
    Transaction commitTransaction = session.beginTransaction(); // Open a transaction
    session.persist(theNode);
    session.persist(location);
    session.persist(currentMove);
    session.persist(anotherNode);
    session.persist(oldMove);
    commitTransaction.commit(); // Commit everything

    assertEquals(theNode, location.getCurrentNode()); // Check that the location is correct
    assertEquals(theNode, location.getCurrentNode(session)); // Check that the location is correct
  }

  /** Tests that old mappings of a node -> the location are ignored */
  @Test
  public void ignoresOldNodeMappingTest() {
    Node node = new Node("node", "build", Node.Floor.L1, 550, 555000); // node
    LocationName currentLocation = new LocationName("lc", LocationName.LocationType.EXIT, "ex");
    LocationName midLocation = new LocationName("aloc", LocationName.LocationType.EXIT, "");
    LocationName oldLocation = new LocationName("li", LocationName.LocationType.ELEV, "");
    Move currentMove = new Move(node, currentLocation, new Date()); // Current move
    Move midMove =
        new Move(node, midLocation, Date.from(Instant.ofEpochSecond(1000))); // Older move
    Move oldMove = new Move(node, oldLocation, Date.from(Instant.ofEpochSecond(10))); // Old move

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

    assertEquals(node, currentLocation.getCurrentNode()); // Assert the correct location is gotten
    assertEquals(
        node, currentLocation.getCurrentNode(session)); // Assert the correct location is gotten
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

    assertEquals(currentNode, location.getCurrentNode()); // Assert the location is right
    assertEquals(currentNode, location.getCurrentNode(session)); // Assert the location is right
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

    assertEquals(node, currentLocation.getCurrentNode()); // Assert the location is right
    assertEquals(node, currentLocation.getCurrentNode(session)); // Assert the location is right
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

    assertEquals(correctNode, correctLocation.getCurrentNode()); // Assert the location is right
    assertEquals(
        correctNode, correctLocation.getCurrentNode(session)); // Assert the location is right
  }
}
