package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "Religion")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class Religion extends ServiceRequest {
  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String patientID;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String religion;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String description;

  /** Creates a new Religion object with a generated id */
  public Religion() {
    super.setStatus(Status.BLANK);
    super.setRequestType("Religion");
  }

  /**
   * Creates a new Religion with a generated id and the specified fields
   *
   * @param patientID the ID of the patient
   * @param theLocation the LocationName to use in the location field
   * @param emp the User to use in the emp field
   * @param datePreference the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   */
  public Religion(
      @NonNull String patientID,
      LocationName theLocation,
      @NonNull String religion,
      @NonNull String request,
      @NonNull Urgency urgency,
      @NonNull Date datePreference,
      @NonNull Date dateOfSubmission,
      HospitalUser emp
      ) {
    this.patientID = patientID;
    super.setLocation(theLocation);
    super.setEmp(emp);
    super.setDate(datePreference);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("Security");
    this.religion = religion;
    this.description = request;
  }
}
