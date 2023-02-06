package edu.wpi.FlashyFrogs.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HelpController {

  @FXML private Text e1;
  @FXML private Text e2;
  @FXML private Text e3;
  @FXML private Text e4;
  @FXML private Text e5;
  @FXML private Text e6;
  @FXML private Text e7;
  @FXML private Text e8;
  @FXML private Text e9;
  @FXML private Text e10;
  @FXML private Text e11;
  @FXML private Text e12;
  @FXML private Text e13;

  @FXML
  public void handleQHome() throws IOException {
    e1.setText(
        "Service Requests Page - Allows the user to select which service request form they would like to fill out or access a list of previously submitted service request forms.");
    e2.setText("Map Data Editor - Allows the user to edit nodes on teh hospital maps");
    e3.setText(
        "Path Finding - Provides the path between a start node and an end node in the hospital");
    e4.setText(
        "Service Requests - Allows the user to navigate to the specific user request form they are trying to visit");
    e5.setText("File -> Close - Allows the user to close the app");
    e6.setText("File -> Load Map - (not sure what this is tbh)");
    e7.setText("File -> Feedback - Allows the user to provide feedback on the app");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQAllRequests() throws IOException {
    e1.setText("ID - the ID of specific service request form");
    e2.setText("Employee Last Name - The last name of the employee that submitted the form");
    e3.setText("Date and Time of Submission - The date and time the form was submitted");
    e4.setText(
        "Status - Whether the request has been completed or not, which can be changed by the user");
    e5.setText("Back - Brings the user back to the service requests page");
    e6.setText("");
    e7.setText("");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQAudioVisual() throws IOException {
    e1.setText(
        "Patient: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the patient");
    e2.setText("Current Location - The location where the audio/visual request is needed");
    e3.setText("Type of Accommodation - Whether the patient needs audio or visual aid");
    e4.setText("Urgency - The urgency of the request");
    e5.setText(
        "Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee submitting the request");
    e6.setText(
        "Assigned Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee being assigned to the request");
    e7.setText(
        "All Audio/Visual Requests - Allows the user to view all previously submitted audio and visual requests");
    e8.setText("Clear - Clears all of the fields on the page");
    e9.setText("Back - Brings the user back to the service requests page");
    e10.setText("Submit - Submits the form");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQIT() throws IOException {
    e1.setText("Device Type: Enter the type of the device that needs IT service");
    e2.setText("Device Model: Enter the type of the device that needs IT service");
    e3.setText("Type of Service - The type of service needed on the device");
    e4.setText("Issue Description - Describe the issue you are having with your device");
    e5.setText("Urgency - The urgency of the request");
    e6.setText(
        "Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee submitting the request");
    e7.setText(
        "Assigned Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee being assigned to the request");
    e8.setText(
        "All Computer Service Requests - Allows the user to view all previously submitted computer service requests");
    e9.setText("Clear - Clears all of the fields on the page");
    e10.setText("Back - Brings the user back to the service requests page");
    e11.setText("Submit - Submits the form");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQConfirmation() throws IOException {
    e1.setText("Home - Brings you back to the home page");
    e2.setText("");
    e3.setText("");
    e4.setText("");
    e5.setText("");
    e6.setText("");
    e7.setText("");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQDBTableEditor() throws IOException {
    e1.setText("someone pls add help info to this cuz idk");
    e2.setText("");
    e3.setText("");
    e4.setText("");
    e5.setText("");
    e6.setText("");
    e7.setText("");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQFeedback() throws IOException {
    e1.setText(
        "Feedback Box - Give your feedback on anything you like or would like to change within the app");
    e2.setText(
        "Employee: First, Middle, Last, and Department - Enter the first name, middle name, last name, and department of the employee submitting the request");
    e3.setText(
        "Email and Phone Number - Enter the email and phone number of the employee submitting the feedback");
    e4.setText("Clear - Clears all of the fields on the page");
    e5.setText("Back - Brings the user back to the service requests page");
    e6.setText("Submit - Submits the form");
    e7.setText("");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQLoadMapPage() throws IOException {
    e1.setText("someone pls add help info to this cuz idk");
    e2.setText("");
    e3.setText("");
    e4.setText("");
    e5.setText("");
    e6.setText("");
    e7.setText("");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQPathFinding() throws IOException {
    e1.setText("Start - Enter the starting location of the path to be generated");
    e2.setText("End - Enter the end location of teh path to be generated");
    e3.setText("Path - The path will appear here after it is generated");
    e4.setText("Clear - Clears all of the fields on the page");
    e5.setText("Back - Brings the user back to the service requests page");
    e6.setText("Get Path - Generates the path given the locations put in start and end");
    e7.setText("");
    e8.setText("");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQRequestsHome() throws IOException {
    e1.setText(
        "All Service Requests - Allows the user to view all previously submitted service request forms and their statuses");
    e2.setText(
        "Internal Patient Transportation - Brings the user to the Internal Patient Transportation Form");
    e3.setText("Sanitation Services - Brings the user to the Sanitation Services Form");
    e4.setText("Security Services - Brings the user to the Security Services Form");
    e5.setText("Audio/Visual Services - Brings the user to the Audio/Visual Services Form");
    e6.setText("Computer Services - Brings the user to the IT Services Form");
    e7.setText("Security Services - Brings the user to the Security Services Form");
    e8.setText("Exit - Allows the user to exit the app");
    e9.setText("");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQSanitation() throws IOException {
    e1.setText("Request Type - The type of sanitation needed");
    e2.setText(
        "Location and Date - The location and the date of the incident needing the sanitation");
    e3.setText("Urgency - The urgency of the request");
    e4.setText(
        "Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee submitting the request");
    e5.setText(
        "Assigned Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee being assigned to the request");
    e6.setText(
        "All Sanitation Service Requests - Allows the user to view all previously submitted sanitation service requests");
    e7.setText("Clear - Clears all of the fields on the page");
    e8.setText("Back - Brings the user back to the service requests page");
    e9.setText("Submit - Submits the form");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQSecurity() throws IOException {
    e1.setText("Description of Incident - Describe the incident needing security");
    e2.setText(
        "Location and Date - The location and the date of the incident needing the sanitation");
    e3.setText("Urgency - The urgency of the request");
    e4.setText(
        "Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee submitting the request");
    e5.setText(
        "Assigned Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee being assigned to the request");
    e6.setText(
        "All Sanitation Service Requests - Allows the user to view all previously submitted sanitation service requests");
    e7.setText("Clear - Clears all of the fields on the page");
    e8.setText("Back - Brings the user back to the service requests page");
    e9.setText("Submit - Submits the form");
    e10.setText("");
    e11.setText("");
    e12.setText("");
    e13.setText("");
  }

  @FXML
  public void handleQTransport() throws IOException {
    e1.setText(
        "Patient: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the patient");
    e2.setText("Current Location - The location where the patient currently is");
    e3.setText("New Location - The location where the patient is being transported to");
    e4.setText("Urgency - The urgency of the request");
    e5.setText("Date of Transport - The date the patient will need to be transported");
    e6.setText(
        "Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee submitting the request");
    e7.setText(
        "Assigned Employee: First, Middle, Last, and Date of Birth - Enter the first name, middle name, last name, and date of birth of the employee being assigned to the request");
    e8.setText(
        "All Internal Patient Transportation Requests - Allows the user to view all previously submitted internal; patient transportation requests");
    e9.setText("Clear - Clears all of the fields on the page");
    e10.setText("Back - Brings the user back to the service requests page");
    e11.setText("Submit - Submits the form");
    e12.setText("");
    e13.setText("");
  }
}
