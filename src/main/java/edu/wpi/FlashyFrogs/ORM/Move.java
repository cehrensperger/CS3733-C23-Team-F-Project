package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "Move")
public class Move {
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  @Getter
  @Setter
  @JoinColumn(name = "node_id", foreignKey = @ForeignKey(name = "node_id_fk"), nullable = false)
  @NonNull
  @ManyToOne
  Node node;

  @Id
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  @Getter
  @Setter
  @JoinColumn(
      name = "longName",
      foreignKey = @ForeignKey(name = "location_name_fk"),
      nullable = false)
  @NonNull
  @ManyToOne
  LocationName location;

  @Temporal(TemporalType.TIMESTAMP)
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.ALL)
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  Date moveDate;

  /** Creates a new Move with empty fields */
  public Move() {}

  /**
   * Creates a new Move with the given fields
   *
   * @param node the Node to be used in the node field
   * @param location the LocationName to be used in the location field
   * @param date the Date to be used in the moveDate field
   */
  public Move(@NonNull Node node, @NonNull LocationName location, @NonNull Date date) {
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
    return this.node.getId() + "_" + this.location.getLongName() + "_" + this.moveDate;
  }
}
