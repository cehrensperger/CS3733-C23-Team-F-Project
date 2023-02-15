package edu.wpi.FlashyFrogs.Accounts;

import edu.wpi.FlashyFrogs.ORM.User;

public enum CurrentUserEntity {
  CURRENT_USER; // The current user
  private User currentUser;

  public User getCurrentuser() {
    return this.currentUser;
  }

  public boolean getAdmin() {
    return true;
  }

  public void setCurrentUser(User user) {
    this.currentUser = user;
  }
}
