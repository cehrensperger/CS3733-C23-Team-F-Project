package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Edge")
// @IdClass(EdgePK.class)
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

  public Edge() {}

  public Edge(Node node1, Node node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  @Override
  @NonNull
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null) return false;
    if (this.getClass() != other.getClass()) return false;
    Edge edge = (Edge) other;
    return (this.getNode1().equals(edge.getNode1())
        && this.getNode2().getId().equals(edge.getNode2().getId()));
  }

  @Override
  @NonNull
  public int hashCode() {
    return Objects.hash(this.node1, this.node2);
  }

  //  @Override
  //  @NonNull
  //  public String toString() {
  //    return
  //  }
}
