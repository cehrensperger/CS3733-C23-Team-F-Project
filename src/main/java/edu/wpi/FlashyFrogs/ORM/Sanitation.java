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

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  Location location;

  public Sanitation() {}

  public Sanitation(long id) {
    this.id = id;
  }

  public Sanitation(
      @NonNull SanitationType theType,
      long theId,
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
      @NonNull Location location) {
    this.type = theType;
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
    this.location = location;
  }

  /** Enumerated type for the possible types we can create */
  public enum SanitationType {
    MOPPING("mopping"),
    SWEEPING("sweeping"),
    VACUUMING("vacuuming");

    @NonNull public final String SanitationType; // Number backing for the Floor

    /**
     * Creates a new status with the given String backing
     *
     * @param type the type to create. Must not be null
     */
    SanitationType(@NonNull String type) {
      SanitationType = type; // The floor to create
    }
  }

  /** Enumerated type for the possible locations we can create */
  public enum Location {
    ROOM_1("room 1"),
    ROOM_2("room 2"),
    PUBLIC_SPACE_1("public space 1"),
    PUBLIC_SPACE_2("public space 2");

    @NonNull public final String Location; // Number backing for the Floor

    /**
     * Creates a new status with the given String backing
     *
     * @param location the location to create. Must not be null
     */
    Location(@NonNull String location) {
      Location = location; // The floor to create
    }
  }
}
