package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Creates iteration of Sanitation
public class AudioVisualTest {
  AudioVisual testAV =
      new AudioVisual(
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
    testAV.setEmpFirstName("Wilson");
    testAV.setEmpMiddleName("Softeng");
    testAV.setEmpLastName("Wong");
    testAV.setAssignedEmpFirstName("Jonathan");
    testAV.setAssignedEmpMiddleName("Elias");
    testAV.setAssignedEmpLastName("Golden");
    testAV.setEmpDept(ServiceRequest.EmpDept.CARDIOLOGY);
    testAV.setAssignedEmpDept(ServiceRequest.EmpDept.MAINTENANCE);
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

  /** Tests setter for empFirstName */
  @Test
  public void setEmpFirstName() {
    String newEmpFirstName = "Greg";
    testAV.setEmpFirstName(newEmpFirstName);
    assertEquals(newEmpFirstName, testAV.getEmpFirstName());
  }

  /** Tests setter for empMiddleName */
  @Test
  void setEmpMiddleName() {
    String newEmpMiddleName = "Grag";
    testAV.setEmpMiddleName(newEmpMiddleName);
    assertEquals(newEmpMiddleName, testAV.getEmpMiddleName());
  }

  /** Tests setter for empLastName */
  @Test
  void setEmpLastName() {
    String newEmpLastName = "Gregson";
    testAV.setEmpLastName(newEmpLastName);
    assertEquals(newEmpLastName, testAV.getEmpLastName());
  }

  /** Tests setter for assignedEmpFirstName */
  @Test
  void setAssignedEmpFirstName() {
    String newAssignedEmpFirstName = "William";
    testAV.setAssignedEmpFirstName(newAssignedEmpFirstName);
    assertEquals(newAssignedEmpFirstName, testAV.getAssignedEmpFirstName());
  }

  /** Tests setter for assignedEmpMiddleName */
  @Test
  void setAssignedEmpMiddleName() {
    String newAssignedEmpMiddleName = "Martin";
    testAV.setAssignedEmpMiddleName(newAssignedEmpMiddleName);
    assertEquals(newAssignedEmpMiddleName, testAV.getAssignedEmpMiddleName());
  }

  /** Tests setter for assignedEmpLastName */
  @Test
  void setAssignedEmpLastName() {
    String newAssignedEmpLastName = "Joel";
    testAV.setAssignedEmpLastName(newAssignedEmpLastName);
    assertEquals(newAssignedEmpLastName, testAV.getAssignedEmpLastName());
  }

  /** Tests setter for empDept */
  @Test
  void setEmpDept() {
    testAV.setEmpDept(ServiceRequest.EmpDept.NURSING);
    assertEquals(ServiceRequest.EmpDept.NURSING, testAV.getEmpDept());
  }

  /** Tests setter for assignedEmpDept */
  @Test
  void setAssignedEmpDept() {
    testAV.setAssignedEmpDept(ServiceRequest.EmpDept.RADIOLOGY);
    assertEquals(ServiceRequest.EmpDept.RADIOLOGY, testAV.getAssignedEmpDept());
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

  /** Tests if the equals in Sanitation.java correctly compares two Sanitation objects */
  @Test
  void testEquals() {
    AudioVisual otherAV =
        new AudioVisual(
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
            AudioVisual.AccommodationType.AUDIO,
            "Emre",
            "Rusen",
            "Sabaz",
            new LocationName("Name", LocationName.LocationType.EXIT, "name"),
            new Date(2001 - 12 - 8));
    assertEquals(testAV, otherAV);
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testAV.hashCode();
    testAV.setId(1);
    testAV.setDateOfSubmission(new Date(2023 - 01 - 30));
    assertNotEquals(testAV.hashCode(), originalHash);
  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void testToString() {
    String sanToString = testAV.toString();
    assertEquals(sanToString, testAV.getClass().getSimpleName() + "_" + testAV.getId());
  }
}
