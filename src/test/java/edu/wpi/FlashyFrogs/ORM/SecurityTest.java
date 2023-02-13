package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class SecurityTest {
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
      connection.createMutationQuery("DELETE FROM Security").executeUpdate();
      connection.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
      connection.createMutationQuery("DELETE FROM LocationName").executeUpdate();
      connection.createMutationQuery("DELETE FROM User").executeUpdate();
      connection.createMutationQuery("DELETE FROM Department").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  Security testSecurity =
      new Security(
          "Incident Report",
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT);

  private final Department sourceDept = new Department("a", "b");

  /** Reset testSecurity after each test */
  @BeforeEach
  @AfterEach
  public void resetTestSecurity() {
    testSecurity.setIncidentReport("Incident Report");
    testSecurity.setLocation(
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testSecurity.setAssignedEmp(assignedEmp);
    testSecurity.setDateOfIncident(new Date(2023 - 1 - 31));
    testSecurity.setDateOfSubmission(new Date(2023 - 2 - 1));
    testSecurity.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
  }

  /** Tests setter for incidentReport */
  @Test
  void setIncidentReport() {
    testSecurity.setIncidentReport("Something Else");
    assertEquals("Something Else", testSecurity.getIncidentReport());
  }

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSecurity.setEmp(newEmp);
    assertEquals(newEmp, testSecurity.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSecurity.setAssignedEmp(newEmp);
    assertEquals(newEmp, testSecurity.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 1 - 17);
    testSecurity.setDateOfIncident(newDOI);
    assertEquals(newDOI, testSecurity.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 1 - 17);
    testSecurity.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testSecurity.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgency() {
    testSecurity.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testSecurity.getUrgency());
  }

  /** Tests setter for location */
  @Test
  void testSetLocation() {
    LocationName newLoc =
        new LocationName("NewLocLong", LocationName.LocationType.EXIT, "NewLocShort");
    testSecurity.setLocation(newLoc);
    assertEquals(newLoc, testSecurity.getLocation());
  }

  /** Tests setter for incidentReport */
  @Test
  void testSetIncidentReport() {
    String newIncRep = "New Report";
    testSecurity.setIncidentReport(newIncRep);
    assertEquals(newIncRep, testSecurity.getIncidentReport());
  }

  /**
   * Tests the equals and hash code methods for the AudioVisual class, ensures that fetched objects
   * are equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, sourceDept);
    LocationName location = new LocationName("Name", LocationName.LocationType.EXIT, "name");

    session.persist(sourceDept);
    session.persist(emp);
    session.persist(location);
    // Create the security request we will use
    Security sec =
        new Security(
            "Incident Report",
            location,
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(sec);

    // Assert that the one thing in the database matches this
    assertEquals(sec, session.createQuery("FROM Security ", Security.class).getSingleResult());
    assertEquals(
        sec.hashCode(),
        session.createQuery("FROM Security", Security.class).getSingleResult().hashCode());

    // Identical security request that should have a different ID
    Security sec2 =
        new Security(
            "Incident Report",
            location,
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(sec2); // Load sec2 into the DB, set its ID

    assertNotEquals(sec, sec2); // Assert sec and sec2 aren't equal
    assertNotEquals(sec.hashCode(), sec2.hashCode()); // Assert their has hash codes are different

    // Completely different av request
    Security sec3 =
        new Security(
            "NewIncident Report",
            location,
            emp,
            new Date(2022 - 5 - 26),
            new Date(2022 - 6 - 2),
            ServiceRequest.Urgency.VERY_URGENT);
    session.persist(sec3); // Load sec3 into the DB, set its ID

    assertNotEquals(sec, sec3); // Assert sec and sec3 aren't equal
    assertNotEquals(sec.hashCode(), sec3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }

  /** Checks to see if toString makes a string in the same format specified in Security.java */
  @Test
  void testToString() {
    String sanToString = testSecurity.toString();
    assertEquals(sanToString, testSecurity.getClass().getSimpleName() + "_" + testSecurity.getId());
  }
}
