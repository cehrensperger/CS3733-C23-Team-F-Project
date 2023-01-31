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

  /**
   * overrides the default equals method with a new method that compares primary keys
   *
   * @param other the edge object to compare primary keys against
   * @return boolean whether the edges have the same primary keys or not
   */
  @Override
  @NonNull
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    Edge other = (Edge) obj;
    return (this.getNode1().equals(other.getNode1())
        && this.getNode2().getId().equals(other.getNode2().getId()));
  }

  /**
   * Overrides the default hashcode with one that combines the both primary keys
   *
   * @return the hashcode of the edge
   */
  @Override
  @NonNull
  public int hashCode() {
    return Objects.hash(this.node1, this.node2);
  }

  /**
   * Overrides the default toString with one that returns the Ids of the two node objects
   * concatenated with an underscore between
   *
   * @return the ids of both node primary keys with an underscore between
   */
  @Override
  @NonNull
  public String toString() {
    return this.node1.getId() + "_" + this.node2.getId();
  }
}
