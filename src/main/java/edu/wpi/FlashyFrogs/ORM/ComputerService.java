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
  DeviceType deviceType;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String model;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  ServiceType serviceType;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String issue;

  /** Creates a new ComputerService with a generated id */
  public ComputerService() {
    this.requestType = "ComputerService";
  }

  /**
   * Creates a new ComputerService with a generated id and the specified fields
   *
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
   * @param issue the String to use in the issue field
   * @param model the String to use in the issue field
   * @param deviceType the String to use in the deviceType field
   * @param serviceType the ServiceType to use in the serviceType field
   */
  public ComputerService(
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
      @NonNull DeviceType deviceType,
      @NonNull String model,
      @NonNull String issue,
      @NonNull ServiceType serviceType) {
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
    this.requestType = "ComputerService";
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
