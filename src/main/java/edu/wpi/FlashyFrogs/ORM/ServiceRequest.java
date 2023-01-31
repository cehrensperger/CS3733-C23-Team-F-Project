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
  @Basic @Getter @Setter String status;
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

  /** Enumerated type for the possible statuses we can create */
  public enum Status {
    BLANK("blank"),
    PROCESSING("processing"),
    DONE("done");

    @NonNull
    public final String status; // Number backing for the Floor

    /**
     * Creates a new floor with the given String backing
     *
     * @param floor the floor to create. Must not be null
     */
    Status(@NonNull String statusVal) {
      status = statusVal; // The floor to create
    }
  }
}
