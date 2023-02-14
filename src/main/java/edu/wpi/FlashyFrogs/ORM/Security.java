package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

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

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private ThreatType threatType;

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
   * @param emp the User to use in the emp field
   * @param datePreference the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   */
  public Security(
      @NonNull String theIncidentReport,
      LocationName theLocation,
      User emp,
      @NonNull Date datePreference,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      @NonNull ThreatType threatType) {
    this.incidentReport = theIncidentReport;
    super.setLocation(theLocation);
    super.setEmp(emp);
    super.setDate(datePreference);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("Security");
    this.threatType = threatType;
  }

  /** Enumerated type for the possible types we can create */
  public enum ThreatType {
    NONE("No Threat"),
    INTRUDER("Intruder"),
    WEAPON("Weapon"),
    PATIENT("Patient");

    @NonNull public final String ThreatType;

    /**
     * Creates a new status with the given String backing
     *
     * @param threatType the type to create. Must not be null
     */
    ThreatType(@NonNull String threatType) {
      ThreatType = threatType;
    }

    @Override
    public String toString() {
      return this.ThreatType;
    }
  }
}
