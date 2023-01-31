package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Sanitation")
@PrimaryKeyJoinColumn(
    name = "service_request_id",
    foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class Sanitation extends ServiceRequest {
  @Basic @Getter @Setter String sanitationType; // should be enum
  @Basic @Getter @Setter String location; // should be enum
  @Basic @Getter @Setter String urgency; // should be enum

  public Sanitation() {}

  public Sanitation(long id) {
    this.id = id;
  }

  public Sanitation(String theLocation, String theType, long theId) {
    this.location = theLocation;
    this.type = theType;
    this.id = theId;
  }
}
