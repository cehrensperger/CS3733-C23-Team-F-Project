package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InternalTransportTest {
  // Creates iteration of InternalTransportTest

  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL);
  InternalTransport testIntTransp =
      new InternalTransport(
          new Date(2002 - 10 - 02),
          new LocationName("NewLocLongName", LocationName.LocationType.DEPT, "NewLocShortName"),
          new LocationName("OldLocLongName", LocationName.LocationType.HALL, "OldLocShortName"),
          "John",
          "B",
          "Doe",
          emp,
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
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testIntTransp.setAssignedEmp(assignedEmp);
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

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testIntTransp.setEmp(newEmp);
    assertEquals(newEmp, testIntTransp.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testIntTransp.setAssignedEmp(newEmp);
    assertEquals(newEmp, testIntTransp.getAssignedEmp());
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
  //  @Test
  //  void testEquals() {
  //    InternalTransport otherIntTransport =
  //        new InternalTransport(
  //            new Date(2002 - 10 - 02),
  //            new LocationName("NewLocLongName", LocationName.LocationType.DEPT,
  // "NewLocShortName"),
  //            new LocationName("OldLocLongName", LocationName.LocationType.HALL,
  // "OldLocShortName"),
  //            "John",
  //            "B",
  //            "Doe",
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
  //            ServiceRequest.Urgency.MODERATELY_URGENT);
  //    assertTrue(testIntTransp.equals(otherIntTransport));
  //  }

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
