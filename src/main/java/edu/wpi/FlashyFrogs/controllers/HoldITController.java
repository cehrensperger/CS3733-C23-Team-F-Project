package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.ComputerService;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HoldITController {

  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;
  @FXML MFXButton back;
  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML TextField number;
  @FXML SearchableComboBox location;
  @FXML SearchableComboBox device;
  @FXML SearchableComboBox repair;
  @FXML SearchableComboBox urgency;

  @FXML TextField type;
  @FXML TextField model;
  @FXML TextField description;
  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;
  @FXML Text h8;
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

    Session session = CONNECTION.getSessionFactory().openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    objects.sort(String::compareTo);

    ObservableList<String> observableList = FXCollections.observableList(objects);

    location.setItems(observableList);
    device.getItems().addAll("Yes", "No");
    repair.getItems().addAll("Yes", "No");
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String urgencyString = urgency.getValue().toString().toUpperCase().replace(" ", "_");

      // check
      if (number.getText().equals("")
          || location.getValue().toString().equals("")
          || device.getValue().toString().equals("")
          || repair.getValue().toString().equals("")
          || type.getText().equals("")
          || model.getText().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      ComputerService informationTechnology = new ComputerService();
      // this needs to be updated when database is fixed
      /*informationTechnology.setNumber(number.getText());
      informationTechnology.setLocation(session.find(LocationName.class, location.getValue().toString()));
      informationTechnology.setNewDevice(device.getValue().toString());
      informationTechnology.setRepair(repair.getValue().toString());
      informationTechnology.setDeviceType(type.getText()) ;
      informationTechnology.setModel(model.getText());
      informationTechnology.setUrgency(ServiceRequest.Urgency.valueOf(urgencyString));
      informationTechnology.setDescription(description.getText());*/
      try {
        session.persist(informationTechnology);
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
    number.setText("");
    location.valueProperty().set(null);
    device.valueProperty().set(null);
    repair.valueProperty().set(null);
    type.setText("");
    model.setText("");
    urgency.valueProperty().set(null);
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
      hDone = false;
    }
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
    Fapp.handleBack();
  }

  public void onClose() {}
}
