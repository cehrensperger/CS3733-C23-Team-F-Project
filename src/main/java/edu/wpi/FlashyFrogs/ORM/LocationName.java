package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name = "LocationName")
public class LocationName {
  @Id @Getter @Setter String longName;

  @Basic @Getter @Setter String shortName;
  @Basic @Getter LocationType locationType;

  public LocationName() {}

  public LocationName(String longName) {
    this.longName = longName;
  }

  public LocationName(String thelongName, LocationType thelocationType, String theShortName) {
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
    SERV("SERV");

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
  @NonNull
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
  @NonNull
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
}
