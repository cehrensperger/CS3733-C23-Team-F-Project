package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

public class ServiceRequest {
    @Getter @Setter int id;
    @Getter @Setter String status;
    @Getter @Setter String empName;
    @Temporal(TemporalType.DATE) @Getter @Setter String empDept;
    @Getter @Setter Date dateOfIncident;
}
