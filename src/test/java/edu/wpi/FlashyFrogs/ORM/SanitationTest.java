package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

  @Test
  void setType() {
    testSan.setType(Sanitation.SanitationType.SWEEPING);
    assertEquals(Sanitation.SanitationType.SWEEPING, testSan.getType());
  }

  @Test
  void setEmpFirstName() {
    String newEmpFirstName = "Greg";
    testSan.setEmpFirstName(newEmpFirstName);
    assertEquals(newEmpFirstName, testSan.getEmpFirstName());
  }

  @Test
  void setEmpMiddleName() {
    String newEmpMiddleName = "Grag";
    testSan.setEmpMiddleName(newEmpMiddleName);
    assertEquals(newEmpMiddleName, testSan.getEmpMiddleName());
  }

  @Test
  void setEmpLastName() {
    String newEmpLastName = "Gregson";
    testSan.setEmpLastName(newEmpLastName);
    assertEquals(newEmpLastName, testSan.getEmpLastName());
  }

  @Test
  void setAssignedEmpFirstName() {
    String newAssignedEmpFirstName = "William";
    testSan.setAssignedEmpFirstName(newAssignedEmpFirstName);
    assertEquals(newAssignedEmpFirstName, testSan.getAssignedEmpFirstName());
  }

  @Test
  void setAssignedEmpMiddleName() {
    String newAssignedEmpMiddleName = "Martin";
    testSan.setAssignedEmpMiddleName(newAssignedEmpMiddleName);
    assertEquals(newAssignedEmpMiddleName, testSan.getAssignedEmpMiddleName());
  }

  @Test
  void setAssignedEmpLastName() {
    String newAssignedEmpLastName = "Joel";
    testSan.setAssignedEmpLastName(newAssignedEmpLastName);
    assertEquals(newAssignedEmpLastName, testSan.getAssignedEmpLastName());
  }

  @Test
  void setEmpDept() {
    testSan.setEmpDept(ServiceRequest.EmpDept.NURSING);
    assertEquals(ServiceRequest.EmpDept.NURSING, testSan.getEmpDept());
  }

  @Test
  void setAssignedEmpDept() {
    testSan.setAssignedEmpDept(ServiceRequest.EmpDept.RADIOLOGY);
    assertEquals(ServiceRequest.EmpDept.RADIOLOGY, testSan.getAssignedEmpDept());
  }

  @Test
  void setDateOfIncident() {
    Date newDOI = new Date(2002 - 01 - 17);
    testSan.setDateOfIncident(newDOI);
    assertEquals(newDOI, testSan.getDateOfIncident());
  }

  @Test
  void setDateOfSubmission() {
    Date newDOS = new Date(2002 - 01 - 17);
    testSan.setDateOfSubmission(newDOS);
    assertEquals(newDOS, testSan.getDateOfSubmission());
  }

  @Test
  void setUrgency() {
    testSan.setUrgency(ServiceRequest.Urgency.NOT_URGENT);
    assertEquals(ServiceRequest.Urgency.NOT_URGENT, testSan.getUrgency());
  }

  @Test
  void setLocation() {
    LocationName newLoc =
        new LocationName("NewLocLong", LocationName.LocationType.EXIT, "NewLocShort");
    testSan.setLocation(newLoc);
    assertEquals(newLoc, testSan.getLocation());
  }

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
    assertTrue(testSan.equals(otherSan));
  }

  @Test
  void testHashCode() {
    int originalHash = testSan.hashCode();
    testSan.setId(1);
    testSan.setDateOfSubmission(new Date(2023 - 01 - 30));
    assertNotEquals(testSan.hashCode(), originalHash);
  }

  @Test
  void testToString() {
    String sanToString = testSan.toString();
    assertEquals(sanToString, testSan.getClass().getSimpleName() + "_" + testSan.getId());
  }
}
