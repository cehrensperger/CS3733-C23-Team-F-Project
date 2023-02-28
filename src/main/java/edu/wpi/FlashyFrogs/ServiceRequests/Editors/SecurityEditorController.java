package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
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
public class SecurityEditorController extends ServiceRequestController implements IController {

  @FXML Pane errtoast;
  @FXML Rectangle errcheck2;
  @FXML Rectangle errcheck1;
  @FXML Rectangle check2;
  @FXML Rectangle check1;
  @FXML Pane toast;
  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
  @FXML SearchableComboBox<Security.ThreatType> threat;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML DatePicker date;
  @FXML TextField description;
  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;
  private Security secReq = new Security();
  PopOver popOver;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<HospitalUser> users =
        session.createQuery("FROM HospitalUser", HospitalUser.class).getResultList();

    users.sort(Comparator.comparing(HospitalUser::getFirstName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    assignedBox.setItems(FXCollections.observableArrayList(users));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    threat.setItems(FXCollections.observableArrayList(Security.ThreatType.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    session.close();
  }

  public void updateFields() {
    locationBox.setValue(secReq.getLocation());
    urgency.setValue(secReq.getUrgency());
    statusBox.setValue(secReq.getStatus());
    date.setValue(
        Instant.ofEpochMilli(secReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    threat.setValue(secReq.getThreatType());
    statusBox.setValue(secReq.getStatus());
    description.setText(secReq.getIncidentReport());
    if (secReq.getAssignedEmp() != null) {
      assignedBox.setValue(secReq.getAssignedEmp());
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
      if (locationBox.getValue().toString().equals("")
          || threat.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfRequest =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      secReq.setIncidentReport(description.getText());
      secReq.setAssignedEmp(assignedBox.getValue());
      secReq.setUrgency(urgency.getValue());
      secReq.setStatus(statusBox.getValue());
      secReq.setLocation(locationBox.getValue());
      secReq.setThreatType(threat.getValue());
      secReq.setDate(dateOfRequest);

      try {
        session.merge(secReq);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        toastAnimation();
        Sound.SUBMITTED.play();
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
    secReq = (Security) request;
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    locationBox.valueProperty().set(null);
    threat.valueProperty().set(null);
    urgency.valueProperty().set(null);
    date.valueProperty().set(null);
    description.setText("");
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
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
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
