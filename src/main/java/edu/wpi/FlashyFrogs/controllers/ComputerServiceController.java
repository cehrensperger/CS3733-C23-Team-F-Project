package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.ComputerService;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ComputerServiceController extends ServiceRequestController {

  @FXML private MFXComboBox deviceType;
  @FXML private MFXTextField deviceModel;
  @FXML private MFXComboBox accommodationType;
  @FXML private MFXTextField issueDescription;
  @FXML private MFXComboBox urgencyEntry;
  @FXML private MFXTextField firstEntry;
  @FXML private MFXTextField middleEntry;
  @FXML private MFXTextField lastEntry;
  @FXML private MFXComboBox departmentEntry;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox department2;
  @FXML private Label errorMessage;

  @FXML
  public void initialize() {}

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    // Fapp.setScene("AllComputerService");
  }

  @FXML
  public void handleClear(ActionEvent actionEvent) throws IOException {
    deviceType.clear();
    deviceModel.clear();
    accommodationType.clear();
    issueDescription.clear();
    urgencyEntry.clear();
    firstEntry.clear();
    middleEntry.clear();
    lastEntry.clear();
    departmentEntry.clear();
    first2.clear();
    middle2.clear();
    last2.clear();
    department2.clear();
  }

  @FXML
  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts = {};
      String departmentEnumString = departmentEntry.getText().toUpperCase();
      parts = urgencyEntry.getText().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (deviceType.getText().equals("")
          || deviceModel.getText().equals("")
          || accommodationType.getText().equals("")
          || issueDescription.getText().equals("")
          || urgencyEntry.getText().equals("")
          || firstEntry.getText().equals("")
          || middleEntry.getText().equals("")
          || lastEntry.getText().equals("")
          || departmentEntry.getText().equals("")
          || first2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")) {
        throw new NullPointerException();
      }

      String departmentEnumString2 = department2.getText().toUpperCase().replace(" ", "_");
      String deviceTypeEnumString = deviceType.getText().toUpperCase().replace(" ", "_");
      String serviceTypeEnumString =
          accommodationType.getTypeSelector().toUpperCase().replace(" ", "_");
      // Date dateOfIncident =
      //        Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      ComputerService sanitationRequest =
          new ComputerService(
              firstEntry.getText(),
              middleEntry.getText(),
              lastEntry.getText(),
              first2.getText(),
              middle2.getText(),
              last2.getText(),
              ComputerService.EmpDept.valueOf(departmentEnumString),
              ComputerService.EmpDept.valueOf(departmentEnumString2),
              Date.from(Instant.now()),
              Date.from(Instant.now()),
              ServiceRequest.Urgency.valueOf(urgencyEnumString),
              ComputerService.DeviceType.valueOf(deviceTypeEnumString),
              deviceModel.getText(),
              issueDescription.getText(),
              ComputerService.ServiceType.valueOf(serviceTypeEnumString));
      try {
        session.persist(sanitationRequest);
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

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }
}
