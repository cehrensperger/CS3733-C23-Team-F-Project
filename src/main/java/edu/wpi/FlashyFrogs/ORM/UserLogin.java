package edu.wpi.FlashyFrogs.ORM;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "userlogin")
public class UserLogin {
  /**
   * Reference to the user ID that is being logged in. Does not update on user change, as that is
   * supposed to be unchangable. However, should cascade on delete
   */
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.DELETE)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey =
          @ForeignKey(
              name = "user_id_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (user_id) REFERENCES hospital_user(id) ON DELETE CASCADE"))
  @OneToOne(optional = false)
  @NonNull
  @Getter
  @Setter
  private HospitalUser user;

  @Column(nullable = false, unique = true)
  @NonNull
  @Getter
  @Setter
  private String userName;

  @Column(unique = true)
  @Getter
  private String RFIDBadge; // The users RFID badge ID

  @Column(unique = true)
  @Getter
  private String RFIDPW; // The users RFID badge PW

  @Basic
  @Column(nullable = false)
  @NonNull // Warning can be ignored, it is initialized in the constructor
  @Getter
  private String hash;

  /**
   * Sets a new password for the user, including hashing it
   *
   * @param newPassword the password to set
   */
  public void setPassword(String newPassword) {
    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
    hash = argon2.hash(2, 15 * 1024, 1, newPassword.toCharArray());
  }

  /**
   * Sets a new RFID for the user, including hashing it
   *
   * @param rfidBadge the password to set
   */
  public void setRFIDBadge(String rfidBadge) {
    // Null-check the badge
    if (rfidBadge != null) {
      String rfidPW = rfidBadge.substring(0, 5);
      String rfidID = rfidBadge.substring(5, 10);
      RFIDBadge = rfidID;
      // If it's valid, encrypt it
      Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
      RFIDPW = argon2.hash(2, 15 * 1024, 1, rfidPW.toCharArray());
    } else {
      RFIDBadge = null; // otherwise clear it
      RFIDPW = null;
    }
  }

  /** Creates a new UserLogin with empty fields */
  public UserLogin() {}

  /**
   * Creates a user log in with filled in fields
   *
   * @param user the user to create the thing for
   * @param theUserName the username of the user
   * @param thePassword the password of the user
   */
  public UserLogin(
      @NonNull HospitalUser user,
      @NonNull String theUserName,
      String RFID,
      @NonNull String thePassword) {
    this.user = user;
    this.userName = theUserName;

    setPassword(thePassword);
    setRFIDBadge(RFID);
  }

  /**
   * Overrides the default equals method with one that compares primary keys
   *
   * @param obj the node object to compare to
   * @return boolean whether the primary keys are the same or not
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    UserLogin other = (UserLogin) obj;
    return this.user.equals(other.getUser());
  }

  /**
   * Overrides the default hashCode method with one that uses the id, xcoord, and ycoord of the node
   * object
   *
   * @return the new hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.user);
  }

  /**
   * Overrides the default toString method with one that returns the id of the Node object
   *
   * @return the id of the UserLogin object
   */
  @Override
  @NonNull
  public String toString() {
    return this.userName;
  }

  /**
   * Checks a potential password for this user
   *
   * @param potentialPassword given by user to be checked against actual password
   * @return true if the password matches the current password, false otherwise
   */
  public boolean checkPasswordEqual(@NonNull String potentialPassword) {
    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
    return argon2.verify(hash, potentialPassword.toCharArray());
  }

  /**
   * Checks a potential RFID badge for this user
   *
   * @param potentialBadge the potential badge for the user
   * @return true if the logins are equal, false otherwise
   */
  public boolean checkRFIDBadgeEqual(@NonNull String potentialBadge) {
    if (RFIDPW == null) return false; // Short-circuit if the RFID badge is null

    Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
    return argon2.verify(RFIDPW, potentialBadge.toCharArray());
  }
}
