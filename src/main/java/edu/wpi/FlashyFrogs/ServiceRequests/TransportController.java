package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.Sound;
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
import javafx.scene.control.*;
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
public class TransportController implements IController {
  @FXML Rectangle check2;
  @FXML Rectangle check1;
  @FXML Pane toast;
  @FXML MFXButton AV;
  @FXML MFXButton equipmentButton;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;

  @FXML TextField patient;
  @FXML SearchableComboBox<InternalTransport.VisionStatus> vision;
  @FXML SearchableComboBox<InternalTransport.HearingStatus> hearing;
  @FXML SearchableComboBox<InternalTransport.ConsciousnessStatus> consciousness;
  @FXML SearchableComboBox<InternalTransport.HealthStatus> condition;
  @FXML SearchableComboBox<LocationName> to;
  @FXML SearchableComboBox<LocationName> from;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML SearchableComboBox<InternalTransport.Equipment> equipment;
  @FXML DatePicker date;
  @FXML SearchableComboBox<InternalTransport.ModeOfTransport> mode;
  @FXML CheckBox isolation;
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
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    to.setItems(FXCollections.observableArrayList(locations));
    from.setItems(FXCollections.observableArrayList(locations));
    vision.setItems(FXCollections.observableArrayList(InternalTransport.VisionStatus.values()));
    hearing.setItems(FXCollections.observableArrayList(InternalTransport.HearingStatus.values()));
    consciousness.setItems(
        FXCollections.observableArrayList(InternalTransport.ConsciousnessStatus.values()));
    condition.setItems(FXCollections.observableArrayList(InternalTransport.HealthStatus.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    equipment.setItems(FXCollections.observableArrayList(InternalTransport.Equipment.values()));
    mode.setItems(FXCollections.observableArrayList(InternalTransport.ModeOfTransport.values()));

    urgency.setButtonCell(
        new ListCell<ServiceRequest.Urgency>() {
          @Override
          protected void updateItem(ServiceRequest.Urgency item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Urgency");
            } else {
              setText(item.toString());
            }
          }
        });

    to.setButtonCell(
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

    from.setButtonCell(
        new ListCell<LocationName>() {
          @Override
          protected void updateItem(LocationName item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Transfer From");
            } else {
              setText(item.toString());
            }
          }
        });

    vision.setButtonCell(
        new ListCell<InternalTransport.VisionStatus>() {
          @Override
          protected void updateItem(InternalTransport.VisionStatus item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Vision");
            } else {
              setText(item.toString());
            }
          }
        });
    hearing.setButtonCell(
        new ListCell<InternalTransport.HearingStatus>() {
          @Override
          protected void updateItem(InternalTransport.HearingStatus item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Hearing");
            } else {
              setText(item.toString());
            }
          }
        });
    consciousness.setButtonCell(
        new ListCell<InternalTransport.ConsciousnessStatus>() {
          @Override
          protected void updateItem(InternalTransport.ConsciousnessStatus item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Consciousness");
            } else {
              setText(item.toString());
            }
          }
        });
    condition.setButtonCell(
        new ListCell<InternalTransport.HealthStatus>() {
          @Override
          protected void updateItem(InternalTransport.HealthStatus item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Condition");
            } else {
              setText(item.toString());
            }
          }
        });

    equipment.setButtonCell(
        new ListCell<InternalTransport.Equipment>() {
          @Override
          protected void updateItem(InternalTransport.Equipment item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Equipment");
            } else {
              setText(item.toString());
            }
          }
        });

    mode.setButtonCell(
        new ListCell<InternalTransport.ModeOfTransport>() {
          @Override
          protected void updateItem(InternalTransport.ModeOfTransport item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Mode of Transfer");
            } else {
              setText(item.toString());
            }
          }
        });
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
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
          || personal.getText().equals("")
          || reason.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfTransport =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      InternalTransport transport =
          new InternalTransport(
              patient.getText(),
              vision.getValue(),
              hearing.getValue(),
              consciousness.getValue(),
              condition.getValue(),
              from.getValue(),
              to.getValue(),
              urgency.getValue(),
              equipment.getValue(),
              dateOfTransport,
              Date.from(Instant.now()),
              CurrentUserEntity.CURRENT_USER.getCurrentUser(),
              mode.getValue(),
              isolation.isSelected(),
              personal.getText(),
              reason.getText());

      try {
        session.persist(transport);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#012D5A"));
        errorMessage.setText("Successfully submitted.");
        toastAnimation();
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#b6000b"));
        errorMessage.setText("Please fill all fields.");
        session.close();
        Sound.ERROR.play();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#b6000b"));
      errorMessage.setText("Please fill all fields.");
      session.close();
      Sound.ERROR.play();
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
    isolation.setSelected(false);
    personal.setText("");
    reason.setText("");
    date.valueProperty().set(null);
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleEquipment(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "EquipmentTransport");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ComputerService");
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
    if (!hDone) {
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
    } else if (hDone) {
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

  public void toastAnimation() {
    // Create a TranslateTransition to move the first rectangle to the left
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), toast);
    translate1.setByX(-280.0);
    translate1.setAutoReverse(true);
    check1.setFill(Color.web("#012D5A"));
    check2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), check1, Color.web("#012D5A"), Color.web("#F6BD38"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), check2, Color.web("#012D5A"), Color.web("#F6BD38"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), toast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.play();
  }

  @Override
  public void onClose() {}
}
