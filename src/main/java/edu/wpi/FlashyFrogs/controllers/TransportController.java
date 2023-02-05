package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.SubmitInfo;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

    submitInfo = new SubmitInfo();

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
  private boolean connectToDB() {

    try {
      Class.forName(
          "org.apache.derby.jdbc.ClientDriver"); // Check that proper driver is packaged for Apache
      // Derby
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("NO DRIVER");
      return false;
    }
    try {
      // create Connection at specified URL
      this.connection =
          DriverManager.getConnection(
              "jdbc:derby://localhost:1527/testDB;create=true",
              "app",
              "derbypass"); // This will change for each team as their DB is developed
      if (this.connection != null) {
        System.out.println("Connected to the database!");
      } else {
        System.out.println("Failed to make connection!");
      }
    } catch (SQLException e) {
      System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }

    // connection successful, return true
    return true;
  }
}
