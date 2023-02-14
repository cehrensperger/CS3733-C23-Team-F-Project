package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.time.Instant;
import java.util.Date;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

// Creates iteration of Sanitation
public class SanitationTest {
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
      connection.createMutationQuery("DELETE FROM Sanitation").executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
      connection.createMutationQuery("DELETE FROM LocationName").executeUpdate();
      connection.createMutationQuery("DELETE FROM User").executeUpdate();
      connection.createMutationQuery("DELETE FROM Department").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  private final User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  private final User assignedEmp =
      new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  private final Sanitation testSan =
      new Sanitation(
          Sanitation.SanitationType.MOPPING,
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT,
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));

  /** Reset testSan after each test */
  @BeforeEach
  @AfterEach
  public void resetTestSanitation() {
    testSan.setType(Sanitation.SanitationType.MOPPING);
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testSan.setAssignedEmp(assignedEmp);
    testSan.setTargetDate(new Date(2023 - 1 - 31));
    testSan.setDateOfSubmission(new Date(2023 - 2 - 1));
    testSan.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testSan.setLocation(new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
  }

  /** Tests setter for sanitationType */
  @Test
  public void setTypeTest() {
    testSan.setType(Sanitation.SanitationType.SWEEPING);
    assertEquals(Sanitation.SanitationType.SWEEPING, testSan.getType());
  }

  /** Tests setter for emp */
  @Test
  public void changeEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSan.setEmp(newEmp);
    assertEquals(newEmp, testSan.getEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearEmpTest() {
    testSan.setEmp(null);
    assertNull(testSan.getEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setEmpTest() {
    Sanitation test =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            new LocationName("A", LocationName.LocationType.BATH, "B"));
    test.setEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullEmployeeTest() {
    Sanitation test =
        new Sanitation(
            Sanitation.SanitationType.MOPPING,
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            null);
    test.setEmp(null);

    // Assert that the location is correct
    assertNull(test.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void changeAssignedEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSan.setAssignedEmp(newEmp);
    assertEquals(newEmp, testSan.getAssignedEmp());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearAssignedEmpTest() {
    testSan.setAssignedEmp(emp);
    testSan.setAssignedEmp(null);
    assertNull(testSan.getAssignedEmp());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setAssignedEmpTest() {
    Sanitation test =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            null,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            null);
    test.setAssignedEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null), test.getAssignedEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullAssignedEmployeeTest() {
    Sanitation test =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            new LocationName("B", LocationName.LocationType.INFO, "E"));
    test.setAssignedEmp(null);
    test.setAssignedEmp(null);

    // Assert that the location is correct
    assertNull(test.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncidentTest() {
    Date newDOI = new Date(2002 - 1 - 17);
    testSan.setTargetDate(newDOI);
    assertEquals(newDOI, testSan.getTargetDate());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmissionTest() {
    Date newDOS = new Date(2002 - 1 - 17);
    testSan.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testSan.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgencyTest() {
    testSan.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testSan.getUrgency());
  }

  /** Tests setter for location */
  @Test
  public void updateLocationTest() {
    testSan.setLocation(new LocationName("Hello", LocationName.LocationType.CONF, "Hello"));
    assertEquals(
        new LocationName("Hello", LocationName.LocationType.CONF, "Hello"), testSan.getLocation());
  }

  /** Tests that the department clears (something -> null) correctly */
  @Test
  public void clearLocationTest() {
    testSan.setLocation(null);
    assertNull(testSan.getLocation());
  }

  /** Starts the location as null, then sets it to be something */
  @Test
  public void setLocationTest() {
    Sanitation test =
        new Sanitation(
            Sanitation.SanitationType.VACUUMING,
            assignedEmp,
            Date.from(Instant.ofEpochSecond(100)),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            null);
    test.setLocation(new LocationName("a", LocationName.LocationType.INFO, "B"));

    // Assert that the location is correct
    assertEquals(new LocationName("a", LocationName.LocationType.INFO, "B"), test.getLocation());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullLocationTest() {
    Sanitation test =
        new Sanitation(
            Sanitation.SanitationType.MOPPING,
            emp,
            new Date(),
            Date.from(Instant.ofEpochSecond(10000)),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            null);
    test.setLocation(null);

    // Assert that the location is correct
    assertNull(test.getLocation());
  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void toStringTest() {
    String sanToString = testSan.toString();
    assertEquals(sanToString, testSan.getClass().getSimpleName() + "_" + testSan.getId());
  }

  /**
   * Tests the equals and hash code methods for the AudioVisual class, ensures that fetched objects
   * are equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
    LocationName location = new LocationName("Name", LocationName.LocationType.EXIT, "name");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            location);
    session.persist(av);

    // Assert that the one thing in the database matches this
    assertEquals(av, session.createQuery("FROM Sanitation ", Sanitation.class).getSingleResult());
    assertEquals(
        av.hashCode(),
        session.createQuery("FROM Sanitation ", Sanitation.class).getSingleResult().hashCode());

    // Identical av request that should have a different ID
    Sanitation av2 =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            location);
    session.persist(av2); // Load av2 into the DB, set its ID

    assertNotEquals(av, av2); // Assert av and av2 aren't equal
    assertNotEquals(av.hashCode(), av2.hashCode()); // Assert their has hash codes are different

    session.persist(assignedEmp); // Save the emp we will use, so we can use it

    // Completely different av request
    Sanitation av3 =
        new Sanitation(
            Sanitation.SanitationType.MOPPING,
            assignedEmp,
            Date.from(Instant.ofEpochSecond(100000)),
            Date.from(Instant.EPOCH),
            ServiceRequest.Urgency.NOT_URGENT,
            null);
    session.persist(av3); // Load av3 into the DB, set its ID

    assertNotEquals(av, av3); // Assert av and av3 aren't equal
    assertNotEquals(av.hashCode(), av3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }

  /** Tests that deleting the emp this is associated to with a query sets it to null */
  @Test
  public void locationDeleteCascadeQueryTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("b", "a", "d", User.EmployeeType.MEDICAL, null);
    LocationName location = new LocationName("q", LocationName.LocationType.EXIT, "name");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            location);
    session.persist(av);

    // Remove the location
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate();

    session.flush();

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(session.createQuery("FROM LocationName", LocationName.class).uniqueResult());
    assertNull(av.getLocation()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /** Test that updating the location cascades */
  @Test
  public void locationUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("jhj", "aew", "hgfd", User.EmployeeType.ADMIN, null);
    LocationName location = new LocationName("b", LocationName.LocationType.EXIT, "a");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.MOPPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            location);
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
        av.getLocation()); // Assert the location is null

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
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            location);
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

    session.persist(emp);
    // Create the av request we will use
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.MOPPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.VERY_URGENT,
            null);
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

    session.persist(emp);
    // Create the av request we will use
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            null);
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
    av = session.createQuery("FROM Sanitation", Sanitation.class).getSingleResult();

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
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            location);
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

    session.persist(assignedEmp);
    session.persist(emp);
    // Create the av request we will use
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            assignedEmp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            null);
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
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            location);
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
    Sanitation av =
        new Sanitation(
            Sanitation.SanitationType.SWEEPING,
            emp,
            new Date(),
            new Date(),
            ServiceRequest.Urgency.NOT_URGENT,
            location);
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
