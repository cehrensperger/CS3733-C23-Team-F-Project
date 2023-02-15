package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Security;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class SecurityController implements IController {

  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML MFXButton credits;
  @FXML MFXButton back;
  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<Security.ThreatType> threat;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML DatePicker date;
  @FXML TextField description;
  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    threat.setItems(FXCollections.observableArrayList(Security.ThreatType.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    session.close();
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // check
      if (locationBox.getValue().toString().equals("")
          || threat.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfRequest =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      Security securityRequest =
          new Security(
              description.getText(),
              locationBox.getValue(),
              CurrentUserEntity.CURRENT_USER.getCurrentuser(),
              dateOfRequest,
              Date.from(Instant.now()),
              urgency.getValue(),
              threat.getValue());

      try {
        session.persist(securityRequest);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#012D5A"));
        errorMessage.setText("Successfully submitted.");
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#b6000b"));
        errorMessage.setText("Please fill all fields.");
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#b6000b"));
      errorMessage.setText("Please fill all fields.");
      session.close();
    }
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    locationBox.valueProperty().set(null);
    threat.valueProperty().set(null);
    urgency.valueProperty().set(null);
    date.valueProperty().set(null);
    description.setText("");
  }

  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      hDone = false;
    }
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ITService");
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

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.handleBack();
  }

  @Override
  public void onClose() {}
}
