package edu.wpi.FlashyFrogs.ORM;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.DBConnection;
import java.time.Instant;
import java.util.Date;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Tests for the announcement class */
public class AnnouncementTest {
  @BeforeAll
  public static void setupDBConnection() {
    DBConnection.CONNECTION.connect(); // Connect
  }

  /** Tears down the database, meant to be used after all tests finish */
  @AfterAll
  public static void disconnectDBConnection() {
    DBConnection.CONNECTION.disconnect(); // Disconnect
  }

  /** Cleans up the user table. Runs after each test */
  @AfterEach
  public void teardownTable() {
    // If the prior test is open
    try {
      Session priorSession = DBConnection.CONNECTION.getSessionFactory().getCurrentSession();
      if (priorSession != null && priorSession.isOpen()) {

        // If the transaction is still active
        if (priorSession.getTransaction().isActive()) {
          priorSession.getTransaction().rollback(); // Roll it back
        }

        priorSession.close(); // Close it, so we can create new ones
      }
    } catch (HibernateException ignored) {
    }

    // Use a closure to manage the session to use
    try (Session connection = DBConnection.CONNECTION.getSessionFactory().openSession()) {
      Transaction cleanupTransaction = connection.beginTransaction(); // Begin a cleanup transaction
      connection.createMutationQuery("DELETE FROM Announcement").executeUpdate();
      connection.createMutationQuery("DELETE FROM User").executeUpdate();
      connection.createMutationQuery("DELETE FROM Department").executeUpdate();
      cleanupTransaction.commit(); // Commit the cleanup
    }
  }

  /** Creates a simple announcement, tests its getters and setters */
  @Test
  public void simpleAnnouncementTest() {
    Session testSession =
        DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction =
        testSession.beginTransaction(); // Begin a transaction to commit the stuff with

    Date testDate = new Date(); // Create a date to use with testing

    // Create a user to use
    User newUser = new User("A", "B", "C", User.EmployeeType.STAFF, null);

    // Create the string to use
    String announcement = "announce";

    Announcement testAnnouncement = new Announcement(testDate, newUser, announcement);

    testSession.persist(newUser);
    testSession.persist(testAnnouncement);

    // Assert that the fields are gotten correctly
    assertEquals(testDate, testAnnouncement.getCreationDate());
    assertEquals(newUser, testAnnouncement.getAuthor());
    assertEquals(announcement, testAnnouncement.getAnnouncement());
    assertEquals(Long.toString(testAnnouncement.getId()), testAnnouncement.toString());

    testAnnouncement.setAnnouncement("asdfasf"); // Change the announcement

    // Check the fields on the announcements
    assertEquals(testDate, testAnnouncement.getCreationDate());
    assertEquals(newUser, testAnnouncement.getAuthor());
    assertEquals("asdfasf", testAnnouncement.getAnnouncement());
    assertEquals(Long.toString(testAnnouncement.getId()), testAnnouncement.toString());

    // Update the date
    Date newDate = Date.from(Instant.ofEpochSecond(100000));
    testAnnouncement.setCreationDate(newDate);

    // Check the fields
    assertEquals(newDate, testAnnouncement.getCreationDate());
    assertEquals(newUser, testAnnouncement.getAuthor());
    assertEquals("asdfasf", testAnnouncement.getAnnouncement());
    assertEquals(Long.toString(testAnnouncement.getId()), testAnnouncement.toString());

    transaction.rollback(); // Cancel the TXN, it's necessary
    testSession.close();
  }

  /** Creates a simple announcement, tests its getters and setters */
  @Test
  public void anotherSimpleAnnouncementTest() {
    Session testSession =
        DBConnection.CONNECTION.getSessionFactory().openSession(); // Open a session
    Transaction transaction =
        testSession.beginTransaction(); // Begin a transaction to commit the stuff with

    Date testDate = Date.from(Instant.ofEpochSecond(1000)); // Create a date to use with testing

    // Create a user to use
    User newUser = new User("cxbcvxb", "qwer", "yetuyiu", User.EmployeeType.ADMIN, null);

    // Create the string to use
    String announcement = "something";

    Announcement testAnnouncement = new Announcement(testDate, newUser, announcement);

    testSession.persist(newUser);
    testSession.persist(testAnnouncement);

    // Assert that the fields are gotten correctly
    assertEquals(testDate, testAnnouncement.getCreationDate());
    assertEquals(newUser, testAnnouncement.getAuthor());
    assertEquals(announcement, testAnnouncement.getAnnouncement());

    testAnnouncement.setAnnouncement("something new"); // Change the announcement

    // Check the fields on the announcements
    assertEquals(testDate, testAnnouncement.getCreationDate());
    assertEquals(newUser, testAnnouncement.getAuthor());
    assertEquals("something new", testAnnouncement.getAnnouncement());

    // Update the date
    Date newDate = new Date();
    testAnnouncement.setCreationDate(newDate);

    // Check the fields
    assertEquals(newDate, testAnnouncement.getCreationDate());
    assertEquals(newUser, testAnnouncement.getAuthor());
    assertEquals("something new", testAnnouncement.getAnnouncement());

    transaction.rollback(); // Cancel the TXN, it's necessary
    testSession.close();
  }

