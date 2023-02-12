package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "ServiceRequest")
@Inheritance(strategy = InheritanceType.JOINED)
public class ServiceRequest {
  @Basic
  @Id
  @Column(nullable = false)
  @Getter
  @GeneratedValue
  private long id;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Status status;

  @Getter
  @Setter
  @JoinColumn(
      name = "empid",
      foreignKey =
          @ForeignKey(
              name = "empid_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (empid) REFERENCES " + "\"emp\"(id) ON DELETE SET NULL"),
      nullable = false)
  @NonNull
  @ManyToOne(optional = false)
  private User emp;

  @Getter
  @Setter
  @JoinColumn(
      name = "assignedempid",
      foreignKey =
          @ForeignKey(
              name = "assignedempid_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (assignedempid) REFERENCES " + "\"emp\"(id) ON DELETE SET NULL"))
  @ManyToOne(optional = false)
  private User assignedEmp;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Date dateOfIncident;

  @Basic
  @Temporal(TemporalType.DATE)
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Date dateOfSubmission;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Urgency urgency;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String requestType;

  /** Enumerated type for the possible statuses we can create */
  public enum Status {
    BLANK("blank"),
    PROCESSING("processing"),
    DONE("done");

    @NonNull public final String status;

    /**
     * Creates a new status with the given String backing
     *
     * @param statusVal the status to create. Must not be null
     */
    Status(@NonNull String statusVal) {
      status = statusVal;
    }

    /**
     * Override for the toString, returns the status as a string
     *
     * @return the status as a string
     */
    @Override
    public String toString() {
      return this.status;
    }
  }

  /** Enumerated type for the possible departments we can create */
  public enum EmpDept {
    NURSING("nursing"),
    CARDIOLOGY("cardiology"),
    RADIOLOGY("radiology"),
    MAINTENANCE("maintenance"),
    TRAUMA_UNIT("trauma unit");

    @NonNull public final String EmpDept;

    /**
     * Creates a new status with the given String backing
     *
     * @param dept the dept to create. Must not be null
     */
    EmpDept(@NonNull String dept) {
      EmpDept = dept;
    }

    /**
     * Override for the toString, returns the department as a string
     *
     * @return the department as a string
     */
    @Override
    public String toString() {
      return this.EmpDept;
    }
  }

  /** Enumerated type for the possible urgencies we can create */
  public enum Urgency {
    VERY_URGENT("very urgent"),
    MODERATELY_URGENT("moderately urgent"),
    NOT_URGENT("not urgent");

    @NonNull public final String Urgency;

    /**
     * Creates a new status with the given String backing
     *
     * @param urgency the urgency to create. Must not be null
     */
    Urgency(@NonNull String urgency) {
      Urgency = urgency;
    }

    /**
     * Override for the toString, returns the urgency as a string
     *
     * @return the urgency as a string
     */
    @Override
    public String toString() {
      return this.Urgency;
    }
  }

  /**
   * Overrides the default equals method with one that compares the primary key of the
   * ServiceRequests
   *
   * @param obj the Service Request to compare primary keys against
   * @return boolean whether the primary keys are equal or not
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    ServiceRequest other = (ServiceRequest) obj;
    return (this.getId() == other.getId());
  }

  /**
   * Overrides the default hashCode method with one that uses the id of the object
   *
   * @return the new hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }

  /**
   * Overrides the default toString method with one that returns the type of service request that it
   * is, concatenated with its id
   *
   * @return the className and id separated by an underscore
   */
  @Override
  @NonNull
  public String toString() {
    return this.getClass().getSimpleName() + "_" + this.id;
  }
}
