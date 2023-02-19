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
  private SanitationType type;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Boolean isolation;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private BiohazardLevel biohazard;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String description;

  /** Creates a new Sanitation with a generated id */
  public Sanitation() {
    super.setStatus(Status.BLANK);
    super.setRequestType("Sanitation");
  }

  /**
   * Creates a new Sanitation with a generated id and the specified fields
   *
   * @param location the LocationName to use in the location field
   * @param sanitationType the SanitationType to use in the type field
   * @param emp the User to use in the emp field
   * @param datePreference the Date to use in the date field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   * @param isolation the Boolean to indicate if isolation is needed
   * @param biohazard the Biohazard to use in the biohazard field
   * @param description the description of the incident
   */
  public Sanitation(
      @NonNull SanitationType sanitationType,
      HospitalUser emp,
      @NonNull Date datePreference,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      LocationName location,
      @NonNull Boolean isolation,
      @NonNull BiohazardLevel biohazard,
      @NonNull String description) {
    this.type = sanitationType;
    super.setEmp(emp);
    super.setDate(datePreference);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setLocation(location);
    super.setRequestType("Sanitation");
    this.isolation = isolation;
    this.biohazard = biohazard;
    this.description = description;
  }

  /** Enumerated type for biohazard levels */
  public enum BiohazardLevel {
    BSL1("BSL-1"),
    BSL2("BSL-2"),
    BSL3("BSL-3"),
    BSL4("BSL-4");

    @NonNull public final String BiohazardLevel;

    /**
     * Creates a new status with the given String backing
     *
     * @param biohazard the biohazard to create. Must not be null
     */
    BiohazardLevel(@NonNull String biohazard) {
      BiohazardLevel = biohazard;
    }

    @Override
    public String toString() {
      return this.BiohazardLevel;
    }
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

    @Override
    public String toString() {
      return this.SanitationType;
    }
  }
}
