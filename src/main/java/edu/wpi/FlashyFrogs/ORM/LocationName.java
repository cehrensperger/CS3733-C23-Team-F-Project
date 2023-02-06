package edu.wpi.FlashyFrogs.ORM;

import edu.wpi.FlashyFrogs.DBConnection;
import jakarta.persistence.*;

import java.util.List;
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
  String longName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  String shortName;

  @Basic
  @Column(nullable = false)
  @NonNull
  @Getter
  @Setter
  LocationType locationType; // why is this mad at me but node is not????

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
    // use the connection to create a query that gets the most recent result that's not in the
    // future for
    // this location, limiting by one to get the single most recent result. Returns null if there
    // is no
    // result
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

  /**
   * Updates the long name for a given location. This is required (and extremely roundabout) because Hibernate
   * does not support ON UPDATE CASCADE =( . This essentially creates a new location, sets everything that
   * refers to the old one to refer to the new one (including this sort of copying in the case of moves), and then
   * deletes the old location. This manages all DB interaction regarding that conversion with the provided
   * session
   * @param oldLocation the old location to switch. This should not be referred to after this method has run
   *                    (unless an Exception is thrown)
   * @param longName the new long name for the location
   * @param session the session to use for the switches
   * @return the newly created location that things now refer to
   */
  public static LocationName updateLongName(@NonNull LocationName oldLocation, @NonNull String longName,
                                            @NonNull Session session) {
    LocationName newLocation =
                  new LocationName(
                      longName, oldLocation.getLocationType(), oldLocation.getShortName());
      session.persist(newLocation); // Persist the new location

    List<AudioVisual> audioVisualRequests = session.createQuery("FROM AudioVisual WHERE location = :location",
            AudioVisual.class).setParameter("location", oldLocation).getResultList();

    for (AudioVisual avRequest : audioVisualRequests) {
      avRequest.setLocation(newLocation); // Change over the location
      session.merge(avRequest); // Persist the changes
    }

        // Get the transport requests with the new location as the location
      List<InternalTransport> transportRequests =
          session
              .createQuery(
                  "FROM InternalTransport where newLoc = :location OR oldLoc = :location",
                  InternalTransport.class).setParameter("location", oldLocation)
              .getResultList();

      // Fix the transports. In this case, we can use setters because the locations are not PK elements
      for (InternalTransport transport : transportRequests) {
          // If the transports new location is this location
          if (transport.getNewLoc().equals(oldLocation)) {
              transport.setNewLoc(newLocation); // Change it
          }

          // If the transports old location is this location
          if (transport.getOldLoc().equals(oldLocation)) {
              transport.setOldLoc(newLocation); // Change it
          }

          session.merge(transport); // Update the transport
      }

      // Get the list of sanitation requests to update
      List<Sanitation> sanitationRequests = session.createQuery("FROM Sanitation WHERE location = :location",
              Sanitation.class).setParameter("location", oldLocation).getResultList();

      // For each one
      for (Sanitation sanitation : sanitationRequests) {
        sanitation.setLocation(newLocation); // Update the location
        session.merge(sanitation); // Save the changes
      }

      // Get the list of security requests
      List<Security> securityRequests = session.createQuery("FROM Security WHERE location = :location",
              Security.class).setParameter("location", oldLocation).getResultList();

      // For each one
      for (Security security : securityRequests) {
        security.setLocation(newLocation); // Update the location
        session.merge(security); // Save the changes
      }

      // Get the list of moves associated with the old location
      List<Move> movesToDelete =
          session
              .createQuery("FROM Move where location = :location", Move.class)
              .setParameter("location", oldLocation)
              .getResultList();

      // For each location with the old move
      for (Move moveToMove : movesToDelete) {
        // Shallow-copy the move
        Move newMove =
            new Move(moveToMove.getNode(), newLocation, moveToMove.getMoveDate());
        session.persist(newMove); // Persist the new move
        session.remove(moveToMove); // Delete the old move
      }

      session.remove(oldLocation); // Delete the old location

      return newLocation;
  }
}
