package edu.wpi.FlashyFrogs.ServiceRequests;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

@GeneratedExclusion
public abstract class ServiceRequestController {

  // @FXML MFXButton backButton;
  @FXML private Pane rootPane;
  Stage stage;
  @FXML private PopOver popOver;

  @FXML
  public void initialize() {}

  @FXML
  public void handleClose(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  protected abstract void handleClear(ActionEvent event) throws IOException;

  protected abstract void handleDelete(ActionEvent event);

  protected abstract void handleBack(ActionEvent event) throws IOException;

  public abstract void handleSubmit(ActionEvent event) throws IOException;

  public abstract void setRequest(ServiceRequest request);

  public abstract void updateFields();

  public abstract void setPopOver(PopOver popOver);
}
