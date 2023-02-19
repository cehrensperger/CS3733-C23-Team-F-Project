package edu.wpi.FlashyFrogs.Accounts;

import edu.wpi.FlashyFrogs.ORM.HospitalUser;

public enum CurrentUserEntity {
  CURRENT_USER; // The current user
  private HospitalUser currentUser;

  public HospitalUser getCurrentuser() {
    return this.currentUser;
  }

  public boolean getAdmin() {
    return this.currentUser.getEmployeeType().equals(HospitalUser.EmployeeType.ADMIN);
  }

  public void setCurrentUser(HospitalUser user) {
    this.currentUser = user;
  }
}
