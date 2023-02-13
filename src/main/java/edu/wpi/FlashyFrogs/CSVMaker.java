package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.hibernate.Session;

public class CSVMaker {

  public static void makeCSVs() {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();

    makeEdgesCSV(session);
    makeNodesCSV(session);
    makeMovesCSV(session);
    makeLocationsCSV(session);
  }

  private static void makeEdgesCSV(Session session) {
    List<Edge> edges = session.createQuery("SELECT e FROM Edge e", Edge.class).getResultList();
    File edgesFile = new File("edges.csv");
    try {
      FileWriter fileWriter = new FileWriter(edgesFile);
      fileWriter.write("startNode,endNode\n");

      for (Edge edge : edges) {

        fileWriter.write(edge.getNode1() + ",");
        fileWriter.write(edge.getNode2() + "\n");
      }

      fileWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void makeNodesCSV(Session session) {
    List<Node> nodes = session.createQuery("SELECT e FROM Node e", Node.class).getResultList();
    File nodesFile = new File("nodes.csv");
    try {
      FileWriter fileWriter = new FileWriter(nodesFile);
      fileWriter.write("NodeID,xcoord,ycoord,floor,building\n");

      for (Node node : nodes) {

        fileWriter.write(node.getId() + ",");
        fileWriter.write(node.getXCoord() + ",");
        fileWriter.write(node.getYCoord() + ",");
        fileWriter.write(node.getFloor().floorNum + ",");
        fileWriter.write(node.getBuilding() + "\n");
      }

      fileWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void makeMovesCSV(Session session) {
    List<Move> moves = session.createQuery("SELECT e FROM Move e", Move.class).getResultList();
    File movesFile = new File("moves.csv");
    try {
      FileWriter fileWriter = new FileWriter(movesFile);
      fileWriter.write("nodeID,longName\n");

      for (Move move : moves) {

        fileWriter.write(move.getNode().getId() + ",");
        fileWriter.write(move.getLocation().getLongName() + "\n");
      }

      fileWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void makeLocationsCSV(Session session) {
    List<LocationName> locationNames =
        session.createQuery("SELECT e FROM LocationName e", LocationName.class).getResultList();
    File movesFile = new File("locations.csv");
    try {
      FileWriter fileWriter = new FileWriter(movesFile);
      fileWriter.write("nodeType,longName,shortName\n");

      for (LocationName locationName : locationNames) {

        fileWriter.write(locationName.getLocationType() + ",");
        fileWriter.write(locationName.getLongName() + ",");
        fileWriter.write(locationName.getShortName() + "\n");
      }

      fileWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
