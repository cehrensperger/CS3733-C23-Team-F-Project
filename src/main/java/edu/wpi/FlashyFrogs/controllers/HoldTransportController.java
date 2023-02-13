package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import jakarta.persistence.RollbackException;
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

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

public class HoldTransportController {

  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;
  @FXML MFXButton back;

  @FXML TextField patient;
  @FXML SearchableComboBox vision;
  @FXML SearchableComboBox hearing;
  @FXML SearchableComboBox consciousness;
  @FXML SearchableComboBox condition;
  @FXML SearchableComboBox to;
  @FXML SearchableComboBox from;
  @FXML SearchableComboBox urgency;
  @FXML SearchableComboBox equipment;
  @FXML DatePicker date;
  @FXML TextField time;
  @FXML SearchableComboBox mode;
  @FXML SearchableComboBox isolation;
  @FXML SearchableComboBox personal;
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
  @FXML Text h15;
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
    h15.setVisible(false);

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
        .addAll("Good", "Poor", "Deaf", "Hearing Aid (Left)", "Hearing Aid (Right)", "Hearing Aid (Both)");
    consciousness.getItems().addAll("Good", "Moderate", "Poor");
    condition.getItems().addAll("Healthy", "Moderate", "Poor");
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    equipment.getItems().addAll("None", "Cane", "Walker", "Wheel Chair", "Bed");
    mode.getItems().addAll("Self", "With Help", "Equipment Needed");
    isolation.getItems().addAll("Yes", "No");
    personal
        .getItems()
        .addAll("None", "Glasses", "Walker", "Cane", "Hearing Aids", "Dentures", "Other");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String urgencyString = urgency.getValue().toString().toUpperCase().replace(" ", "_");
      String timeString = time.getText().toUpperCase().replace(" ", "_");

      // check
      if (patient.getText().equals("")
              || vision.getValue().toString().equals("")
              || hearing.getValue().toString().equals("")
              || consciousness.getValue().toString().equals("")
              || condition.getValue().toString().equals("")
              || to.getValue().toString().equals("")
              || from.getValue().toString().equals("")
              || equipment.getValue().toString().equals("")
              || mode.getValue().toString().equals("")
              || isolation.getValue().toString().equals("")
              || personal.getValue().toString().equals("")
              || reason.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfTransport =
              Date.from(
                      date
                              .getValue()
                              .atStartOfDay(ZoneId.systemDefault())
                              .toInstant());

      InternalTransport transport = new InternalTransport();
      //this needs to be updated
      /*transport.setPatientID(patient.getText());
      transport.setVision(vision.getValue().toString());
      transport.setHearing(hearing.getValue().toString());
      transport.setConsciousness(consciousness.getValue().toString());
      transport.setCondition(condition.getValue().toString());
      transport.setTransferTo(session.find(LocationName.class, to.getValue().toString()));
      transport.setTransferFrom(session.find(LocationName.class, from.getValue().toString()));
      transport.setUrgency(ServiceRequest.Urgency.valueOf(urgencyString));
      transport.setDateOfIncident(dateOfTransport);
      transport.setTime(timeString);
      transport.setMode(mode.getValue().toString());
      transport.setIsolation(isolation.getValue().toString());
      transport.setPersonal(personal.getValue().toString());
      transport.setReason(reason.getText());*/
      try {
        session.persist(transport);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#44ff00"));
        errorMessage.setText("Successfully submitted.");
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#ff0000"));
        errorMessage.setText("Please fill all fields.");
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#ff0000"));
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
    time.setText("");
    mode.valueProperty().set(null);
    isolation.valueProperty().set(null);
    personal.valueProperty().set(null);
    reason.setText("");
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "AudioVisualService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "ITService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SecurityService");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Home");
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
      h15.setVisible(true);
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
      h15.setVisible(false);
      hDone = false;
    }
  }
}
