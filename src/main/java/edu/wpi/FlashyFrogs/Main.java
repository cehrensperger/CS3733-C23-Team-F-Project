package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Main {

  static final StandardServiceRegistry registry =
      new StandardServiceRegistryBuilder()
          .configure("./edu/wpi/FlashyFrogs/hibernate.cfg.xml") // Load settings
          .build();

  public static SessionFactory factory =
      new MetadataSources(registry).buildMetadata().buildSessionFactory();

  public static void main(String[] args) throws FileNotFoundException {
    File nodeFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/L1Nodes.csv");
    File edgeFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/L1Edges.csv");
    File moveFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/move.csv");
    File locationFile =
        new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/locationName.csv");

    CSVParser.readFiles(nodeFile, edgeFile, locationFile, moveFile);

    Session session = Main.factory.openSession();
    Transaction transaction = session.beginTransaction();

    String id = "L122550849";
    String newID = "1";
    Node node = session.find(Node.class, id);

    Node newNode =
        new Node(newID, node.getBuilding(), node.getFloor(), node.getXCoord(), node.getYCoord());
    session.persist(newNode);
    transaction.commit();

    transaction = session.beginTransaction();

    List<Edge> edges1 =
        session
            .createQuery("Select e From Edge e Where node1 = :node", Edge.class)
            .setParameter("node", node)
            .getResultList();
    List<Edge> edges2 =
        session
            .createQuery("Select e From Edge e Where node2 = :node", Edge.class)
            .setParameter("node", node)
            .getResultList();
    List<Move> moves =
        session
            .createQuery("Select m From Move m Where node = :node", Move.class)
            .setParameter("node", node)
            .getResultList();

    if (edges1.size() != 0) {
      for (int i = 0; i < edges1.size(); i++) {
        Edge edge = new Edge(newNode, edges1.get(i).getNode2());
        session.persist(edge);
        session.remove(edges1.get(i));
      }
    }
    if (edges2.size() != 0) {
      for (int i = 0; i < edges2.size(); i++) {
        Edge edge = new Edge(edges2.get(i).getNode1(), newNode);
        session.persist(edge);
        session.remove(edges2.get(i));
      }
    }
    if (moves.size() != 0) {
      for (int i = 0; i < moves.size(); i++) {
        Move move = new Move(newNode, moves.get(i).getLocation(), moves.get(i).getMoveDate());
        session.persist(move);
        session.remove(moves.get(i));
      }
    }
    transaction.commit();
    transaction = session.beginTransaction();
    session.remove(node);
    transaction.commit();

    transaction = session.beginTransaction();
    LocationName location = session.find(LocationName.class, "Anesthesia Conf Floor L1");
    moves =
        session
            .createQuery("Select m From Move m Where location = :location", Move.class)
            .setParameter("location", location)
            .getResultList();

    LocationName newLocation =
        new LocationName("new name", location.getLocationType(), location.getShortName());
    session.persist(newLocation);
    transaction.commit();
    transaction = session.beginTransaction();
    if (moves.size() != 0) {
      for (int i = 0; i < moves.size(); i++) {
        Move move = new Move(moves.get(i).getNode(), newLocation, moves.get(i).getMoveDate());
        session.persist(move);
        session.remove(moves.get(i));
      }
    }
    transaction.commit();
    transaction = session.beginTransaction();
    session.remove(location);
    transaction.commit();

    //    Fapp.launch(Fapp.class, args);
    factory.close();
    registry.close();
  }
}
