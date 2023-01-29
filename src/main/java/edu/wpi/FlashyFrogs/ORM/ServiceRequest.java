package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public class ServiceRequest {
  @Basic @Id @Getter @Setter @GeneratedValue long id;
  @Basic @Getter @Setter String status;
  @Basic @Getter @Setter String empName;
  @Basic @Getter @Setter String empDept;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfIncident;
}
