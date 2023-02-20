package edu.wpi.FlashyFrogs.ORM;

import edu.wpi.FlashyFrogs.DBConnection;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "Node")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Node {
  @Id
  @Column(nullable = false)
  @NonNull
  @Getter
  private String id;

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  private int xCoord;

  @Basic
  @Column(nullable = false)
  @Getter
  @Setter
  private int yCoord;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private Floor floor;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String building;

  /** Creates a new Node with empty fields */
  public Node() {}

  public Node(@NonNull String theBuilding, @NonNull Floor theFloor, int theXCoord, int theYCoord) {
    this.id = theFloor.floorNum + String.format("X%04dY%04d", theXCoord, theYCoord);
    this.building = theBuilding;
    this.floor = theFloor;
    this.xCoord = theXCoord;
    this.yCoord = theYCoord;
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

    public static Floor getEnum(@NonNull String value) {
      for (Floor f : Floor.values()) {

        if (f.floorNum.equals(value)) return f;
      }
      return null;
    }

    /**
     * Override for the toString, returns the floor num as a string
     *
     * @return the floor num as a string
     */
    @Override
    public String toString() {
      return this.floorNum;
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
  public List<LocationName> getCurrentLocation(@NonNull Session session, Date date) {

    // associate location with a node
    // then associate that location with a new node
    // don't delete previous node to location association
    // when getting location of node, get most recent location associated with this node
    // also check that the location returned has this as the most recent associated node as well

    // now instead get two most recent locations associated with this node if they are on the same
    // day
    // check that they aren't null
    // also check that list of locations' most recent associated nodes are both this

    // Try getting the location first. This gets the most recent location that is the node and not
    // in the future
    // sorts by move date, and then limits by one. Unique result ensures that this either gets the
    // one result,
    // or null

    // If the past two locations for this node are on the same day, return both.
    // Otherwise, return the most recent.
    List<LocationName> locations =
        session
            .createQuery(
                """
                                        SELECT location
                                        FROM Move
                                        WHERE node = :node AND moveDate <= :date
                                        ORDER BY moveDate DESC
                                        LIMIT 2
                                        """,
                LocationName.class)
            .setParameter("node", this)
            .setParameter("date", date)
            .setCacheable(true)
            .getResultList();
    if (locations.isEmpty()) {
      return locations;
    }

    locations.removeIf(location -> !location.getCurrentNode(session, date).equals(this));

    return locations;
  }

  /**
   * Looks up the current location associated with this Node in the database, potentially returning
   * null if that is not available. Defines the current location as the move associating this node
   * to a location the least in the past but NOT in the future. Also ensures that the provided
   * location is not representing something else more currently
   *
   * @return either the location this node is storing, or null if there is none
   */
  public Collection<LocationName> getCurrentLocation(Date date) {
    // Trys to create a connection, auto-closing it when this is done. This also re-throws any
    // exceptions that
    // may occur
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      return getCurrentLocation(connection, date);
    }
  }
}
