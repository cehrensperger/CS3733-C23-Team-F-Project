package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "alert")
public class Alert {
  @Id
  @Basic
  @Column(nullable = false)
  @Getter
  @GeneratedValue
  private long id; // UUID for the announcement

  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  @Getter
  @Setter
  @NonNull
  private Date startDisplayDate; // Date that the announcement is for

  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  @Getter
  @Setter
  @NonNull
  private Date endDisplayDate; // Date that the announcement is for

  @ManyToOne
  @Getter
  @JoinColumn(
      name = "author",
      foreignKey =
          @ForeignKey(
              name = "author_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (author) REFERENCES hospital_user(id) ON DELETE SET NULL"))
  private HospitalUser author; // Author for the message

  @ManyToOne
  @Getter
  @Setter
  @JoinColumn(
      name = "department",
      foreignKey =
          @ForeignKey(
              name = "department_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (department) REFERENCES department(longName) ON DELETE SET NULL"))
  private Department department; // Department for the message

  @Basic
  @Getter
  @Setter
  @Column(nullable = false)
  @NonNull
  private String description;

  @Basic
  @Getter
  @Setter
  @Column(nullable = false)
  @NonNull
  private String announcement;

  @Basic
  @Getter
  @Setter
  @Column(nullable = false)
  @NonNull
  private Severity severity;

  /** Empty constructor, required by hibernate */
  public Alert() {}

  /**
   * Creates the announcement with the given parameters
   *
   * @param startDisplayDate the date to create
   * @param author the author of the announcement
   * @param announcement the announcement body to create
   */
  public Alert(
      @NonNull Date startDisplayDate,
      @NonNull Date endDisplayDate,
      HospitalUser author,
      @NonNull String description,
      @NonNull String announcement,
      @NonNull Department department,
      @NonNull Severity severity) {
    this.startDisplayDate = startDisplayDate;
    this.endDisplayDate = endDisplayDate;
    this.author = author;
    this.description = description;
    this.announcement = announcement;
    this.department = department;
    this.severity = severity;
  }

  /** Enumerated type for the possible severities */
  public enum Severity {
    NONE("None"),
    MILD("Mild"),
    INTERMEDIATE("Intermediate"),
    SEVERE("Severe");

    @NonNull public final String severity;

    /**
     * Creates a new severity with the given String backing
     *
     * @param severityVal the severity to create. Must not be null
     */
    Severity(@NonNull String severityVal) {
      severity = severityVal;
    }

    /**
     * Override for the toString, returns the severity as a string
     *
     * @return the status as a string
     */
    @Override
    public String toString() {
      return this.severity;
    }
  }

  /**
   * Equals method, only returns true if the ids are equal
   *
   * @param o the object to compare this to
   * @return true if both are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // If the two are the same in memory, easy
    if (o == null || getClass() != o.getClass())
      return false; // If they aren't the same class or one is null, bad

    Alert that = (Alert) o; // Compare announcements

    return getId() == that.getId(); // Check IDs, do it based on that
  }

  /**
   * Hash code, hashes the ID
   *
   * @return the ID hash
   */
  @Override
  public int hashCode() {
    return Objects.hash(getId());
  }

  /**
   * ToString for the announcement, returns the ID
   *
   * @return the ID as a string
   */
  @Override
  public String toString() {
    return Long.toString(this.id);
  }
}
