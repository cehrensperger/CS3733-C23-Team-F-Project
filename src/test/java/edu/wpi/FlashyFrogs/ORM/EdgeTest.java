package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EdgeTest {

  //Create Test Edge using Test Nodes
  Node testNode1 = new Node("Test", "Building", Node.Floor.L2, 0, 1);
  Node testNode2 = new Node("Other Test", "Building", Node.Floor.L2, 0, 1);
  Edge testEdge = new Edge(testNode1, testNode2);

  /**
   * Reset testEdge after each test
   */
  @BeforeEach
  @AfterEach
  public void resetTestEdge() {
    testEdge.setNode1(testNode1);
    testEdge.setNode2(testNode2);
    testEdge.getNode1().setId("Test");
    testEdge.getNode1().setId("Other Test");
  }
  /**
   * Tests the equals Edge method using three edges: two are the same (between nodes 1 and 2) and
   * one is different (between nodes 2 and 3). The test passes when equals returns true when
   * comparing edges 1 and 2 and fails when comparing edges 2 and 3.
   */
  @Test
  public void testEquals() {
    Node testNode3 = new Node("Another Test", "0", Node.Floor.ONE, 2, 0);
    Edge testEdge2 = new Edge(testNode1, testNode2);
    Edge testEdge3 = new Edge(testNode2, testNode3);
    assertTrue(testEdge.equals(testEdge2));
    assertFalse(testEdge.equals(testEdge3));
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  public void testHashCode() {
    int originalHash = testEdge.hashCode();
    testEdge.getNode1().setId("DifferentID");
    testEdge.getNode2().setId("AnotherDifferentID");
    assertNotEquals(testEdge.hashCode(), originalHash);
  }

  /**
   * Checks to see if toString makes a string in the same format specified in Edge.java
   */
  @Test
  void testToString() {
    String stringEdge = testEdge.toString();
    assertEquals(stringEdge, testEdge.getNode1().getId() + "_" + testEdge.getNode2());
  }

  /**
   * Tests setter for Node1
   */
  @Test
  void setNode1() {
    Node newNode = new Node("New ID", "New Building", Node.Floor.L2, 0, 0);
    testEdge.setNode1(newNode);
    assertEquals(newNode, testEdge.getNode1());
  }

  /**
   * Tests setter for Node2
   */
  @Test
  void setNode2() {
    Node newNode = new Node("New ID", "New Building", Node.Floor.L2, 0, 0);
    testEdge.setNode2(newNode);
    assertEquals(newNode, testEdge.getNode2());
  }
}
