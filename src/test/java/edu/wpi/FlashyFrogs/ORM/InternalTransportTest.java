package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class InternalTransportTest {
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
      connection
          .createMutationQuery("DELETE FROM InternalTransport")
          .executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  InternalTransport testIntTransp =
      new InternalTransport(
          new Date(2002 - 10 - 2),
          new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName"),
          new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName"),
          "John",
          "B",
          "Doe",
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT);

  /** Reset testInternalTransport after each test */
  @BeforeEach
  @AfterEach
  public void resetTestInternalTransport() {
    testIntTransp.setDateOfBirth(new Date(2002 - 10 - 2));
    testIntTransp.setNewLoc(
        new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName"));
    testIntTransp.setOldLoc(
        new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName"));
    testIntTransp.setPatientFirstName("John");
    testIntTransp.setPatientMiddleName("B");
    testIntTransp.setPatientLastName("Doe");
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testIntTransp.setAssignedEmp(assignedEmp);
    testIntTransp.setDateOfIncident(new Date(2023 - 1 - 31));
    testIntTransp.setDateOfSubmission(new Date(2023 - 2 - 1));
    testIntTransp.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
  }

  /** Tests setter for dateOfBirth */
  @Test
  void setDateOfBirth() {
    Date newDate = new Date(2002 - 1 - 17);
    testIntTransp.setDateOfBirth(newDate);
    assertEquals(newDate, testIntTransp.getDateOfBirth());
  }

  /** Tests setter for location */
  @Test
  public void updateOldLocationLocationTest() {
    testIntTransp.setOldLoc(new LocationName("Hello", LocationName.LocationType.CONF, "Hello"));
    assertEquals(
        new LocationName("Hello", LocationName.LocationType.CONF, "Hello"),
        testIntTransp.getOldLoc());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearOldLocationTest() {
    testIntTransp.setOldLoc(null);
    assertNull(testIntTransp.getOldLoc());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setLocationTest() {
    InternalTransport test =
        new InternalTransport(
            new Date(),
            null,
            null,
            "a",
            "b",
            "c",
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT);
    test.setOldLoc(new LocationName("a", LocationName.LocationType.INFO, "B"));

    // Assert that the location is correct
    assertEquals(new LocationName("a", LocationName.LocationType.INFO, "B"), test.getOldLoc());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullLocationTest() {
    InternalTransport test =
        new InternalTransport(
            new Date(),
            null,
            null,
            "b",
            "c",
            "d",
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT);
    test.setOldLoc(null);

    // Assert that the location is correct
    assertNull(test.getOldLoc());
  }

  /** Tests setter for oldLoc */
  @Test
  void setOldLoc() {
    LocationName newOldLoc =
        new LocationName("NewOldLocLong", LocationName.LocationType.EXIT, "NewOldLocShort");
    testIntTransp.setOldLoc(newOldLoc);
    assertEquals(newOldLoc, testIntTransp.getOldLoc());
  }

  /** Tests setter for patientName */
  @Test
  void setPatientName() {
    String patientFirstName = "Jimboo";
    String patientLastName = "Jones";
    testIntTransp.setPatientFirstName(patientFirstName);
    testIntTransp.setPatientLastName(patientLastName);
    assertEquals(patientFirstName, testIntTransp.getPatientFirstName());
    assertEquals(patientLastName, testIntTransp.getPatientLastName());
  }

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testIntTransp.setEmp(newEmp);
    assertEquals(newEmp, testIntTransp.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testIntTransp.setAssignedEmp(newEmp);
    assertEquals(newEmp, testIntTransp.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 1 - 17);
    testIntTransp.setDateOfIncident(newDOI);
    assertEquals(newDOI, testIntTransp.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 1 - 17);
    testIntTransp.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testIntTransp.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgency() {
    testIntTransp.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testIntTransp.getUrgency());
  }

  /**
   * Tests the equals and hash code methods for the InternalTransport class, ensures that fetched
   * objects are equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);

    LocationName loc1 =
        new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName");
    LocationName loc2 =
        new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName");

    session.persist(emp);
    session.persist(loc1);
    session.persist(loc2);
    // Create the transport request we will use
    InternalTransport it =
        new InternalTransport(
            new Date(2002 - 10 - 2),
            loc1,
            loc2,
            "John",
            "B",
            "Doe",
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(it);

    // Assert that the one thing in the database matches this
    assertEquals(
        it,
        session.createQuery("FROM InternalTransport", InternalTransport.class).getSingleResult());
    assertEquals(
        it.hashCode(),
        session
            .createQuery("FROM InternalTransport", InternalTransport.class)
            .getSingleResult()
            .hashCode());

    // Identical transport request that should have a different ID
    InternalTransport it2 =
        new InternalTransport(
            new Date(2002 - 10 - 2),
            loc1,
            loc2,
            "John",
            "B",
            "Doe",
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(it2); // Load it2 into the DB, set its ID

    assertNotEquals(it, it2); // Assert it and it2 aren't equal
    assertNotEquals(it.hashCode(), it2.hashCode()); // Assert their has hash codes are different

    // Completely different transport request
    InternalTransport it3 =
        new InternalTransport(
            new Date(2001 - 11 - 22),
            loc2,
            loc1,
            "Jane",
            "L",
            "Smith",
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.VERY_URGENT);
    session.persist(it3); // Load it3 into the DB, set its ID

    assertNotEquals(it, it3); // Assert it and it3 aren't equal
    assertNotEquals(it.hashCode(), it3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }

  /**
   * Checks to see if toString makes a string in the same format specified in
   * InternalTransportTest.java
   */
  @Test
  void testToString() {
    String sanToString = testIntTransp.toString();
    assertEquals(
        sanToString, testIntTransp.getClass().getSimpleName() + "_" + testIntTransp.getId());
  }
}
