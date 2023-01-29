package edu.wpi.FlashyFrogs.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ServiceRequestController {

  // @FXML MFXButton backButton;
  @FXML private Pane rootPane;
  Stage stage;

  @FXML
  public void initialize() {}

  @FXML
  public void handleClose(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }
}
