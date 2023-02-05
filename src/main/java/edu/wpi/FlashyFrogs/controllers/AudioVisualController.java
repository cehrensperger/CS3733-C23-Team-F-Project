package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.AudioVisual;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AudioVisualController extends ServiceRequestController {

  @FXML private MFXTextField patientFirst;
  @FXML private MFXTextField patientMiddle;
  @FXML private MFXTextField patientLast;
  @FXML private MFXDatePicker dateOfBirthEntry;
  @FXML private MFXComboBox locationEntry;
  @FXML private MFXComboBox accommodationEntry;
  @FXML private MFXComboBox urgencyEntry;
  @FXML private MFXTextField empFirst;
  @FXML private MFXTextField empMiddle;
  @FXML private MFXTextField empLast;
  @FXML private MFXComboBox empDeptEntry;
  @FXML private MFXTextField assignedEmpFirst;
  @FXML private MFXTextField assignedEmpMiddle;
  @FXML private MFXTextField assignedEmpLast;
  @FXML private MFXComboBox assignedEmpDeptEntry;
  @FXML private Label errorMessage;

  public void initialize() {
    ServiceRequest.EmpDept[] depts = ServiceRequest.EmpDept.values();
    for (int i = 0; i < depts.length; i++) {
      empDeptEntry.getItems().add(depts[i].toString().replace("_", " ").toLowerCase());
      assignedEmpDeptEntry.getItems().add(depts[i].toString().replace("_", " ").toLowerCase());
    }

    AudioVisual.AccommodationType[] accommodations = AudioVisual.AccommodationType.values();
    System.out.println(accommodations.length);
    for (int i = 0; i < accommodations.length; i++) {
      accommodationEntry
          .getItems()
          .add(accommodations[i].toString().replace("_", " ").toLowerCase());
    }

    ServiceRequest.Urgency[] urgencies = ServiceRequest.Urgency.values();
    System.out.println(urgencies.length);
    for (int i = 0; i < urgencies.length; i++) {
      urgencyEntry.getItems().add(urgencies[i].toString().replace("_", " ").toLowerCase());
    }

    Session session = factory.openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();
    session.close();
    locationEntry.setItems(FXCollections.observableList(objects));
  }

  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    // Fapp.setScene("AllAudioVisualRequest");
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    patientFirst.clear();
    patientMiddle.clear();
    patientLast.clear();
    dateOfBirthEntry.clear();
    locationEntry.clear();
    accommodationEntry.clear();
    urgencyEntry.clear();
    empFirst.clear();
    empMiddle.clear();
    empLast.clear();
    empDeptEntry.clear();
    assignedEmpFirst.clear();
    assignedEmpMiddle.clear();
    assignedEmpLast.clear();
    assignedEmpDeptEntry.clear();
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts = {};
      String departmentEnumString = empDeptEntry.getText().toUpperCase();
      parts = urgencyEntry.getText().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (patientFirst.getText().equals("")
          || patientMiddle.getText().equals("")
          || patientLast.getText().equals("")
          || dateOfBirthEntry.getText().equals("")
          || locationEntry.getText().equals("")
          || accommodationEntry.getText().equals("")
          || urgencyEntry.getText().equals("")
          || empFirst.getText().equals("")
          || empMiddle.getText().equals("")
          || empLast.getText().equals("")
          || empDeptEntry.getText().equals("")
          || assignedEmpFirst.getText().equals("")
          || assignedEmpMiddle.getText().equals("")
          || assignedEmpLast.getText().equals("")
          || assignedEmpDeptEntry.getText().equals("")) {
        throw new NullPointerException();
      }

      String departmentEnumString2 = assignedEmpDeptEntry.getText().toUpperCase();
      // String requestTypeEnumString = requestTypeDropDown.getText().toUpperCase();

      Date dateOfIncident = Date.from(Instant.now());
      Date dateOfBirth =
          Date.from(dateOfBirthEntry.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
      AudioVisual audioVisual =
          new AudioVisual(
              empFirst.getText(),
              empMiddle.getText(),
              empLast.getText(),
              assignedEmpFirst.getText(),
              assignedEmpMiddle.getText(),
              assignedEmpLast.getText(),
              AudioVisual.EmpDept.valueOf(departmentEnumString),
              AudioVisual.EmpDept.valueOf(departmentEnumString2),
              dateOfIncident,
              Date.from(Instant.now()),
              AudioVisual.Urgency.valueOf(urgencyEnumString),
              AudioVisual.AccommodationType.valueOf(
                  accommodationEntry.getText().replace(" ", "_").toUpperCase()),
              patientFirst.getText(),
              patientMiddle.getText(),
              patientLast.getText(),
              session.find(LocationName.class, locationEntry.getText()),
              dateOfBirth);
      try {
        session.persist(audioVisual);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(Paint.valueOf("#44ff00"));
        errorMessage.setText("Successfully submitted.");
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(Paint.valueOf("#ff0000"));
        errorMessage.setText("Please fill all fields.");
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#ff0000"));
      errorMessage.setText("Please fill all fields.");
      session.close();
    }
  }
}
