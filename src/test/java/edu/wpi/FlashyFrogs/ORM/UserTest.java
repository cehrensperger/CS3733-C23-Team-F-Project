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
      connection.createMutationQuery("DELETE FROM Department ").executeUpdate(); // Do the drop
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  /** Simple tests for the first name setter */
  @Test
  public void setFirstNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Create the department to use
    Department department = new Department("a", "b");

    // Try creating a new user
    User u = new User("a", "b", "c", User.EmployeeType.STAFF, department);

    session.persist(department); // Persist the department to enable using the session
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting first name
    assertEquals("a", u.getFirstName()); // Assert the field is right

    // Try to set the first name
    u.setFirstName("asdfasf");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("b", u.getMiddleName()); // Check middle name side effects
    assertEquals("c", u.getLastName()); // Check last name side effects
    assertEquals(new Department("a", "b"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
  }

  /** Tries setting the middle name from something to null */
  @Test
  public void clearMiddleNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("long", "short");

    // Try creating a new user
    User u = new User("abasb", "dfhdfsgh", "afgawert", User.EmployeeType.ADMIN,
            department);

    session.persist(department); // persist the department to enable persisting the user
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals("a", u.getMiddleName()); // Assert the field is right

    // Try to set
    u.setMiddleName(null);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("dfhdfsgh", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertEquals(new Department("long", "short"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /**
   * Tries setting the middle name from null to something
   */
  @Test
  public void setMiddleNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("long", "short");

    // Try creating a new user
    User u = new User("abasb", null, "afgawert", User.EmployeeType.ADMIN,
            department);

    session.persist(department); // persist the department to enable persisting the user
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertNull(u.getMiddleName()); // Assert the field is right

    // Try to set
    u.setMiddleName("bbB");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("bbB", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertEquals(new Department("long", "short"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /**
   * Sets the middle name from one value to another
   */
  @Test
  public void updateMiddleNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("long", "short");

    // Try creating a new user
    User u = new User("abasb", "b", "afgawert", User.EmployeeType.ADMIN,
            department);

    session.persist(department); // persist the department to enable persisting the user
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals("b", u.getMiddleName()); // Assert the field is right

    // Try to set
    u.setMiddleName("hj");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("hj", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertEquals(new Department("long", "short"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /**
   * Starts the middle name as null, and tests setting it to the same
   */
  @Test
  public void nullToNullMiddleNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("long", "short");

    // Try creating a new user
    User u = new User("abasb", "val1", "afgawert", User.EmployeeType.ADMIN,
            department);

    session.persist(department); // persist the department to enable persisting the user
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals("val1", u.getMiddleName()); // Assert the field is right

    // Try to set
    u.setMiddleName("val2");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("val2", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertEquals(new Department("long", "short"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /** Simple test for setting the last name */
  @Test
  public void setLastNameTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("bbb", null, "qwerty", User.EmployeeType.MEDICAL, null);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals("qwerty", u.getLastName()); // Assert the field is right

    // Try to set
    u.setLastName("pppppp");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("bbb", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertNull(u.getDepartment()); // Assert the department stays null
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Check type side effects
  }

  /** Simple test for setting the type */
  @Test
  public void setTypeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("lll", null, "qwerty", User.EmployeeType.MEDICAL, null );

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Assert the field is right

    // Try to set the field
    u.setEmployeeType(User.EmployeeType.STAFF);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("lll", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertNull(u.getDepartment());
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
  }

  /**
   * Test where the department is set to something from null
   */
  @Test
  public void setDepartmentTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Create the department
    Department department = new Department("short", "long");

    // Try creating a new user
    User u = new User("lll", null, "qwerty", User.EmployeeType.MEDICAL, null);

    session.persist(department); // Persist the department (so that we can persist the user)
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals(new Department("short", "long"), u.getDepartment()); // Assert the field is right

    // Try to set the field
    u.setDepartment(department);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("lll", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(new Department("short", "long"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
  }

<<<<<<< HEAD
  /** Tests setting all parameters one at a time and testing for sequential side effects */
=======
  /**
   * Test where the department is valid and is changed from one valid thing to another
   */
  @Test
  public void changeDepartmentTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("long", "short");

    // Try creating a new user
    User u = new User("abasb", "b", "afgawert", User.EmployeeType.ADMIN,
            department);

    Department otherDepartment = new Department("b", "c"); // Other department name

    session.persist(department); // persist the department to enable persisting the user
    session.persist(u); // Persist U to set its ID (not to make it actually work)
    session.persist(otherDepartment); // Persist the other department

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals(new Department("long", "short"),
            u.getDepartment()); // Assert the field is right

    // Try to set
    u.setDepartment(otherDepartment);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("hj", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertEquals(new Department("b", "c"), u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /**
   * Tries setting the department from one null value to another
   */
>>>>>>> 6dcd39e98aca71cd23231bf85e76240eb18ea289
  @Test
  public void clearDepartmentTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("j", "k");

    // Try creating a new user
    User u = new User("abasb", "b", "afgawert", User.EmployeeType.ADMIN,
            department);

    session.persist(department); // persist the department to enable persisting the user
    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertEquals(new Department("j", "k"),
            u.getDepartment()); // Assert the field is right

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("hj", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertNull(u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /**
   * Tests setting the department from one null value to another
   */
  @Test
  public void nullToNullDepartmentTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("abasb", "b", "afgawert", User.EmployeeType.ADMIN,
            null);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting
    assertNull(u.getDepartment()); // Assert the department name is right

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdfasf", u.getFirstName()); // Check the first name
    assertEquals("hj", u.getMiddleName()); // Check middle name side effects
    assertEquals("afgawert", u.getLastName()); // Check last name side effects
    assertNull(u.getDepartment()); // Check the department
    assertEquals(User.EmployeeType.ADMIN, u.getEmployeeType()); // Check type side effects
  }

  /** Tests setting all parameters one at a time and testing for sequential side effects */
  @Test
  public void setAllTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Try creating a new user
    User u = new User("pop", null, "pod", User.EmployeeType.MEDICAL, null);

    session.persist(u); // Persist U to set its ID (not to make it actually work)

    transaction.rollback(); // Rollback
    session.close(); // Close

    long originalID = u.getId(); // Get the original ID

    // Check the starting fields
     assertEquals(originalID, u.getId());
    assertEquals("pop", u.getFirstName()); // Check the first name
    assertNull(null); // Check middle name side effects
    assertEquals("pod", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
    assertNull(u.getDepartment());


    // Try to set the first name
    u.setFirstName("asdf");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(null); // Check middle name side effects
    assertEquals("pod", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
    assertNull(u.getDepartment());

    // Set the middle name
    u.setMiddleName(null);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("pod", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
    assertNull(u.getDepartment());

    // Set the last name
    u.setLastName("qwerty");

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.STAFF, u.getEmployeeType()); // Check type side effects
    assertNull(u.getDepartment());

    // Set the type
    u.setEmployeeType(User.EmployeeType.MEDICAL);

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Check type side effects
    assertNull(u.getDepartment());

    // Set the department
    u.setDepartment(new Department("b", "c"));

    // Check the values
    assertEquals(originalID, u.getId());
    assertEquals("asdf", u.getFirstName()); // Check the first name
    assertNull(u.getMiddleName()); // Check middle name side effects
    assertEquals("qwerty", u.getLastName()); // Check last name side effects
    assertEquals(User.EmployeeType.MEDICAL, u.getEmployeeType()); // Check type side effects
    assertEquals(new Department("b", "c"), u.getDepartment()); // Check the department
  }

  /** Tests that duplicate names are allowed (and that they end up with different names) */
  @Test
  public void duplicateNamesAllowedTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    // Department
    Department department = new Department("my dept", "d");

    // Try creating a new user
    User u = new User("John", "G", "Smith", User.EmployeeType.MEDICAL, department);
    User uCopy = new User("John", "G", "Smith", User.EmployeeType.MEDICAL, department);

    session.persist(department); // Persist the department
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

    Department department = new Department("a", "b"); // Test department
    Department d2 = new Department("c", "d"); // Second test department

    // Persist the departments so we can use them
    session.persist(department);
    session.persist(d2);

    // Create the user we will use
    User u = new User("a", "b", "c", User.EmployeeType.MEDICAL, department);
    session.persist(u); // Save u

    // Assert that the one thing in the database matches this
    assertEquals(u, session.createQuery("FROM User", User.class).getSingleResult());
    assertEquals(
        u.hashCode(), session.createQuery("FROM User", User.class).getSingleResult().hashCode());

    // Identical user that should have a different ID
    User u2 = new User("a", "b", "c", User.EmployeeType.MEDICAL, department);
    session.persist(u2); // Load U2 into the DB, set its ID

    assertNotEquals(u, u2); // Assert U and U2 aren't equal
    assertNotEquals(u.hashCode(), u2.hashCode()); // Assert their has hash codes are different

    // Completely different user
    User u3 = new User("b", "c", "d", User.EmployeeType.ADMIN, d2);
    session.persist(u3); // Load u3 into the DB, set its ID

    assertNotEquals(u, u3); // Assert U and U3 aren't equal
    assertNotEquals(u.hashCode(), u3.hashCode()); // Assert their hash codes are different

    transaction.rollback(); // Rollback the transaction
    session.close(); // Close the session
  }

  /**
   * Tests for deleting from the departments, the user should be set to null
   */
  @Test
  public void departmentDeleteTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("a", "b"); // Test department

    // Persist the departments so we can use them
    session.persist(department);

    // Create the user we will use
    User u = new User("a", "b", "c", User.EmployeeType.MEDICAL, department);
    session.persist(u); // Save u

    session.remove(department); // Delete the department

    session.refresh(u); // Refresh u, to get the new user

    assertNull(u.getDepartment()); // Assert the department is null

    transaction.rollback(); // Rollback the transaction
    session.close(); // Close the session
  }

  /**
   * Tests for deleting from the departments in a query, the user should be set to null
   */
  @Test
  public void departmentDeleteQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("a", "b"); // Test department

    // Persist the departments so we can use them
    session.persist(department);

    // Create the user we will use
    User u = new User("a", "b", "c", User.EmployeeType.MEDICAL, department);
    session.persist(u); // Save u

    session.createMutationQuery("DELETE FROM Department ").executeUpdate(); // Delete

    session.refresh(u); // Refresh u, to get the new user

    assertNull(u.getDepartment()); // Assert the department is null

    transaction.rollback(); // Rollback the transaction
    session.close(); // Close the session
  }

  /**
   * Tests that updating department long name via a query will result in a cascade
   */
  @Test
  public void departmentUpdateTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    Department department = new Department("a", "b"); // Test department

    // Persist the departments so we can use them
    session.persist(department);

    // Create the user we will use
    User u = new User("a", "b", "c", User.EmployeeType.MEDICAL, department);
    session.persist(u); // Save u

    session.createMutationQuery("UPDATE Department SET longName = 'b'").executeUpdate(); // Delete

    session.refresh(u); // Refresh u, to get the new user

    assertEquals(new Department("b", "b"), u.getDepartment()); // Assert the department is correct

    transaction.rollback(); // Rollback the transaction
    session.close(); // Close the session
  }
}
