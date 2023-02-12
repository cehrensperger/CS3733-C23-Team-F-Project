package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Creates iteration of Sanitation
public class ComputerServiceTest {
  User emp = new User("Wilson", "Softeng", "Wong", User.EmployeeType.MEDICAL);
  User assignedEmp = new User("Jonathan", "Elias", "Golden", User.EmployeeType.MEDICAL);
  ComputerService testCS =
      new ComputerService(
          emp,
          new Date(2023 - 01 - 31),
          new Date(2023 - 02 - 01),
          ServiceRequest.Urgency.MODERATELY_URGENT,
          ComputerService.DeviceType.LAPTOP,
          "Lenovo Rogue",
          "Bad battery life",
          ComputerService.ServiceType.HARDWARE_REPAIR);

  /** Reset testSan after each test */
  @BeforeEach
  @AfterEach
  public void resetTestSanitation() {
    emp.setFirstName("Wilson");
    emp.setMiddleName("Softeng");
    emp.setLastName("Wong");
    assignedEmp.setFirstName("Jonathan");
    assignedEmp.setMiddleName("Elias");
    assignedEmp.setLastName("Golden");
    emp.setEmployeeType(User.EmployeeType.MEDICAL);
    assignedEmp.setEmployeeType(User.EmployeeType.MEDICAL);
    testCS.setAssignedEmp(assignedEmp);
    testCS.setDateOfIncident(new Date(2023 - 01 - 31));
    testCS.setDateOfSubmission(new Date(2023 - 02 - 01));
    testCS.setUrgency(ServiceRequest.Urgency.MODERATELY_URGENT);
    testCS.setDeviceType(ComputerService.DeviceType.LAPTOP);
    testCS.setModel("Lenovo Rogue");
    testCS.setIssue("Bad battery life");
    testCS.setServiceType(ComputerService.ServiceType.HARDWARE_REPAIR);
  }

  /** Tests setter for emp */
  @Test
  public void setEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testCS.setEmp(newEmp);
    assertEquals(newEmp, testCS.getEmp());
  }

  /** Test setter for Assigned emp */
  @Test
  public void setAssignedEmp() {
    User newEmp = new User("Bob", "Bobby", "Jones", User.EmployeeType.ADMIN);
    testCS.setAssignedEmp(newEmp);
    assertEquals(newEmp, testCS.getAssignedEmp());
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
  //  @Test
  //  void testEquals() {
  //    ComputerService otherCS =
  //        new ComputerService(
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
  //            ComputerService.DeviceType.LAPTOP,
  //            "Lenovo Rogue",
  //            "Bad battery life",
  //            ComputerService.ServiceType.HARDWARE_REPAIR);
  //    assertEquals(testCS, otherCS);
  //  }

  /** Checks to see if toString makes a string in the same format specified in Sanitation.java */
  @Test
  void testToString() {
    String sanToString = testCS.toString();
    assertEquals(sanToString, testCS.getClass().getSimpleName() + "_" + testCS.getId());
  }
}
