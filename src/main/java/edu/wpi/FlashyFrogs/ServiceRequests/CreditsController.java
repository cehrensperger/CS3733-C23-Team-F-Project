package edu.wpi.FlashyFrogs.ServiceRequests;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

@GeneratedExclusion
public class CreditsController implements IController {

  @FXML MFXButton MD;
  @FXML MFXButton religious;
  @FXML MFXButton credits;
  @FXML MFXButton back;
  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML Text h1;
  boolean hDone = false;

  public void initialize() {
    h1.setVisible(false);
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ComputerService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  public void handleMD(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Medicine Delivery");
  }

  public void handleReligious(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Religious");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.handleBack();
  }

  public void onClose() {}

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      hDone = false;
    }
  }
}
