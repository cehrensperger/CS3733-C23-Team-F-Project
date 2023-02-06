package edu.wpi.FlashyFrogs;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Tests for the DBConnection enumerated type, which manages connections to/from the database */
public class DBConnectionTest {
  /**
   * After each test, try disconnecting (ignore errors) to ensure that the system is disconnected
   */
  @AfterEach
  public void cleanupConnection() {
    // Try disconnecting
    try {
      DBConnection.CONNECTION.disconnect();
    } catch (IllegalStateException ignored) { // Ignore errors

    }
  }

  /**
   * Tests that once the connection is initialized, it can be accessed, and is the same object each
   * time
   */
  @Test
  public void connectTest() {
    DBConnection.CONNECTION.connect(); // Connect the connection
    SessionFactory factory = DBConnection.CONNECTION.getSessionFactory(); // Get the session factory
    assertNotNull(factory); // Assert the factory isn't null (AKA that we got the connection)
    assertSame(
        factory,
        DBConnection.CONNECTION.getSessionFactory()); // Assert that the factory is the same
  }

  /** Tests that once a connection is disconnected, it can no longer be accessed (returns null) */
  @Test
  public void disconnectTest() {
    assertNull(
        DBConnection.CONNECTION.getSessionFactory()); // Assert the connection is null to start

    DBConnection.CONNECTION.connect(); // Connect, to reset the session factory
    assertNotNull(
        DBConnection.CONNECTION.getSessionFactory()); // Assert that the connection isn't null

    DBConnection.CONNECTION.disconnect(); // Disconnect the factory
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert the connection is null
  }

  /**
   * Tests that connecting and then re-connecting throws an exception and doesn't change the
   * connection
   */
  @Test
  public void reconnectTest() {
    DBConnection.CONNECTION.connect(); // Connect the connection
    SessionFactory connection =
        DBConnection.CONNECTION.getSessionFactory(); // Get the session factory
    assertThrows(
        IllegalStateException.class,
        DBConnection.CONNECTION::connect); // Assert that connecting throws
    assertSame(
        connection,
        DBConnection.CONNECTION.getSessionFactory()); // Assert the session factory is the same
  }

  /**
   * Tests that disconnecting again either before or after connection throws an exception and
   * doesn't change the connection
   */
  @Test
  public void disconnectionTest() {
    assertThrows(
        IllegalStateException.class,
        DBConnection.CONNECTION::disconnect); // Assert disconnecting throws
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert the factory is still null

    // Connect-disconnect to re-try disconnecting
    DBConnection.CONNECTION.connect(); // Connect
    DBConnection.CONNECTION.disconnect(); // Disconnect

    assertThrows(
        IllegalStateException.class,
        DBConnection.CONNECTION::disconnect); // Assert disconnecting throws
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert the factory is still null

    assertThrows(
        IllegalStateException.class,
        DBConnection.CONNECTION::disconnect); // Assert disconnecting throws
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert the factory is still null
  }

  /** Tests that when a DBConnection is instantiated, connect/disconnect works the same way */
  @Test
  public void instantiationTest() {
    DBConnection connection = DBConnection.CONNECTION; // Instantiate the connection

    assertNull(connection.getSessionFactory()); // Assert the connection is null to start
    assertNull(
        DBConnection.CONNECTION.getSessionFactory()); // Assert the full connection is the same

    assertThrows(
        IllegalStateException.class, connection::disconnect); // Assert disconnecting again throws
    assertNull(connection.getSessionFactory()); // Assert the factory is null still
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert is null still

    connection.connect(); // Connect the connection
    SessionFactory sessionFactory = connection.getSessionFactory(); // Get the factory
    assertNotNull(sessionFactory); // Assert the factory isn't null
    assertSame(
        sessionFactory,
        connection.getSessionFactory()); // Assert the factory is the same a second time
    assertSame(
        sessionFactory,
        DBConnection.CONNECTION.getSessionFactory()); // Assert the direct enum is the same
    assertThrows(
        IllegalStateException.class, connection::connect); // Assert re-trying to connect throws
    assertSame(
        sessionFactory,
        connection.getSessionFactory()); // Assert the factory is the same a second time
    assertSame(
        sessionFactory,
        DBConnection.CONNECTION.getSessionFactory()); // Assert the direct enum is the same

    connection.disconnect(); // Disconnect the connection
    assertNull(connection.getSessionFactory()); // Assert the factory is null
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert the raw factory is also null
    assertThrows(
        IllegalStateException.class, connection::disconnect); // Assert disconnecting again throws
    assertNull(connection.getSessionFactory()); // Assert the factory is null still
    assertNull(DBConnection.CONNECTION.getSessionFactory()); // Assert is null still
  }
}
