package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SecurityTest {

  //Creates iteration of LocationName
  Security testSecurity =
      new Security(
          "Incident Report",
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          "Wilson",
          "Softeng",
          "Wong",
          "Jonathan",
          "Elias",
          "Golden",
          ServiceRequest.EmpDept.CARDIOLOGY,
          ServiceRequest.EmpDept.MAINTENANCE,
          new Date(2023 - 1 - 31),
          new Date(2023 - 2 - 1),
          ServiceRequest.Urgency.MODERATELY_URGENT);

  /**
   * Reset testSecurity after each test
   */
  @BeforeEach
  @AfterEach
  public void resetTestSecurity() {
    testSecurity.setIncidentReport("Incident Report");
    testSecurity.setLocation(
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
    testSecurity.setEmpFirstName("Wilson");
    testSecurity.setEmpMiddleName("Softeng");
    testSecurity.setEmpLastName("Wong");
    testSecurity.setAssignedEmpFirstName("Jonathan");
    testSecurity.setAssignedEmpMiddleName("Elias");
    testSecurity.setAssignedEmpLastName("Golden");
    testSecurity.setEmpDept(ServiceRequest.EmpDept.CARDIOLOGY);
    testSecurity.setAssignedEmpDept(ServiceRequest.EmpDept.MAINTENANCE);
    testSecurity.setDateOfIncident(new Date(2023 - 1 - 31));
    testSecurity.setDateOfSubmission(new Date(2023 - 2 - 1));
    testSecurity.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
  }

  /**
   * Tests setter for incidentReport
   */
  @Test
  void setIncidentReport() {
    testSecurity.setIncidentReport("Something Else");
    assertEquals("Something Else", testSecurity.getIncidentReport());
  }

  /**
   * Tests setter for empFirstName
   */
  @Test
  void setEmpFirstName() {
    String newEmpFirstName = "Greg";
    testSecurity.setEmpFirstName(newEmpFirstName);
    assertEquals(newEmpFirstName, testSecurity.getEmpFirstName());
  }

  /**
   * Tests setter for empMiddleName
   */
  @Test
  void setEmpMiddleName() {
    String newEmpMiddleName = "Grag";
    testSecurity.setEmpMiddleName(newEmpMiddleName);
    assertEquals(newEmpMiddleName, testSecurity.getEmpMiddleName());
  }

  /**
   * Tests setter for empLastName
   */
  @Test
  void setEmpLastName() {
    String newEmpLastName = "Gregson";
    testSecurity.setEmpLastName(newEmpLastName);
    assertEquals(newEmpLastName, testSecurity.getEmpLastName());
  }

  /**
   * Tests setter for assignedEmpFirstName
   */
  @Test
  void setAssignedEmpFirstName() {
    String newAssignedEmpFirstName = "William";
    testSecurity.setAssignedEmpFirstName(newAssignedEmpFirstName);
    assertEquals(newAssignedEmpFirstName, testSecurity.getAssignedEmpFirstName());
  }

  /**
   * Tests setter for assignedEmpMiddleName
   */
  @Test
  void setAssignedEmpMiddleName() {
    String newAssignedEmpMiddleName = "Martin";
    testSecurity.setAssignedEmpMiddleName(newAssignedEmpMiddleName);
    assertEquals(newAssignedEmpMiddleName, testSecurity.getAssignedEmpMiddleName());
  }

  /**
   * Tests setter for assignedEmpLastName
   */
  @Test
  void setAssignedEmpLastName() {
    String newAssignedEmpLastName = "Joel";
    testSecurity.setAssignedEmpLastName(newAssignedEmpLastName);
    assertEquals(newAssignedEmpLastName, testSecurity.getAssignedEmpLastName());
  }

  /**
   * Tests setter for empDept
   */
  @Test
  void setEmpDept() {
    testSecurity.setEmpDept(ServiceRequest.EmpDept.NURSING);
    assertEquals(ServiceRequest.EmpDept.NURSING, testSecurity.getEmpDept());
  }

  /**
   * Tests setter for assignedEmpDept
   */
  @Test
  void setAssignedEmpDept() {
    testSecurity.setAssignedEmpDept(ServiceRequest.EmpDept.RADIOLOGY);
    assertEquals(ServiceRequest.EmpDept.RADIOLOGY, testSecurity.getAssignedEmpDept());
  }

  /**
   * Tests setter for dateOfIncident
   */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 1 - 17);
    testSecurity.setDateOfIncident(newDOI);
    assertEquals(newDOI, testSecurity.getDateOfIncident());
  }

  /**
   * Tests setter for dateOfSubmission
   */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 1 - 17);
    testSecurity.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testSecurity.getDateOfSubmission());
  }

  /**
   * Tests setter for urgency
   */
  @Test
  void setUrgency() {
    testSecurity.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testSecurity.getUrgency());
  }

  /**
   * Tests setter for location
   */
  @Test
  void testSetLocation() {
    LocationName newLoc =
        new LocationName("NewLocLong", LocationName.LocationType.EXIT, "NewLocShort");
    testSecurity.setLocation(newLoc);
    assertEquals(newLoc, testSecurity.getLocation());
  }

  /**
   * Tests setter for incidentReport
   */
  @Test
  void testSetIncidentReport() {
    String newIncRep = "New Report";
    testSecurity.setIncidentReport(newIncRep);
    assertEquals(newIncRep, testSecurity.getIncidentReport());
  }

  /**
   * Tests if the equals in Security.java correctly compares two Security objects
   */
  @Test
  void testEquals() {
    Security otherSec =
        new Security(
            "Incident Report",
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            "Wilson",
            "Softeng",
            "Wong",
            "Jonathan",
            "Elias",
            "Golden",
            ServiceRequest.EmpDept.CARDIOLOGY,
            ServiceRequest.EmpDept.MAINTENANCE,
            new Date(2023 - 1 - 31),
            new Date(2023 - 2 - 1),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    assertTrue(testSecurity.equals(otherSec));
  }

  /**
   * Tests to see that HashCode changes when attributes that determine HashCode changes
   */
  @Test
  void testHashCode() {
    int originalHash = testSecurity.hashCode();
    testSecurity.setId(1);
    testSecurity.setDateOfSubmission(new Date(2023 - 1 - 30));
    assertNotEquals(testSecurity.hashCode(), originalHash);
  }

  /**
   * Checks to see if toString makes a string in the same format specified in Security.java
   */
  @Test
  void testToString() {
    String sanToString = testSecurity.toString();
    assertEquals(sanToString, testSecurity.getClass().getSimpleName() + "_" + testSecurity.getId());
  }
}
