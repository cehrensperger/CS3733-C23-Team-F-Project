package edu.wpi.FlashyFrogs.ORM;

import jakarta.persistence.*;
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

  public LocationName(String thelongName, LocationType thelocationType, String theShortName){
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
}
