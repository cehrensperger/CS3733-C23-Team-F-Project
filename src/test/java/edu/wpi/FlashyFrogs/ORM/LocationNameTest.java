package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocationNameTest {

  LocationName testLocName =
      new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");

  @BeforeEach
  @AfterEach
  public void resetTestLocationName() {
    testLocName.setLongName("LongName");
    testLocName.setLocationType(LocationName.LocationType.HALL);
    testLocName.setShortName("ShortName");
  }

  @Test
  void testEquals() {
    LocationName otherLocName =
        new LocationName("LongName", LocationName.LocationType.HALL, "ShortName");
    assertTrue(testLocName.equals(otherLocName));
  }

  @Test
  void testHashCode() {
    int originalHash = testLocName.hashCode();
    testLocName.setLongName("NewLongName");
    testLocName.setLocationType(LocationName.LocationType.ELEV);
    assertNotEquals(testLocName.hashCode(), originalHash);
  }

  @Test
  void testToString() {
    String locNameToString = testLocName.toString();
    assertEquals(locNameToString, testLocName.getLongName());
  }

  @Test
  void setLongName() {
    String newLongName = "NewLongName";
    testLocName.setLongName(newLongName);
    assertEquals(newLongName, testLocName.getLongName());
  }

  @Test
  void setShortName() {
    String newShortName = "NewShortName";
    testLocName.setShortName(newShortName);
    assertEquals(newShortName, testLocName.getShortName());
  }

  @Test
  void setLocationType() {
    testLocName.setLocationType(LocationName.LocationType.LABS);
    assertEquals(LocationName.LocationType.LABS, testLocName.getLocationType());
  }
}
