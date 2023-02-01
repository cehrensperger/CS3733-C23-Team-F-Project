package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NodeTest {

  // Creates iteration of Node
  Node testNode = new Node("Test", "Building", Node.Floor.L2, 0, 1);

  /** Reset testNode after each test */
  @BeforeEach
  @AfterEach
  public void resetTestNode() {
    testNode.setId("Test");
    testNode.setBuilding("Building");
    testNode.setFloor(Node.Floor.L2);
    testNode.setXCoord(0);
    testNode.setYCoord(1);
  }

  /** Tests if the equals in Node.java correctly compares two Node objects */
  @Test
  public void testEquals() {
    Node node2 = new Node("Test", "0", Node.Floor.ONE, 1, 0);
    Node node3 = new Node("Another Test", "0", Node.Floor.ONE, 1, 0);
    assertTrue(testNode.equals(node2));
    assertFalse(testNode.equals(node3));
  }

  /** Checks to see if toString makes a string in the same format specified in Node.java */
  @Test
  public void testToString() {
    String stringId = testNode.toString();
    assertEquals(stringId, testNode.getId());
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testNode.hashCode();
    testNode.setId("NewID");
    testNode.setXCoord(100);
    testNode.setYCoord(100000);
    assertNotEquals(testNode.hashCode(), originalHash);
  }

  /** Tests setter for id */
  @Test
  void setId() {
    testNode.setId("Changed");
    assertEquals("Changed", testNode.getId());
  }

  /** Tests setter for xCoord */
  @Test
  void setXCoord() {
    testNode.setXCoord(1);
    assertEquals(1, testNode.getXCoord());
  }

  /** Tests setter for yCoord */
  @Test
  void setYCoord() {
    testNode.setYCoord(1);
    assertEquals(1, testNode.getYCoord());
  }

  /** Tests setter for floor */
  @Test
  void setFloor() {
    testNode.setFloor(Node.Floor.L1);
    assertEquals(Node.Floor.L1, testNode.getFloor());
  }

  /** Tests setter for building */
  @Test
  void setBuilding() {
    testNode.setBuilding("New Building");
    assertEquals("New Building", testNode.getBuilding());
  }
}
