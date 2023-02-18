package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class CSVUploadController {
  @FXML private Label nodeFileLabel;
  @FXML private Label edgeFileLabel;
  @FXML private Label locationFileLabel;
  @FXML private Label moveFileLabel;
  @FXML private Label errorMessage;

  PopOver popOver;

  boolean allFiles = false;
  boolean node = false;
  boolean edge = false;
  boolean location = false;
  boolean move = false;
  FileData fileData;

  public void initialize() {
    fileData = new FileData();
  }

  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void handleChooseFiles() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    List<File> selectedFiles = fileChooser.showOpenMultipleDialog(popOver);
    for (int i = 0; i < selectedFiles.size(); i++) {
      try {
        Scanner fileScanner = new Scanner(selectedFiles.get(i));
        if (fileScanner.hasNextLine()) {
          String string = fileScanner.nextLine();
          if (string.contains("nodeID,xcoord,ycoord,floor,building")) {
            fileData.setNodesFile(selectedFiles.get(i));
            nodeFileLabel.setText(" File 1: " + selectedFiles.get(i).getName());
            node = true;
          } else if (string.contains("startNode,endNode")) {
            fileData.setEdgesFile(selectedFiles.get(i));
            edgeFileLabel.setText(" File 2: " + selectedFiles.get(i).getName());
            edge = true;
          } else if (string.contains("nodeID,longName")) {
            fileData.setMovesFile(selectedFiles.get(i));
            moveFileLabel.setText(" File 4: " + selectedFiles.get(i).getName());
            move = true;
          } else if (string.contains("nodeType,longName,shortName")) {
            fileData.setLocationsFile(selectedFiles.get(i));
            locationFileLabel.setText(" File 3: " + selectedFiles.get(i).getName());
            location = true;
          } else errorMessage.setText("Please select 4 files with the proper column headers.");
        } else {
          errorMessage.setText("Please select 4 files with the proper column headers.");
        }
      } catch (FileNotFoundException e) {
        errorMessage.setText("Please select 4 files with the proper column headers.");
      }
    }

    if (!node || !edge || !move || !location) {
      errorMessage.setText("Please select 4 files with the proper column headers.");
    } else {
      allFiles = true;
    }
  }

  public void handleUpload(ActionEvent actionEvent) {
    if (allFiles) {
      Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
      Transaction deleteTransaction = session.beginTransaction(); // Clearing transaction

      session.createMutationQuery("DELETE FROM Move").executeUpdate(); // Drop move
      session.createMutationQuery("DELETE FROM Edge").executeUpdate(); // Drop edge
      session.createMutationQuery("DELETE FROM LocationName").executeUpdate(); // Drop location
      session.createMutationQuery("DELETE FROM Node").executeUpdate(); // Drop node

      deleteTransaction.commit(); // Commit the transaction
      session.close();

      try {
        CSVParser.readFiles(
            fileData.getNodesFile(),
            fileData.getEdgesFile(),
            fileData.getLocationsFile(),
            fileData.getMovesFile(),
            DBConnection.CONNECTION.getSessionFactory());
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
      popOver.hide();
    } else {
      errorMessage.setText("Please select 4 files with the proper column headers.");
    }
  }

  public void handleBackup() {
    CSVMaker.makeCSVs();
    popOver.hide();
  }

  public void handleCancel() {
    fileData.setNodesFile(null);
    fileData.setEdgesFile(null);
    fileData.setLocationsFile(null);
    fileData.setMovesFile(null);
    popOver.hide();
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQLoadMapPage();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
