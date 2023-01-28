package edu.wpi.FlashyFrogs.ORM;

import lombok.Getter;
import lombok.Setter;
import java.util.*;

public class Move {
    @Getter @Setter String nodeID;
    @Getter @Setter String longName;
    @Getter @Setter Date moveDate;
}
