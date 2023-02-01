package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationNameTest {

  //Creates iteration of LocationName
  LocationName testLocName =
      new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");

  /**
   * Reset testLocationName after each test
   */
  @BeforeEach
  @AfterEach
  public void resetTestLocationName() {
    testLocName.setLongName("LongName");
    testLocName.setLocationType(LocationName.LocationType.HALL);
    testLocName.setShortName("ShortName");
  }

  /**
   * Tests if the equals in LocationName.java correctly compares two LocationName objects
   */
  @Test
  void testEquals() {
    LocationName otherLocName =
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");
    assertTrue(testLocName.equals(otherLocName));
  }

  /**
   * Tests to see that HashCode changes when attributes that determine HashCode changes
   */
  @Test
  void testHashCode() {
    int originalHash = testLocName.hashCode();
    testLocName.setLongName("NewLongName");
    testLocName.setLocationType(LocationName.LocationType.ELEV);
    assertNotEquals(testLocName.hashCode(), originalHash);
  }

  /**
   * Checks to see if toString makes a string in the same format specified in LocationName.java
   */
  @Test
  void testToString() {
    String locNameToString = testLocName.toString();
    assertEquals(locNameToString, testLocName.getLongName());
  }

  /**
   * Tests setter for longName
   */
  @Test
  void setLongName() {
    String newLongName = "NewLongName";
    testLocName.setLongName(newLongName);
    assertEquals(newLongName, testLocName.getLongName());
  }

  /**
   * Tests setter for shortName
   */
  @Test
  void setShortName() {
    String newShortName = "NewShortName";
    testLocName.setShortName(newShortName);
    assertEquals(newShortName, testLocName.getShortName());
  }

  /**
   * Tests setter for locationType
   */
  @Test
  void setLocationType() {
    testLocName.setLocationType(LocationName.LocationType.LABS);
    assertEquals(LocationName.LocationType.LABS, testLocName.getLocationType());
  }
}
