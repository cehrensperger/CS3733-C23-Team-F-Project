package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Creates iteration of Sanitation
public class SanitationTest {
  Sanitation testSan =
      new Sanitation(
          Sanitation.SanitationType.MOPPING,
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
          ServiceRequest.Urgency.MODERATELY_URGENT,
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));

  /** Reset testSan after each test */
  @BeforeEach
  @AfterEach
  public void resetTestSanitation() {
    testSan.setType(Sanitation.SanitationType.MOPPING);
    testSan.setEmpFirstName("Wilson");
    testSan.setEmpMiddleName("Softeng");
    testSan.setEmpLastName("Wong");
    testSan.setAssignedEmpFirstName("Jonathan");
    testSan.setAssignedEmpMiddleName("Elias");
    testSan.setAssignedEmpLastName("Golden");
    testSan.setEmpDept(ServiceRequest.EmpDept.CARDIOLOGY);
    testSan.setAssignedEmpDept(ServiceRequest.EmpDept.MAINTENANCE);
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

  /** Tests setter for empFirstName */
  @Test
  void setEmpFirstName() {
    String newEmpFirstName = "Greg";
    testSan.setEmpFirstName(newEmpFirstName);
    assertEquals(newEmpFirstName, testSan.getEmpFirstName());
  }

  /** Tests setter for empMiddleName */
  @Test
  void setEmpMiddleName() {
    String newEmpMiddleName = "Grag";
    testSan.setEmpMiddleName(newEmpMiddleName);
    assertEquals(newEmpMiddleName, testSan.getEmpMiddleName());
  }

  /** Tests setter for empLastName */
  @Test
  void setEmpLastName() {
    String newEmpLastName = "Gregson";
    testSan.setEmpLastName(newEmpLastName);
    assertEquals(newEmpLastName, testSan.getEmpLastName());
  }

  /** Tests setter for assignedEmpFirstName */
  @Test
  void setAssignedEmpFirstName() {
    String newAssignedEmpFirstName = "William";
    testSan.setAssignedEmpFirstName(newAssignedEmpFirstName);
    assertEquals(newAssignedEmpFirstName, testSan.getAssignedEmpFirstName());
  }

  /** Tests setter for assignedEmpMiddleName */
  @Test
  void setAssignedEmpMiddleName() {
    String newAssignedEmpMiddleName = "Martin";
    testSan.setAssignedEmpMiddleName(newAssignedEmpMiddleName);
    assertEquals(newAssignedEmpMiddleName, testSan.getAssignedEmpMiddleName());
  }

  /** Tests setter for assignedEmpLastName */
  @Test
  void setAssignedEmpLastName() {
    String newAssignedEmpLastName = "Joel";
    testSan.setAssignedEmpLastName(newAssignedEmpLastName);
    assertEquals(newAssignedEmpLastName, testSan.getAssignedEmpLastName());
  }

  /** Tests setter for empDept */
  @Test
  void setEmpDept() {
    testSan.setEmpDept(ServiceRequest.EmpDept.NURSING);
    assertEquals(ServiceRequest.EmpDept.NURSING, testSan.getEmpDept());
  }

  /** Tests setter for assignedEmpDept */
  @Test
  void setAssignedEmpDept() {
    testSan.setAssignedEmpDept(ServiceRequest.EmpDept.RADIOLOGY);
    assertEquals(ServiceRequest.EmpDept.RADIOLOGY, testSan.getAssignedEmpDept());
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
  @Test
  void testEquals() {
    Sanitation otherSan =
        new Sanitation(
            Sanitation.SanitationType.MOPPING,
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
            ServiceRequest.Urgency.MODERATELY_URGENT,
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
    assertEquals(testSan, otherSan);
  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void testToString() {
    String sanToString = testSan.toString();
    assertEquals(sanToString, testSan.getClass().getSimpleName() + "_" + testSan.getId());
  }
}
