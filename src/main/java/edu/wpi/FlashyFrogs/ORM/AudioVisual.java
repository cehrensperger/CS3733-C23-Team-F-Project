package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
  private String deviceType;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String reason;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String description;

  /** Creates a new AudioVisual with a generated id */
  public AudioVisual() {
    super.setStatus(Status.BLANK);
    super.setRequestType("AudioVisual");
  }

  /**
   * Creates a new AudioVisual with a generated id and the specified fields
   *
   * @param emp the User to use in the emp field
   * @param requestDate the Date to use in the requestDate field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   * @param deviceType the requested device type
   * @param reason the reason the device is being requested
   * @param description the description for hte request
   * @param location the LocationName to use in the location field
   */
  public AudioVisual(
      HospitalUser emp,
      @NonNull Date requestDate,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      @NonNull String deviceType,
      @NonNull String reason,
      @NonNull String description,
      LocationName location) {
    super.setEmp(emp);
    super.setDate(requestDate);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("AudioVisual");
    super.setLocation(location);
    this.deviceType = deviceType;
    this.reason = reason;
    this.description = description;
  }
}
