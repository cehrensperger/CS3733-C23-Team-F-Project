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
  @Basic @Id @Column(nullable = false)
  @NonNull @Getter @Setter @GeneratedValue long id;
  @Basic  @Column(nullable = false)
  @NonNull@Getter @Setter Status status; // should be enum
  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String empFirstName;
  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String empMiddleName;
  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String empLastName;
  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String empDept; // should be enum

  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String type; // should be enum

  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String assignedEmpFirstName;
  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String assignedEmpMiddleName;
  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String assignedEmpLastName;

  @Basic @Column(nullable = false)
  @NonNull @Getter @Setter String assignedEmpDept; // should be enum

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  Date dateOfIncident;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  Date dateOfSubmission;


  /** Enumerated type for the possible statuses we can create */
  public enum Status {
    BLANK("blank"),
    PROCESSING("processing"),
    DONE("done");

    @NonNull public final String status; // Number backing for the Floor

    /**
     * Creates a new status with the given String backing
     *
     * @param statusVal the status to create. Must not be null
     */
    Status(@NonNull String statusVal) {
      status = statusVal; // The floor to create
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
  @NonNull
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    ServiceRequest other = (ServiceRequest) obj;
    return (this.getId() == other.getId());
  }

  /**
   * Overrides the default hashCode method with one that uses the id and dateOfSubmission of the
   * object
   *
   * @return the new hashcode
   */
  @Override
  @NonNull
  public int hashCode() {
    return Objects.hash(this.id, this.dateOfSubmission);
  }

  /**
   * Overrides the default toString method with one that returns the type of service request that it is, concatenated with its id
   * @return the className and id separated by an underscore
   */
  @Override
  @NonNull
  public String toString() {
    return this.getClass().getSimpleName() + "_" + this.id;
  }
}
