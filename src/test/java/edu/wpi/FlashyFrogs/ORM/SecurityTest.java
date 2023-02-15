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

  private final Department sourceDept = new Department("a", "b");
  private final Department endDept = new Department("c", "d");
  private final User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  private final User assignedEmp =
      new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  private final Security testSecurity =
      new Security(
          "Incident Report",
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT,
          Security.ThreatType.INTRUDER);

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
    testSecurity.setDate(new Date(2023 - 1 - 31));
    testSecurity.setDateOfSubmission(new Date(2023 - 2 - 1));
    testSecurity.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testSecurity.setThreatType(Security.ThreatType.INTRUDER);
  }

  /** Tests setter for incidentReport */
  @Test
  void setIncidentReport() {
    testSecurity.setIncidentReport("Something Else");
    assertEquals("Something Else", testSecurity.getIncidentReport());
  }

  /** Tests setter for emp */
  @Test
  public void changeEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, endDept);
    testSecurity.setEmp(newEmp);
    assertEquals(newEmp, testSecurity.getEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearEmpTest() {
    testSecurity.setEmp(null);
    assertNull(testSecurity.getEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setEmpTest() {
    Security test =
        new Security(
            "R",
            null,
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            Security.ThreatType.PATIENT);
    test.setEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullEmployeeTest() {
    Security test =
        new Security(
            "BSDFSDF",
            null,
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            Security.ThreatType.WEAPON);
    test.setEmp(null);

    // Assert that the location is correct
    assertNull(test.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void changeAssignedEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, sourceDept);
    testSecurity.setAssignedEmp(newEmp);
    assertEquals(newEmp, testSecurity.getAssignedEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearAssignedEmpTest() {
    testSecurity.setAssignedEmp(emp);
    testSecurity.setAssignedEmp(null);
    assertNull(testSecurity.getAssignedEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setAssignedEmpTest() {
    Security test =
        new Security(
            "there is a goose",
            null,
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.INTRUDER);
    test.setAssignedEmp(null);
    test.setAssignedEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getAssignedEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullAssignedEmployeeTest() {
    Security test =
        new Security(
            "incident",
            new LocationName("b", LocationName.LocationType.INFO, "a"),
            new User("b", "a", "b", User.EmployeeType.ADMIN, null),
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            Security.ThreatType.WEAPON);
    test.setAssignedEmp(null);
    test.setAssignedEmp(null);

    // Assert that the location is correct
    assertNull(test.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 1 - 17);
    testSecurity.setDate(newDOI);
    assertEquals(newDOI, testSecurity.getDate());
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

  /** Checks to see if toString makes a string in the same format specified in Security.java */
  @Test
  void testToString() {
    String sanToString = testSecurity.toString();
    assertEquals(sanToString, testSecurity.getClass().getSimpleName() + "_" + testSecurity.getId());
  }

  /** Checks to se if toString makes a string in the same format specified in Security.java */
  @Test
  void testThreatToString() {
    Security.ThreatType none = Security.ThreatType.NONE;
    Security.ThreatType intruder = Security.ThreatType.INTRUDER;
    Security.ThreatType weapon = Security.ThreatType.WEAPON;
    Security.ThreatType patient = Security.ThreatType.PATIENT;

    assertEquals("No Threat", none.toString());
    assertEquals("Intruder", intruder.toString());
    assertEquals("Weapon", weapon.toString());
    assertEquals("Patient", patient.toString());
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.WEAPON);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.WEAPON);
    session.persist(sec2); // Load sec2 into the DB, set its ID

    assertNotEquals(sec, sec2); // Assert sec and sec2 aren't equal
    assertNotEquals(sec.hashCode(), sec2.hashCode()); // Assert their has hash codes are different

    // Completely different security request
    Security sec3 =
        new Security(
            "NewIncident Report",
            location,
            emp,
            new Date(2022 - 5 - 26),
            new Date(2022 - 6 - 2),
            ServiceRequest.Urgency.VERY_URGENT,
            Security.ThreatType.INTRUDER);
    session.persist(sec3); // Load sec3 into the DB, set its ID

    assertNotEquals(sec, sec3); // Assert sec and sec3 aren't equal
    assertNotEquals(sec.hashCode(), sec3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the location this is associated to with a query sets it to null */
  @Test
  public void locationDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("b", "a", "d", User.EmployeeType.MEDICAL, sourceDept);
    LocationName location = new LocationName("q", LocationName.LocationType.EXIT, "name");

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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.NONE);
    session.persist(sec);

    // Remove the location
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate();

    session.flush();

    // Update the request
    session.refresh(sec);

    // Assert the location is actually gone
    assertNull(session.createQuery("FROM LocationName", LocationName.class).uniqueResult());
    assertNull(sec.getLocation()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Test that updating the location cascades */
  @Test
  public void locationUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("jhj", "aew", "hgfd", User.EmployeeType.ADMIN, endDept);
    LocationName location = new LocationName("b", LocationName.LocationType.EXIT, "a");

    session.persist(endDept);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.PATIENT);
    session.persist(sec);

    // Change the location
    session.createMutationQuery("UPDATE LocationName SET longName = 'newName'").executeUpdate();

    // Update the request
    session.refresh(sec);

    // Assert the location is actually gone
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        session.find(LocationName.class, "newName"));
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        sec.getLocation()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is referenced to sets it to null */
  @Test
  public void empDeleteCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(endDept);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.PATIENT);
    session.persist(sec);

    session.flush();

    // Change the emp
    session.remove(emp);

    session.flush();

    // Update the request
    session.refresh(sec);

    // Assert the location is actually gone
    assertNull(session.find(User.class, emp.getId()));
    assertNull(sec.getEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void empDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(endDept);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.PATIENT);
    session.persist(sec);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    session.flush();

    // Update the request
    session.refresh(sec);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(sec.getEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that updating the employee results in a cascade update failure */
  @Test
  public void empUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(endDept);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.PATIENT);
    session.persist(sec);

    // Commit stuff so we can access it later (it's persisted)
    transaction.commit();
    transaction = session.beginTransaction();

    // Change the enp
    assertThrows(
        Exception.class,
        () ->
            session
                .createMutationQuery("UPDATE User SET id = 999 WHERE id = :id")
                .setParameter("id", emp.getId())
                .executeUpdate());

    transaction.rollback(); // This transaction is trash due to the SQL error
    transaction = session.beginTransaction(); // Create a new transaction

    // Update the request
    session.refresh(sec);

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, sec.getEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void assignedEmpDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(assignedEmp);
    session.persist(endDept);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.NONE);
    sec.setAssignedEmp(emp);
    session.persist(sec);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    // Update the request
    session.refresh(sec);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(sec.getAssignedEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that updating the employee results in a cascade update failure */
  @Test
  public void assignedEmpUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(endDept);
    session.persist(assignedEmp);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.NONE);
    sec.setAssignedEmp(emp);
    session.persist(sec);

    transaction.commit(); // Commit what we have, so that we can get it after the failure

    transaction = session.beginTransaction(); // Open a new transaction
    // Change the enp
    assertThrows(
        Exception.class,
        () ->
            session
                .createMutationQuery("UPDATE User SET id = 999 WHERE id = :id")
                .setParameter("id", emp.getId())
                .executeUpdate());

    session.flush();

    transaction.rollback(); // End that transaction

    transaction = session.beginTransaction();

    // Update the request
    session.refresh(sec);

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, sec.getAssignedEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void bothEmpDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(assignedEmp);
    session.persist(endDept);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.WEAPON);
    sec.setAssignedEmp(emp);
    session.persist(sec);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    // Update the request
    session.refresh(sec);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(sec.getAssignedEmp()); // Assert the location is null
    assertNull(sec.getEmp());

    transaction.rollback();
    session.close();
  }

  /** Tests that updating the employee results in a cascade update failure */
  @Test
  public void bothEmpUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, endDept);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(endDept);
    session.persist(assignedEmp);
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            Security.ThreatType.NONE);
    sec.setAssignedEmp(emp);
    session.persist(sec);

    transaction.commit(); // Commit what we have, so that we can get it after the failure

    transaction = session.beginTransaction(); // Open a new transaction
    // Change the enp
    assertThrows(
        Exception.class,
        () ->
            session
                .createMutationQuery("UPDATE User SET id = 999 WHERE id = :id")
                .setParameter("id", emp.getId())
                .executeUpdate());

    session.flush();

    transaction.rollback(); // End that transaction

    transaction = session.beginTransaction();

    // Update the request
    session.refresh(sec);

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, sec.getAssignedEmp()); // Assert the location is null
    assertEquals(emp, sec.getEmp());

    transaction.rollback();
    session.close();
  }
}
