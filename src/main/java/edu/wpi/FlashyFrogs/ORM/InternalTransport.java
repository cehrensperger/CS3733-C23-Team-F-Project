package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "InternalTransport")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class InternalTransport extends ServiceRequest {
  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String patientName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String oldLoc;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String newLoc;

  @Column(nullable = false)
  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfBirth;

  public InternalTransport() {}

  public InternalTransport(long id) {
    this.id = id;
  }

  public InternalTransport(
      @NonNull Date theDOB,
      @NonNull String theNewLoc,
      @NonNull String theOldLoc,
      @NonNull String thePatientName,
      long theId, @NonNull String empFirstName, @NonNull String empMiddleName, @NonNull String empLastName, @NonNull String assignedEmpFirstName, @NonNull String assignedEmpMiddleName, @NonNull String assignedEmpLastName, @NonNull EmpDept empDept, @NonNull EmpDept assignedEmpDept, @NonNull Date dateOfIncident, @NonNull Date dateOfSubmission, @NonNull Urgency urgency) {
    this.dateOfBirth = theDOB;
    this.newLoc = theNewLoc;
    this.oldLoc = theOldLoc;
    this.patientName = thePatientName;
    this.id = theId;
    this.empFirstName = empFirstName;
    this.empMiddleName = empMiddleName;
    this.empLastName = empLastName;
    this.empDept = empDept;
    this.assignedEmpFirstName = assignedEmpFirstName;
    this.assignedEmpMiddleName = assignedEmpMiddleName;
    this.assignedEmpLastName = assignedEmpLastName;
    this.assignedEmpDept = assignedEmpDept;
    this.dateOfIncident = dateOfIncident;
    this.dateOfSubmission = dateOfSubmission;
    this.status = Status.BLANK;
    this.urgency = urgency;
  }
}
