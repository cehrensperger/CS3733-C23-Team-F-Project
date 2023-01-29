package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "InternalTransport")
@PrimaryKeyJoinColumn(name = "service_request_id",
        foreignKey = @ForeignKey(name = "service_request_id_fk"))
public class InternalTransport extends ServiceRequest {
  @Getter @Setter String patientName;
  @Getter @Setter String oldLoc;
  @Getter @Setter String newLoc;

  @Temporal(TemporalType.TIMESTAMP)
  @Getter
  @Setter
  Date dateOfBirth;
}
