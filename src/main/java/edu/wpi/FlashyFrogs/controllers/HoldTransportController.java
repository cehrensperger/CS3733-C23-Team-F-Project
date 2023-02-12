package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.controlsfx.control.SearchableComboBox;

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

  boolean hDone = false;


  public void initialize()
  {
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

    vision.getItems().addAll("Good", "Poor", "Blind", "Glasses");
    hearing.getItems().addAll("Good", "Poor", "Deaf", "Hearing Aid (Left)", "Hearing Aid (Right)", "Hearing Aid (Both)");
    consciousness.getItems().addAll("Good", "Moderate", "Poor");
    condition.getItems().addAll("Healthy", "Moderate", "Poor");
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    equipment.getItems().addAll("None", "Cane", "Walker", "Wheel Chair", "Bed");
    mode.getItems().addAll("Self", "With Help", "Equipment Needed");
    isolation.getItems().addAll("Yes", "No");
    personal.getItems().addAll("None", "Glasses", "Walker", "Cane", "Hearing Aids", "Dentures", "Other");

  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    //
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
    Fapp.setScene("views", "AV2");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "IT2");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "T2");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Sa2");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Se2");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Home");
  }

  public void help()
  {
    if(hDone = false) {
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
    if(hDone = true)
    {
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
