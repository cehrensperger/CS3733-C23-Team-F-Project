package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

/** Class representing a user in the database */
@Entity
@Table(name = "user")
public class User {
  /**
   * Type representing the unique, auto-generated, employee ID. This should not be mutated while the
   * employee exists, only deleted
   */
  @Basic
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.DELETE)
  @Column(nullable = false)
  @Getter
  @GeneratedValue
  private long id;

  /** String representing the employees first name */
  @Basic
  @Column(nullable = false)
  @Setter
  @Getter
  @NonNull
  private String firstName;

  /** String representing the employees middle name */
  @Basic @Setter @Getter private String middleName;

  /** String representing the employees last name */
  @Basic
  @Column(nullable = false)
  @Setter
  @Getter
  @NonNull
  private String lastName;

  /** The type of employee the employee is. This determines their access level */
  @Basic
  @Column(nullable = false)
  @Setter
  @Getter
  @NonNull
  private EmployeeType employeeType;

  /** The department the employee is a member of */
  @JoinColumn(
      name = "department_name",
      foreignKey =
          @ForeignKey(
              name = "department_name_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (department_name) REFERENCES department(longName) "
                      + "ON UPDATE CASCADE ON DELETE SET NULL"))
  @ManyToOne
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @Setter
  @Getter
  private Department department;

  /** Enumerated type representing the possible employee types */
  public enum EmployeeType {
    ADMIN("Administrator"), // Admin employee
    MEDICAL("Medical Practitioner"), // Medical employee
    STAFF("Staff Member"); // Staff, e.g., janitors

    @NonNull public final String type; // Display type

    /**
     * Sets the type for the employee, i.e., string backing
     *
     * @param type the type of employee
     */
    EmployeeType(@NonNull String type) {
      this.type = type;
    }

    /**
     * Override for the toString, returns the type as a string
     *
     * @return the type as a string
     */
    @Override
    public String toString() {
      return this.type;
    }
  }

  /** Empty constructor, required for Hibernate */
  public User() {}

  /**
   * Creates a user with filled-in fields
   *
   * @param firstName the first name
   * @param middleName the middle name
   * @param lastName the last name
   * @param employeeType the type of employee
   * @param department the department
   */
  public User(
      @NonNull String firstName,
      String middleName,
      @NonNull String lastName,
      @NonNull EmployeeType employeeType,
      Department department) {
    this.firstName = firstName; // Set the first name
    this.middleName = middleName; // Set the middle name
    this.lastName = lastName; // Set the last name
    this.employeeType = employeeType; // Set the employee type
    this.department = department; // Set the department
  }

  /**
   * ToString method, returns the first name " " the last name
   *
   * @return the display representation of the user
   */
  @Override
  public String toString() {
    return getFirstName() + " " + getLastName();
  }

  /**
   * Determines if two users are equal by comparing their IDs
   *
   * @param o the other object
   * @return true if and only if both are Users and the second user has the same ID
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // Quick check
    if (o == null || getClass() != o.getClass())
      return false; // Bad the other one is a different class

    User user = (User) o; // Cast

    return getId() == user.getId(); // Check the cast
  }

  /**
   * Hash code implementation, hashes the ID
   *
   * @return the hash of the ID
   */
  @Override
  public int hashCode() {
    return Objects.hash(id); // hash the ID
  }
}
