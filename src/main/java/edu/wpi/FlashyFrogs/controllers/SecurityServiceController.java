package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Security;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class SecurityServiceController extends ServiceRequestController {

  @FXML private AnchorPane rootPane;
  @FXML private Text securityServiceText;
  @FXML private Text incidentReportText;
  @FXML private MFXTextField incidentReportEntry;
  @FXML private MFXComboBox locationEntry;
  @FXML private MFXDatePicker dateEntry;
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
  @FXML private Label errorMessage;

  /** initializes when app starts */
  public void initialize() {

    urgencyEntry.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    departmentEntry.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");
    department2.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");

    Session session = CONNECTION.getSessionFactory().openSession();

    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();
    session.close();
    locationEntry.setItems(FXCollections.observableList(objects));
  }

  /**
   * \clears the field values on the security service page
   *
   * @param event when the user clicks the clear button
   * @throws IOException
   */
  public void handleClear(ActionEvent event) throws IOException {
    incidentReportEntry.clear();
    locationEntry.clear();
    dateEntry.clear();
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
   * @param actionEvent the submit button is clicked
   * @throws IOException
   */
  public void handleSubmit(ActionEvent actionEvent) throws IOException {

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts = {};
      String departmentEnumString = departmentEntry.getText().toUpperCase().replace(" ", "_");
      String departmentEnumString2 = department2.getText().toUpperCase().replace(" ", "_");
      parts = urgencyEntry.getText().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (firstEntry.getText().equals("")
          || middleEntry.getText().equals("")
          || lastEntry.getText().equals("")
          || first2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")
          || departmentEntry.getText().equals("")
          || dateEntry.getText().equals("")
          || locationEntry.getText().equals("")
          || incidentReportEntry.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfIncident =
          Date.from(dateEntry.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      Security securityRequest = new Security();
      securityRequest.setEmpFirstName(firstEntry.getText());
      securityRequest.setEmpMiddleName(middleEntry.getText());
      securityRequest.setEmpLastName(lastEntry.getText());
      securityRequest.setAssignedEmpFirstName(first2.getText());
      securityRequest.setAssignedEmpMiddleName(middle2.getText());
      securityRequest.setAssignedEmpLastName(last2.getText());
      securityRequest.setEmpDept(ServiceRequest.EmpDept.valueOf(departmentEnumString));
      securityRequest.setAssignedEmpDept(ServiceRequest.EmpDept.valueOf(departmentEnumString2));
      securityRequest.setDateOfIncident(dateOfIncident);
      securityRequest.setDateOfSubmission(Date.from(Instant.now()));
      securityRequest.setUrgency(ServiceRequest.Urgency.valueOf(urgencyEnumString));
      securityRequest.setLocation(session.find(LocationName.class, locationEntry.getText()));
      securityRequest.setIncidentReport(incidentReportEntry.getText());

      try {
        session.persist(securityRequest);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(Paint.valueOf("#44ff00"));
        errorMessage.setText("Successfully submitted.");
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(Paint.valueOf("#ff0000"));
        errorMessage.setText("Please fill all fields.");
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#ff0000"));
      errorMessage.setText("Please fill all fields.");
      session.close();
    }
  }

  /**
   * allows the user to move back to the home screen
   *
   * @param actionEvent when the back button is clicked
   * @throws IOException
   */
  @FXML
  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("AllSecurityService");
  }
}
