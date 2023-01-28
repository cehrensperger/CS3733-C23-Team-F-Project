package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

public class InternalTransport {
    @Getter @Setter String patientName;
    @Getter @Setter String oldLoc;
    @Getter @Setter String newLoc;
    @Temporal(TemporalType.DATE) @Getter @Setter Date dateOfBirth;
    @Getter @Setter int srID;
}
