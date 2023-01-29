package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Edge")
public class Edge {
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Getter
  @Setter
  String nodeID1;

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Getter
  @Setter
  String nodeID2;
}
