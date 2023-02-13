package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Creates iteration of Sanitation
public class SanitationTest {
  private final User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  private final User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
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
    testSan.setDateOfIncident(new Date(2023 - 1 - 31));
    testSan.setDateOfSubmission(new Date(2023 - 2 - 1));
    testSan.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testSan.setLocation(new LocationName("LongName", LocationName.LocationType.HALL,
            "ShortName"));
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
        new Sanitation(Sanitation.SanitationType.SWEEPING, null, new Date(), new Date(),
                ServiceRequest.Urgency.NOT_URGENT,
                new LocationName("A", LocationName.LocationType.BATH, "B"));
    test.setEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null),
            test.getEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullEmployeeTest() {
    Sanitation test =
        new Sanitation(Sanitation.SanitationType.MOPPING, null, new Date(), new Date(),
                ServiceRequest.Urgency.VERY_URGENT, null);
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
        new Sanitation(Sanitation.SanitationType.SWEEPING, null, new Date(), new Date(),
                ServiceRequest.Urgency.MODERATELY_URGENT, null);
    test.setAssignedEmp(new User("a", "b", "c", User.EmployeeType.MEDICAL, null));

    // Assert that the location is correct
    assertEquals(new User("a", "b", "c", User.EmployeeType.MEDICAL, null),
            test.getAssignedEmp());
  }

  /** Starts the location name as null and sets it to null */
  @Test
  public void nullToNullAssignedEmployeeTest() {
    Sanitation test =
        new Sanitation(Sanitation.SanitationType.SWEEPING, assignedEmp, new Date(), new Date(),
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
    testSan.setDateOfIncident(newDOI);
    assertEquals(newDOI, testSan.getDateOfIncident());
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
  void setLocationTest() {
    LocationName newLoc =
        new LocationName("NewLocLong", LocationName.LocationType.EXIT, "NewLocShort");
    testSan.setLocation(newLoc);
    assertEquals(newLoc, testSan.getLocation());
  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void toStringTest() {
    String sanToString = testSan.toString();
    assertEquals(sanToString, testSan.getClass().getSimpleName() + "_" + testSan.getId());
  }
}
