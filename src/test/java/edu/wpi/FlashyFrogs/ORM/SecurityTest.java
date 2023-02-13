package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SecurityTest {
  // Creates iteration of LocationName

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL, null);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL, null);
  Security testSecurity =
      new Security(
          "Incident Report",
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          emp,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT);

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
    testSecurity.setDateOfIncident(new Date(2023 - 1 - 31));
    testSecurity.setDateOfSubmission(new Date(2023 - 2 - 1));
    testSecurity.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
  }

  /** Tests setter for incidentReport */
  @Test
  void setIncidentReport() {
    testSecurity.setIncidentReport("Something Else");
    assertEquals("Something Else", testSecurity.getIncidentReport());
  }

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSecurity.setEmp(newEmp);
    assertEquals(newEmp, testSecurity.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN, null);
    testSecurity.setAssignedEmp(newEmp);
    assertEquals(newEmp, testSecurity.getAssignedEmp());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 1 - 17);
    testSecurity.setDateOfIncident(newDOI);
    assertEquals(newDOI, testSecurity.getDateOfIncident());
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

  /** Tests if the equals in Security.java correctly compares two Security objects */
  //  @Test
  //  void testEquals() {
  //    Security otherSec =
  //        new Security(
  //            "Incident Report",
  //            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
  //            "Wilson",
  //            "Softeng",
  //            "Wong",
  //            "Jonathan",
  //            "Elias",
  //            "Golden",
  //            ServiceRequest.EmpDept.CARDIOLOGY,
  //            ServiceRequest.EmpDept.MAINTENANCE,
  //            new Date(2023 - 1 - 31),
  //            new Date(2023 - 2 - 1),
  //            ServiceRequest.Urgency.MODERATELY_URGENT);
  //    assertEquals(testSecurity, otherSec);
  //  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testSecurity.hashCode();
    Security sameSecurity =
        new Security(
            "Incident Report",
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            emp,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    testSecurity.setDateOfSubmission(new Date(2023 - 1 - 30));
    assertEquals(testSecurity.hashCode(), originalHash);
    assertNotEquals(originalHash, sameSecurity.hashCode());
  }

  /** Checks to see if toString makes a string in the same format specified in Security.java */
  @Test
  void testToString() {
    String sanToString = testSecurity.toString();
    assertEquals(sanToString, testSecurity.getClass().getSimpleName() + "_" + testSecurity.getId());
  }
}
