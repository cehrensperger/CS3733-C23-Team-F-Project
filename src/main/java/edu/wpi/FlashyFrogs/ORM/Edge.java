package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Edge")
public class Edge {
  @Id
  @JoinColumn(name = "node1id")
  @ManyToOne
  @Getter
  @Setter
  Node node1;

  @Id
  @JoinColumn(name = "node2id")
  @ManyToOne
  @Getter
  @Setter
  Node node2;
}
