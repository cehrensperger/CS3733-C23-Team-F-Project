package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ServiceRequest")
public class ServiceRequest {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Getter
  @Setter
  int id;

  @Basic @Getter @Setter String status;
  @Basic @Getter @Setter String empName;
  @Basic @Getter @Setter String empDept;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfIncident;
}
