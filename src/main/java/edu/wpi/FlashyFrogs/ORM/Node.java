package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "Node")
public class Node {
  @Id
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String id;

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  int xCoord;

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  int yCoord;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  Floor floor;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String building;

  /** Creates a new Node with empty fields */
  public Node() {}

  /**
   * Creates a new Node with the given primary key (unnecessary)
   *
   * @param id the String to be used as the id
   */
  public Node(@NonNull String id) {
    this.id = id;
  }

  /**
   * Creates a new Node with the given fields
   *
   * @param theId the String to be used in the id field
   * @param theBuilding the String to be used in the building field
   * @param theFloor the Floor Enumeration to be used in the floor field
   * @param theXCoord the int to be used in the xCoord field
   * @param theYCoord the int to be used in the yCoord field
   */
  public Node(
      @NonNull String theId,
      @NonNull String theBuilding,
      @NonNull Floor theFloor,
      int theXCoord,
      int theYCoord) {

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

  /**
   * Overrides the default equals method with one that compares primary keys
   *
   * @param obj the node object to compare to
   * @return boolean whether the primary keys are the same or not
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    Node other = (Node) obj;
    return this.id.equals(other.getId());
  }

  /**
   * Overrides the default hashCode method with one that uses the id, xcoord, and ycoord of the node
   * object
   *
   * @return the new hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.id, this.xCoord, this.yCoord);
  }

  /**
   * Overrides the default toString method with one that returns the id of the Node object
   *
   * @return the id of the Node object
   */
  @Override
  @NonNull
  public String toString() {
    return this.id;
  }
}
