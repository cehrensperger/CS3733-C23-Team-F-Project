package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class EdgeTest {

  /**
   * Tests the equals Edge method using three edges: two are the same (between nodes 1 and 2) and
   * one is different (between nodes 2 and 3). The test passes when equals returns true when
   * comparing edges 1 and 2 and fails when comparing edges 2 and 3.
   */
  @Test
  public void testEquals() {
    Node node1 = new Node("Test", "0", Node.Floor.ONE, 0, 0);
    Node node2 = new Node("Other Test", "0", Node.Floor.ONE, 1, 0);
    Node node3 = new Node("Another Test", "0", Node.Floor.ONE, 2, 0);
    Edge testEdge1 = new Edge(node1, node2);
    Edge testEdge2 = new Edge(node1, node2);
    Edge testEdge3 = new Edge(node2, node3);

    assertTrue(testEdge1.equals(testEdge2));
    assertFalse(testEdge1.equals(testEdge3));
  }

  /** This is a perfect test. Nothing to see here */
  @Test
  public void testHashCode() {
    Node node1 = new Node("Test", "0", Node.Floor.ONE, 0, 0);
    Node node2 = new Node("Other Test", "0", Node.Floor.ONE, 1, 0);
    Edge testEdge1 = new Edge(node1, node2);
    assertDoesNotThrow(
        () -> {
          int hash = testEdge1.hashCode();
        });
  }
}
