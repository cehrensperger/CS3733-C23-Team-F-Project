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
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class MedicineDeliveryEditorController extends ServiceRequestController
    implements IController {

  @FXML MFXButton clear;
  @FXML MFXButton submit;

  @FXML TextField reason;
  @FXML TextField medicine;
  @FXML TextField dosage;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML DatePicker date;
  @FXML TextField patient;
  @FXML SearchableComboBox<LocationName> locationofPatient;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;

  private MedicineDelivery tpReq = new MedicineDelivery();
  PopOver popOver;

  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;

  public void initialize() {

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<HospitalUser> users =
        session.createQuery("FROM HospitalUser", HospitalUser.class).getResultList();

    users.sort(Comparator.comparing(HospitalUser::getFirstName));

    locationofPatient.setItems(FXCollections.observableArrayList(locations));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    assignedBox.setItems(FXCollections.observableArrayList(users));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    session.close();
  }

  public void updateFields() {
    patient.setText(tpReq.getPatientID());
    locationofPatient.setValue(tpReq.getLocation());
    reason.setText(tpReq.getReason());
    medicine.setText(tpReq.getMedicine());
    dosage.setText(dosage.getText());
    urgency.setValue(tpReq.getUrgency());
    statusBox.setValue(tpReq.getStatus());
    date.setValue(
        Instant.ofEpochMilli(tpReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());

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
    System.out.println("here");
    try {
      // check
      if (patient.getText().equals("")
          || locationofPatient.getValue().toString().equals("")
          || reason.getText().equals("")
          || medicine.getText().equals("")
          || date.getValue().toString().equals("")
          || urgency.getValue().toString().equals("")
          || dosage.getText().equals("")) {
        System.out.println("here2");
        throw new NullPointerException();
      }

      Date dateNeeded = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      tpReq.setPatientID(patient.getText());
      tpReq.setLocation(locationofPatient.getValue());
      tpReq.setReason(reason.getText());
      tpReq.setAssignedEmp(assignedBox.getValue());
      tpReq.setUrgency(urgency.getValue());
      tpReq.setStatus(statusBox.getValue());
      tpReq.setMedicine(medicine.getText());
      tpReq.setDosage(Double.parseDouble(dosage.getText()));
      tpReq.setDate(dateNeeded);

      try {
        session.merge(tpReq);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        popOver.hide();
      } catch (RollbackException exception) {
        session.clear();
        session.close();
        Sound.ERROR.play();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      session.close();
      Sound.ERROR.play();
    }
  }

  @Override
  public void setRequest(ServiceRequest request) {
    tpReq = (MedicineDelivery) request;
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    patient.setText("");
    locationofPatient.valueProperty().set(null);
    reason.setText("");
    assignedBox.valueProperty().set(null);
    statusBox.valueProperty().set(null);
    medicine.setText("");
    dosage.setText("");
    urgency.valueProperty().set(null);
    date.valueProperty().set(null);
  }

  @Override
  protected void handleBack(ActionEvent event) throws IOException {}

  public void help() {
    if (!hDone) {
      hDone = true;
    } else if (hDone) {
      hDone = false;
    }
  }

  public void onClose() {}
}
