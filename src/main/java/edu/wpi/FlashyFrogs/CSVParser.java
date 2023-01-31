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

  public void CSVParser(SessionFactory sf) {}

  public static void readFiles(File nodeFile, File edgeFile, File locationFile, File moveFile) throws FileNotFoundException {
    try {
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
        new LocationName();
      }
      while (moveFileScanner.hasNextLine()) {
        //maybe skip the first line, which happens to outline the type of data instead of being data...
        new Move();
      }
    } catch(FileNotFoundException e) {
      System.out.println("Use a valid filepath.");
    }
  }
}
