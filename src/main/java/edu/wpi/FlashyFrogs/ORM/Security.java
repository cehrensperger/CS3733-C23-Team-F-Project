package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

@Entity
@Table(name = "Security")
@PrimaryKeyJoinColumn(name = "service_request_id")
public class Security extends ServiceRequest {
  @Basic @Getter @Setter String incidentReport;
  @Basic @Getter @Setter String location;
}
