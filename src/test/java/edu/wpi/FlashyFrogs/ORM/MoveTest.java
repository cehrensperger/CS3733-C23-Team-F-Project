package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveTest {

  // Creates iteration of move
  Move testMove =
      new Move(
          new Node("Test", "Building", Node.Floor.L2, 0, 0),
          new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
          new Date(2023 - 1 - 31));

  /** Reset testMove after each test */
  @BeforeEach
  @AfterEach
  public void resetTestMove() {
    testMove.setNode(new Node("Test", "Building", Node.Floor.L2, 0, 0));
    testMove.setLocation(new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"));
    testMove.setMoveDate(new Date(2023 - 1 - 31));
  }

  /** Tests if the equals in Move.java correctly compares two Move objects */
  @Test
  void testEquals() {
    Move otherTestMove =
        new Move(
            new Node("Test", "Building", Node.Floor.L2, 0, 0),
            new LocationName("LongName", LocationName.LocationType.HALL, "ShortName"),
            new Date(2023 - 1 - 31));
    assertEquals(testMove, otherTestMove);
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testMove.hashCode();
    testMove.setNode(new Node("OtherTest", "SecondBuilding", Node.Floor.G, 0, 10));
    testMove.setLocation(
        new LocationName("DifferentLong", LocationName.LocationType.DEPT, "NewShort"));
    testMove.setMoveDate(new Date(2023 - 2 - 1));
    assertNotEquals(testMove.hashCode(), originalHash);
  }

  /** Checks to see if toString makes a string in the same format specified in Move.java */
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

  /** Tests setter for Node */
  @Test
  void setNode() {
    Node newNode = new Node("NewNode", "NewBuilding", Node.Floor.G, 10, 10);
    testMove.setNode(newNode);
    assertEquals(newNode, testMove.getNode());
  }

  /** Tests setter for location */
  @Test
  void setLocation() {
    LocationName newLoc = new LocationName("NewLong", LocationName.LocationType.ELEV, "NewShort");
    testMove.setLocation(newLoc);
    assertEquals(newLoc, testMove.getLocation());
  }

  /** Tests setter for moveDate */
  @Test
  void setMoveDate() {
    Date newDate = new Date(2003 - 6 - 23);
    testMove.setMoveDate(newDate);
    assertEquals(newDate, testMove.getMoveDate());
  }

  /**
   * Tests that the empty constructor with the setters is equa to the filled constructor without
   * them
   */
  @Test
  public void emptyConstructorTest() {
    // Test node
    Node testNode = new Node("a", "b", Node.Floor.L2, 0, 0);

    // Location name
    LocationName locationName = new LocationName("a", LocationName.LocationType.SERV, "b");

    // Test date
    Date testDate = new Date();

    // Create a move with the parameters done through setters
    Move emptyMove = new Move();
    emptyMove.setLocation(locationName);
    emptyMove.setMoveDate(testDate);
    emptyMove.setNode(testNode);

    // Assert that a basic move is equal to the filled node
    assertEquals(new Move(testNode, locationName, testDate), emptyMove);
  }
}
