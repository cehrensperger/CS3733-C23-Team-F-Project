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
import javafx.scene.paint.Paint;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class ReligiousEditorController extends ServiceRequestController implements IController {

  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML Label errorMessage;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
  @FXML TextField patient;
  @FXML SearchableComboBox<LocationName> locationofPatient;
  @FXML TextField religion;
  @FXML TextField requestDescription;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML DatePicker serviceDate;

  boolean hDone = false;
  private Connection connection = null;
  private Religion relReq = new Religion();
  PopOver popOver;

  public void initialize() {

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<HospitalUser> users =
        session.createQuery("FROM HospitalUser", HospitalUser.class).getResultList();

    users.sort(Comparator.comparing(HospitalUser::getFirstName));

    locationofPatient.setItems(FXCollections.observableArrayList(locations));
    assignedBox.setItems(FXCollections.observableArrayList(users));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    session.close();
  }

  public void updateFields() {
    patient.setText(relReq.getPatientID());
    locationofPatient.setValue(relReq.getLocation());
    assignedBox.setValue(relReq.getAssignedEmp());
    urgency.setValue(relReq.getUrgency());
    serviceDate.setValue(
        Instant.ofEpochMilli(relReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    requestDescription.setText(relReq.getDescription());
    statusBox.setValue(relReq.getStatus());
    religion.setText(relReq.getReligion());
    if (relReq.getAssignedEmp() != null) {
      assignedBox.setValue(relReq.getAssignedEmp());
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
      if (locationofPatient.getValue().toString().equals("")
          || patient.getText().equals("")
          || religion.getText().equals("")
          || serviceDate.getValue().toString().equals("")
          || requestDescription.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfRequest =
          Date.from(serviceDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      relReq.setPatientID(patient.getText());
      relReq.setLocation(locationofPatient.getValue());
      relReq.setAssignedEmp(assignedBox.getValue());
      relReq.setDate(dateOfRequest);
      relReq.setStatus(statusBox.getValue());
      relReq.setReligion(religion.getText());
      relReq.setDescription(requestDescription.getText());
      relReq.setUrgency(urgency.getValue());

      try {
        session.merge(relReq);
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
    relReq = (Religion) request;
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    locationofPatient.valueProperty().set(null);
    urgency.valueProperty().set(null);
    serviceDate.valueProperty().set(null);
    religion.setText("");
    patient.setText("");
    requestDescription.setText("");
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

  @Override
  public void onClose() {}
}
