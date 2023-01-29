package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Sanitation")
@PrimaryKeyJoinColumn(name = "service_request_id")
public class Sanitation extends ServiceRequest {
  @Basic @Getter @Setter String type;
  @Basic @Getter @Setter String location;
}
