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

  @Getter
  @Setter
  @JoinColumn(
      name = "location",
      foreignKey =
          @ForeignKey(
              name = "location_name_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (location) REFERENCES locationname(longname) "
                      + "ON UPDATE CASCADE ON DELETE SET NULL"),
      nullable = false)
  @NonNull
  @ManyToOne(optional = false)
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  private LocationName location;

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
   * @param dateOfIncident the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   */
  public Security(
      @NonNull String theIncidentReport,
      @NonNull LocationName theLocation,
      @NonNull User emp,
      @NonNull Date dateOfIncident,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency) {
    this.incidentReport = theIncidentReport;
    this.location = theLocation;
    super.setEmp(emp);
    super.setDateOfIncident(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    super.setRequestType("Security");
  }
}
