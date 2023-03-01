package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
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
public class SanitationEditorController extends ServiceRequestController implements IController {

  @FXML Pane errtoast;
  @FXML Rectangle errcheck2;
  @FXML Rectangle errcheck1;
  @FXML Rectangle check2;
  @FXML Rectangle check1;
  @FXML Pane toast;

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
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<Sanitation.SanitationType> sanitationType;
  @FXML DatePicker date;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML CheckBox isolation;
  @FXML SearchableComboBox<Sanitation.BiohazardLevel> biohazard;
  @FXML TextField description;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
  boolean hDone = false;
  @FXML private Label errorMessage;
  private Connection connection = null;

  private Sanitation sanitationReq = new Sanitation();
  PopOver popOver;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    h7.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();
    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<HospitalUser> users =
        session.createQuery("SELECT u FROM HospitalUser u", HospitalUser.class).getResultList();
    users.sort(Comparator.comparing(HospitalUser::getFirstName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    sanitationType.setItems(FXCollections.observableArrayList(Sanitation.SanitationType.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    biohazard.setItems(FXCollections.observableArrayList(Sanitation.BiohazardLevel.values()));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    assignedBox.setItems(FXCollections.observableArrayList(users));

    session.close();
  }

  @Override
  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void updateFields() {
    locationBox.setValue(sanitationReq.getLocation());
    sanitationType.setValue(sanitationReq.getType());
    urgency.setValue(sanitationReq.getUrgency());
    biohazard.setValue(sanitationReq.getBiohazard());
    statusBox.setValue(sanitationReq.getStatus());
    date.setValue(
        Instant.ofEpochMilli(sanitationReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    isolation.setSelected(sanitationReq.getIsolation());
    description.setText(sanitationReq.getDescription());
    if (sanitationReq.getAssignedEmp() != null) {
      assignedBox.setValue(sanitationReq.getAssignedEmp());
    }
  }

  public void setRequest(ServiceRequest serviceRequest) {
    sanitationReq = (Sanitation) serviceRequest;
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      if (locationBox.getValue().toString().equals("")
          || sanitationType.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || biohazard.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfIncident =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      sanitationReq.setDescription(description.getText());
      sanitationReq.setIsolation(isolation.isSelected());
      sanitationReq.setBiohazard(biohazard.getValue());
      sanitationReq.setAssignedEmp(assignedBox.getValue());
      sanitationReq.setUrgency(urgency.getValue());
      sanitationReq.setDate(dateOfIncident);
      sanitationReq.setType(sanitationType.getValue());
      sanitationReq.setStatus(statusBox.getValue());
      sanitationReq.setLocation(locationBox.getValue());

      try {
        session.merge(sanitationReq);
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

  public void handleClear(ActionEvent actionEvent) throws IOException {
    locationBox.valueProperty().set(null);
    sanitationType.valueProperty().set(null);
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    isolation.setSelected(false);
    biohazard.valueProperty().set(null);
    description.setText("");
  }

  public void handleDelete(ActionEvent event) {
    Session session = CONNECTION.getSessionFactory().openSession();

    session.beginTransaction();
    session
        .createMutationQuery("DELETE FROM Sanitation WHERE id=:ID")
        .setParameter("ID", sanitationReq.getId())
        .executeUpdate();
    session.getTransaction().commit();
    session.close();

    popOver.hide();
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
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      h7.setVisible(false);
      hDone = false;
    }
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
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
