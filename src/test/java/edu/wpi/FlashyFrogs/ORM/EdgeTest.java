package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class EdgeTest {
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
        priorSession.close(); // Close it, so we can create new ones
      }
    } catch (HibernateException ignored) {
    }

    // Use a closure to manage the session to use
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      Transaction cleanupTransaction = connection.beginTransaction(); // Begin a cleanup transaction
      connection.createMutationQuery("DELETE FROM Edge").executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM Node").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  // Create Test Edge using Test Nodes
  Node testNode1 = new Node("Test", "Building", Node.Floor.L2, 0, 1);
  Node testNode2 = new Node("Other Test", "Building", Node.Floor.L2, 0, 1);
  Edge testEdge = new Edge(testNode1, testNode2);

  /** Reset testEdge after each test */
  @BeforeEach
  @AfterEach
  public void resetTestEdge() {
    testNode1 = new Node("Test", "Building", Node.Floor.L2, 0, 1);
    testNode2 = new Node("Other Test", "Building", Node.Floor.L2, 0, 1);
    testEdge = new Edge(testNode1, testNode2);
  }

  /** Checks to see if toString makes a string in the same format specified in Edge.java */
  @Test
  void testToString() {
    String stringEdge = testEdge.toString();
    assertEquals(stringEdge, testEdge.getNode1().getId() + "_" + testEdge.getNode2());
  }

  /** Test setters with the blank edge constructor */
  @Test
  public void blankEdgeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Create a blank edge, set its fields
    Edge blankEdge = new Edge();

    // Check that persist throws an exception
    assertThrows(
        Exception.class,
        () -> {
          session.persist(blankEdge);
          transaction.commit();
        });
    session.close();
  }

  /**
   * Tests the equals and hash code methods for edge. Should be completely dependent on the edge
   * being the same
   */
  @Test
  public void equalsAndHashCodeTest() {
    Node node1 = new Node("Test", "Building", Node.Floor.L2, 0, 1);
    Node node2 = new Node("Other Test", "Building", Node.Floor.L2, 1, 1);
    Edge edge = new Edge(node1, node2);
    Edge sameEdge = new Edge(node1, node2);
    Edge reversedEdge = new Edge(node2, node1);
    Node node3 = new Node("Third Test", "Building", Node.Floor.L1, 2, 2);
    Edge diffEdge = new Edge(node1, node3);

    // Assert that the edges are the right equals including hash code
    assertEquals(edge, sameEdge);
    assertEquals(edge.hashCode(), sameEdge.hashCode());
    assertNotEquals(edge, reversedEdge);
    assertNotEquals(edge.hashCode(), reversedEdge.hashCode());
    assertNotEquals(edge, diffEdge);
    assertNotEquals(edge.hashCode(), diffEdge.hashCode());
  }

  /** Tests that deleting node1 deletes the edge */
  @Test
  public void deleteNode1CascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Get a session
    Transaction commitTransaction = session.beginTransaction(); // begin a transaction

    Node node1 = new Node("Test", "Building", Node.Floor.L2, 0, 1);
    session.persist(node1);
    Node node2 = new Node("Other Test", "Building", Node.Floor.L2, 1, 1);
    session.persist(node2);
    Edge edge = new Edge(node1, node2);
    session.persist(edge);

    session.remove(node1);

    assertNull(session.createQuery("FROM Edge", Edge.class).uniqueResult());

    session.close();
  }

  /** Tests that deleting node2 deletes the edge */
  @Test
  public void deleteNode2CascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Get a session
    Transaction commitTransaction = session.beginTransaction(); // begin a transaction

    Node node1 = new Node("Test", "Building", Node.Floor.L2, 0, 1);
    session.persist(node1);
    Node node2 = new Node("Other Test", "Building", Node.Floor.L2, 1, 1);
    session.persist(node2);
    Edge edge = new Edge(node1, node2);
    session.persist(edge);

    session.remove(node2);

    assertNull(session.createQuery("FROM Edge", Edge.class).uniqueResult());

    session.close();
  }
}
