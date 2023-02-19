package edu.wpi.FlashyFrogs.Accounts;

import edu.wpi.FlashyFrogs.ORM.HospitalUser;

public enum CurrentUserEntity {
  CURRENT_USER; // The current user
  private HospitalUser currentHospitalUser;

  public HospitalUser getCurrentuser() {
    return this.currentHospitalUser;
  }

  public boolean getAdmin() {
    return this.currentHospitalUser.getEmployeeType().equals(HospitalUser.EmployeeType.ADMIN);
  }

  public void setCurrentUser(HospitalUser hospitalUser) {
    this.currentHospitalUser = hospitalUser;
  }
}
