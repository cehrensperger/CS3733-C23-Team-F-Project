package edu.wpi.FlashyFrogs.ORM;

import edu.wpi.FlashyFrogs.DBConnection;
import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.Session;

@Entity
@Table(name = "LocationName")
public class LocationName {
  @Id
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String longName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private String shortName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  private LocationType locationType; // why is this mad at me but node is not????

  /** Creates a new LocationName with empty fields */
  public LocationName() {}

  /**
   * Creates a new LocationName with the given fields
   *
   * @param thelongName the String to be used in the longName field
   * @param thelocationType the LocationType to be used in the locationType field
   * @param theShortName the String to be used in the shortName field
   */
  public LocationName(
      @NonNull String thelongName,
      @NonNull LocationType thelocationType,
      @NonNull String theShortName) {
    this.longName = thelongName;
    this.locationType = thelocationType;
    this.shortName = theShortName;
  }

  /** Enumerated type for the type of location we can create */
  public enum LocationType {
    HALL("HALL"),
    ELEV("ELEV"),
    REST("REST"),
    STAI("STAI"),
    DEPT("DEPT"),
    LABS("LABS"),
    INFO("INFO"),
    CONF("CONF"),
    EXIT("EXIT"),
    RETL("RETL"),
    SERV("SERV"),
    BATH("BATH");

    @NonNull public final String name; // Name backing for the type of location this is

    /**
     * Initializes a location type with the given string name backing
     *
     * @param name the string name backing
     */
    LocationType(@NonNull String name) {
      this.name = name; // The name to provide
    }
  }

  /**
   * Overrides the default equals method with one that compares equality of primary keys
   *
   * @param obj the LocationName object to compare primary keys against
   * @return boolean whether the primary keys are equal or not
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (this.getClass() != obj.getClass()) return false;
    LocationName other = (LocationName) obj;
    return this.longName.equals(other.getLongName());
  }

  /**
   * Overrides the default hashCode method with one that uses the longName and locationType of the
   * object
   *
   * @return the new hashcode
   */
  @Override
  public int hashCode() {
    return Objects.hash(this.longName, this.locationType.name());
  }

  /**
   * Overrides the default toString method with one that returns the longName of the object
   *
   * @return the longName of the object
   */
  @Override
  @NonNull
  public String toString() {
    return this.longName;
  }

  /**
   * Gets the node that this location is currently in (defined as the least in the past, but NOT in
   * the future), or null if there is none
   *
   * @return the Node this location is stored in, or null if there is none
   */
  public Node getCurrentNode(@NonNull Session session) {
    //     use the connection to create a query that gets the most recent result that's not in the
    //     future for
    //     this location, limiting by one to get the single most recent result. Returns null if
    // there
    //     is no
    //     result
    Node node =
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
            .setParameter("location", this)
            .uniqueResult();

    // if the node isn't null
    if (node != null) {
      // Check the move it has most recently
      LocationName nodeLocation =
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
              .setParameter("node", node)
              .uniqueResult();

      // If that is this
      if (nodeLocation.equals(this)) {
        return node; // Then this location is associated with the node
      }
    }

    return null; // Otherwise, it's not, return null
  }

  /**
   * Gets the node that this location is currently in (defined as the least in the past, but NOT in
   * the future), or null if there is none
   *
   * @return the Node this location is stored in, or null if there is none
   */
  public Node getCurrentNode() {
    // Create a connection. This syntax auto-closes the connection when we're done, and throws an
    // exception if
    // anything goes wrong
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      return getCurrentNode(connection);
    }
  }
}
