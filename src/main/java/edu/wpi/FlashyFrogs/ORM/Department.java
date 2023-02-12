package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Department table, represents departments an employee can belong to */
@Entity
@Table(name = "department")

@AllArgsConstructor
public class Department {

  /** Long (formal) name for the department */
  @Id
  @Column(nullable = false)
  @NonNull
  @Getter
  private String longName;


  /** Short (display) name for the department */
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String shortName;


  /**
   * Equals method, returns true if the long names are identical
   *
   * @param o the other department to compare this to
   * @return true if the two are equal in long name, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Department that = (Department) o;

    return getLongName().equals(that.getLongName());
  }

  /**
   * Hash code implementation
   *
   * @return the hash code for the long name
   */
  @Override
  public int hashCode() {
    return getLongName().hashCode();
  }

  /**
   * Implementation for the to-string method, gets the short name
   *
   * @return the short name for the employee
   */
  @Override
  public String toString() {
    return getShortName();
  }

  /** Empty constructor, required by hibernate */
  public Department() {}
}
