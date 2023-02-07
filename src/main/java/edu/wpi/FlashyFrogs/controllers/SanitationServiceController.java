package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PopOver;
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
  @FXML MFXComboBox departmentDropDown;
  @FXML MFXComboBox urgencyEntry;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox department2;
  @FXML private MFXButton allButton;
  @FXML private MFXButton question;
  @FXML Label errorMessage;
  private Connection connection = null; // connection to database

  /** Method run when controller is initializes */
  public void initialize() {
    urgencyEntry.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    requestTypeDropDown.getItems().addAll("Mopping", "Sweeping", "Vacuuming");
    // locationDropDown.getItems().addAll("room 1", "room 2", "public space 1", "public space 2");

    // I don't really know what to put here
    departmentDropDown.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");

    Session session = CONNECTION.getSessionFactory().openSession();

    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();
    session.close();
    locationDropDown.setItems(FXCollections.observableList(objects));

    department2.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");
  }

  /**
   * clears all fields and drop-downs
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void handleClear(ActionEvent actionEvent) throws IOException {
    requestTypeDropDown.clear();
    locationDropDown.clear();
    date.clear();
    firstName.clear();
    lastName.clear();
    middleName.clear();
    departmentDropDown.clear();
    urgencyEntry.clear();
    first2.clear();
    middle2.clear();
    last2.clear();
    department2.clear();
  }

  /**
   * submits all fields by setting attributes of SubmitInfo instance
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void handleSubmit(ActionEvent actionEvent) throws IOException {

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts = {};
      String departmentEnumString = departmentDropDown.getText().toUpperCase().replace(" ", "_");
      String departmentEnumString2 = department2.getText().toUpperCase().replace(" ", "_");
      parts = urgencyEntry.getText().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (firstName.getText().equals("")
          || middleName.getText().equals("")
          || lastName.getText().equals("")
          || first2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")
          || departmentDropDown.getText().equals("")
          || date.getText().equals("")
          || locationDropDown.getText().equals("")
          || requestTypeDropDown.getText().equals("")) {
        throw new NullPointerException();
      }

      String requestTypeEnumString = requestTypeDropDown.getText().toUpperCase();

      Date dateOfIncident =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      Sanitation sanitationRequest =
          new Sanitation(
              Sanitation.SanitationType.valueOf(requestTypeEnumString),
              firstName.getText(),
              middleName.getText(),
              lastName.getText(),
              first2.getText(),
              middle2.getText(),
              last2.getText(),
              ServiceRequest.EmpDept.valueOf(departmentEnumString),
              ServiceRequest.EmpDept.valueOf(departmentEnumString2),
              dateOfIncident,
              Date.from(Instant.now()),
              ServiceRequest.Urgency.valueOf(urgencyEnumString),
              session.find(LocationName.class, locationDropDown.getText()));
      try {
        session.persist(sanitationRequest);
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
   * loads another scene
   *
   * @param actionEvent event that triggered method
   * @throws IOException
   */
  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {

    Fapp.setScene("AllSanitationRequest");
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("../views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQSanitation();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
