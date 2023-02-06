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
    e1.setText("Service Request Type - The type of service request of the form");
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
}
