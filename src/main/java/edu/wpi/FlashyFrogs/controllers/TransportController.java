package edu.wpi.FlashyFrogs.controllers;

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
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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

    currentLocationComboBox
        .getItems()
        .addAll("Intensive Care Unit", "Emergency Room", "Operating Room");
    newLocationComboBox.getItems().addAll("Intesive Care Unit", "Emergency Room", "Operating Room");
    departmentComboBox.getItems().addAll("Cardiology", "Radiology", "Trauma Unit");
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    System.out.println("clear button was clicked");
    System.out.println(this.logData() ? "Data logged" : "Data was not logged");
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
    System.out.println("Submit Button was Clicked");
    System.out.println(this.logData() ? "Data logged" : "Data was not logged");
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

  /**
   * When the button is clicked, the method will log the data in the terminal and database
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void buttonClicked(ActionEvent actionEvent) throws IOException {
    System.out.println("Button was clicked");
    System.out.println(this.logData() ? "Data logged" : "Data NOT logged");
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

  /**
   * generates a table to store button click information
   *
   * @return true when table is successfully created or already exists, false otherwise
   */
  private boolean createTable() {

    boolean table_exists = false;

    if (this.connection != null) {
      String createQuery =
          "CREATE TABLE APP.buttonClicks("
              + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
              + "btn_name VARCHAR(50), "
              + "time_stamp TIMESTAMP NOT NULL, "
              + "PRIMARY KEY(id) )";
      try {
        Statement statement = this.connection.createStatement();
        statement.execute(createQuery);

        table_exists = true;
      } catch (SQLException e) {
        // Error code 955 is "name is already used by an existing object", so this table name
        // already exists
        if (e.getErrorCode() == 955 || e.getMessage().contains("already exists"))
          table_exists = true;
        else e.printStackTrace();
      }
    }
    return table_exists;
  }

  /**
   * Stores button click data to database
   *
   * @return true if data is stored successfully, false otherwise
   */
  private boolean logData() {
    if (connection != null) {
      String writeQuery =
          "INSERT INTO APP.buttonClicks(btn_name, time_stamp) VALUES ( 'ClickButton', CURRENT_TIMESTAMP ) ";
      try {
        Statement statement = this.connection.createStatement();
        statement.execute(writeQuery);
        return true;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return false;
  }
}
