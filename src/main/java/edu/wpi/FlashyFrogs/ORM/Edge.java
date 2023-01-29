package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Edge")
public class Edge {
  @Column(name = "node1ID")
  @ManyToOne
  @Getter
  @Setter
  Node node1;

  @Column(name = "node2ID")
  @ManyToOne
  @Getter
  @Setter
  Node node2;
}
