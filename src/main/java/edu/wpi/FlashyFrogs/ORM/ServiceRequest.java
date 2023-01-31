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
  @Basic @Id @Getter @Setter @GeneratedValue long id;
  @Basic @Getter @Setter Status status;
  @Basic @Getter @Setter String empName;
  @Basic @Getter @Setter String empDept;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfIncident;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfSubmission;

  public ServiceRequest() {
    this.status = ServiceRequest.Status.valueOf("BLANK");
  }

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
