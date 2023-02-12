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

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL);
  AudioVisual testAV =
      new AudioVisual(
          emp,
          new Date(2023 - 01 - 31),
          new Date(2023 - 02 - 01),
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
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testAV.setAssignedEmp(assignedEmp);
    testAV.setDateOfIncident(new Date(2023 - 01 - 31));
    testAV.setDateOfSubmission(new Date(2023 - 02 - 01));
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
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testAV.setEmp(newEmp);
    assertEquals(newEmp, testAV.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testAV.setAssignedEmp(newEmp);
    assertEquals(newEmp, testAV.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 01 - 17);
    testAV.setDateOfIncident(newDOI);
    assertEquals(newDOI, testAV.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 01 - 17);
    testAV.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testAV.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgency() {
    testAV.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testAV.getUrgency());
  }

  /** Tests setter for accoommodationType */
  @Test
  void setAccommodationType() {
    testAV.setAccommodationType(AudioVisual.AccommodationType.BOTH);
    assertEquals(AudioVisual.AccommodationType.BOTH, testAV.getAccommodationType());
  }

  /** Tests setter for patientFirstName */
  @Test
  void setPatientFirst() {
    testAV.setPatientFirstName("Steve");
    assertEquals("Steve", testAV.getPatientFirstName());
  }

  /** Tests setter for patientMiddleName */
  @Test
  void setPatientMiddle() {
    testAV.setPatientMiddleName("Does");
    assertEquals("Does", testAV.getPatientMiddleName());
  }

  /** Tests setter for patientLastName */
  @Test
  void setPatientLast() {
    testAV.setPatientLastName("Jobs");
    assertEquals("Jobs", testAV.getPatientLastName());
  }

  /** Tests setter for location */
  @Test
  void setLocation() {
    testAV.setLocation(new LocationName("Hello", LocationName.LocationType.CONF, "Hello"));
    assertEquals(
        new LocationName("Hello", LocationName.LocationType.CONF, "Hello"), testAV.getLocation());
  }

  @Test
  void setDateOfBirth() {
    testAV.setDateOfBirth(new Date(2001 - 1 - 1));
    assertEquals(new Date(2001 - 1 - 1), testAV.getDateOfBirth());
  }

  /**
   * Tests the equals and hash code methods for the AudioVisual class, ensures that fetched objects
   * are equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL);
    LocationName location = new LocationName("Name", LocationName.LocationType.EXIT, "name");

    session.persist(emp);
    session.persist(location);
    // Create the av request we will use
    AudioVisual av =
        new AudioVisual(
            emp,
            new Date(2023 - 01 - 31),
            new Date(2023 - 02 - 01),
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
            new Date(2023 - 01 - 31),
            new Date(2023 - 02 - 01),
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
            new Date(2024 - 02 - 20),
            new Date(2024 - 03 - 21),
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

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void testToString() {
    String sanToString = testAV.toString();
    assertEquals(sanToString, testAV.getClass().getSimpleName() + "_" + testAV.getId());
  }
}
