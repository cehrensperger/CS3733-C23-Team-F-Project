package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ORM.User;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

@GeneratedExclusion
public class TransportEditor implements IController {
  @FXML TextField patient;
  @FXML SearchableComboBox<InternalTransport.VisionStatus> vision;
  @FXML SearchableComboBox<InternalTransport.HearingStatus> hearing;
  @FXML SearchableComboBox<InternalTransport.ConsciousnessStatus> consciousness;
  @FXML SearchableComboBox<InternalTransport.HealthStatus> condition;
  @FXML SearchableComboBox<User> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
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

    List<User> users = session.createQuery("FROM User", User.class).getResultList();

    users.sort(Comparator.comparing(User::getFirstName));

    to.setItems(FXCollections.observableArrayList(locations));
    from.setItems(FXCollections.observableArrayList(locations));
    vision.setItems(FXCollections.observableArrayList(InternalTransport.VisionStatus.values()));
    hearing.setItems(FXCollections.observableArrayList(InternalTransport.HearingStatus.values()));
    consciousness.setItems(
        FXCollections.observableArrayList(InternalTransport.ConsciousnessStatus.values()));
    condition.setItems(FXCollections.observableArrayList(InternalTransport.HealthStatus.values()));
    assignedBox.setItems(FXCollections.observableArrayList(users));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    equipment.setItems(FXCollections.observableArrayList(InternalTransport.Equipment.values()));
    mode.setItems(FXCollections.observableArrayList(InternalTransport.ModeOfTransport.values()));
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
              CurrentUserEntity.CURRENT_USER.getCurrentuser(),
              mode.getValue(),
              isolation.isSelected(),
              personal.getText(),
              reason.getText());

      try {
        session.persist(transport);
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

  @Override
  public void onClose() {}
}
