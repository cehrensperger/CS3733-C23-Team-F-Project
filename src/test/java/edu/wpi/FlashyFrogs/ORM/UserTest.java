package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.List;
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

    // Check the starting
    assertEquals("a", u.getMiddleName()); // Assert the first name is right

    // Try to set
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

    // Check the starting
    assertEquals("qwerty", u.getLastName()); // Assert the first name is right

    // Try to set
    u.setLastName("pppppp");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("bbb", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Check type side effects
  }

  /** Simple test for setting the type */
  @Test
  public void setTypeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("lll", null, "qwerty", User.EmployeeType.MEDICAL);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Assert the first name is right

    // Try to set the field
    u.setEmployeeType(User.EmployeeType.STAFF);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("lll", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
  }

  /** Tests setting all parameters one at a time and testing for sequential side effects */
  @Test
  public void setAllTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("pop", "pad", "pod", User.EmployeeType.MEDICAL);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting first name
    assertEquals("pop", u.getFirstName()); // Assert the first name is right

    // Try to set the first name
    u.setFirstName("asdf");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertEquals("pad", u.getMiddleName()); // Check middle name side effects
    assertEquals("pod", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects

    // Set the middle name
    u.setMiddleName(null);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("pod", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects

    // Set the last name
    u.setLastName("qwerty");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects

    // Set the type
    u.setEmployeeType(User.EmployeeType.MEDICAL);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Check type side effects
  }

  /** Tests that duplicate names are allowed (and that they end up with different names) */
  @Test
  public void duplicateNamesAllowedTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("John", "G", "Smith", User.EmployeeType.MEDICAL);
    User uCopy = new User("John", "G", "Smith", User.EmployeeType.MEDICAL);

    session.persist(u); // Persist U to set its ID (not to make it actually work)
    session.persist(uCopy); // Persist the other one with identical fields

    // Assert that the users are correct
    assertEquals(List.of(u, uCopy), session.createQuery("FROM User", User.class).getResultList());

    transaction.rollback(); // Rollback
    session.close(); // Close
  }

  /**
   * Tests the equals and hash code methods for the user class, ensures that fetched objects are
   * equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Create the user we will use
    User u = new User("a", "b", "c", User.EmployeeType.MEDICAL);
    session.persist(u); // Save u

    // Assert that the one thing in the database matches this
    assertEquals(u, session.createQuery("FROM User", User.class).getSingleResult());
    assertEquals(
        u.hashCode(), session.createQuery("FROM User", User.class).getSingleResult().hashCode());

    // Identical user that should have a different ID
    User u2 = new User("a", "b", "c", User.EmployeeType.MEDICAL);
    session.persist(u2); // Load U2 into the DB, set its ID

    assertNotEquals(u, u2); // Assert U and U2 aren't equal
    assertNotEquals(u.hashCode(), u2.hashCode()); // Assert their has hash codes are different

    // Completely different user
    User u3 = new User("b", "c", "d", User.EmployeeType.ADMIN);
    session.persist(u3); // Load u3 into the DB, set its ID

    assertNotEquals(u, u3); // Assert U and U3 aren't equal
    assertNotEquals(u.hashCode(), u3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }
}
