package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
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
public class HoldSanitationController {

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
  @FXML Text h8;
  @FXML Text h9;
  @FXML SearchableComboBox location;
  @FXML SearchableComboBox type;
  @FXML SearchableComboBox sanitationType;
  @FXML DatePicker date;
  @FXML TextField time;
  @FXML SearchableComboBox urgency;
  @FXML SearchableComboBox isolation;
  @FXML SearchableComboBox biohazard;
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
    h8.setVisible(false);
    h9.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    objects.sort(String::compareTo);

    ObservableList<String> observableList = FXCollections.observableList(objects);

    location.setItems(observableList);
    type.getItems()
        .addAll(
            "Lobby", "Waiting Room", "Patient Room", "Hallway", "Stairway", "Elevator", "Other");
    sanitationType.getItems().addAll("Sweeping", "Mopping", "Sanitizing");
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    isolation.getItems().addAll("Yes", "No");
    biohazard.getItems().addAll("Yes", "No");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts;
      parts = urgency.getValue().toString().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (location.getValue().toString().equals("")
          || type.getValue().toString().equals("")
          || sanitationType.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || time.getText().equals("")
          || isolation.getValue().toString().equals("")
          || biohazard.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      // String requestTypeEnumString = requestTypeDropDown.getText().toUpperCase();

      Date dateOfIncident =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      Sanitation sanitationRequest = new Sanitation();
      /*sanitationRequest.setLocation(session.find(LocationName.class, location.getValue().toString()));
       sanitationRequest.setLocationType(type.getValue().toString());
       sanitationRequest.setSanitationType(sanitationType.getValue().toString());
       sanitationRequest.setDateOfIncident(dateOfIncident);
       sanitationRequest.setTime(time.getText());
       sanitationRequest.setUrgency.valueOf(urgencyEnumString);
       sanitationRequest.setIsolation(isolation.getValue().toString());
       sanitationRequest.setBiohazard(biohazard.getValue().toString());
       sanitationRequest.setDescription(description.getText());
      */
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
    location.valueProperty().set(null);
    type.valueProperty().set(null);
    sanitationType.valueProperty().set(null);
    date.valueProperty().set(null);
    time.setText("");
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
      h8.setVisible(true);
      h9.setVisible(true);
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
      hDone = false;
    }
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "ITService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Home");
  }
}
