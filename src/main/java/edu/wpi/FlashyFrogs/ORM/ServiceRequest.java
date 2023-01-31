package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ServiceRequest")
@Inheritance(strategy = InheritanceType.JOINED)
public class ServiceRequest {
  @Basic @Id @Getter @Setter @GeneratedValue long id;
  @Basic @Getter @Setter String status; // should be enum
  @Basic @Getter @Setter String empFirstName;
  @Basic @Getter @Setter String empMiddleName;
  @Basic @Getter @Setter String empLastName;
  @Basic @Getter @Setter String empDept; // should be enum

  @Basic @Getter @Setter String type; // should be enum

  @Basic @Getter @Setter String assignedEmpFirstName;
  @Basic @Getter @Setter String assignedEmpMiddleName;
  @Basic @Getter @Setter String assignedEmpLastName;

  @Basic @Getter @Setter String assignedEmpDept; // should be enum

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfIncident;

  @Basic
  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfSubmission;
}
