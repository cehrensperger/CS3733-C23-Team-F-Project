package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "ComputerService")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class ComputerService extends ServiceRequest {

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private DeviceType deviceType;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String model;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private ServiceType serviceType;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String description;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String bestContact;

  /** Creates a new ComputerService with a generated id */
  public ComputerService() {
    super.setStatus(Status.BLANK);
    super.setRequestType("Computer");
  }

  /**
   * Creates a new ComputerService with a generated id and the specified fields
   *
   * @param emp the User to use in the emp field
   * @param location the Location of the service request
   * @param dateNeededBy the Date the Computer Service is needed by
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   * @param description the String to use in the issue field
   * @param deviceType the deviceTyper to use in the deviceType field
   * @param serviceType the ServiceType to use in the serviceType field
   * @param bestContact the String to use in the bestContact field
   */
  public ComputerService(
      HospitalUser emp,
      LocationName location,
      @NonNull Date dateNeededBy,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      @NonNull DeviceType deviceType,
      @NonNull String model,
      @NonNull String description,
      @NonNull ServiceType serviceType,
      @NonNull String bestContact) {
    super.setEmp(emp);
    super.setLocation(location);
    super.setDate(dateNeededBy);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("Computer");
    this.deviceType = deviceType;
    this.model = model;
    this.description = description;
    this.serviceType = serviceType;
    this.bestContact = bestContact;
  }

  /** Enumerated type for the possible serviceTypes we can create */
  public enum ServiceType {
    HARDWARE_REPAIR("hardware repair"),
    SOFTWARE_REPAIR("software repair"),
    CONNECTION_ISSUE("connection issue"),
    MISC("miscellaneous");

    @NonNull public final String ServiceType;

    /**
     * Creates a new serviceType with the given String backing
     *
     * @param serviceType the serviceType to create. Must not be null
     */
    ServiceType(@NonNull String serviceType) {
      ServiceType = serviceType;
    }

    /**
     * Override for the toString, returns the type as a string
     *
     * @return the type as a string
     */
    @Override
    public String toString() {
      return this.ServiceType;
    }
  }

  /** Enumerated type for the possible DeviceTypes we can create */
  public enum DeviceType {
    KIOSK("kiosk"),
    LAPTOP("laptop"),
    DESKTOP("desktop"),
    PERSONAL("personal");

    @NonNull public final String DeviceType;

    /**
     * Creates a new DeviceType with the given String backing
     *
     * @param deviceType the DeviceType to create. Must not be null
     */
    DeviceType(@NonNull String deviceType) {
      DeviceType = deviceType;
    }
  }
}
