package edu.wpi.FlashyFrogs;

public class SanitationServiceData {
  private String requestType;
  private String locationInfo;
  private String dateInfo;
  private String employeeFirstName;
  private String employeeLastName;
  private String employeeMiddleName;
  private String employeeDepartment;

  public SanitationServiceData() {}

  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  public void setLocationInfo(String locationInfo) {
    this.locationInfo = locationInfo;
  }

  public void setDateInfo(String dateInfo) {
    this.dateInfo = dateInfo;
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

  public String getRequestType() {
    return requestType;
  }

  public String getLocationInfo() {
    return locationInfo;
  }

  public String getDateInfo() {
    return dateInfo;
  }

  public String getEmployeeFirstName() {
    return employeeFirstName;
  }

  public String getEmployeeLastName() {
    return employeeLastName;
  }

  public String getEmployeeMiddleName() {
    return employeeMiddleName;
  }

  public String getEmployeeDepartment() {
    return employeeDepartment;
  }

  @Override
  public String toString() {
    return "SanitationServiceData{"
        + "requestType='"
        + requestType
        + '\''
        + ", locationInfo='"
        + locationInfo
        + '\''
        + ", dateInfo='"
        + dateInfo
        + '\''
        + ", employeeFirstName='"
        + employeeFirstName
        + '\''
        + ", employeeLastName='"
        + employeeLastName
        + '\''
        + ", employeeMiddleName='"
        + employeeMiddleName
        + '\''
        + ", employeeDepartment='"
        + employeeDepartment
        + '\''
        + '}';
  }
}
