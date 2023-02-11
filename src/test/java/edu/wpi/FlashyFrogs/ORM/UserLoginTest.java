package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserLoginTest {

  // Creates iteration of Login
  private UserLogin testLogin = new UserLogin(new User("a", "b", "c", User.EmployeeType.ADMIN),
          "testUserName", "testPassword");

  /** Reset testLogin after each test */
  @BeforeEach
  @AfterEach
  public void resetTestLogin() {
    testLogin = new UserLogin(new User("a", "b", "c", User.EmployeeType.ADMIN),
          "testUserName", "testPassword");
  }

  /** Checks to see if toString makes a string in the same format specified in UserLogin.java */
  @Test
  public void testToString() {
    String stringId = testLogin.toString();
    assertEquals(stringId, testLogin.getUserName());
  }

  /** Tests setter for UserName */
  @Test
  public void testSetUserName() {
    testLogin.setUserName("Changed");
    assertEquals("Changed", testLogin.getUserName());
  }

  /** Tests setter for password */
  @Test
  public void testSetPassword() {
    testLogin.setPassword("New Password");
    assertTrue(testLogin.checkPasswordEqual("New Password"));
    assertFalse(testLogin.checkPasswordEqual("testPassword"));
  }

  /** Tests checkPasswordEqual function that will be used to verify passwords */
  @Test
  public void testCheckPasswordEqual() {
    assertTrue(testLogin.checkPasswordEqual("testPassword"));
    assertFalse(testLogin.checkPasswordEqual("Literally Anything Else"));
  }
}
