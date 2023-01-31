package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
  String incidentReport;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String location;

  public Security() {}

  public Security(long id) {
    this.id = id;
  }

  public Security(
      @NonNull String theIncidentReport,
      @NonNull String theLocation,
      long theId) { // needs to also take in super class fields
    this.incidentReport = theIncidentReport;
    this.location = theLocation;
    this.id = theId;
  }
}
