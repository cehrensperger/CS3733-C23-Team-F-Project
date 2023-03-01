package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/** Class that provides utilities for importing CSV Files and uploading them to the Database */
public class CSVParser {

  /**
   * Static method that reads files from the CSVs and uploads them to the database. Will either
   * successfully upload everything to the database OR nothing
   *
   * @param nodeFile the node file
   * @param edgeFile the edge file
   * @param locationFile the file with the locations
   * @param moveFile the file with the moves
   * @param factory the factory to create a session to access the database
   * @throws FileNotFoundException if any of the files could not be found
   */
  @SneakyThrows
  public static void readFiles(
      File nodeFile, File edgeFile, File locationFile, File moveFile, SessionFactory factory)
      throws FileNotFoundException {
    // Open a session to use, and a transaction
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction(); // Open the transaction

    // NodeID to Node mapping
    Map<String, Node> nodes = new HashMap<>();

    // Location name string to LocationName mapping
    Map<String, LocationName> locations = new HashMap<>();

    String[] fields; // Fields for each line

    try { // Try-catch to ensure atomcitiy with respect to the DB
      Scanner nodeFileScanner = new Scanner(nodeFile); // Scanner for the node file
      if (nodeFileScanner.hasNextLine())
        nodeFileScanner.nextLine(); // Skip the header line if there is one

      Scanner edgeFileScanner = new Scanner(edgeFile); // Edge file
      if (edgeFileScanner.hasNextLine()) edgeFileScanner.nextLine(); // Skip header if there is one

      Scanner locationFileScanner = new Scanner(locationFile); // Location file
      if (locationFileScanner.hasNextLine())
        locationFileScanner.nextLine(); // Skip the header if there is one

      Scanner moveFileScanner = new Scanner(moveFile); // Move file
      if (moveFileScanner.hasNextLine())
        moveFileScanner.nextLine(); // Skip the header if there is one

      // While there are new nodes
      while (nodeFileScanner.hasNextLine()) {

        // Find the fields by splitting on the comma
        fields = nodeFileScanner.nextLine().split(",");

        // Create the Node
        Node node = null;
        if (fields.length == 4) {
          node =
              new Node(
                  fields[0], // ID
                  "", // Building
                  Node.Floor.getEnum(fields[3]), // Floor
                  Integer.parseInt(fields[1]), // X-Coord
                  Integer.parseInt(fields[2])); // Y-Coord
        } else {

          node =
              new Node(
                  fields[0], // ID
                  fields[4], // Building
                  Node.Floor.getEnum(fields[3]), // Floor
                  Integer.parseInt(fields[1]), // X-Coord
                  Integer.parseInt(fields[2])); // Y-Coord
        }
        nodes.put(fields[0], node); // Put the node into the table with its ID
        session.persist(node);
      }

      // While there are new edges
      while (edgeFileScanner.hasNextLine()) {
        fields = edgeFileScanner.nextLine().split(","); // Get the fields by splitting on the comma

        // Create the edge by getting the Node objects from the Node apps
        Edge edge = new Edge(nodes.get(fields[0]), nodes.get(fields[1]));

        session.persist(edge); // Persist the Edge
      }

      session.flush();

      // While the locations have newlines
      while (locationFileScanner.hasNextLine()) {
        fields = locationFileScanner.nextLine().split(","); // Find the location fields

        // Create the location
        LocationName location =
            new LocationName(fields[1], LocationName.LocationType.valueOf(fields[0]), fields[2]);
        locations.put(fields[1], location); // Put the location name to location into the table
        session.persist(location); // Persist the location
      }

      session.flush();

      // While there are more moves
      while (moveFileScanner.hasNextLine()) {
        fields = moveFileScanner.nextLine().split(","); // Find the fields

        // Create the move from the lookups
        Move move =
            new Move(
                session.get(Node.class, fields[0]),
                locations.get(fields[1]),
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").parse(fields[2]));
        session.persist(move); // Persist the move
      }

    } catch (Exception error) {
      transaction.rollback(); // if something fails, rollback any/all changes
      session.close(); // Close the session

      throw error; // THEN rethrow the error
    }

    transaction.commit(); // If we succeeded, commit the changes to the DB
    session.close(); // And close the session
  }
}
