package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

@GeneratedExclusion
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
    e2.setText("Map Data Editor - Allows the user to edit nodes on the hospital maps");
    e3.setText(
        "Path Finding - Provides the path between a start node and an end node in the hospital");
    e4.setText(
        "Service Requests - Allows the user to navigate to the specific user request form they are trying to visit");
    e5.setText("File -> Close - Allows the user to close the app");
    e6.setText(
        "File -> Load Map - Allows the user to upload CSV files to clear the database and input custom data");
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

  // this page is never seen I think
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
    e2.setText("End - Enter the end location of the path to be generated");
    e3.setText("Clear - Clears all of the fields on the page");
    e4.setText("Back - Brings the user back to the service requests page");
    e5.setText(
        "Get Path - Generates the path given the locations put in start and end and displays the path on the map");
    e6.setText(
        "         If the path goes between floors, switch floors using the drop down to view the other floors");
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

  // write help things
  @FXML
  public void handleQMapEditor() {
    e1.setText("The Map Editor allows editing of the hospital map. The map is separated into two");
    e2.setText(
        "main ideas: Nodes, and Locations. Nodes are essentially places on the map - a physical location.");
    e3.setText(
        "Nodes are linked to each other by edges - this is primarily for pathfinding purposes. A nodes location"
            + "is required to be unique on its floor.");
    e4.setText("");
    e5.setText(
        "Locations are something that goes in a place on the map - for instance a department, or a bathroom.");
    e6.setText(
        "Locations can be created or deleted independently of nodes, and are associated with a node via the ");
    e7.setText(
        "use of Moves. Moves occur on a given date - a location is associated with the node that");
    e8.setText(
        "is most recently mapped to it (but not in the future!). Note that if something else is mapped to the");
    e9.setText("node more recently, the location will be unmapped.");
    e10.setText("");
    e11.setText("To edit locations, click on them in the table.");
    e12.setText("To edit nodes, click on them on the map.");
    e13.setText("");
  }

  public void onClose() {
//    System.out.println("nothing to be done");
  }
}
