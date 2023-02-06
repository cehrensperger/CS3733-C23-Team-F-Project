package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserLoginTest {

  // Creates iteration of Login
  UserLogin testLogin = new UserLogin("testUserName", "testPassword");

  public UserLoginTest() throws NoSuchAlgorithmException {}

  /** Reset testLogin after each test */
  @BeforeEach
  @AfterEach
  public void resetTestLogin() throws NoSuchAlgorithmException {
    testLogin.setUserName("testUserName");
    testLogin.setPassword("testPassword");
  }

  /** Tests if the equals in Node.java correctly compares two UserLogin objects */
  @Test
  public void testEquals() throws NoSuchAlgorithmException {
    UserLogin otherLogin = new UserLogin("testUserName", "testPassword");
    UserLogin anotherLogin = new UserLogin("anotherUserName", "anotherPassword");
    assertEquals(testLogin, otherLogin);
    assertNotEquals(testLogin, anotherLogin);
  }

  /** Checks to see if toString makes a string in the same format specified in UserLogin.java */
  @Test
  public void testToString() {
    String stringId = testLogin.toString();
    assertEquals(stringId, testLogin.getUserName());
  }

  /** Tests to see that HashCode changes when attributes that determine HashCode changes */
  @Test
  void testHashCode() {
    int originalHash = testLogin.hashCode();
    testLogin.setUserName("NewUserName");
    assertNotEquals(testLogin.hashCode(), originalHash);
  }

  /** Tests setter for UserName */
  @Test
  void testSetUserName() {
    testLogin.setUserName("Changed");
    assertEquals("Changed", testLogin.getUserName());
  }

  /** Tests setter for password */
  @Test
  void setPassword() throws NoSuchAlgorithmException {
    testLogin.setPassword("New Password");
    assertEquals("New Password", testLogin.getPassword());
  }
}
