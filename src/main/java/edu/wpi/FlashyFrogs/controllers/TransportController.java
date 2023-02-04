package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.SubmitInfo;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.hibernate.Session;

public class TransportController extends ServiceRequestController {
  @FXML MFXTextField firstNameTextfield; // ID of the first name text field
  @FXML MFXTextField lastNameTextfield;
  @FXML MFXTextField middleNameTextfield;
  @FXML MFXDatePicker dateOfBirthDatePicker;
  @FXML MFXComboBox currentLocationComboBox;
  @FXML MFXComboBox newLocationComboBox;
  @FXML MFXDatePicker dateOfTransportDatePicker;
  @FXML MFXTextField firstNameTextfield2;
  @FXML MFXTextField lastNameTextfield2;
  @FXML MFXTextField middleNameTextfield2;
  @FXML MFXComboBox departmentComboBox;
  @FXML MFXButton clearButton;
  @FXML MFXButton submitButton;
  @FXML MFXButton backButton;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox department2;
  @FXML private MFXButton allButton;

  private Connection connection = null; // connection to database

  private SubmitInfo submitInfo;

  /** Method run when controller is initializes */
  public void initialize() {
    // if connection is successful
    //    if (this.connectToDB()) {
    //      this.createTable();
    //    }

    submitInfo = new SubmitInfo();

    Session session = CONNECTION.getSessionFactory().openSession();
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
    firstNameTextfield2.clear();
    lastNameTextfield2.clear();
    middleNameTextfield2.clear();
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
    //    System.out.println("Submit Button was Clicked");
    //    System.out.println(this.logData() ? "Data logged" : "Data was not logged");
    submitInfo.setPatientFirstName(firstNameTextfield.getText());
    submitInfo.setPatientLastName(lastNameTextfield.getText());
    submitInfo.setPatientMiddleName(middleNameTextfield.getText());
    submitInfo.setDOB(dateOfBirthDatePicker.getText());
    submitInfo.setCurrentLocationInfo(currentLocationComboBox.getText());
    submitInfo.setNewLocationInfo(newLocationComboBox.getText());
    submitInfo.setDOT(dateOfTransportDatePicker.getText()); // Date of Transport
    submitInfo.setEmployeeFirstName(firstNameTextfield2.getText());
    submitInfo.setEmployeeLastName(lastNameTextfield2.getText());
    submitInfo.setEmployeeMiddleName(middleNameTextfield2.getText());
    submitInfo.setEmployeeDepartment(departmentComboBox.getText());
    System.out.println(submitInfo.getDOB());
    System.out.println(submitInfo.getPatientMiddleName());

    //    Session session = factory.openSession();
    //    Transaction transaction = session.beginTransaction();
    //    InternalTransport transportRequest =
    //        new InternalTransport(
    //            //            new Date(),
    //            //            session.find(LocationName.class, newLocationComboBox.getText()),
    //            //            session.find(LocationName.class, currentLocationComboBox.getText()),
    //
    //            );
    // securityRequest.setLocation(locationEntry.getText());

    //    transportRequest.setType("Transport");//transport no
    //    // longer has a type, to get "Transport" do Class.simpleName()

    //    transportRequest.setEmpFirstName(firstNameTextfield.getText());
    //    transportRequest.setEmpMiddleName(middleNameTextfield.getText());
    //    transportRequest.setEmpLastName(lastNameTextfield.getText());
    //    transportRequest.setDateOfSubmission(Date.from(Instant.now()));
    //
    //    session.persist(transportRequest);
    //    transaction.commit();
    //    session.close();
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }
}
