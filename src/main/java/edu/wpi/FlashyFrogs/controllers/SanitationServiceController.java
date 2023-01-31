package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Main;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
import edu.wpi.FlashyFrogs.SanitationServiceData;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.sql.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SanitationServiceController extends ServiceRequestController {
  @FXML MFXButton clearButton; // fx:ID of the button in the ExampleFXML
  @FXML MFXButton submitButton;
  @FXML MFXButton backButton;
  @FXML MFXComboBox requestTypeDropDown;
  @FXML MFXComboBox locationDropDown;
  @FXML MFXDatePicker date;
  @FXML MFXTextField firstName;
  @FXML MFXTextField lastName;
  @FXML MFXTextField middleName;
  @FXML MFXTextField timeEntry;
  @FXML MFXComboBox departmentDropDown;

  @FXML MFXComboBox urgencyEntry;
  private Connection connection = null; // connection to database
  private SanitationServiceData sanitationServiceData;

  /** Method run when controller is initializes */
  public void initialize() {
    // if connection is successful
    //    if (this.connectToDB()) {
    //      this.createTable();
    //    }

    sanitationServiceData = new SanitationServiceData();
    urgencyEntry.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    requestTypeDropDown.getItems().addAll("Mopping", "Sweeping", "Vacuuming");
    locationDropDown.getItems().addAll("room 1", "room 2", "public space 1", "public space 2");

    // I don't really know what to put here
    departmentDropDown.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");
  }

  /**
   * clears all fields and drop downs
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void clearButtonClicked(ActionEvent actionEvent) throws IOException {
    //    System.out.println("clear button was clicked");
    //    System.out.println(this.logData() ? "Data logged" : "Data NOT logged");
    requestTypeDropDown.clear();
    locationDropDown.clear();
    date.clear();
    firstName.clear();
    lastName.clear();
    middleName.clear();
    departmentDropDown.clear();
    timeEntry.clear();
    urgencyEntry.clear();
  }

  /**
   * submits all fields by setting attributes of SubmitInfo instance
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void submitButtonClicked(ActionEvent actionEvent) throws IOException {
    //    System.out.println("Submit button was clicked");
    //    System.out.println(this.logData() ? "Data logged" : "Data NOT logged");
    sanitationServiceData.setDateInfo(date.getText());
    sanitationServiceData.setLocationInfo(locationDropDown.getText());
    sanitationServiceData.setRequestType(requestTypeDropDown.getText());
    sanitationServiceData.setEmployeeDepartment(departmentDropDown.getText());
    sanitationServiceData.setEmployeeFirstName(firstName.getText());
    sanitationServiceData.setEmployeeLastName(lastName.getText());
    sanitationServiceData.setEmployeeMiddleName(middleName.getText());
    addSanitationRequest(locationDropDown.getText(), requestTypeDropDown.getText());
    System.out.println(sanitationServiceData);
  }

  private void addSanitationRequest(String location, String type) {
    Session session = Main.factory.openSession();
    Transaction transaction = session.beginTransaction();
    Sanitation sanitationRequest = new Sanitation();
    sanitationRequest.setLocation(location);
    sanitationRequest.setType(type);

    session.persist(sanitationRequest);
    transaction.commit();
  }

  /**
   * loads another scene
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void backButtonClicked(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  /**
   * generates a table to store button click information
   *
   * @return true when table is successfully created or already exists, false otherwise
   */
  //  private boolean createTable() {
  //
  //    boolean table_exists = false;
  //
  //    if (this.connection != null) {
  //      String createQuery =
  //          "CREATE TABLE APP.buttonClicks("
  //              + "id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY
  // 1), "
  //              + "btn_name VARCHAR(50), "
  //              + "time_stamp TIMESTAMP NOT NULL, "
  //              + "PRIMARY KEY(id) )";
  //      try {
  //        Statement statement = this.connection.createStatement();
  //        statement.execute(createQuery);
  //
  //        table_exists = true;
  //      } catch (SQLException e) {
  //        // Error code 955 is "name is already used by an existing object", so this table name
  //        // already exists
  //        if (e.getErrorCode() == 955 || e.getMessage().contains("already exists"))
  //          table_exists = true;
  //        else e.printStackTrace();
  //      }
  //    }
  //    return table_exists;
  //  }
  //
  //  /**
  //   * Stores button click data to database
  //   *
  //   * @return true if data is stored successfully, false otherwise
  //   */
  //  private boolean logData() {
  //    if (connection != null) {
  //      String writeQuery =
  //          "INSERT INTO APP.buttonClicks(btn_name, time_stamp) VALUES ( 'ClickButton',
  // CURRENT_TIMESTAMP ) ";
  //      try {
  //        Statement statement = this.connection.createStatement();
  //        statement.execute(writeQuery);
  //        return true;
  //      } catch (SQLException e) {
  //        e.printStackTrace();
  //      }
  //    }
  //    return false;
  //  }
}
