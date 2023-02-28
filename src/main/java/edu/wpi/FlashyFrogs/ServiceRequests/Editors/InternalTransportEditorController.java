package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ServiceRequests.ServiceRequestController;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class InternalTransportEditorController extends ServiceRequestController
    implements IController {
  @FXML Pane errtoast;
  @FXML Rectangle errcheck2;
  @FXML Rectangle errcheck1;
  @FXML Rectangle check2;
  @FXML Rectangle check1;
  @FXML Pane toast;
  @FXML MFXButton submit;
  @FXML TextField patient;
  @FXML SearchableComboBox<InternalTransport.VisionStatus> vision;
  @FXML SearchableComboBox<InternalTransport.HearingStatus> hearing;
  @FXML SearchableComboBox<InternalTransport.ConsciousnessStatus> consciousness;
  @FXML SearchableComboBox<InternalTransport.HealthStatus> condition;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
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
  private InternalTransport tpReq = new InternalTransport();
  PopOver popOver;

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

    List<HospitalUser> users =
        session.createQuery("FROM HospitalUser", HospitalUser.class).getResultList();

    users.sort(Comparator.comparing(HospitalUser::getFirstName));

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
    session.close();
  }

  public void updateFields() {
    to.setValue(tpReq.getTargetLocation());
    from.setValue(tpReq.getLocation());
    urgency.setValue(tpReq.getUrgency());
    statusBox.setValue(tpReq.getStatus());
    date.setValue(
        Instant.ofEpochMilli(tpReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    statusBox.setValue(tpReq.getStatus());
    reason.setText(tpReq.getReason());
    vision.setValue(tpReq.getVision());
    hearing.setValue(tpReq.getHearing());
    consciousness.setValue(tpReq.getConsciousness());
    condition.setValue(tpReq.getHealthStatus());
    equipment.setValue(tpReq.getEquipment());
    mode.setValue(tpReq.getMode());
    isolation.setSelected(tpReq.isIsolation());
    personal.setText(tpReq.getPersonalItems());
    patient.setText(tpReq.getPatientID());
    if (tpReq.getAssignedEmp() != null) {
      assignedBox.setValue(tpReq.getAssignedEmp());
    }
  }

  @Override
  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
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

      tpReq.setReason(reason.getText());
      tpReq.setAssignedEmp(assignedBox.getValue());
      tpReq.setUrgency(urgency.getValue());
      tpReq.setStatus(statusBox.getValue());
      tpReq.setLocation(from.getValue());
      tpReq.setPatientID(patient.getText());
      tpReq.setVision(vision.getValue());
      tpReq.setHearing(hearing.getValue());
      tpReq.setConsciousness(consciousness.getValue());
      tpReq.setHealthStatus(condition.getValue());
      tpReq.setLocation(from.getValue());
      tpReq.setTargetLocation(to.getValue());
      tpReq.setEquipment(equipment.getValue());
      tpReq.setDate(dateOfTransport);
      tpReq.setMode(mode.getValue());
      tpReq.setPersonalItems(personal.getText());
      tpReq.setIsolation(isolation.isSelected());

      try {
        session.merge(tpReq);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        toastAnimation();
      } catch (RollbackException exception) {
        session.clear();
        errortoastAnimation();
        session.close();
        Sound.ERROR.play();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errortoastAnimation();
      session.close();
      Sound.ERROR.play();
    }
  }

  @Override
  public void setRequest(ServiceRequest request) {
    tpReq = (InternalTransport) request;
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

  @Override
  protected void handleBack(ActionEvent event) throws IOException {}

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

  public void errortoastAnimation() {
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck1.setFill(Color.web("#012D5A"));
    errcheck2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck1, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck2, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            submit.setDisable(false);
          }
        });
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
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {

            popOver.hide();
          }
        });
  }

  @Override
  public void onClose() {}
}
