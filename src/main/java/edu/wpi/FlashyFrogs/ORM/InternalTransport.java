package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

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
  private String patientID; // ID of the patient to be transported

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
  @ManyToOne
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  private LocationName targetLocation; // The target location for transport

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private VisionStatus vision; // Vision status for the patient

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private HearingStatus hearing; // Hearing status for the patient

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private ConsciousnessStatus consciousness; // Consciousness for the patient

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private HealthStatus healthStatus; // Health status of the patient

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Equipment equipment; // Additional equipment for the patient

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private InternalTransport.ModeOfTransport mode; // Mode of transport for the patient

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  private boolean isolation; // Whether the patient is in isolation

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  private String personalItems; // Personal items description for the patient

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  private String reason; // The reason for transport

  /** Creates a new InternalTransport with a generated id */
  public InternalTransport() {
    super.setStatus(Status.BLANK);
    super.setRequestType("InternalTransport");
  }

  /**
   * Creates a new InternalTransport with a generated id and the specified fields
   *
   * @param patientID the ID of the patient to transport
   * @param vision the vision status of the patient
   * @param consciousness the consciousness of the patient
   * @param condition the condition of the patient
   * @param sourceLocation the current location of the patient
   * @param endLocation the target location of the patient
   * @param urgency the urgency of the request
   * @param equipment the equipment needed for transportation
   * @param dateTimePreference the date/time for the move to occur
   * @param submissionDate the date/time the form was submitted
   * @param emp the employee requesting the transport
   * @param modeOfTransport the mode of transport for the patient
   * @param isolation the isolation for the patient (yes or no)
   * @param personalItems personal items to be brought with the person
   * @param reason the reason for the transport
   */
  public InternalTransport(
      @NonNull String patientID, @NonNull VisionStatus vision,
      @NonNull ConsciousnessStatus consciousness, @NonNull HealthStatus condition,
      LocationName sourceLocation, LocationName endLocation, @NonNull Urgency urgency,
      @NonNull Equipment equipment, @NonNull Date dateTimePreference, @NonNull Date submissionDate, User emp,
      @NonNull ModeOfTransport modeOfTransport, boolean isolation, @NonNull String personalItems,
      @NonNull String reason) {
    this.patientID = patientID;
    this.vision = vision;
    this.consciousness = consciousness;
    this.healthStatus = condition;
    this.targetLocation = endLocation;
    this.equipment = equipment;
    this.mode = modeOfTransport;
    this.isolation = isolation;
    this.personalItems = personalItems;
    this.reason = reason;
    super.setLocation(sourceLocation);
    super.setEmp(emp);
    super.setDate(dateTimePreference);
    super.setDateOfSubmission(submissionDate);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("InternalTransport");
  }

  /**
   * Enumerated type for vision status
   */
  public enum VisionStatus {
    GOOD("Good"),
    POOR("Poor"),
    BLIND("Blind"),
    GLASSES("Glasses");

    // The string of the status
    public final String status;

    /**
     * Creates the status string
     * @param status the status string
     */
    VisionStatus(@NonNull String status) {
      this.status = status;
    }

    /**
     * Returns the string representation of the status
     * @return the string representation of the status
     */
    @Override
    public String toString() {
      return this.status;
    }
  }

  /**
   * Enumerated type for internal transport status
   */
  public enum HearingStatus {
    GOOD("Good"),
    POOR("Poor"),
    DEAF("Deaf"),
    AID_LEFT("Hearing Aid (Left)"),
    AID_RIGHT("Hearing Aid (Right)"),
    AID_BOTH("Hearing Aid(Both");

    // The string of the status
    public final String status;

    /**
     * Creates the status string
     * @param status the status string
     */
    HearingStatus(@NonNull String status) {
      this.status = status;
    }

    /**
     * Returns the string representation of the status
     * @return the string representation of the status
     */
    @Override
    public String toString() {
      return this.status;
    }
  }

  /**
   * Consciousness status for the patient
   */
  public enum ConsciousnessStatus {
    GOOD("Good"),
    MODERATE("Moderate"),
    POOR("Poor");

    // The string of the status
    public final String status;

    /**
     * Creates the status string
     * @param status the status string
     */
    ConsciousnessStatus(@NonNull String status) {
      this.status = status;
    }

    /**
     * Returns the string representation of the status
     * @return the string representation of the status
     */
    @Override
    public String toString() {
      return this.status;
    }
  }

  /**
   * Health status for the patient
   */
  public enum HealthStatus {
    HEALTHY("Healthy"),
    MODERATE("Moderate"),
    POOR("Poor");

    // The string of the status
    public final String status;

    /**
     * Creates the status string
     * @param status the status string
     */
    HealthStatus(@NonNull String status) {
      this.status = status;
    }

    /**
     * Returns the string representation of the status
     * @return the string representation of the status
     */
    @Override
    public String toString() {
      return this.status;
    }
  }

  /**
   * Equipment status for the patient (what they need to be transported)
   */
  public enum Equipment {
    NONE("None"),
    CANE("Cane"),
    WALKER("Walker"),
    WHEEL_CHAIR("Wheel Chair"),
    BED("Bed");

    // The string of the status
    public final String status;

    /**
     * Creates the status string
     * @param status the status string
     */
    Equipment(@NonNull String status) {
      this.status = status;
    }

    /**
     * Returns the string representation of the status
     * @return the string representation of the status
     */
    @Override
    public String toString() {
      return this.status;
    }
  }

  /**
   * Mode for the patient, e.g., how much they need to be assisted
   */
  public enum ModeOfTransport {
    SELF("Self"),
    HELP("With Help"),
    EQUIPMENT("Equipment Needed");

    // The string of the status
    public final String status;

    /**
     * Creates the status string
     * @param status the status string
     */
    ModeOfTransport(@NonNull String status) {
      this.status = status;
    }

    /**
     * Returns the string representation of the status
     * @return the string representation of the status
     */
    @Override
    public String toString() {
      return this.status;
    }
  }
}
