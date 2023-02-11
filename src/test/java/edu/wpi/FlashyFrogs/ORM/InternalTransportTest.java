package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InternalTransportTest {

  // Creates iteration of InternalTransportTest
  InternalTransport testIntTransp =
      new InternalTransport(
          new Date(2002 - 10 - 02),
          new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName"),
          new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName"),
          "John",
          "B",
          "Doe",
          "Wilson",
          "Softeng",
          "Wong",
          "Jonathan",
          "Elias",
          "Golden",
          ServiceRequest.EmpDept.CARDIOLOGY,
          ServiceRequest.EmpDept.MAINTENANCE,
          new Date(2023 - 01 - 31),
          new Date(2023 - 02 - 01),
          ServiceRequest.Urgency.MODERATELY_URGENT);

  /** Reset testInternalTransport after each test */
  @BeforeEach
  @AfterEach
  public void resetTestInternalTransport() {
    testIntTransp.setDateOfBirth(new Date(2002 - 10 - 02));
    testIntTransp.setNewLoc(
        new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName"));
    testIntTransp.setOldLoc(
        new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName"));
    testIntTransp.setPatientFirstName("John");
    testIntTransp.setPatientMiddleName("B");
    testIntTransp.setPatientLastName("Doe");
    testIntTransp.setEmpFirstName("Wilson");
    testIntTransp.setEmpMiddleName("Softeng");
    testIntTransp.setEmpLastName("Wong");
    testIntTransp.setAssignedEmpFirstName("Jonathan");
    testIntTransp.setAssignedEmpMiddleName("Elias");
    testIntTransp.setAssignedEmpLastName("Golden");
    testIntTransp.setEmpDept(ServiceRequest.EmpDept.CARDIOLOGY);
    testIntTransp.setAssignedEmpDept(ServiceRequest.EmpDept.MAINTENANCE);
    testIntTransp.setDateOfIncident(new Date(2023 - 01 - 31));
    testIntTransp.setDateOfSubmission(new Date(2023 - 02 - 01));
    testIntTransp.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
  }

  /** Tests setter for dateOfBirth */
  @Test
  void setDateOfBirth() {
    Date newDate = new Date(2002 - 01 - 17);
    testIntTransp.setDateOfBirth(newDate);
    assertEquals(newDate, testIntTransp.getDateOfBirth());
  }

  /** Tests setter for newLoc */
  @Test
  void setNewLoc() {
    LocationName newerLoc =
        new LocationName("NewerLocLong", LocationName.LocationType.EXIT, "NewerLocShort");
    testIntTransp.setNewLoc(newerLoc);
    assertEquals(newerLoc, testIntTransp.getNewLoc());
  }

  /** Tests setter for oldLoc */
  @Test
  void setOldLoc() {
    LocationName newOldLoc =
        new LocationName("NewOldLocLong", LocationName.LocationType.EXIT, "NewOldLocShort");
    testIntTransp.setOldLoc(newOldLoc);
    assertEquals(newOldLoc, testIntTransp.getOldLoc());
  }

  /** Tests setter for patientName */
  @Test
  void setPatientName() {
    String patientFirstName = "Jimboo";
    String patientLastName = "Jones";
    testIntTransp.setPatientFirstName(patientFirstName);
    testIntTransp.setPatientLastName(patientLastName);
    assertEquals(patientFirstName, testIntTransp.getPatientFirstName());
    assertEquals(patientLastName, testIntTransp.getPatientLastName());
  }

  /** Tests setter for empFirstName */
  @Test
  void setEmpFirstName() {
    String newEmpFirstName = "Greg";
    testIntTransp.setEmpFirstName(newEmpFirstName);
    assertEquals(newEmpFirstName, testIntTransp.getEmpFirstName());
  }

  /** Tests setter for empMiddleName */
  @Test
  void setEmpMiddleName() {
    String newEmpMiddleName = "Grag";
    testIntTransp.setEmpMiddleName(newEmpMiddleName);
    assertEquals(newEmpMiddleName, testIntTransp.getEmpMiddleName());
  }

  /** Tests setter for empLastName */
  @Test
  void setEmpLastName() {
    String newEmpLastName = "Gregson";
    testIntTransp.setEmpLastName(newEmpLastName);
    assertEquals(newEmpLastName, testIntTransp.getEmpLastName());
  }

  /** Tests setter for assignedEmpFirstName */
  @Test
  void setAssignedEmpFirstName() {
    String newAssignedEmpFirstName = "William";
    testIntTransp.setAssignedEmpFirstName(newAssignedEmpFirstName);
    assertEquals(newAssignedEmpFirstName, testIntTransp.getAssignedEmpFirstName());
  }

  /** Tests setter for assignedEmpMiddleName */
  @Test
  void setAssignedEmpMiddleName() {
    String newAssignedEmpMiddleName = "Martin";
    testIntTransp.setAssignedEmpMiddleName(newAssignedEmpMiddleName);
    assertEquals(newAssignedEmpMiddleName, testIntTransp.getAssignedEmpMiddleName());
  }

  /** Tests setter for assignedEmpLastName */
  @Test
  void setAssignedEmpLastName() {
    String newAssignedEmpLastName = "Joel";
    testIntTransp.setAssignedEmpLastName(newAssignedEmpLastName);
    assertEquals(newAssignedEmpLastName, testIntTransp.getAssignedEmpLastName());
  }

  /** Tests setter for empDept */
  @Test
  void setEmpDept() {
    testIntTransp.setEmpDept(ServiceRequest.EmpDept.NURSING);
    assertEquals(ServiceRequest.EmpDept.NURSING, testIntTransp.getEmpDept());
  }

  /** Tests setter for assignedEmpDept */
  @Test
  void setAssignedEmpDept() {
    testIntTransp.setAssignedEmpDept(ServiceRequest.EmpDept.RADIOLOGY);
    assertEquals(ServiceRequest.EmpDept.RADIOLOGY, testIntTransp.getAssignedEmpDept());
  }

  /** Tests setter for dateOfIncident */
  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 01 - 17);
    testIntTransp.setDateOfIncident(newDOI);
    assertEquals(newDOI, testIntTransp.getDateOfIncident());
  }

  /** Tests setter for dateOfSubmission */
  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 01 - 17);
    testIntTransp.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testIntTransp.getDateOfSubmission());
  }

  /** Tests setter for urgency */
  @Test
  void setUrgency() {
    testIntTransp.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testIntTransp.getUrgency());
  }

  /**
   * Tests if the equals in InternalTransportTest.java correctly compares two InternalTransportTest
   * objects
   */
  @Test
  void testEquals() {
    InternalTransport otherIntTransport =
        new InternalTransport(
            new Date(2002 - 10 - 02),
            new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName"),
            new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName"),
            "John",
            "B",
            "Doe",
            "Wilson",
            "Softeng",
            "Wong",
            "Jonathan",
            "Elias",
            "Golden",
            ServiceRequest.EmpDept.CARDIOLOGY,
            ServiceRequest.EmpDept.MAINTENANCE,
            new Date(2023 - 01 - 31),
            new Date(2023 - 02 - 01),
            ServiceRequest.Urgency.MODERATELY_URGENT);
    assertTrue(testIntTransp.equals(otherIntTransport));
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testIntTransp.hashCode();
    testIntTransp.setId(1);
    testIntTransp.setDateOfSubmission(new Date(2023 - 01 - 30));
    assertNotEquals(testIntTransp.hashCode(), originalHash);
  }

  /**
   * Checks to see if toString makes a string in the same format specified in
   * InternalTransportTest.java
   */
  @Test
  void testToString() {
    String sanToString = testIntTransp.toString();
    assertEquals(
        sanToString, testIntTransp.getClass().getSimpleName() + "_" + testIntTransp.getId());
  }
}
