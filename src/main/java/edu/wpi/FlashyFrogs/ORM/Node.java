package edu.wpi.FlashyFrogs.ORM;

import edu.wpi.FlashyFrogs.DBConnection;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.annotations.Cascade;

@Entity
@Table(name = "Node")
public class Node {
  @Id
  @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
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

    public static Floor getEnum(String value) {
      for (Floor f : Floor.values()) {
        if (f.floorNum.equals(value)) return f;
      }
      return null;
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

  /**
   * Looks up the current location associated with this Node in the database, potentially returning
   * null if that is not available. Defines the current location as the move associating this node
   * to a location the least in the past but NOT in the future. Also ensures that the provided
   * location is not representing something else more currently
   *
   * @param session the session to use for the lookup
   * @return either the location this node is storing, or null if there is none
   */
  public LocationName getCurrentLocation(@NonNull Session session) {
    // Try getting the location first. This gets the most recent location that is the node and not
    // in the future
    // sorts by move date, and then limits by one. Unique result ensures that this either gets the
    // one result,
    // or null
    LocationName location =
        session
            .createQuery(
                """
                                        SELECT location
                                        FROM Move
                                        WHERE node = :node AND moveDate <= current timestamp
                                        ORDER BY moveDate DESC
                                        LIMIT 1
                                        """,
                LocationName.class)
            .setParameter("node", this)
            .uniqueResult();

    // If the location isn't null
    if (location != null) {
      // Get the node most recently associated with this location
      Node locationNode =
          session
              .createQuery(
                  """
                                            SELECT node
                                            FROM Move
                                            WHERE location = :location AND moveDate <= current timestamp
                                            ORDER BY moveDate DESC
                                            LIMIT 1
                                            """,
                  Node.class)
              .setParameter("location", location)
              .uniqueResult();

      // If that locations most recent node is this
      if (locationNode.equals(this)) {
        return location; // Return the location
      }
    }

    // Otherwise, just return null
    return null;
  }

  /**
   * Looks up the current location associated with this Node in the database, potentially returning
   * null if that is not available. Defines the current location as the move associating this node
   * to a location the least in the past but NOT in the future. Also ensures that the provided
   * location is not representing something else more currently
   *
   * @return either the location this node is storing, or null if there is none
   */
  public LocationName getCurrentLocation() {
    // Trys to create a connection, auto-closing it when this is done. This also re-throws any
    // exceptions that
    // may occur
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      return getCurrentLocation(connection);
    }
  }
}
