package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.*;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

// Creates iteration of Sanitation
public class ComputerServiceTest {
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
      connection.createMutationQuery("DELETE FROM ComputerService").executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
      connection.createMutationQuery("DELETE FROM LocationName").executeUpdate();
      connection.createMutationQuery("DELETE FROM User").executeUpdate();
      connection.createMutationQuery("DELETE FROM Department").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  ComputerService testCS =
      new ComputerService(
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT,
          ComputerService.DeviceType.LAPTOP,
          "Lenovo Rogue",
          "Bad battery life",
          ComputerService.ServiceType.HARDWARE_REPAIR);

  /** Reset testSan after each test */
  @BeforeEach
  @AfterEach
  public void resetTestSanitation() {
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testCS.setAssignedEmp(assignedEmp);
    testCS.setDateOfIncident(new Date(2023 - 1 - 31));
    testCS.setDateOfSubmission(new Date(2023 - 2 - 1));
    testCS.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testCS.setDeviceType(ComputerService.DeviceType.LAPTOP);
    testCS.setModel("Lenovo Rogue");
    testCS.setIssue("Bad battery life");
    testCS.setServiceType(ComputerService.ServiceType.HARDWARE_REPAIR);
  }

  /** Tests setter for emp */
  @Test
  public void changeEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testCS.setEmp(newEmp);
    assertEquals(newEmp, testCS.getEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearEmpTest() {
    testCS.setEmp(null);
    assertNull(testCS.getEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setEmpTest() {
    ComputerService test =
        new ComputerService(
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            ComputerService.DeviceType.PERSONAL,
            "a",
            "b",
            ComputerService.ServiceType.CONNECTION_ISSUE);
    test.setEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullEmployeeTest() {
    ComputerService test =
        new ComputerService(
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            ComputerService.DeviceType.KIOSK,
            "a",
            "b",
            ComputerService.ServiceType.MISC);
    test.setEmp(null);

    // Assert that the location is correct
    assertNull(test.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void changeAssignedEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testCS.setAssignedEmp(newEmp);
    assertEquals(newEmp, testCS.getAssignedEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearAssignedEmpTest() {
    testCS.setAssignedEmp(emp);
    testCS.setAssignedEmp(null);
    assertNull(testCS.getAssignedEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setAssignedEmpTest() {
    ComputerService test =
        new ComputerService(
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            ComputerService.DeviceType.PERSONAL,
            "a",
            "b",
            ComputerService.ServiceType.MISC);
    test.setAssignedEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getAssignedEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullAssignedEmployeeTest() {
    ComputerService test =
        new ComputerService(
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            ComputerService.DeviceType.KIOSK,
            "b",
            "as",
            ComputerService.ServiceType.SOFTWARE_REPAIR);
    test.setAssignedEmp(null);

    // Assert that the location is correct
    assertNull(test.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 1 - 17);
    testCS.setDateOfIncident(newDOI);
    assertEquals(newDOI, testCS.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 1 - 17);
    testCS.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testCS.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgency() {
    testCS.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testCS.getUrgency());
  }

  /** Tests setter for deviceType */
  @Test
  void setDeviceType() {
    testCS.setDeviceType(ComputerService.DeviceType.DESKTOP);
    assertEquals(ComputerService.DeviceType.DESKTOP, testCS.getDeviceType());
  }

  /** Tests setter for Model */
  @Test
  void setModel() {
    testCS.setModel("Shmock");
    assertEquals("Shmock", testCS.getModel());
  }

  /** Tests setter for Issue */
  @Test
  void setIssue() {
    testCS.setIssue("OONGA BOONGA");
    assertEquals("OONGA BOONGA", testCS.getIssue());
  }

  /** Tests setter for serviceType */
  @Test
  void setServiceType() {
    testCS.setServiceType(ComputerService.ServiceType.MISC);
    assertEquals(ComputerService.ServiceType.MISC, testCS.getServiceType());
  }

  /**
   * Tests the equals and hash code methods for the ComputerService class, ensures that fetched
   * objects are equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);

    session.persist(emp);
    // Create the cs request we will use
    ComputerService cs =
        new ComputerService(
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            ComputerService.DeviceType.LAPTOP,
            "Lenovo Rogue",
            "Bad battery life",
            ComputerService.ServiceType.HARDWARE_REPAIR);
    session.persist(cs);

    // Assert that the one thing in the database matches this
    assertEquals(
        cs, session.createQuery("FROM ComputerService", ComputerService.class).getSingleResult());
    assertEquals(
        cs.hashCode(),
        session
            .createQuery("FROM ComputerService", ComputerService.class)
            .getSingleResult()
            .hashCode());

    // Identical cs request that should have a different ID
    ComputerService cs2 =
        new ComputerService(
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            ComputerService.DeviceType.LAPTOP,
            "Lenovo Rogue",
            "Bad battery life",
            ComputerService.ServiceType.HARDWARE_REPAIR);
    session.persist(cs2); // Load acs2 into the DB, set its ID

    assertNotEquals(cs, cs2); // Assert cs and cs2 aren't equal
    assertNotEquals(cs.hashCode(), cs2.hashCode()); // Assert their has hash codes are different

    // Completely different cs request
    ComputerService cs3 =
        new ComputerService(
            emp,
            new Date(2024 - 2 - 20),
            new Date(2024 - 3 - 21),
            ServiceRequest.Urgency.VERY_URGENT,
            ComputerService.DeviceType.DESKTOP,
            "MacBook Pro",
            "No internet",
            ComputerService.ServiceType.CONNECTION_ISSUE);
    session.persist(cs3); // Load cs3 into the DB, set its ID

    assertNotEquals(cs, cs3); // Assert cs and cs3 aren't equal
    assertNotEquals(cs.hashCode(), cs3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void testToString() {
    String sanToString = testCS.toString();
    assertEquals(sanToString, testCS.getClass().getSimpleName() + "_" + testCS.getId());
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
    AudioVisual av =
        new AudioVisual(
            emp,
            new Date(201674 - 2 - 14),
            new Date(20126 - 1 - 12),
            ServiceRequest.Urgency.NOT_URGENT,
            AudioVisual.AccommodationType.VISUAL,
            "a",
            "d",
            "jh",
            location,
            new Date(2002 - 10 - 8));
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
    ComputerService sr =
        new ComputerService(
            emp,
            new Date(201674 - 2 - 14),
            new Date(20126 - 1 - 12),
            ServiceRequest.Urgency.NOT_URGENT,
            ComputerService.DeviceType.PERSONAL,
            "a",
            "d",
            ComputerService.ServiceType.MISC);
    session.persist(sr);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    session.flush();

    // Update the request
    session.refresh(sr);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(sr.getEmp()); // Assert the location is null

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
    ComputerService sr =
        new ComputerService(
            emp,
            new Date(201674 - 2 - 14),
            new Date(20126 - 1 - 12),
            ServiceRequest.Urgency.NOT_URGENT,
            ComputerService.DeviceType.KIOSK,
            "a",
            "d",
            ComputerService.ServiceType.HARDWARE_REPAIR);
    session.persist(sr);

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
    sr = session.createQuery("FROM ComputerService", ComputerService.class).getSingleResult();

    // Assert the location is not actually gone
    assertEquals(emp, session.find(User.class, emp.getId()));
    assertEquals(emp, sr.getEmp()); // Assert the location is null

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
    ComputerService sr =
        new ComputerService(
            assignedEmp,
            new Date(201674 - 2 - 14),
            new Date(20126 - 1 - 12),
            ServiceRequest.Urgency.NOT_URGENT,
            ComputerService.DeviceType.PERSONAL,
            "a",
            "d",
            ComputerService.ServiceType.CONNECTION_ISSUE);
    sr.setAssignedEmp(emp);
    session.persist(sr);

    // Change the enp
    session
        .createMutationQuery("DELETE FROM User WHERE id = :id")
        .setParameter("id", emp.getId())
        .executeUpdate();

    // Update the request
    session.refresh(sr);

    // Assert the location is actually gone
    assertNull(
        session
            .createQuery("FROM User WHERE id = :id", User.class)
            .setParameter("id", emp.getId())
            .uniqueResult());
    assertNull(sr.getAssignedEmp()); // Assert the location is null

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
    ComputerService av =
        new ComputerService(
            emp,
            new Date(201674 - 2 - 14),
            new Date(20126 - 1 - 12),
            ServiceRequest.Urgency.NOT_URGENT,
            ComputerService.DeviceType.PERSONAL,
            "a",
            "d",
            ComputerService.ServiceType.HARDWARE_REPAIR);
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
}
