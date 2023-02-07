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
        testCS.setEmpFirstName("Wilson");
        testCS.setEmpMiddleName("Softeng");
        testCS.setEmpLastName("Wong");
        testCS.setAssignedEmpFirstName("Jonathan");
        testCS.setAssignedEmpMiddleName("Elias");
        testCS.setAssignedEmpLastName("Golden");
        testCS.setEmpDept(ServiceRequest.EmpDept.CARDIOLOGY);
        testCS.setAssignedEmpDept(ServiceRequest.EmpDept.MAINTENANCE);
        testCS.setDateOfIncident(new Date(2023 - 01 - 31));
        testCS.setDateOfSubmission(new Date(2023 - 02 - 01));
        testCS.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
        testCS.setDeviceType(ComputerService.DeviceType.LAPTOP);
        testCS.setModel("Lenovo Rogue");
        testCS.setIssue("Bad battery life");
        testCS.setServiceType(ComputerService.ServiceType.HARDWARE_REPAIR);
    }

    /** Tests setter for empFirstName */
    @Test
    public void setEmpFirstName() {
        String newEmpFirstName = "Greg";
        testCS.setEmpFirstName(newEmpFirstName);
        assertEquals(newEmpFirstName, testCS.getEmpFirstName());
    }

    /** Tests setter for empMiddleName */
    @Test
    void setEmpMiddleName() {
        String newEmpMiddleName = "Grag";
        testCS.setEmpMiddleName(newEmpMiddleName);
        assertEquals(newEmpMiddleName, testCS.getEmpMiddleName());
    }

    /** Tests setter for empLastName */
    @Test
    void setEmpLastName() {
        String newEmpLastName = "Gregson";
        testCS.setEmpLastName(newEmpLastName);
        assertEquals(newEmpLastName, testCS.getEmpLastName());
    }

    /** Tests setter for assignedEmpFirstName */
    @Test
    void setAssignedEmpFirstName() {
        String newAssignedEmpFirstName = "William";
        testCS.setAssignedEmpFirstName(newAssignedEmpFirstName);
        assertEquals(newAssignedEmpFirstName, testCS.getAssignedEmpFirstName());
    }

    /** Tests setter for assignedEmpMiddleName */
    @Test
    void setAssignedEmpMiddleName() {
        String newAssignedEmpMiddleName = "Martin";
        testCS.setAssignedEmpMiddleName(newAssignedEmpMiddleName);
        assertEquals(newAssignedEmpMiddleName, testCS.getAssignedEmpMiddleName());
    }

    /** Tests setter for assignedEmpLastName */
    @Test
    void setAssignedEmpLastName() {
        String newAssignedEmpLastName = "Joel";
        testCS.setAssignedEmpLastName(newAssignedEmpLastName);
        assertEquals(newAssignedEmpLastName, testCS.getAssignedEmpLastName());
    }

    /** Tests setter for empDept */
    @Test
    void setEmpDept() {
        testCS.setEmpDept(ServiceRequest.EmpDept.NURSING);
        assertEquals(ServiceRequest.EmpDept.NURSING, testCS.getEmpDept());
    }

    /** Tests setter for assignedEmpDept */
    @Test
    void setAssignedEmpDept() {
        testCS.setAssignedEmpDept(ServiceRequest.EmpDept.RADIOLOGY);
        assertEquals(ServiceRequest.EmpDept.RADIOLOGY, testCS.getAssignedEmpDept());
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
    @Test
    void testEquals() {
        ComputerService otherCS =
                new ComputerService(
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
                        ComputerService.DeviceType.LAPTOP,
                        "Lenovo Rogue",
                        "Bad battery life",
                        ComputerService.ServiceType.HARDWARE_REPAIR);
        assertTrue(testCS.equals(otherCS));
    }

    /** Tests to see that HashCode changes when attributes that determine HashCode changes */
    @Test
    void testHashCode() {
        int originalHash = testCS.hashCode();
        testCS.setId(1);
        testCS.setDateOfSubmission(new Date(2023 - 01 - 30));
        assertNotEquals(testCS.hashCode(), originalHash);
    }

    /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
    @Test
    void testToString() {
        String sanToString = testCS.toString();
        assertEquals(sanToString, testCS.getClass().getSimpleName() + "_" + testCS.getId());
    }
}
