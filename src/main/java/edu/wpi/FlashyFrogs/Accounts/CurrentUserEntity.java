package edu.wpi.FlashyFrogs.Accounts;

import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import lombok.Getter;
import lombok.Setter;

public enum CurrentUserEntity {
  CURRENT_USER; // The current user
  @Getter @Setter private HospitalUser currentUser;

  public boolean getAdmin() {
    return this.currentUser.getEmployeeType().equals(HospitalUser.EmployeeType.ADMIN);
  }
}
