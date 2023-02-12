package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.FlashyFrogs.DBConnection;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class UserLoginTest {
  /** Sets up the DB connection before all the tests run */
  @BeforeAll
  public static void setupDBConnection() {
    DBConnection.CONNECTION.connect(); // Connect
  }

  /** Tears down the DB connection after all the tests run */
  @AfterAll
  public static void teardownDBConnection() {
    DBConnection.CONNECTION.disconnect(); // Disconnect
  }

  // Creates iteration of Login
  private UserLogin testLogin =
      new UserLogin(
          new User("a", "b", "c", User.EmployeeType.ADMIN), "testUserName", "testPassword");

  /** Reset testLogin after each test */
  @BeforeEach
  @AfterEach
  public void resetTestLogin() {
    testLogin =
        new UserLogin(
            new User("a", "b", "c", User.EmployeeType.ADMIN), "testUserName", "testPassword");
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

  /**
   * Tests the equals and hash code methods for user login. Should be completely dependent on the
   * user being the same
   */
  @Test
  public void equalsAndHashCodeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Get a session
    Transaction commitTransaction = session.beginTransaction(); // begin a transaction
    // Two different users
    User user = new User("a", "b", "c", User.EmployeeType.ADMIN);
    session.persist(user); // Persist the user, set the ID
    User differetUser = new User("b", "c", "d", User.EmployeeType.STAFF);
    session.persist(differetUser); // Persist the other user, set the ID

    commitTransaction.rollback(); // Rollback the transaction, all we care about is the IDs
    session.close(); // Close the session

    // Create the logins
    UserLogin newLogin = new UserLogin(user, "a", "b");
    UserLogin sameUserDifferent = new UserLogin(user, "b", "c");
    UserLogin differentUserSame = new UserLogin(differetUser, "a", "c");

    // Assert that the logins are the right equals including hash code
    assertEquals(newLogin, sameUserDifferent);
    assertEquals(newLogin.hashCode(), sameUserDifferent.hashCode());
    assertNotEquals(newLogin, differentUserSame);
    assertNotEquals(newLogin.hashCode(), differentUserSame.hashCode());
  }

  /** Tests that attempting to do an update cascade results in an exception */
  @Test
  public void updateCascadeTest() {
    // Create a dummy user, we will try to update this
    User user = new User("bob", "dad", "pop", User.EmployeeType.ADMIN);
    UserLogin userLogin = new UserLogin(user, "bobuser", "B");

    // Begin the transaction to commit the things with
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    session.beginTransaction();
    session.persist(user);
    session.persist(userLogin);

    // Assert that the mutation query updating the name throws
    assertThrows(
        Exception.class,
        () -> session.createMutationQuery("UPDATE User SET id=50").executeUpdate());

    session.close(); // Close the session
  }

  /** Tests that deleting does not throw an exception with a query */
  @Test
  public void deleteQueryCascadeTest() {
    // Create a dummy user, we will try to delete this
    User user = new User("test", "test", "pop", User.EmployeeType.ADMIN);
    UserLogin userLogin = new UserLogin(user, "login", "password");

    // Begin the transaction to commit the things with
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    session.persist(user);
    session.persist(userLogin);

    // Delete the user
    session.createMutationQuery("DELETE FROM User").executeUpdate();

    // Assert the result list is 0 (there are no UserLogins)
    assertEquals(0, session.createQuery("FROM UserLogin", UserLogin.class).getResultList().size());

    transaction.rollback(); // Abort the transaction
    session.close(); // Close the session
  }

  /** Tests that when remove is done, it cascades */
  @Test
  public void deleteHibernateCascadeTest() {
    // Create a dummy user, we will try to delete this
    User user = new User("sadf", "nffghj", "dsfg", User.EmployeeType.ADMIN);
    UserLogin userLogin = new UserLogin(user, "hj", "sdfasdf");

    // Begin the transaction to commit the things with
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    session.persist(user);
    session.persist(userLogin);

    // Delete the user
    session.remove(user);

    // Assert the result list is 0 (there are no UserLogins)
    assertEquals(0, session.createQuery("FROM UserLogin", UserLogin.class).getResultList().size());

    transaction.rollback(); // Abort the transaction
    session.close(); // Close the session
  }
}
