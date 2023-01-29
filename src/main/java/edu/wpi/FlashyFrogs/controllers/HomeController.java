package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class HomeController extends ServiceRequestController {

  @FXML private StackPane rootPane;
  @FXML private MFXButton serviceRequestsButton;
  @FXML private MFXButton mapDataEditorButton;
  @FXML private MFXButton pathfindingButton;
  @FXML private MFXButton exitButton;
  @FXML private MenuItem closeMenuItem;

  Stage stage;

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleClose(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  public void handleServiceRequestsButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  public void handleMapDataEditorButton(ActionEvent actionEvent) throws IOException {
    //
  }

  public void handlePathfindingButton(ActionEvent actionEvent) throws IOException {
    //
  }

  public void handleSecurityMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SecurityService");
  }

  public void handleTransportMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Transport");
  }

  public void handleSanitationMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SanitationService");
  }
}
