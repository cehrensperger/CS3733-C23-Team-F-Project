package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Move")
public class Move {
  @Id
  @Getter
  @Setter
  @JoinColumn(name = "node_id", foreignKey = @ForeignKey(name = "node_id_fk"))
  @ManyToOne
  Node node;

  @Id
  @Getter
  @Setter
  @JoinColumn(name = "longName", foreignKey = @ForeignKey(name = "location_name_fk"))
  @ManyToOne
  LocationName location;

  @Temporal(TemporalType.TIMESTAMP)
  @Id
  @Getter
  @Setter
  Date moveDate;

  public Move() {}

  public Move(Node node, LocationName location, Date date) {
    this.node = node;
    this.location = location;
    this.moveDate = date;
  }

  /**
   * Overrides the default equals method with one that checks for equality among primary keys
   *
   * @param obj the Move object to compare primary keys against
   * @return boolean whether the primary keys are equal or not
   */
  @Override
  @NonNull
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    Move other = (Move) obj;
    return (this.getNode().equals(other.getNode())
        && this.getLocation().equals(other.getLocation())
        && this.getMoveDate().compareTo(other.getMoveDate()) == 0);
  }

  /**
   * Overrides the defaul hashCode method with one that uses the node, location, and date of the
   * move object
   *
   * @return the new hashcode
   */
  @Override
  @NonNull
  public int hashCode() {
    return Objects.hash(this.node, this.location, this.moveDate);
  }

  /**
   * Overrides the default toString method with one that returns a concatenation of the id of the
   * move's node and the longName of the move's locations
   *
   * @return the move's node id and location longname with an underscore between
   */
  @Override
  @NonNull
  public String toString() {
    return this.node.getId() + "_" + this.location.getLongName();
  }
}
