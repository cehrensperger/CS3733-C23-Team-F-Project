package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Tests for the department class */
public class DepartmentTest {
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
      connection.createMutationQuery("DELETE FROM Department").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  /** Simple department, tests the two getters and the one setter */
  @Test
  public void simpleDepartmentTest() {
    Department department = new Department("A", "B"); // Simple department

    // Assert the names are valid
    assertEquals("A", department.getLongName());
    assertEquals("B", department.getShortName());
    assertEquals("B", department.toString());

    department.setShortName("newShortName"); // Reset the short name

    // Assert the short name is corect
    assertEquals("newShortName", department.getShortName());

    assertEquals("newShortName", department.toString());
  }

  /** Another simple department test */
  @Test
  public void extraDepartmentTest() {
    Department department = new Department("abasbsdfh", "qwer"); // Simple department

    // Assert the names are valid
    assertEquals("abasbsdfh", department.getLongName());
    assertEquals("qwer", department.getShortName());
    assertEquals("qwer", department.toString());

    department.setShortName("ex"); // Reset the short name

    // Assert the short name is corect
    assertEquals("ex", department.getShortName());

    assertEquals("ex", department.toString());
  }

  /** Tests for the department equals and hash code methods */
  @Test
  public void equalsAndHashCodeTest() {
    Department departmentOne = new Department("deptName", "short");
    Department exactlyEqualDepartment = new Department("deptName", "short");
    Department onlyLongNameEqualDepartment = new Department("deptName", "different");
    Department onlyShortNameEqualDepartment = new Department("different", "short");
    Department completelyDifferentDepartment = new Department("completely", "different");

    // Assert the departments have the right equality based on only long name matters
    assertEquals(departmentOne, exactlyEqualDepartment);
    assertEquals(departmentOne.hashCode(), exactlyEqualDepartment.hashCode());
    assertEquals(departmentOne, onlyLongNameEqualDepartment);
    assertEquals(departmentOne.hashCode(), onlyLongNameEqualDepartment.hashCode());
    assertNotEquals(departmentOne, onlyShortNameEqualDepartment);
    assertNotEquals(departmentOne.hashCode(), onlyShortNameEqualDepartment.hashCode());
    assertNotEquals(departmentOne, completelyDifferentDepartment);
    assertNotEquals(departmentOne.hashCode(), completelyDifferentDepartment.hashCode());
  }

  /**
   * Tests to ensure that there are no duplicate longnames allowed, but duplicate shortnames are
   * allowed
   */
  @Test
  public void duplicatesTest() {
    // Get the session to use
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction(); // Open a transaction to use
    Department testDepartment = new Department("test", "test");
    session.persist(testDepartment); // Test department

    transaction.commit(); // Commit the initial transaction

    transaction = session.beginTransaction(); // Open a new transaction

    assertNotNull(
        session.find(
            Department.class, testDepartment.getLongName())); // Assert the dept still exists

    // Identical department
    Department identicalDepartment = new Department("test", "test");
    assertThrows(Exception.class, () -> session.persist(identicalDepartment));

    transaction.rollback(); // Close the transaction, it's now trash

    transaction = session.beginTransaction(); // Re-open a new one
    assertNotNull(
        session.find(
            Department.class, testDepartment.getLongName())); // Assert the dept still exists

    Department onlyLongNameEqualDepartment = new Department("test", "different");
    assertThrows(
        Exception.class,
        () -> session.persist(onlyLongNameEqualDepartment)); // Assert the error happens
    transaction.rollback(); // Rollback

    transaction = session.beginTransaction(); // Open a new transaction now
    assertNotNull(session.find(Department.class, "test")); // Assert the dept still exists
    Department completelyDifferentDepartment = new Department("diff", "diff");
    Department departmentWithSameShortName = new Department("different", "test");

    // Persist the stuff
    session.persist(completelyDifferentDepartment);
    session.persist(departmentWithSameShortName);
    transaction.commit();

    // Open a new transaction
    transaction = session.beginTransaction();

    // Assert everything is in the DB
    assertNotNull(session.find(Department.class, "test"));
    assertNotNull(session.find(Department.class, "diff"));
    assertNotNull(session.find(Department.class, "different"));
    transaction.commit();

    session.close(); // Close the session
  }
}
