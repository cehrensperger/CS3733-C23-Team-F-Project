package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Edge")
public class Edge {
  @Id @Getter @Setter String nodeID1;
  @Id @Getter @Setter String nodeID2;
  //  @Getter @Setter String
}
