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
  private String issue;

  /** Creates a new ComputerService with a generated id */
  public ComputerService() {
    super.setStatus(Status.BLANK);
    super.setRequestType("ComputerService");
  }

  /**
   * Creates a new ComputerService with a generated id and the specified fields
   *
   * @param emp the User to use in the emp field
   * @param dateOfIncident the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   * @param issue the String to use in the issue field
   * @param model the String to use in the issue field
   * @param deviceType the String to use in the deviceType field
   * @param serviceType the ServiceType to use in the serviceType field
   */
  public ComputerService(
      User emp,
      @NonNull Date dateOfIncident,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      @NonNull DeviceType deviceType,
      @NonNull String model,
      @NonNull String issue,
      @NonNull ServiceType serviceType) {
    super.setEmp(emp);
    super.setTargetDate(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("ComputerService");
    this.deviceType = deviceType;
    this.model = model;
    this.issue = issue;
    this.serviceType = serviceType;
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
