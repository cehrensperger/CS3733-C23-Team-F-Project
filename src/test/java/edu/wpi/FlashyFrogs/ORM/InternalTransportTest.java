package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.time.Instant;
import java.util.Date;
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
      connection.createMutationQuery("DELETE FROM LocationName ").executeUpdate();
      connection.createMutationQuery("DELETE FROM User").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  private final User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  private final User assignedEmp =
      new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  private final InternalTransport testIntTransp =
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
    testIntTransp.setTargetDate(new Date(2023 - 1 - 31));
    testIntTransp.setDateOfSubmission(new Date(2023 - 2 - 1));
    testIntTransp.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
  }

  /** Tests setter for emp */
  @Test
  public void changeEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testIntTransp.setEmp(newEmp);
    assertEquals(newEmp, testIntTransp.getEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearEmpTest() {
    testIntTransp.setEmp(null);
    assertNull(testIntTransp.getEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setEmpTest() {
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
    test.setEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullEmployeeTest() {
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
    test.setEmp(null);

    // Assert that the location is correct
    assertNull(test.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void changeAssignedEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testIntTransp.setAssignedEmp(newEmp);
    assertEquals(newEmp, testIntTransp.getAssignedEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearAssignedEmpTest() {
    testIntTransp.setAssignedEmp(emp);
    testIntTransp.setAssignedEmp(null);
    assertNull(testIntTransp.getAssignedEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setAssignedEmpTest() {
    InternalTransport test =
        new InternalTransport(
            new Date(),
            new LocationName("A", LocationName.LocationType.INFO, "B"),
            null,
            "A",
            "B",
            "C",
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    test.setAssignedEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getAssignedEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullAssignedEmployeeTest() {
    InternalTransport test =
        new InternalTransport(
            new Date(),
            null,
            new LocationName("b", LocationName.LocationType.SERV, "B"),
            "D",
            "E",
            "F",
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    test.setAssignedEmp(null);
    test.setAssignedEmp(null);

    // Assert that the location is correct
    assertNull(test.getAssignedEmp());
  }

  /** Tests setter for dateOfBirth */
  @Test
  void setDateOfBirthTest() {
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
  public void setOldLocationTest() {
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
  public void nullToNullOldLocationTest() {
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

  /** Tests setter for location */
  @Test
  public void updateNewLocationLocationTest() {
    testIntTransp.setNewLoc(new LocationName("Hello", LocationName.LocationType.CONF, "Hello"));
    assertEquals(
        new LocationName("Hello", LocationName.LocationType.CONF, "Hello"),
        testIntTransp.getNewLoc());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearNewLocationTest() {
    testIntTransp.setNewLoc(null);
    assertNull(testIntTransp.getNewLoc());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setNewLocationTest() {
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
    test.setNewLoc(new LocationName("a", LocationName.LocationType.INFO, "B"));

    // Assert that the location is correct
    assertEquals(new LocationName("a", LocationName.LocationType.INFO, "B"), test.getNewLoc());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullNewLocationTest() {
    InternalTransport test =
        new InternalTransport(
            new Date(),
            null,
            new LocationName("A", LocationName.LocationType.BATH, "C"),
            "b",
            "c",
            "d",
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT);
    test.setNewLoc(null);

    // Assert that the location is correct
    assertNull(test.getNewLoc());
  }

  /** Tests setter for patientName */
  @Test
  void setPatientNameTest() {
    String patientFirstName = "Jimboo";
    String patientLastName = "Jones";
    testIntTransp.setPatientFirstName(patientFirstName);
    testIntTransp.setPatientLastName(patientLastName);
    assertEquals(patientFirstName, testIntTransp.getPatientFirstName());
    assertEquals(patientLastName, testIntTransp.getPatientLastName());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncidentTest() {
    Date newDOI = new Date(2002 - 1 - 17);
    testIntTransp.setTargetDate(newDOI);
    assertEquals(newDOI, testIntTransp.getTargetDate());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmissionTest() {
    Date newDOS = new Date(2002 - 1 - 17);
    testIntTransp.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testIntTransp.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgencyTest() {
    testIntTransp.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testIntTransp.getUrgency());
  }

  /**
   * Tests the equals and hash code methods for the InternalTransport class, ensures that fetched
   * objects are equal
   */
  @Test
  public void equalsAndHashCodeTest() {
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
  void toStringTest() {
    String sanToString = testIntTransp.toString();
    assertEquals(
        sanToString, testIntTransp.getClass().getSimpleName() + "_" + testIntTransp.getId());
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void startLocationDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("b", "a", "d", User.EmployeeType.MEDICAL, null);
    LocationName location = new LocationName("q", LocationName.LocationType.EXIT, "name");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            null,
            location,
            "B",
            "C",
            "D",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    session.persist(av);

    // Remove the location
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate();

    session.flush();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(session.createQuery("FROM LocationName", LocationName.class).uniqueResult());
    assertNull(av.getOldLoc()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Test that updating the location cascades */
  @Test
  public void startLocationUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("jhj", "aew", "hgfd", User.EmployeeType.ADMIN, null);
    LocationName location = new LocationName("b", LocationName.LocationType.EXIT, "a");
    LocationName newLocation = new LocationName("new", LocationName.LocationType.SERV, "n");

    session.persist(newLocation);
    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            newLocation,
            location,
            "D",
            "A",
            "D",
            emp,
            new Date(),
            Date.from(Instant.ofEpochSecond(100)),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(av);

    // Change the location
    session
        .createMutationQuery(
            "UPDATE LocationName SET longName = 'newName' WHERE longName = :oldName")
        .setParameter("oldName", location.getLongName())
        .executeUpdate();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        session.find(LocationName.class, "newName"));
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        av.getOldLoc()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void endLocationDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("b", "a", "d", User.EmployeeType.MEDICAL, null);
    LocationName location = new LocationName("q", LocationName.LocationType.EXIT, "name");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            null,
            "B",
            "C",
            "D",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    session.persist(av);

    // Remove the location
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate();

    session.flush();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(session.createQuery("FROM LocationName", LocationName.class).uniqueResult());
    assertNull(av.getNewLoc()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Test that updating the location cascades */
  @Test
  public void endLocationUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("jhj", "aew", "hgfd", User.EmployeeType.ADMIN, null);
    LocationName location = new LocationName("b", LocationName.LocationType.EXIT, "a");
    LocationName newLocation = new LocationName("new", LocationName.LocationType.SERV, "n");

    session.persist(newLocation);
    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            newLocation,
            "D",
            "A",
            "D",
            emp,
            new Date(),
            Date.from(Instant.ofEpochSecond(100)),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(av);

    // Change the location
    session
        .createMutationQuery(
            "UPDATE LocationName SET longName = 'newName' WHERE longName = :oldName")
        .setParameter("oldName", location.getLongName())
        .executeUpdate();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        session.find(LocationName.class, "newName"));
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        av.getNewLoc()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void bothLocationDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("b", "a", "d", User.EmployeeType.MEDICAL, null);
    LocationName location = new LocationName("q", LocationName.LocationType.EXIT, "name");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            location,
            "B",
            "C",
            "D",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    session.persist(av);

    // Remove the location
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate();

    session.flush();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(session.createQuery("FROM LocationName", LocationName.class).uniqueResult());
    assertNull(av.getOldLoc()); // Assert the location is null
    assertNull(av.getNewLoc());

    transaction.rollback();
    session.close();
  }

  /** Test that updating the location cascades */
  @Test
  public void bothLocationUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("jhj", "aew", "hgfd", User.EmployeeType.ADMIN, null);
    LocationName location = new LocationName("b", LocationName.LocationType.EXIT, "a");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            location,
            "D",
            "A",
            "D",
            emp,
            new Date(),
            Date.from(Instant.ofEpochSecond(100)),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(av);

    // Change the location
    session.createMutationQuery("UPDATE LocationName SET longName = 'newName'").executeUpdate();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        session.find(LocationName.class, "newName"));
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"),
        av.getOldLoc()); // Assert the location is null
    assertEquals(
        new LocationName("newName", LocationName.LocationType.EXIT, "name"), av.getNewLoc());

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is referenced to sets it to null */
  @Test
  public void empDeleteCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            null,
            location,
            "D",
            "A",
            "D",
            emp,
            new Date(),
            Date.from(Instant.ofEpochSecond(100)),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    session.persist(av);

    session.flush();

    // Change the emp
    session.remove(emp);

    session.flush();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(session.find(User.class, emp.getId()));
    assertNull(av.getEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void empDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            location,
            "B",
            "C",
            "E",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT);
    session.persist(av);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    session.flush();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(av.getEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that updating the employee results in a cascade update failure */
  @Test
  public void empUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            null,
            "something",
            "b",
            "jim",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    session.persist(av);

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
    av = session.createQuery("FROM InternalTransport ", InternalTransport.class).getSingleResult();

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, av.getEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void assignedEmpDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(assignedEmp);
    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            null,
            null,
            "b",
            "c",
            "d",
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    av.setAssignedEmp(emp);
    session.persist(av);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(av.getAssignedEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that updating the employee results in a cascade update failure */
  @Test
  public void assignedEmpUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(assignedEmp);
    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            location,
            "E",
            "G",
            "H",
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    av.setAssignedEmp(emp);
    session.persist(av);

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
    session.refresh(av);

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, av.getAssignedEmp()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void bothEmpDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            null,
            null,
            "b",
            "c",
            "d",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    av.setAssignedEmp(emp);
    session.persist(av);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(av.getAssignedEmp()); // Assert the location is null
    assertNull(av.getEmp());

    transaction.rollback();
    session.close();
  }

  /** Tests that updating the employee results in a cascade update failure */
  @Test
  public void bothEmpUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("basdf", "axcvb", "dxcbv", User.EmployeeType.STAFF, null);
    LocationName location = new LocationName("qwq", LocationName.LocationType.EXIT, "zx");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    InternalTransport av =
        new InternalTransport(
            new Date(),
            location,
            location,
            "E",
            "G",
            "H",
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT);
    av.setAssignedEmp(emp);
    session.persist(av);

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
    session.refresh(av);

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, av.getAssignedEmp()); // Assert the location is null
    assertEquals(emp, av.getEmp());

    transaction.rollback();
    session.close();
  }
}
