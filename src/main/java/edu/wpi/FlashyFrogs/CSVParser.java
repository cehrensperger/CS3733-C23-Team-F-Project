package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.wpi.FlashyFrogs.ORM.locationName;
import org.hibernate.SessionFactory;

public class CSVParser {

  public static void main(String args[]) throws FileNotFoundException {
    readFiles();
  }

  public void CSVParser(SessionFactory sf) {}

  public static void readFiles() throws FileNotFoundException {
    try {
      File nodeFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/L1Nodes.csv");
      File edgeFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/L1Edges.csv");
      File locationFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/locationName.csv");
      File moveFile = new File("src/main/resources/edu/wpi/FlashyFrogs/CSVFiles/move.csv");
      Scanner nodeFileScanner = new Scanner(nodeFile);
      Scanner edgeFileScanner = new Scanner(edgeFile);
      Scanner locationFileScanner = new Scanner(locationFile);
      Scanner moveFileScanner = new Scanner(moveFile);
      while (nodeFileScanner.hasNextLine()) {
        //maybe skip the first line, which happens to outline the type of data instead of being data...
        new Node();
      }
      while (edgeFileScanner.hasNextLine()) {
        //maybe skip the first line, which happens to outline the type of data instead of being data...
        new Edge();
      }
      while (locationFileScanner.hasNextLine()) {
        //maybe skip the first line, which happens to outline the type of data instead of being data...
        new locationName();
      }
      while (moveFileScanner.hasNextLine()) {
        //maybe skip the first line, which happens to outline the type of data instead of being data...
        new Move();
      }
    }
  }
}
