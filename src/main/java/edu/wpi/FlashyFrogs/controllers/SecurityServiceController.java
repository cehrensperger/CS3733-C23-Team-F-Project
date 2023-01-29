package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.SecurityServiceData;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class SecurityServiceController {

  @FXML private AnchorPane rootPane;
  @FXML private Text securityServiceText;
  @FXML private Text incidentReportText;
  @FXML private TextField incidentReportEntry;
  @FXML private Text descriptionOfIncidentText;
  @FXML private TextField locationEntry;
  @FXML private MFXDatePicker dateEntry;
  @FXML private TextField timeEntry;
  @FXML private Text locationText;
  @FXML private Text dateText;
  @FXML private Text timeText;
  @FXML private Text employeeInformationText;
  @FXML private Text nameText;
  @FXML private TextField firstEntry;
  @FXML private TextField middleEntry;
  @FXML private TextField lastEntry;
  @FXML private TextField departmentEntry;
  @FXML private Text firstText;
  @FXML private Text middleText;
  @FXML private Text lastText;
  @FXML private Text departmentText;
  @FXML private MFXButton clearButton;
  @FXML private MFXButton submitButton;
  @FXML private MFXButton homeButton;

  private SecurityServiceData securityServiceData;

  /** initializes when app starts */
  public void initialize() {

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
    Fapp.setScene("Home");
  }
}
