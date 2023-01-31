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
  @Basic @Getter @Setter String type;
  @Basic @Getter @Setter String location;

  public Sanitation() {
    super();
  }

  public Sanitation(long id) {
    this.id = id;
  }

  public Sanitation(String theLocation, String theType, long theId) {
    super();
    this.location = theLocation;
    this.type = theType;
    this.id = theId;
  }
}
