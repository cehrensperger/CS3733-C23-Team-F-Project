package edu.wpi.FlashyFrogs.ServiceRequests;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@GeneratedExclusion
public abstract class ServiceRequestController {

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

  abstract void handleClear(ActionEvent event) throws IOException;

  abstract void handleBack(ActionEvent event) throws IOException;

  abstract void handleSubmit(ActionEvent event) throws IOException;
}
