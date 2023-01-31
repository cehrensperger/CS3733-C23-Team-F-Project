package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Edge")
public class Edge {
  @Id
  @JoinColumn(name = "node1_id", foreignKey = @ForeignKey(name = "node1_id_fk"))
  @ManyToOne
  @Getter
  @Setter
  Node node1;

  @Id
  @JoinColumn(name = "node2_id", foreignKey = @ForeignKey(name = "node2_id_fk"))
  @ManyToOne
  @Getter
  @Setter
  Node node2;

  public boolean equals(Edge edge) {
    return (this.getNode1().getId().equals(edge.getNode1().getId())
        && this.getNode2().getId().equals(edge.getNode2().getId()));
  }

  /*
  create hashcode method
  */
}
