package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
public class HoldSanitationController implements IController{

  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;
  @FXML MFXButton back;
  @FXML MFXButton clear;
  @FXML MFXButton submit;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;
  @FXML SearchableComboBox<String> locationBox;
  @FXML SearchableComboBox<String> sanitationType;
  @FXML DatePicker date;
  @FXML SearchableComboBox<String> urgency;
  @FXML SearchableComboBox<String> isolation;
  @FXML SearchableComboBox<String> biohazard;
  @FXML TextField description;
  boolean hDone = false;
  @FXML private Label errorMessage;
  private Connection connection = null;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    h7.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    objects.sort(String::compareTo);

    ObservableList<String> observableList = FXCollections.observableList(objects);

    locationBox.setItems(observableList);
    sanitationType.getItems().addAll("Sweeping", "Mopping", "Sanitizing");
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    isolation.getItems().addAll("Yes", "No");
    biohazard.getItems().addAll("Yes", "No");
    session.close();
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts;
      String urgencyString = urgency.getValue().toString().toUpperCase().replace(" ", "_");

      if (locationBox.getValue().toString().equals("")
          || sanitationType.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || isolation.getValue().toString().equals("")
          || biohazard.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      // String requestTypeEnumString = requestTypeDropDown.getText().toUpperCase();

      Date dateOfIncident =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      String sanitationTypeEnumString =
          sanitationType.getValue().toString().toUpperCase().replace(" ", "_");
      boolean isIsolation = false;
      if (isolation.getValue().toString().equals("Yes")) {
        isIsolation = true;
      }
      String bioTypeEnumString = biohazard.getValue().toString().toUpperCase().replace(" ", "_");

      Sanitation sanitationRequest = new Sanitation();
      sanitationRequest.setLocation(
          session.find(LocationName.class, locationBox.getValue().toString()));
      sanitationRequest.setType(Sanitation.SanitationType.valueOf(sanitationTypeEnumString));
      sanitationRequest.setEmp(CurrentUserEntity.CURRENT_USER.getCurrentuser());
      sanitationRequest.setDate(dateOfIncident);
      sanitationRequest.setDateOfSubmission(Date.from(Instant.now()));
      sanitationRequest.setUrgency(ServiceRequest.Urgency.valueOf(urgencyString));
      sanitationRequest.setIsolation(isIsolation);
      sanitationRequest.setBiohazard(Sanitation.BiohazardLevel.valueOf(bioTypeEnumString));
      sanitationRequest.setDescription(description.getText());
      try {
        session.persist(sanitationRequest);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(Paint.valueOf("#012D5A"));
        errorMessage.setText("Successfully submitted.");
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(Paint.valueOf("#b6000b"));
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
    sanitationType.valueProperty().set(null);
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    isolation.valueProperty().set(null);
    biohazard.valueProperty().set(null);
    description.setText("");
  }

  public void help() {
    if (hDone = false) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      h7.setVisible(true);
      hDone = true;
    }
    if (hDone = true) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      h7.setVisible(false);
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
