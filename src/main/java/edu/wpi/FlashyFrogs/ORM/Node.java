package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Node")
public class Node {
  @Id @Getter @Setter String id;
  @Basic @Getter @Setter int xCoord;
  @Basic @Getter @Setter int yCoord;
  @Basic @Getter @Setter Floor floor;
  @Basic @Getter @Setter String building;

  public Node() {}

  public Node(String id) {
    this.id = id;
  }

  public Node(String theId, String theBuilding, Floor theFloor, int theXCoord, int theYCoord) {

    this.id = theId;
    this.building = theBuilding;
    this.floor = theFloor;
    this.xCoord = theXCoord;
    this.yCoord = theYCoord;
  }

  /** Enumerated type for the possible floors we can create */
  public enum Floor {
    L2("L2"),
    L1("L1"),
    G("G"),
    ONE("1"),
    TWO("2"),
    THREE("3");

    @NonNull public final String floorNum; // Number backing for the Floor

    /**
     * Creates a new floor with the given String backing
     *
     * @param floor the floor to create. Must not be null
     */
    Floor(@NonNull String floor) {
      floorNum = floor; // The floor to create
    }
  }

  @Override
  public boolean equals(Object obj) {
    Node other = (Node) obj;
    return this.getId().equals(other.getId());
  }

  @Override
  public String toString() {
    return this.id;
  }
}
