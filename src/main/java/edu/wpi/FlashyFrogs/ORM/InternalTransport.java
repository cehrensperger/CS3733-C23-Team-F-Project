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
      name = "oldLoc",
      foreignKey =
          @ForeignKey(
              name = "location_name1_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (oldLoc) REFERENCES "
                      + "locationname(longName) ON UPDATE CASCADE ON DELETE SET NULL"))
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @ManyToOne
  private LocationName oldLoc;

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
  private LocationName newLoc;

  @Column(nullable = false)
  @NonNull
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  private Date dateOfBirth;

  /** Creates a new InternalTransport with a generated id */
  public InternalTransport() {
    super.setStatus(Status.BLANK);
    super.setRequestType("InternalTransport");
  }

  /**
   * Creates a new InternalTransport with a generated id and the specified fields
   *
   * @param theDOB the Date to use in the DOB field
   * @param theNewLoc the LocationName to use in the newLoc field
   * @param theOldLoc the LocationName to use in the oldLoc field
   * @param thePatientFirstName the String to use in the patientFirstName field
   * @param thePatientMiddleName the String to use in the patientMiddleName field
   * @param thePatientLastName the String to use in the patientlastName field
   * @param emp the User to use in the emp field
   * @param dateOfIncident the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   */
  public InternalTransport(
      @NonNull Date theDOB,
      LocationName theNewLoc,
      LocationName theOldLoc,
      @NonNull String thePatientFirstName,
      @NonNull String thePatientMiddleName,
      @NonNull String thePatientLastName,
      User emp,
      @NonNull Date dateOfIncident,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency) {
    this.dateOfBirth = theDOB;
    this.newLoc = theNewLoc;
    this.oldLoc = theOldLoc;
    this.patientFirstName = thePatientFirstName;
    this.patientMiddleName = thePatientMiddleName;
    this.patientLastName = thePatientLastName;
    super.setEmp(emp);
    super.setDateOfIncident(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("InternalTransport");
  }
}
