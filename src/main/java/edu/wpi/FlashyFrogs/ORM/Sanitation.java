package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Sanitation")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class Sanitation extends ServiceRequest {
  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  SanitationType type;

  @Getter
  @Setter
  @JoinColumn(
      name = "location",
      foreignKey = @ForeignKey(name = "location_name_fk"),
      nullable = false)
  @NonNull
  @ManyToOne
  LocationName location;

  /** Creates a new Sanitation with a generated id */
  public Sanitation() {this.requestType = "Sanitation";}

  /**
   * Creates a new Sanitation with a generated id and the specified fields
   *
   * @param location the LocationName to use in the location field
   * @param theType the SanitationType to use in the type field
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
  public Sanitation(
      @NonNull SanitationType theType,
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
      @NonNull Urgency urgency,
      @NonNull LocationName location) {
    this.type = theType;
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
    this.location = location;
    this.requestType = "Sanitation";
  }

  /** Enumerated type for the possible types we can create */
  public enum SanitationType {
    MOPPING("mopping"),
    SWEEPING("sweeping"),
    VACUUMING("vacuuming");

    @NonNull public final String SanitationType;

    /**
     * Creates a new status with the given String backing
     *
     * @param type the type to create. Must not be null
     */
    SanitationType(@NonNull String type) {
      SanitationType = type;
    }
  }
}
