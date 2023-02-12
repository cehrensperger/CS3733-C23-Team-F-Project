package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import edu.wpi.FlashyFrogs.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Tests for the ORM user class */
public class UserTest {
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
      connection.createMutationQuery("DELETE FROM User").executeUpdate(); // Do the drop
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  /** Simple tests for the first name setter */
  @Test
  public void setFirstNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("a", "b", "c", User.EmployeeType.STAFF);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting first name
    assertEquals("a", u.getFirstName()); // Assert the first name is right

    // Try to set the first name
    u.setFirstName("asdfasf");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("b", u.getMiddleName()); // Check middle name side effects
    assertEquals("c", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
  }

  /** Simple tests for the middle name setter */
  @Test
  public void setMiddleNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("abasb", "dfhdfsgh", "afgawert", User.EmployeeType.ADMIN);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting first name
    assertEquals("a", u.getMiddleName()); // Assert the first name is right

    // Try to set the first name
    u.setMiddleName(null);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("dfhdfsgh", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /** Simple test for setting the last name */
  @Test
  public void setLastNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("bbb", null, "qwerty", User.EmployeeType.MEDICAL);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting first name
    assertEquals("qwerty", u.getLastName()); // Assert the first name is right

    // Try to set the first name
    u.setLastName("pppppp");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("bbb", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Check type side effects
  }

  /** Simple test for setting the type */
  public void setTypeTest() {}
}
