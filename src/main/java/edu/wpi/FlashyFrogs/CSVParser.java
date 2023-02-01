package edu.wpi.FlashyFrogs;

import static java.lang.Number.*;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class CSVParser {

  public static void readFiles(
      File nodeFile, File edgeFile, File locationFile, File moveFile, SessionFactory sessionFactory)
      throws FileNotFoundException {
    Session session = sessionFactory.openSession();
    Transaction transaction = session.beginTransaction();
    Map<String, Node> nodes = new HashMap<>();
    Map<String, LocationName> locations = new HashMap<>();
    String[] fields;

    try {
      Scanner nodeFileScanner = new Scanner(nodeFile);
      nodeFileScanner.nextLine();

      Scanner edgeFileScanner = new Scanner(edgeFile);
      edgeFileScanner.nextLine();

      Scanner locationFileScanner = new Scanner(locationFile);
      locationFileScanner.nextLine();

      Scanner moveFileScanner = new Scanner(moveFile);
      moveFileScanner.nextLine();

      while (nodeFileScanner.hasNextLine()) {
        fields = nodeFileScanner.nextLine().split(",");

        Node node =
            new Node(
                fields[0],
                fields[4],
                Node.Floor.valueOf(fields[3]),
                Integer.parseInt(fields[1]),
                Integer.parseInt(fields[2]));
        nodes.put(fields[0], node);
        session.persist(node);
      }
      while (edgeFileScanner.hasNextLine()) {
        fields = edgeFileScanner.nextLine().split(",");

        Edge edge = new Edge(nodes.get(fields[0]), nodes.get(fields[1]));
        session.persist(edge);
      }
      while (locationFileScanner.hasNextLine()) {
        fields = locationFileScanner.nextLine().split(",");

        LocationName location =
            new LocationName(fields[1], LocationName.LocationType.valueOf(fields[0]), fields[2]);
        locations.put(fields[1], location);
        session.persist(location);
      }
      while (moveFileScanner.hasNextLine()) {
        fields = moveFileScanner.nextLine().split(",");

        Move move =
            new Move(nodes.get(fields[0]), locations.get(fields[1]), Date.from(Instant.now()));
        session.persist(move);
      }

    } catch (Exception error) {
      transaction.rollback();
      session.close();

      throw error;
    }

    transaction.commit();
    session.close();
  }
}
