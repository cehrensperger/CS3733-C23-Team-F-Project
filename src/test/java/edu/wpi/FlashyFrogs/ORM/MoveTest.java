package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.Date;
import org.hibernate.HibernateException;
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

    // Use a closure to manage the session to use
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      Transaction cleanupTransaction = connection.beginTransaction(); // Begin a cleanup transaction
      connection.createMutationQuery("DELETE FROM Move").executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM Node").executeUpdate();
      connection.createMutationQuery("DELETE FROM LocationName").executeUpdate();
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
    assertThrows(
        Exception.class,
        () -> {
          session.persist(emptyMove);
          transaction.commit();
        });
    session.close();
  }

  /**
   * Tests the equals and hash code methods for move. Should be completely dependent on the move
   * being the same
   */
  @Test
  public void equalsAndHashCodeTest() {
    // Two different moves
    Move move =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 1 - 31));
    Move sameMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 1 - 31));
    Move differentNodeMove =
        new Move(
            new Node("Test2", "Building2", Node.Floor.L1, 1, 1),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 1 - 31));
    Move differentLocationMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("NewLongName", LocationName.LocationType.BATH, "NewShortName"),
            new Date(2023 - 1 - 31));
    Move differentDateMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2022 - 12 - 24));
    Move differentMove =
        new Move(
            new Node("Test2", "Building2", Node.Floor.L1, 1, 1),
            new LocationName("NewLongName", LocationName.LocationType.BATH, "NewShortName"),
            new Date(2022 - 12 - 24));

    // Assert that the moves are the right equals including hash code
    assertEquals(move, sameMove);
    assertEquals(move.hashCode(), sameMove.hashCode());
    assertNotEquals(move, differentNodeMove);
    assertNotEquals(move.hashCode(), differentNodeMove.hashCode());
    assertNotEquals(move, differentLocationMove);
    assertNotEquals(move.hashCode(), differentLocationMove.hashCode());
    assertNotEquals(move, differentDateMove);
    assertNotEquals(move.hashCode(), differentDateMove.hashCode());
    assertNotEquals(move, differentMove);
    assertNotEquals(move.hashCode(), differentMove.hashCode());
  }

  /** Tests that deleting a node associated with a move also deletes that move */
  @Test
  public void deleteNodeCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Get a session
    Transaction commitTransaction = session.beginTransaction(); // begin a transaction

    Node node = new Node("Test", "Building", Node.Floor.L2, 0, 0);
    session.persist(node);

    LocationName location =
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");
    session.persist(location);

    Move move = new Move(node, location, new Date(2023 - 1 - 31));
    session.persist(move);

    session.remove(node);

    assertNull(session.createQuery("FROM Move", Move.class).uniqueResult());

    session.close();
  }

  /** Tests that deleting a location associated with a move also deletes the move */
  @Test
  public void deleteLocationCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Get a session
    Transaction commitTransaction = session.beginTransaction(); // begin a transaction

    Node node = new Node("Test", "Building", Node.Floor.L2, 0, 0);
    session.persist(node);

    LocationName location =
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");
    session.persist(location);

    Move move = new Move(node, location, new Date(2023 - 1 - 31));
    session.persist(move);

    session.remove(location);

    assertNull(session.createQuery("FROM Move", Move.class).uniqueResult());

    session.close();
  }
}
