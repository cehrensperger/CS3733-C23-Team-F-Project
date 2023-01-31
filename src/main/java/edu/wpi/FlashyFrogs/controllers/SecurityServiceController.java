package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.SecurityServiceData;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import java.io.IOException;

import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class SecurityServiceController  {

  @FXML private AnchorPane rootPane;
  @FXML private Text securityServiceText;
  @FXML private Text incidentReportText;
  @FXML private MFXTextField incidentReportEntry;
  @FXML private MFXTextField locationEntry;
  @FXML private MFXDatePicker dateEntry;
  @FXML private MFXTextField timeEntry;
  @FXML private Text employeeInformationText;
  @FXML private Text nameText;
  @FXML private MFXTextField firstEntry;
  @FXML private MFXTextField middleEntry;
  @FXML private MFXTextField lastEntry;
  @FXML private MFXButton clearButton;
  @FXML private MFXButton submitButton;
  @FXML private MFXButton homeButton;
  @FXML private MFXButton allButton;
  @FXML private MFXComboBox urgencyEntry;
  @FXML private MFXComboBox departmentEntry;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox department2;



  private SecurityServiceData securityServiceData;

  /** initializes when app starts */
  public void initialize() {

    urgencyEntry.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    departmentEntry.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");
    department2.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");
    securityServiceData = new SecurityServiceData();
  }

  /**
   * \clears the field values on the security service page
   *
   * @param event when the user clicks the clear button
   * @throws IOException
   */
  public void handleButtonClear(ActionEvent event) throws IOException {
    incidentReportEntry.clear();
    locationEntry.clear();
    dateEntry.clear();
    timeEntry.clear();
    firstEntry.clear();
    middleEntry.clear();
    lastEntry.clear();
    departmentEntry.clear();
    urgencyEntry.clear();
    first2.clear();
    middle2.clear();
    last2.clear();
    department2.clear();
  }

  /**
   * sends the field information from the security service form to the SecurityServiceData class
   *
   * @param event the submit button is clicked
   * @throws IOException
   */
  public void handleButtonSubmit(ActionEvent event) throws IOException {

    String incidentReport = incidentReportEntry.getText();
    String location = locationEntry.getText();
    String date = dateEntry.getText();
    String time = timeEntry.getText();
    String first = firstEntry.getText();
    String middle = middleEntry.getText();
    String last = lastEntry.getText();
    String department = departmentEntry.getText();

    securityServiceData.setInfo(
        incidentReport, location, date, time, first, middle, last, department);
    System.out.println(securityServiceData.getInfo());

    // SecurityServiceData data = new SecurityServiceData();
  }

  /**
   * allows the user to move back to the home screen
   *
   * @param actionEvent when the back button is clicked
   * @throws IOException
   */
  @FXML
  public void handleHomeButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    //Fapp.setScene("AllSecurityService");
  }
}
