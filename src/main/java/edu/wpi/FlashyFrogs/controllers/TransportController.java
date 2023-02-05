package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
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

public class TransportController extends ServiceRequestController {
  @FXML MFXTextField firstNameTextfield; // ID of the first name text field
  @FXML MFXTextField lastNameTextfield;
  @FXML MFXTextField middleNameTextfield;
  @FXML MFXDatePicker dateOfBirthDatePicker;
  @FXML MFXComboBox currentLocationComboBox;
  @FXML MFXComboBox newLocationComboBox;
  @FXML MFXDatePicker dateOfTransportDatePicker;
  @FXML MFXComboBox departmentComboBox;
  @FXML MFXButton clearButton;
  @FXML MFXButton submitButton;
  @FXML MFXButton backButton;
  @FXML MFXComboBox urgency;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox department2;
  @FXML private MFXButton allButton;
  @FXML private Label errorMessage;

  private Connection connection = null; // connection to database

  /** Method run when controller is initializes */
  public void initialize() {

    Session session = factory.openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    newLocationComboBox.setItems(FXCollections.observableList(objects));
    currentLocationComboBox.setItems(FXCollections.observableList(objects));
    session.close();

    newLocationComboBox.getItems().addAll("Intesive Care Unit", "Emergency Room", "Operating Room");
    departmentComboBox.getItems().addAll("Cardiology", "Radiology", "Trauma Unit");
    department2.getItems().addAll("Cardiology", "Radiology", "Trauma Unit");
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    firstNameTextfield.clear();
    lastNameTextfield.clear();
    middleNameTextfield.clear();
    dateOfBirthDatePicker.clear();
    currentLocationComboBox.clear();
    newLocationComboBox.clear();
    dateOfTransportDatePicker.clear();
    first2.clear();
    last2.clear();
    middle2.clear();
    departmentComboBox.clear();
    first2.clear();
    middle2.clear();
    last2.clear();
    department2.clear();
  }

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("AllTransport");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts = {};
      String departmentEnumString = departmentComboBox.getText().toUpperCase();
      String departmentEnumString2 = department2.getText().toUpperCase();
      parts = urgency.getText().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (firstNameTextfield.getText().equals("")
          || middleNameTextfield.getText().equals("")
          || lastNameTextfield.getText().equals("")
          || first2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")
          || departmentComboBox.getText().equals("")
          || dateOfTransportDatePicker.getText().equals("")
          || dateOfBirthDatePicker.getText().equals("")
          || currentLocationComboBox.getText().equals("")
          || newLocationComboBox.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfTransport =
          Date.from(
              dateOfTransportDatePicker
                  .getValue()
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant());
      Date dateOfBirth =
          Date.from(
              dateOfBirthDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      InternalTransport transport = new InternalTransport();
      transport.setEmpFirstName(firstNameTextfield.getText());
      transport.setEmpMiddleName(middleNameTextfield.getText());
      transport.setEmpLastName(lastNameTextfield.getText());
      transport.setAssignedEmpFirstName(first2.getText());
      transport.setAssignedEmpMiddleName(middle2.getText());
      transport.setAssignedEmpLastName(last2.getText());
      transport.setEmpDept(ServiceRequest.EmpDept.valueOf(departmentEnumString));
      transport.setAssignedEmpDept(ServiceRequest.EmpDept.valueOf(departmentEnumString2));
      transport.setDateOfBirth(dateOfBirth);
      transport.setDateOfIncident(dateOfTransport);
      transport.setDateOfSubmission(Date.from(Instant.now()));
      transport.setUrgency(ServiceRequest.Urgency.valueOf(urgencyEnumString));
      transport.setNewLoc(session.find(LocationName.class, newLocationComboBox.getText()));
      transport.setNewLoc(session.find(LocationName.class, currentLocationComboBox.getText()));

      try {
        session.persist(transport);
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

  /**
   * Generates connection to server on localhost at default port (1521) be aware of the username and
   * password when testing
   *
   * @return True when connection is successful, False when failed
   */
}
