package edu.wpi.FlashyFrogs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;

public class HoldSanitationController {
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
    @FXML SearchableComboBox sanitation;
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
        sanitation.getItems().addAll("Sweeping", "Mopping", "Sanitizing");
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
        sanitation.valueProperty().set(null);
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

}
