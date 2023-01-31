package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import static java.lang.Number.*;

public class CSVParser {

  public void CSVParser(SessionFactory sf) {}

  public static void readFiles(File nodeFile, File edgeFile, File locationFile, File moveFile) throws FileNotFoundException {
    try {
      Session session = Main.factory.openSession();
      Transaction transaction = session.beginTransaction();
      Map<String, Node> nodes = new HashMap<>();
      Map<String, LocationName> locations = new HashMap<>();
      String[] fields;

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

        Node node = new Node(fields[0], fields[1], Node.Floor.valueOf(fields[2]), Integer.parseInt(fields[3]), Integer.parseInt(fields[4]));
        nodes.put(fields[0], node);
        session.persist(node);
      }
      while (edgeFileScanner.hasNextLine()) {
        fields = nodeFileScanner.nextLine().split(",");

        Edge edge = new Edge(nodes.get(fields[0]), nodes.get(fields[1]));
        session.persist(edge);
      }
      while (locationFileScanner.hasNextLine()) {
        fields = nodeFileScanner.nextLine().split(",");

        LocationName location = new LocationName(fields[0], LocationName.LocationType.valueOf(fields[1]), fields[2]);
        locations.put(fields[0], location);
        session.persist(location);
      }
      while (moveFileScanner.hasNextLine()) {
        fields = nodeFileScanner.nextLine().split(",");

        Move move = new Move(nodes.get(fields[0]), locations.get(fields[1]), Date.from());
        session.persist(move);
      }
      transaction.commit();
      session.close();

    } catch(FileNotFoundException e) {
      System.out.println("Use a valid filepath.");
    }
  }
}
