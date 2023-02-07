package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Security")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class Security extends ServiceRequest {
  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String incidentReport;

  @Getter
  @Setter
  @JoinColumn(
      name = "location",
      foreignKey = @ForeignKey(name = "location_name_fk"),
      nullable = false)
  @NonNull
  @ManyToOne
  private LocationName location;

  /** Creates a new Security object with a generated id */
  public Security() {
    super.setStatus(Status.BLANK);
    super.setRequestType("Security");
  }

  /**
   * Creates a new Security with a generated id and the specified fields
   *
   * @param theIncidentReport the String to use in the incidentReport field
   * @param theLocation the LocationName to use in the location field
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
  public Security(
      @NonNull String theIncidentReport,
      @NonNull LocationName theLocation,
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
    this.incidentReport = theIncidentReport;
    this.location = theLocation;
    super.setEmpFirstName(empFirstName);
    super.setEmpMiddleName(empMiddleName);
    super.setEmpLastName(empLastName);
    ;
    super.setEmpDept(empDept);
    super.setAssignedEmpFirstName(assignedEmpFirstName);
    super.setAssignedEmpMiddleName(assignedEmpMiddleName);
    super.setAssignedEmpLastName(assignedEmpLastName);
    super.setAssignedEmpDept(assignedEmpDept);
    super.setDateOfIncident(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("Security");
  }
}
