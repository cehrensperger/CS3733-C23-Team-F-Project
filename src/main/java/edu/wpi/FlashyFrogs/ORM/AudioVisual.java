package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "AudioVisual")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class AudioVisual extends ServiceRequest {
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
      name = "location",
      foreignKey =
          @ForeignKey(
              name = "location_name1_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (location) REFERENCES locationname(longName) "
                      + "ON UPDATE CASCADE ON DELETE SET NULL"),
      nullable = false)
  @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
  @NonNull
  @ManyToOne(optional = false)
  private LocationName location;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private AccommodationType accommodationType;

  @Column(nullable = false)
  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  private Date dateOfBirth;

  /** Creates a new AudioVisual with a generated id */
  public AudioVisual() {
    super.setStatus(Status.BLANK);
    super.setRequestType("AudioVisual");
  }
  /**
   * Creates a new AudioVisual with a generated id and the specified fields
   *
   * @param emp the User to use in the emp field
   * @param dateOfIncident the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   * @param accommodationType the AccommodationType to use in the accommodationType field
   * @param patientFirstName the String to use in the patientFirstName field
   * @param patientMiddleName the String to use in the patientMiddleName field
   * @param patientLastName the String to use in the patientLastName field
   * @param location the LocationName to use in the location field
   * @param dateOfBirth the Date to use in the dateOfBirth field
   */
  public AudioVisual(
      @NonNull User emp,
      @NonNull Date dateOfIncident,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      @NonNull AccommodationType accommodationType,
      @NonNull String patientFirstName,
      @NonNull String patientMiddleName,
      @NonNull String patientLastName,
      @NonNull LocationName location,
      @NonNull Date dateOfBirth) {
    super.setEmp(emp);
    super.setDateOfIncident(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("AudioVisual");
    this.accommodationType = accommodationType;
    this.location = location;
    this.patientFirstName = patientFirstName;
    this.patientMiddleName = patientMiddleName;
    this.patientLastName = patientLastName;
    this.dateOfBirth = dateOfBirth;
  }

  public enum AccommodationType {
    AUDIO("audio"),
    VISUAL("visual"),
    BOTH("both");

    @NonNull public final String AccommodationType;

    /**
     * Creates a new AcomodationType with the given String backing
     *
     * @param accommodationType the AcomodationType to create. Must not be null
     */
    AccommodationType(@NonNull String accommodationType) {
      AccommodationType = accommodationType;
    }
  }
}
