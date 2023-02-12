package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class MoveTest {
  /** Sets up the data base before all tests run */
  @BeforeAll
  public static void setupDBConnection() {
    DBConnection.CONNECTION.connect(); // Connect
  }

  /** Tears down the database, meant to be used after all tests finish */
  @AfterAll
  public static void disconnectDBConnection() {
    DBConnection.CONNECTION.disconnect(); // Disconnect
  }

  /** Cleans up the user table. Runs after each test */
  @AfterEach
  public void teardownTable() {
    // Use a closure to manage the session to use
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      Transaction cleanupTransaction = connection.beginTransaction(); // Begin a cleanup transaction
      connection.createMutationQuery("DELETE FROM Move").executeUpdate(); // Do the drop
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  // Creates iteration of move
  Move testMove =
      new Move(
          new Node("Test", "Building", Node.Floor.L2, 0, 0),
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          new Date(2023 - 1 - 31));

  /** Reset testMove after each test */
  @BeforeEach
  @AfterEach
  public void resetTestMove() {
    testMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 1 - 31));
  }

  /** Tests if the equals in Move.java correctly compares two Move objects */
  @Test
  void testEquals() {
    Move otherTestMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 1 - 31));
    assertEquals(testMove, otherTestMove);
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    Move testMove2 =
        new Move(
            new Node("OtherTest", "SecondBuilding", Node.Floor.L1, 0, 10),
            new LocationName("DifferentLong", LocationName.LocationType.DEPT, "NewShort"),
            new Date(2023 - 2 - 1));
    assertNotEquals(testMove2.hashCode(), testMove.hashCode());
  }

  /** Checks to see if toString makes a string in the same format specified in Move.java */
  @Test
  void testToString() {
    String stringMove = testMove.toString();
    assertEquals(
        stringMove,
        testMove.getNode().getId()
            + "_"
            + testMove.getLocation().getLongName()
            + "_"
            + testMove.getMoveDate());
  }

  /**
   * Tests that the empty constructor with the setters is equa to the filled constructor without
   * them
   */
  @Test
  public void emptyConstructorTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Create a move with the parameters done through setters
    Move emptyMove = new Move();

    // Check that persist throws an exception
    assertThrows(Exception.class, () -> session.persist(emptyMove));
  }
}
