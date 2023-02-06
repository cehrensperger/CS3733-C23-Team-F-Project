package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class LoadMapPageController {
  @FXML private MFXButton chooseNodesButton;
  @FXML private MFXButton chooseEdgesButton;
  @FXML private MFXButton chooseLocationsButton;
  @FXML private MFXButton chooseMovesButton;
  @FXML private MFXButton backButton;
  @FXML private MFXButton question;

  @FXML private Label nodesFileLabel;
  @FXML private Label edgesFileLabel;
  @FXML private Label locationsFileLabel;
  @FXML private Label movesFileLabel;

  FileData fileData;

  public void initialize() {
    fileData = new FileData();
  }

  public void handleChooseNodes(javafx.event.ActionEvent actionEvent) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fileChooser.showOpenDialog(null);
    fileData.setNodesFile(selectedFile);
    nodesFileLabel.setText(selectedFile.getName());
  }

  public void handleChooseEdges(javafx.event.ActionEvent actionEvent) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fileChooser.showOpenDialog(null);
    fileData.setEdgesFile(selectedFile);
    edgesFileLabel.setText(selectedFile.getName());
  }

  public void handleChooseLocations(javafx.event.ActionEvent actionEvent) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fileChooser.showOpenDialog(null);
    fileData.setLocationsFile(selectedFile);
    locationsFileLabel.setText(selectedFile.getName());
  }

  public void handleChooseMoves(javafx.event.ActionEvent actionEvent) {

    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Resource File");
    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
    File selectedFile = fileChooser.showOpenDialog(null);
    fileData.setMovesFile(selectedFile);
    movesFileLabel.setText(selectedFile.getName());
  }

  public void handleLoadFilesButton(ActionEvent actionEvent) {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    Transaction deleteTransaction = session.beginTransaction(); // Clearing transaction

    session.createMutationQuery("DELETE FROM Edge").executeUpdate(); // Drop edge
    session.createMutationQuery("DELETE FROM Move").executeUpdate(); // Drop move
    session.createMutationQuery("DELETE FROM LocationName").executeUpdate(); // Drop location
    session.createMutationQuery("DELETE FROM Node").executeUpdate(); // Drop node

    deleteTransaction.commit(); // Commit the transaction
    session.close();
    if (fileData.allFilesChosen()) {
      System.out.println("Correct num of files!");

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
    } else {
      System.out.println("less than or more than four files chosen");
    }
  }

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("../views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQLoadMapPage();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
