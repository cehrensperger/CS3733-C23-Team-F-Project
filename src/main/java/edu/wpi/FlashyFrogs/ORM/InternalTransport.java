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
  private String patientFirstName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String patientMiddleName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String patientLastName;

  @Getter
  @Setter
  @JoinColumn(
      name = "oldLoc",
      foreignKey =
          @ForeignKey(
              name = "location_name1_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (oldLoc) REFERENCES "
                      + "locationname(longName) ON UPDATE CASCADE ON DELETE SET NULL"),
      nullable = false)
  @NonNull
  @ManyToOne
  private LocationName oldLoc;

  @Getter
  @Setter
  @JoinColumn(
      name = "newLoc",
      foreignKey =
          @ForeignKey(
              name = "location_name2_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (newLoc) REFERENCES locationname(longName) "
                      + "ON UPDATE CASCADE ON DELETE SET NULL"))
  @NonNull
  @ManyToOne
  private LocationName newLoc;

  @Column(nullable = false)
  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  private Date dateOfBirth;

  /** Creates a new InternalTransport with a generated id */
  public InternalTransport() {
    super.setStatus(Status.BLANK);
    super.setRequestType("InternalTransport");
  }

  /**
   * Creates a new InternalTransport with a generated id and the specified fields
   *
   * @param theDOB the Date to use in the DOB field
   * @param theNewLoc the LocationName to use in the newLoc field
   * @param theOldLoc the LocationName to use in the oldLoc field
   * @param thePatientFirstName the String to use in the patientFirstName field
   * @param thePatientMiddleName the String to use in the patientMiddleName field
   * @param thePatientLastName the String to use in the patientlastName field
   * @param empFirstName the String to use in the empFirstName field
   * @param empMiddleName the String to use in the empMiddleName field
   * @param empLastName the String to use in the empLastName field
   * @param assignedEmpFirstName the String to use in the assignedEmpFirstName field
   * @param assignedEmpMiddleName the String to use in the assignedEmpMiddleName field
   * @param assignedEmpLastName the String to use in the assignedEmpLastName field
   * @param empDept the EmpDept to use in the empDept field
   * @param assignedEmpDept the EmpDept to use in the assignedEmpDept field
   * @param dateOfIncident the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   */
  public InternalTransport(
      @NonNull Date theDOB,
      @NonNull LocationName theNewLoc,
      @NonNull LocationName theOldLoc,
      @NonNull String thePatientFirstName,
      @NonNull String thePatientMiddleName,
      @NonNull String thePatientLastName,
      @NonNull String empFirstName,
      @NonNull String empMiddleName,
      @NonNull String empLastName,
      @NonNull String assignedEmpFirstName,
      @NonNull String assignedEmpMiddleName,
      @NonNull String assignedEmpLastName,
      @NonNull EmpDept empDept,
      @NonNull EmpDept assignedEmpDept,
      @NonNull Date dateOfIncident,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency) {
    this.dateOfBirth = theDOB;
    this.newLoc = theNewLoc;
    this.oldLoc = theOldLoc;
    this.patientFirstName = thePatientFirstName;
    this.patientMiddleName = thePatientMiddleName;
    this.patientLastName = thePatientLastName;
    super.setEmpFirstName(empFirstName);
    super.setEmpMiddleName(empMiddleName);
    super.setEmpLastName(empLastName);
    super.setEmpDept(empDept);
    super.setAssignedEmpFirstName(assignedEmpFirstName);
    super.setAssignedEmpMiddleName(assignedEmpMiddleName);
    super.setAssignedEmpLastName(assignedEmpLastName);
    super.setAssignedEmpDept(assignedEmpDept);
    super.setDateOfIncident(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("InternalTransport");
  }
}
