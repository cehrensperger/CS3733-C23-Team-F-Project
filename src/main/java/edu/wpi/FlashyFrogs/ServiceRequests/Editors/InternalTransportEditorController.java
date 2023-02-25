package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class InternalTransportEditorController extends ServiceRequestController
    implements IController {
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
        popOver.hide();
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(Paint.valueOf("#b6000b"));
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

  @Override
  public void onClose() {}
}
