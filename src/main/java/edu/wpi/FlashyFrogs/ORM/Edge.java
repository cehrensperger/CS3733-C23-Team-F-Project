package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "Edge")
public class Edge {
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
  @JoinColumn(name = "node1_id", nullable = false, foreignKey = @ForeignKey(name = "node1_id_fk"))
  @ManyToOne(cascade = CascadeType.ALL)
  @NonNull
  @Getter
  @Setter
  Node node1;

  @Id
  @JoinColumn(name = "node2_id", nullable = false, foreignKey = @ForeignKey(name = "node2_id_fk"))
  @NonNull
  @ManyToOne(cascade = CascadeType.ALL)
  @Getter
  @Setter
  Node node2;

  /** Creates a new Edge with empty fields */
  public Edge() {}

  /**
   * Creates a new Edge with the specified fields
   *
   * @param node1 the Node to be used in the first Node field
   * @param node2 the Node to be used in the second Node field
   */
  public Edge(@NonNull Node node1, @NonNull Node node2) {
    this.node1 = node1;
    this.node2 = node2;
  }

  /**
   * overrides the default equals method with a new method that compares primary keys
   *
   * @param obj the edge object to compare primary keys against
   * @return boolean whether the edges have the same primary keys or not
   */
  @Override
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
