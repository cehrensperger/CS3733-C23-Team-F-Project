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
  @Override
  @NonNull
  public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null) return false;
    if (this.getClass() != other.getClass()) return false;
    Move move = (Move) other;
    return (this.getNode().equals(move.getNode())
        && this.getLocation().equals(move.getLocation())
        && this.getMoveDate().compareTo(move.getMoveDate()) == 0);
  }

  @Override
  @NonNull
  public int hashCode() {
    return Objects.hash(this.node, this.location, this.moveDate);
  }
}
