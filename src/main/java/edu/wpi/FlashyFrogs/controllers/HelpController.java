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
}
