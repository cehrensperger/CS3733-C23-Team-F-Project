package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ServiceRequestTest {
  /** Test toString override on Status enum */
  @Test
  public void statusToStringTest() {
    ServiceRequest.Status blank = ServiceRequest.Status.BLANK;
    ServiceRequest.Status processing = ServiceRequest.Status.PROCESSING;
    ServiceRequest.Status done = ServiceRequest.Status.DONE;

    assertEquals("blank", blank.toString());
    assertEquals("processing", processing.toString());
    assertEquals("done", done.toString());
  }

  /** Test toString override on EmpDept enum */
  @Test
  public void empDeptToStringTest() {
    ServiceRequest.EmpDept nursing = ServiceRequest.EmpDept.NURSING;
    ServiceRequest.EmpDept cardiology = ServiceRequest.EmpDept.CARDIOLOGY;
    ServiceRequest.EmpDept radiology = ServiceRequest.EmpDept.RADIOLOGY;
    ServiceRequest.EmpDept maintenance = ServiceRequest.EmpDept.MAINTENANCE;
    ServiceRequest.EmpDept trauma = ServiceRequest.EmpDept.TRAUMA_UNIT;

    assertEquals("nursing", nursing.toString());
    assertEquals("cardiology", cardiology.toString());
    assertEquals("radiology", radiology.toString());
    assertEquals("maintenance", maintenance.toString());
    assertEquals("trauma unit", trauma.toString());
  }

  /** Test toString override on Urgency enum */
  @Test
  public void urgencyToStringTest() {
    ServiceRequest.Urgency very = ServiceRequest.Urgency.VERY_URGENT;
    ServiceRequest.Urgency moderate = ServiceRequest.Urgency.MODERATELY_URGENT;
    ServiceRequest.Urgency not = ServiceRequest.Urgency.NOT_URGENT;

    assertEquals("very urgent", very.toString());
    assertEquals("moderately urgent", moderate.toString());
    assertEquals("not urgent", not.toString());
  }
}
