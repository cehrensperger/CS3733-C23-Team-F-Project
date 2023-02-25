package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
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
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class MedicineController implements IController {

    @FXML
    TextField personal1;
    @FXML
    TextField medicine;
    @FXML
    TextField dosage;
    @FXML
    SearchableComboBox urgency;
    @FXML
    DatePicker date;
    @FXML
    Label errorMessage;
    @FXML
    TextField patient;
    @FXML
    SearchableComboBox locationofPatient;
    @FXML
    Rectangle check2;
    @FXML
    Rectangle check1;
    @FXML
    Pane toast;
    @FXML
    MFXButton clear;
    @FXML
    MFXButton submit;
    @FXML
    MFXButton credits;
    @FXML
    MFXButton back;
    @FXML
    MFXButton AV;
    @FXML
    MFXButton IT;
    @FXML
    MFXButton IPT;
    @FXML
    MFXButton sanitation;
    @FXML
    MFXButton security;
    @FXML
    Text h1;
    @FXML
    Text h2;
    @FXML
    Text h3;
    @FXML
    Text h4;
    @FXML
    Text h5;
    @FXML
    Text h6;
    @FXML
    Text h7;

    // boolean hDone = false;
    public void initialize() {
        h1.setVisible(false);
        h2.setVisible(false);
        h3.setVisible(false);
        h4.setVisible(false);
        h5.setVisible(false);
        h6.setVisible(false);
        h7.setVisible(false);
    }

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
            session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));

    locationofPatient.setButtonCell(
            new ListCell<LocationName>() {
        @Override
        protected void updateItem(LocationName item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText("Transfer To");
            } else {
                setText(item.toString());
            }
        }
    });

    urgency.setButtonCell(
            new ListCell<ServiceRequest.Urgency>() {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText("Urgency");
            } else {
                setText(item.toString());
            }
        }
    });

}



