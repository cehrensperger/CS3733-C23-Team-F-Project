package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

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

  @Getter
  @Setter
  @JoinColumn(
      name = "location",
      foreignKey =
          @ForeignKey(
              name = "location_name_fk",
              foreignKeyDefinition =
                  "FOREIGN KEY (location) REFERENCES locationname(longName) "
                      + "ON UPDATE CASCADE ON DELETE SET NULL"),
      nullable = false)
  @NonNull
  @ManyToOne(optional = false)
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  private LocationName location;

  /** Creates a new Sanitation with a generated id */
  public Sanitation() {
    super.setStatus(Status.BLANK);
    super.setRequestType("Sanitation");
  }

  /**
   * Creates a new Sanitation with a generated id and the specified fields
   *
   * @param location the LocationName to use in the location field
   * @param theType the SanitationType to use in the type field
   * @param emp the User to use in the emp field
   * @param dateOfIncident the Date to use in the dateOfIncident field
   * @param dateOfSubmission the Date to use in the dateOfSubmission field
   * @param urgency the Urgency to use in the urgency field
   */
  public Sanitation(
      @NonNull SanitationType theType,
      @NonNull User emp,
      @NonNull Date dateOfIncident,
      @NonNull Date dateOfSubmission,
      @NonNull Urgency urgency,
      @NonNull LocationName location) {
    this.type = theType;
    super.setEmp(emp);
    super.setDateOfIncident(dateOfIncident);
    super.setDateOfSubmission(dateOfSubmission);
    super.setStatus(Status.BLANK);
    super.setUrgency(urgency);
    this.location = location;
    super.setRequestType("Sanitation");
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
  }
}
