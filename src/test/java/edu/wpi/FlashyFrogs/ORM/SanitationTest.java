package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Creates iteration of Sanitation
public class SanitationTest {
  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  Sanitation testSan =
      new Sanitation(
          Sanitation.SanitationType.MOPPING,
          emp,
          new Date(2023 - 01 - 31),
          new Date(2023 - 02 - 01),
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
    testSan.setDateOfIncident(new Date(2023 - 01 - 31));
    testSan.setDateOfSubmission(new Date(2023 - 02 - 01));
    testSan.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testSan.setLocation(new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
  }

  /** Tests setter for sanitationType */
  @Test
  void setType() {
    testSan.setType(Sanitation.SanitationType.SWEEPING);
    assertEquals(Sanitation.SanitationType.SWEEPING, testSan.getType());
  }

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSan.setEmp(newEmp);
    assertEquals(newEmp, testSan.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSan.setAssignedEmp(newEmp);
    assertEquals(newEmp, testSan.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 01 - 17);
    testSan.setDateOfIncident(newDOI);
    assertEquals(newDOI, testSan.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 01 - 17);
    testSan.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testSan.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgency() {
    testSan.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testSan.getUrgency());
  }

  /** Tests setter for location */
  @Test
  void setLocation() {
    LocationName newLoc =
        new LocationName("NewLocLong", LocationName.LocationType.EXIT, "NewLocShort");
    testSan.setLocation(newLoc);
    assertEquals(newLoc, testSan.getLocation());
  }

  /** Tests if the equals in Sanitation.java correctly compares two Sanitation objects */
  //  @Test
  //  void testEquals() {
  //    Sanitation otherSan =
  //        new Sanitation(
  //            Sanitation.SanitationType.MOPPING,
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
  //            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
  //    assertEquals(testSan, otherSan);
  //  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void testToString() {
    String sanToString = testSan.toString();
    assertEquals(sanToString, testSan.getClass().getSimpleName() + "_" + testSan.getId());
  }
}
