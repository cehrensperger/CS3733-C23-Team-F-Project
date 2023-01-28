package edu.wpi.FlashyFrogs.ORM;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

public class Security {
    @Getter @Setter String incidentReport;
    @Getter @Setter String location;
    @Getter @Setter int srID;
}
