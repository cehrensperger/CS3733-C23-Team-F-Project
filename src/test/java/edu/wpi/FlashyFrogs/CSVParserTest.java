package edu.wpi.FlashyFrogs;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;

public class CSVParserTest {
  private static Session testSession; // Session to be used for each individual test

  /**
   * Setup method to be run before all tests that creates the session factory and service registry
   */
  @BeforeAll
  public static void setupSessionFactory() {
    // Create the DB Connection we will use
    DBConnection.CONNECTION.connect(); // Connect to the DB
  }

  /**
   * Teardown method to be run after all tests. Cleans up the service registry and session factory
   */
  @AfterAll
  public static void closeSessionFactory() {
    DBConnection.CONNECTION.disconnect(); // Teardown the connection
  }

  static File nodeFile =
      new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/nodesWithNewIDs.csv");
  static File testNodeFile =
      new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/testNodes.csv");
  static File edgeFile = new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/edgesFixed.csv");
  static File moveFile = new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/move.csv");
  static File locationFile =
      new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/locationName.csv");
  static File emptyFile = new File("src/test/resources/edu/wpi/FlashyFrogs/CSVFiles/emptyFile.csv");

  @BeforeEach
  public void setup() {
    testSession = DBConnection.CONNECTION.getSessionFactory().openSession();
  }

  @AfterEach
  public void teardownSession() {
    Transaction deleteTransaction = testSession.beginTransaction(); // Clearing transaction

    testSession.createMutationQuery("DELETE FROM Edge").executeUpdate(); // Drop edge
    testSession.createMutationQuery("DELETE FROM Move").executeUpdate(); // Drop move
    testSession.createMutationQuery("DELETE FROM LocationName").executeUpdate(); // Drop location
    testSession.createMutationQuery("DELETE FROM Node").executeUpdate(); // Drop node

    deleteTransaction.commit(); // Commit the transaction

    testSession.close(); // Close the session
  }

  /** tests that files with the correct data does not fail */
  @Test
  public void readFilesTest() {
    try {
      assertDoesNotThrow(
          () ->
              CSVParser.readFiles(
                  nodeFile,
                  edgeFile,
                  locationFile,
                  moveFile,
                  DBConnection.CONNECTION.getSessionFactory()));
    } catch (Exception e) {
      fail();
    }
  }

  /** tests that empty files will work */
  @Test
  public void readEmptyFilesTest() {
    try {
      assertDoesNotThrow(
          () ->
              CSVParser.readFiles(
                  emptyFile,
                  emptyFile,
                  emptyFile,
                  emptyFile,
                  DBConnection.CONNECTION.getSessionFactory()));
    } catch (Exception e) {
      fail();
    }
  }

  /** tests to ensure that the correct objects are inserted into the database */
  @Test
  public void insertTest() {
    try {
      CSVParser.readFiles(
          testNodeFile,
          emptyFile,
          emptyFile,
          emptyFile,
          DBConnection.CONNECTION.getSessionFactory());
    } catch (FileNotFoundException e) {
      fail();
    }

    try {
      Scanner nodeFileScanner = new Scanner(testNodeFile);
      nodeFileScanner.nextLine();
      String[] fields = nodeFileScanner.nextLine().split(",");
      Node node =
          new Node(
              fields[0],
              fields[4],
              Node.Floor.valueOf(fields[3]),
              Integer.parseInt(fields[1]),
              Integer.parseInt(fields[2]));
      assertEquals(node, testSession.find(Node.class, node.getId()));
    } catch (FileNotFoundException e) {
      fail();
    }
  }

  /** Tests that if the files aren't found, an exception is thrown */
  @Test
  public void testFileNotFoundException() {
    assertThrows(
        Exception.class,
        () ->
            CSVParser.readFiles(
                new File(""),
                new File(""),
                new File(""),
                new File(""),
                DBConnection.CONNECTION.getSessionFactory()));
  }
}
