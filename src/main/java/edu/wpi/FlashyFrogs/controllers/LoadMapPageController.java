package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.CSVParser;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.FileData;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LoadMapPageController {
  @FXML private MFXButton loadFileButton;
  @FXML private MFXButton backButton;
  FileData fileData;

  public void initialize() {
    fileData = new FileData();
  }

  public void handleLoadFileButton(javafx.event.ActionEvent actionEvent) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fileChooser.showOpenDialog(null);
    fileData.addFile(selectedFile);
  }

  public void handleLoadFilesButton(ActionEvent actionEvent) {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction deleteTransaction = session.beginTransaction(); // Clearing transaction

    session.createMutationQuery("DELETE FROM Edge").executeUpdate(); // Drop edge
    session.createMutationQuery("DELETE FROM Move").executeUpdate(); // Drop move
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate(); // Drop location
    session.createMutationQuery("DELETE FROM Node").executeUpdate(); // Drop node

    deleteTransaction.commit(); // Commit the transaction
    session.close();
    if (fileData.correctNumOfFiles()) {
      System.out.println("Correct num of files!");
      List<File> files = fileData.getFiles();
      try {
        CSVParser.readFiles(
            files.get(0), files.get(1), files.get(2), files.get(3), CONNECTION.getSessionFactory());
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    } else {
      System.out.println("less than or more than four files chosen");
    }
  }

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }
}
