package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.util.*;
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
    // Use a closure to manage the session to use
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      Transaction cleanupTransaction = connection.beginTransaction(); // Begin a cleanup transaction
      connection.createMutationQuery("DELETE FROM ComputerService ").executeUpdate(); // Do the drop
      connection.createMutationQuery("DELETE FROM ServiceRequest").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL);
  ComputerService testCS =
      new ComputerService(
          emp,
          new Date(2023 - 01 - 31),
          new Date(2023 - 02 - 01),
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
    testCS.setDateOfIncident(new Date(2023 - 01 - 31));
    testCS.setDateOfSubmission(new Date(2023 - 02 - 01));
    testCS.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testCS.setDeviceType(ComputerService.DeviceType.LAPTOP);
    testCS.setModel("Lenovo Rogue");
    testCS.setIssue("Bad battery life");
    testCS.setServiceType(ComputerService.ServiceType.HARDWARE_REPAIR);
  }

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testCS.setEmp(newEmp);
    assertEquals(newEmp, testCS.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testCS.setAssignedEmp(newEmp);
    assertEquals(newEmp, testCS.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 01 - 17);
    testCS.setDateOfIncident(newDOI);
    assertEquals(newDOI, testCS.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 01 - 17);
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

  /** Tests if the equals in Sanitation.java correctly compares two Sanitation objects */
  //  @Test
  //  void testEquals() {
  //    ComputerService otherCS =
  //        new ComputerService(
  //            "Wilson",
  //            "Softeng",
  //            "Wong",
  //            "Jonathan",
  //            "Elias",
  //            "Golden",
  //            ServiceRequest.EmpDept.CARDIOLOGY,
  //            ServiceRequest.EmpDept.MAINTENANCE,
  //            new Date(2023 - 01 - 31),
  //            new Date(2023 - 02 - 01),
  //            ServiceRequest.Urgency.MODERATELY_URGENT,
  //            ComputerService.DeviceType.LAPTOP,
  //            "Lenovo Rogue",
  //            "Bad battery life",
  //            ComputerService.ServiceType.HARDWARE_REPAIR);
  //    assertEquals(testCS, otherCS);
  //  }

  /**
   * Tests the equals and hash code methods for the ComputerService class, ensures that fetched
   * objects are equal
   */
  @Test
  public void testEqualsAndHashCode() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction = session.beginTransaction(); // Begin a transaction

    User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL);

    session.persist(emp);
    // Create the cs request we will use
    ComputerService cs =
        new ComputerService(
            emp,
            new Date(2023 - 01 - 31),
            new Date(2023 - 02 - 01),
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
            new Date(2023 - 01 - 31),
            new Date(2023 - 02 - 01),
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
            new Date(2024 - 02 - 20),
            new Date(2024 - 03 - 21),
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
}
