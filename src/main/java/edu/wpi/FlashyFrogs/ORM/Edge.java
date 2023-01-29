package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Edge")
public class Edge {
  @Id @ManyToOne @Getter @Setter Node nodeID1;
  @Id @ManyToOne @Getter @Setter Node nodeID2;
}
