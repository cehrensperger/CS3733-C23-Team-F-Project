package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;

public class HoldSanitationController {

    @FXML
    MFXButton AV;
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

        type.getItems().addAll("Lobby", "Waiting Room", "Patient Room", "Hallway", "Stairway", "Elevator", "Other");
        sanitationType.getItems().addAll("Sweeping", "Mopping", "Sanitizing");
        urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
        isolation.getItems().addAll("Yes", "No");
        biohazard.getItems().addAll("Yes", "No");

    }

    public void handleSubmit(ActionEvent actionEvent) throws IOException {
        //
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
            hDone = false;
        }

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

}
