package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "InternalTransport")
public class InternalTransport {
  @Getter @Setter String patientName;
  @Getter @Setter String oldLoc;
  @Getter @Setter String newLoc;

  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfBirth;

  @Basic @Id @Getter @Setter @GeneratedValue long srID;
}
