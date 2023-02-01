package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveTest {

  Move testMove =
      new Move(
          new Node("Test", "Building", Node.Floor.L2, 0, 0),
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          new Date(2023 - 01 - 31));

  @BeforeEach
  @AfterEach
  public void resetTestMove() {
    testMove.setNode(new Node("Test", "Building", Node.Floor.L2, 0, 0));
    testMove.setLocation(new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
    testMove.setMoveDate(new Date(2023 - 01 - 31));
  }

  @Test
  void testEquals() {
    Move otherTestMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 01 - 31));
    assertTrue(testMove.equals(otherTestMove));
  }

  @Test
  void testHashCode() {
    int originalHash = testMove.hashCode();
    testMove.setNode(new Node("OtherTest", "SecondBuilding", Node.Floor.G, 0, 10));
    testMove.setLocation(
        new LocationName("DifferentLong", LocationName.LocationType.DEPT, "NewShort"));
    testMove.setMoveDate(new Date(2023 - 02 - 01));
    assertNotEquals(testMove.hashCode(), originalHash);
  }

  @Test
  void testToString() {
    String stringMove = testMove.toString();
    assertEquals(
        stringMove,
        testMove.getNode().getId()
            + "_"
            + testMove.getLocation().getLongName()
            + "_"
            + testMove.getMoveDate());
  }

  @Test
  void setNode() {
    Node newNode = new Node("NewNode", "NewBuilding", Node.Floor.G, 10, 10);
    testMove.setNode(newNode);
    assertEquals(newNode, testMove.getNode());
  }

  @Test
  void setLocation() {
    LocationName newLoc = new LocationName("NewLong", LocationName.LocationType.ELEV, "NewShort");
    testMove.setLocation(newLoc);
    assertEquals(newLoc, testMove.getLocation());
  }

  @Test
  void setMoveDate() {
    Date newDate = new Date(2003 - 06 - 23);
    testMove.setMoveDate(newDate);
    assertEquals(newDate, testMove.getMoveDate());
  }
}
