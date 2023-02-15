package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
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

public class TransportController implements IController {

  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;
  @FXML MFXButton back;

  @FXML TextField patient;
  @FXML SearchableComboBox<String> vision;
  @FXML SearchableComboBox<String> hearing;
  @FXML SearchableComboBox<String> consciousness;
  @FXML SearchableComboBox<String> condition;
  @FXML SearchableComboBox<String> to;
  @FXML SearchableComboBox<String> from;
  @FXML SearchableComboBox<String> urgency;
  @FXML SearchableComboBox<String> equipment;
  @FXML DatePicker date;
  @FXML SearchableComboBox<String> mode;
  @FXML SearchableComboBox<String> isolation;
  @FXML TextField personal;
  @FXML TextField reason;
  @FXML MFXButton clear;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;
  @FXML Text h8;
  @FXML Text h9;
  @FXML Text h10;
  @FXML Text h11;
  @FXML Text h12;
  @FXML Text h13;
  @FXML Text h14;
  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    h7.setVisible(false);
    h8.setVisible(false);
    h9.setVisible(false);
    h10.setVisible(false);
    h11.setVisible(false);
    h12.setVisible(false);
    h13.setVisible(false);
    h14.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    objects.sort(String::compareTo);

    ObservableList<String> observableList = FXCollections.observableList(objects);

    to.setItems(observableList);
    from.setItems(observableList);
    vision.getItems().addAll("Good", "Poor", "Blind", "Glasses");
    hearing
        .getItems()
        .addAll(
            "Good",
            "Poor",
            "Deaf",
            "Hearing Aid (Left)",
            "Hearing Aid (Right)",
            "Hearing Aid (Both)");
    consciousness.getItems().addAll("Good", "Moderate", "Poor");
    condition.getItems().addAll("Healthy", "Moderate", "Poor");
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    equipment.getItems().addAll("None", "Cane", "Walker", "Wheel Chair", "Bed");
    mode.getItems().addAll("Self", "With Help", "Equipment Needed");
    isolation.getItems().addAll("Yes", "No");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String urgencyString = urgency.getValue().toString().toUpperCase().replace(" ", "_");

      // check
      if (patient.getText().equals("")
          || vision.getValue().toString().equals("")
          || hearing.getValue().toString().equals("")
          || consciousness.getValue().toString().equals("")
          || condition.getValue().toString().equals("")
          || to.getValue().toString().equals("")
          || from.getValue().toString().equals("")
          || equipment.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || mode.getValue().toString().equals("")
          || isolation.getValue().toString().equals("")
          || personal.getText().equals("")
          || reason.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfTransport =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
      String visionEnumString = vision.getValue().toString().toUpperCase().replace(" ", "_");
      String hearingEnumString = hearing.getValue().toString().toUpperCase().replace(" ", "_");
      String consciousnessEnumString =
          consciousness.getValue().toString().toUpperCase().replace(" ", "_");
      String conditionEnumString = condition.getValue().toString().toUpperCase().replace(" ", "_");
      String equipmentEnumString = equipment.getValue().toString().toUpperCase().replace(" ", "_");
      String modeEnumString = mode.getValue().toString().toUpperCase().replace(" ", "_");
      boolean isIsolation = false;
      if (isolation.getValue().toString().equals("Yes")) {
        isIsolation = true;
      }

      InternalTransport transport = new InternalTransport();
      // this needs to be updated when database is fixed
      transport.setPatientID(patient.getText());
      transport.setVision(InternalTransport.VisionStatus.valueOf(visionEnumString));
      transport.setHearing(InternalTransport.HearingStatus.valueOf(hearingEnumString));
      transport.setConsciousness(
          InternalTransport.ConsciousnessStatus.valueOf(consciousnessEnumString));
      transport.setHealthStatus(InternalTransport.HealthStatus.valueOf(conditionEnumString));
      transport.setLocation(session.find(LocationName.class, from.getValue().toString()));
      transport.setTargetLocation(session.find(LocationName.class, to.getValue().toString()));
      transport.setUrgency(ServiceRequest.Urgency.valueOf(urgencyString));
      transport.setEquipment(InternalTransport.Equipment.valueOf(equipmentEnumString));
      transport.setDate(dateOfTransport);
      transport.setDateOfSubmission(Date.from(Instant.now()));
      transport.setEmp(CurrentUserEntity.CURRENT_USER.getCurrentuser());
      transport.setMode(InternalTransport.ModeOfTransport.valueOf(modeEnumString));
      transport.setIsolation(isIsolation);
      transport.setPersonalItems(personal.getText());
      transport.setReason(reason.getText());
      try {
        session.persist(transport);
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
    patient.setText("");
    vision.valueProperty().set(null);
    hearing.valueProperty().set(null);
    consciousness.valueProperty().set(null);
    condition.valueProperty().set(null);
    to.valueProperty().set(null);
    from.valueProperty().set(null);
    urgency.valueProperty().set(null);
    equipment.valueProperty().set(null);
    date.valueProperty().set(null);
    mode.valueProperty().set(null);
    isolation.valueProperty().set(null);
    personal.setText("");
    reason.setText("");
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

  public void help() {
    if (hDone = false) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      h7.setVisible(true);
      h8.setVisible(true);
      h9.setVisible(true);
      h10.setVisible(true);
      h11.setVisible(true);
      h12.setVisible(true);
      h13.setVisible(true);
      h14.setVisible(true);
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
      h8.setVisible(false);
      h9.setVisible(false);
      h10.setVisible(false);
      h11.setVisible(false);
      h12.setVisible(false);
      h13.setVisible(false);
      h14.setVisible(false);
      hDone = false;
    }
  }

  @Override
  public void onClose() {}
}
