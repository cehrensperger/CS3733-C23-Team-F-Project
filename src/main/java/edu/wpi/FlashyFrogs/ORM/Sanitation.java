package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
  String type;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String location;

  public Sanitation() {}

  public Sanitation(long id) {
    this.id = id;
  }

  public Sanitation(@NonNull String theLocation, @NonNull String theType, long theId, String empName) {
    this.location = theLocation;
    this.type = theType;
    this.id = theId;
    //this.empName = empName;
  }
}
