package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Security")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class Security extends ServiceRequest {
  @Basic @Getter @Setter String incidentReport;
  @Basic @Getter @Setter String location;

  public Security() {
    super();
  }

  public Security(long id) {
    this.id = id;
  }

  public Security(String theIncidentReport, String theLocation, long theId) {
    super();
    this.incidentReport = theIncidentReport;
    this.location = theLocation;
    this.id = theId;
  }
}
