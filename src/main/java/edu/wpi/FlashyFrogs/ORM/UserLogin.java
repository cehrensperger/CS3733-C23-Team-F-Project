package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "userlogin")
public class UserLogin {

  @Id
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String userName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  String password;

  public void setPassword(String newPassword) throws NoSuchAlgorithmException {
    this.password = makeSalt(newPassword);
  }

  /** Creates a new UserLogin with empty fields */
  public UserLogin() {}

  public UserLogin(@NonNull String theUserName, @NonNull String thePassword)
      throws NoSuchAlgorithmException {
    this.userName = theUserName;
    this.password = makeSalt(thePassword);
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
    Node other = (Node) obj;
    return this.userName.equals(other.getId());
  }

  /**
   * Overrides the default hashCode method with one that uses the id, xcoord, and ycoord of the node
   * object
   *
   * @return the new hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.userName, this.password);
  }

  /**
   * Overrides the default toString method with one that returns the id of the Node object
   *
   * @return the id of the Node object
   */
  @Override
  @NonNull
  public String toString() {
    return this.userName;
  }

  public String makeSalt(String password) throws NoSuchAlgorithmException {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);

    MessageDigest md = MessageDigest.getInstance("SHA-512");
    md.update(salt);

    byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
    return hashedPassword.toString();
  }
}
