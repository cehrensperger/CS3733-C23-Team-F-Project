package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.*;
import lombok.Getter;
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

  /*
  Create equals method
  */

  /*
  create hashcode method
  */
}
