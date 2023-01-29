package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Move")
public class Move {
  @Id @Getter @Setter String nodeID;
  @Id @Getter @Setter String longName;
  @Temporal(TemporalType.TIMESTAMP)
  @Id
  @Getter
  @Setter
  Date moveDate;
}
