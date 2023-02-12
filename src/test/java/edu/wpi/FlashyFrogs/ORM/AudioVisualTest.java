package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

// Creates iteration of Sanitation
public class AudioVisualTest {
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
      connection.createMutationQuery("DELETE FROM AudioVisual").executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  private final Department sourceDept = new Department("a", "b");
  private final Department endDept = new Department("c", "d");
  private final User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL,
          sourceDept);
  private final User assignedEmp = new User("Jonathan", "Elias", "Golden",
          User.EmployeeType.MEDICAL, endDept);
  AudioVisual testAV =
      new AudioVisual(
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT,
          AudioVisual.AccommodationType.AUDIO,
          "Emre",
          "Rusen",
          "Sabaz",
          new LocationName("Name", LocationName.LocationType.EXIT, "name"),
          new Date(2001 - 12 - 8));

  /** Reset testSan after each test */
  @BeforeEach
  @AfterEach
  public void resetTestSanitation() {
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    emp.setDepartment(sourceDept);
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    assignedEmp.setDepartment(endDept);
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testAV.setAssignedEmp(assignedEmp);
    testAV.setDateOfIncident(new Date(2023 - 1 - 31));
    testAV.setDateOfSubmission(new Date(2023 - 2 - 1));
    testAV.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testAV.setAccommodationType(AudioVisual.AccommodationType.AUDIO);
    testAV.setPatientFirstName("Emre");
    testAV.setPatientMiddleName("Rusen");
    testAV.setPatientLastName("Sabaz");
    testAV.setLocation(new LocationName("Name", LocationName.LocationType.EXIT, "name"));
    testAV.setDateOfBirth(new Date(2001 - 12 - 8));
  }

  /** Tests setter for emp */
  @Test
  public void setEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, endDept);
    testAV.setEmp(newEmp);
    assertEquals(newEmp, testAV.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmpTest() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, sourceDept);
    testAV.setAssignedEmp(newEmp);
    assertEquals(newEmp, testAV.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  public void setDateOfIncidentTest() {
    Date newDOI = new Date(2002 - 1 - 17);
    testAV.setDateOfIncident(newDOI);
    assertEquals(newDOI, testAV.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  public void setDateOfSubmissionTest() {
    Date newDOS = new Date(2002 - 1 - 17);
    testAV.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testAV.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  public void setUrgencyTest() {
    testAV.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testAV.getUrgency());
  }

  /** Tests setter for accoommodationType */
  @Test
  public void setAccommodationTypeTest() {
    testAV.setAccommodationType(AudioVisual.AccommodationType.BOTH);
    assertEquals(AudioVisual.AccommodationType.BOTH, testAV.getAccommodationType());
  }

  /** Tests setter for patientFirstName */
  @Test
  public void setPatientFirstTest() {
    testAV.setPatientFirstName("Steve");
    assertEquals("Steve", testAV.getPatientFirstName());
  }

  /** Tests setter for patientMiddleName */
  @Test
  public void setPatientMiddleTest() {
    testAV.setPatientMiddleName("Does");
    assertEquals("Does", testAV.getPatientMiddleName());
  }

  /** Tests setter for patientLastName */
  @Test
  public void setPatientLastTest() {
    testAV.setPatientLastName("Jobs");
    assertEquals("Jobs", testAV.getPatientLastName());
  }

  /** Tests setter for location */
  @Test
  public void setLocationTest() {
    testAV.setLocation(new LocationName("Hello", LocationName.LocationType.CONF, "Hello"));
    assertEquals(
        new LocationName("Hello", LocationName.LocationType.CONF, "Hello"), testAV.getLocation());
  }

  @Test
  public void setDateOfBirthTest() {
    testAV.setDateOfBirth(new Date(2001 - 1 - 1));
    assertEquals(new Date(2001 - 1 - 1), testAV.getDateOfBirth());
  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  public void toStringTest() {
    String sanToString = testAV.toString();
    assertEquals(sanToString, testAV.getClass().getSimpleName() + "_" + testAV.getId());
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
    // Create the av request we will use
    AudioVisual av =
        new AudioVisual(
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            AudioVisual.AccommodationType.AUDIO,
            "Emre",
            "Rusen",
            "Sabaz",
            location,
            new Date(2001 - 12 - 8));
    session.persist(av);

    // Assert that the one thing in the database matches this
    assertEquals(av, session.createQuery("FROM AudioVisual", AudioVisual.class).getSingleResult());
    assertEquals(
        av.hashCode(),
        session.createQuery("FROM AudioVisual ", AudioVisual.class).getSingleResult().hashCode());

    // Identical av request that should have a different ID
    AudioVisual av2 =
        new AudioVisual(
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT,
            AudioVisual.AccommodationType.AUDIO,
            "Emre",
            "Rusen",
            "Sabaz",
            location,
            new Date(2001 - 12 - 8));
    session.persist(av2); // Load av2 into the DB, set its ID

    assertNotEquals(av, av2); // Assert av and av2 aren't equal
    assertNotEquals(av.hashCode(), av2.hashCode()); // Assert their has hash codes are different

    // Completely different av request
    AudioVisual av3 =
        new AudioVisual(
            emp,
            new Date(2024 - 2 - 20),
            new Date(2024 - 3 - 21),
            ServiceRequest.Urgency.VERY_URGENT,
            AudioVisual.AccommodationType.VISUAL,
            "Owen",
            "Matthew",
            "Krause",
            location,
            new Date(2002 - 11 - 2));
    session.persist(av3); // Load av3 into the DB, set its ID

    assertNotEquals(av, av3); // Assert av and av3 aren't equal
    assertNotEquals(av.hashCode(), av3.hashCode()); // Assert their hash codes are different

    transaction.rollback();
    session.close();
  }

  /**
   * Tests that deleting the emp this is referenced to sets it to null
   */
  @Test
  public void locationDeleteCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("b", "a", "d", User.EmployeeType.MEDICAL, sourceDept);
    LocationName location = new LocationName("q", LocationName.LocationType.EXIT, "name");

    session.persist(sourceDept);
    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    AudioVisual av =
        new AudioVisual(
            emp,
            new Date(2014 - 2 - 14),
            new Date(2026 - 1 - 12),
            ServiceRequest.Urgency.NOT_URGENT,
            AudioVisual.AccommodationType.VISUAL,
            "ab",
            "cd",
            "gjh",
            location,
            new Date(2002 - 12 - 8));
    session.persist(av);

    // Remove the location
    session.remove(location);

    // Update the request
    session.refresh(av);

    // Assert the location is actually gone
    assertNull(session.find(LocationName.class, location.getLongName()));
    assertNull(av.getLocation()); // Assert the location is null

    transaction.rollback();
    session.close();
  }

  /**
   * Tests that deleting the emp this is associated to with a query sets it to null
   */
  @Test
  public void locationDeleteCascadeQueryTest() {

  }

  /**
   * Test that updating the location cascades
   */
  @Test
  public void locationUpdateCascadeTest() {

  }

  /**
   * Tests that deleting the emp this is referenced to sets it to null
   */
  @Test
  public void empDeleteCascadeTest() {}

  /**
   * Tests that deleting the emp this is associated to with a query sets it to null
   */
  @Test
  public void empDeleteCascadeQueryTest() {

  }

  /**
   * Tests that updating the employee results in a cascade update failure
   */
  @Test
  public void empUpdateCascadeTest() {

  }

  /**
   * Tests that deleting the emp this is referenced to sets it to null
   */
  @Test
  public void assignedEmpDeleteCascadeTest() {}

  /**
   * Tests that deleting the emp this is associated to with a query sets it to null
   */
  @Test
  public void assignedEmpDeleteCascadeQueryTest() {

  }

  /**
   * Tests that updating the employee results in a cascade update failure
   */
  @Test
  public void assignedEmpUpdateCascadeTest() {

  }
}