  /** Test for equals and hash code */
  @Test
  public void equalsAndHashCodeTest() {
    // Create the session we will use
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction(); // Open the transaction

    Announcement originalAnnouncement = new Announcement(new Date(), null, "message");
    session.persist(originalAnnouncement);

    assertEquals(
        originalAnnouncement,
        session.createQuery("FROM Announcement", Announcement.class).getSingleResult());

    assertEquals(
        originalAnnouncement.hashCode(),
        session.createQuery("FROM Announcement", Announcement.class).getSingleResult().hashCode());

    // Identical announcement
    Announcement identicalAnnouncement =
        new Announcement(originalAnnouncement.getCreationDate(), null, "message");
    session.persist(identicalAnnouncement);

    // Check the two aren't equal (seq. IDs should be different)
    assertNotEquals(originalAnnouncement, identicalAnnouncement);
    assertNotEquals(originalAnnouncement.hashCode(), identicalAnnouncement.hashCode());

    // User for the completely different announcement
    User user = new User("a", "b", "c", User.EmployeeType.ADMIN, null);

    Announcement completelyDifferentAnnouncement =
        new Announcement(Date.from(Instant.ofEpochSecond(1)), user, "diff");

    // Persist everything
    session.persist(user);
    session.persist(completelyDifferentAnnouncement);

    assertNotEquals(originalAnnouncement, completelyDifferentAnnouncement);
    assertNotEquals(originalAnnouncement.hashCode(), completelyDifferentAnnouncement.hashCode());

    transaction.rollback(); // End the transaction
    session.close(); // End the session
  }

  /** Tests that ON UPDATE CASCADE fails for the Announcement (user PK can not be updated) */
  @Test
  public void onUpdateCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Create a session
    Transaction transaction = session.beginTransaction(); // Begin the transaction

    User user = new User("A", "B", "C", User.EmployeeType.MEDICAL, null);
    session.persist(user);

    Announcement announcement = new Announcement(new Date(), user, "mes");
    session.persist(announcement);

    transaction.commit(); // Commit, so we can access later

    long originalId = user.getId(); // Get the Id originally

    transaction = session.beginTransaction(); // Open a new transaction

    // Try creating the query
    assertThrows(
        Exception.class,
        () -> session.createMutationQuery("UPDATE User SET id=999").executeUpdate());

    transaction.rollback(); // Rollback

    transaction = session.beginTransaction(); // Create a new transaction
    assertEquals(user, session.find(User.class, originalId)); // Find the old user
    assertEquals(
        user,
        session.createQuery("FROM Announcement", Announcement.class).getSingleResult().getAuthor());
    transaction.commit();

    session.close(); // Session close
  }

  /** Tests that ON DELETE SET NULL succeeds for the Announcement */
  @Test
  public void onDeleteCascadeTest() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession(); // Create a session
    Transaction transaction = session.beginTransaction(); // Begin the transaction

    User user = new User("b", "C", "d", User.EmployeeType.MEDICAL, null);
    session.persist(user);

    Announcement announcement = new Announcement(new Date(), user, "asdf");
    session.persist(announcement);

    transaction.commit(); // Commit, so we can access later

    transaction = session.beginTransaction(); // Open a new transaction

    // Try creating the query
    session.createMutationQuery("DELETE FROM User").executeUpdate();

    session.flush();

    transaction.commit(); // Rollback

    session.refresh(announcement);

    transaction = session.beginTransaction(); // Create a new transaction
    assertNull(session.createQuery("FROM User", User.class).uniqueResult()); // Find the old user
    assertNull(
        session.createQuery("FROM Announcement", Announcement.class).getSingleResult().getAuthor());
    transaction.commit();

    session.close(); // Session close
  }

  /**
   * Equals on Announcement should return false for other classes and null
   */
  @Test
  public void equalsOtherTest() {
    assertNotEquals(new Announcement(new Date(), null, "B"), null);
    assertNotEquals(new Announcement(new Date(), null, "b"), "adf");
  }
}
