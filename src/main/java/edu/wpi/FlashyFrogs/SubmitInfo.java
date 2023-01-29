package edu.wpi.FlashyFrogs;

public class SubmitInfo {
  private String patientFirstName;
  private String patientLastName;
  private String patientMiddleName;
  private String DOB;
  private String currentLocationInfo;
  private String newLocationInfo;
  private String DOT;

  private String employeeFirstName;
  private String employeeLastName;
  private String employeeMiddleName;
  private String employeeDepartment;

  public SubmitInfo() {}

  public void setPatientFirstName(String patientFirstName) {
    this.patientFirstName = patientFirstName;
  }

  public void setPatientLastName(String patientLastName) {
    this.patientLastName = patientLastName;
  }

  public void setPatientMiddleName(String patientMiddleName) {
    this.patientMiddleName = patientMiddleName;
  }

  public void setDOB(String DOB) {
    this.DOB = DOB;
  }

  public void setEmployeeFirstName(String employeeFirstName) {
    this.employeeFirstName = employeeFirstName;
  }

  public void setEmployeeLastName(String employeeLastName) {
    this.employeeLastName = employeeLastName;
  }

  public void setEmployeeMiddleName(String employeeMiddleName) {
    this.employeeMiddleName = employeeMiddleName;
  }

  public void setEmployeeDepartment(String employeeDepartment) {
    this.employeeDepartment = employeeDepartment;
  }

  public String getPatientFirstName() {
    return patientFirstName;
  }

  public String getPatientLastName() {
    return patientLastName;
  }

  public String getPatientMiddleName() {
    return patientMiddleName;
  }

  public String getEmployeeFirstName() {
    return employeeFirstName;
  }

  public String getEmployeeLastName() {
    return employeeLastName;
  }

  public String getDOB() {
    return DOB;
  }

  public String DOT() {
    return DOT;
  }

  public String getEmployeeMiddleName() {
    return employeeMiddleName;
  }

  public String getEmployeeDepartment() {
    return employeeDepartment;
  }

  public void setCurrentLocationInfo(String currentLocationInfo) {
    this.currentLocationInfo = currentLocationInfo;
  }

  public void setNewLocationInfo(String newLocationInfo) {
    this.newLocationInfo = newLocationInfo;
  }

  public void setDOT(String DOT) {
    this.DOT = DOT;
  }
}
