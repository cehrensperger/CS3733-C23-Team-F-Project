package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.InternalTransport;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class TransportController extends ServiceRequestController {
  @FXML MFXTextField firstNameTextfield; // ID of the first name text field
  @FXML MFXTextField lastNameTextfield;
  @FXML MFXTextField middleNameTextfield;
  @FXML MFXTextField firstNameTextfield2;
  @FXML MFXTextField lastNameTextfield2;
  @FXML MFXTextField middleNameTextfield2;
  @FXML MFXDatePicker dateOfBirthDatePicker;
  @FXML MFXComboBox<String> currentLocationComboBox;
  @FXML MFXComboBox<String> newLocationComboBox;
  @FXML MFXDatePicker dateOfTransportDatePicker;
  @FXML MFXComboBox<String> departmentComboBox;
  @FXML MFXButton clearButton;
  @FXML MFXButton submitButton;
  @FXML MFXButton question;
  @FXML MFXComboBox<String> urgency;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox<String> department2;
  @FXML private Label errorMessage;

  private Connection connection = null; // connection to database

  /** Method run when controller is initializes */
  public void initialize() {

    Session session = CONNECTION.getSessionFactory().openSession();
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    objects.sort(String::compareTo);

    ObservableList<String> observableList = FXCollections.observableList(objects);

    newLocationComboBox.setItems(observableList);
    currentLocationComboBox.setItems(FXCollections.observableList(objects));
    urgency.getItems().addAll("Very Urgent", "Moderately Urgent", "Not Urgent");
    departmentComboBox.getItems().addAll("Cardiology", "Radiology", "Trauma Unit");
    department2.getItems().addAll("Cardiology", "Radiology", "Trauma Unit");
    session.close();
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    firstNameTextfield.clear();
    lastNameTextfield.clear();
    middleNameTextfield.clear();
    firstNameTextfield2.clear();
    lastNameTextfield2.clear();
    middleNameTextfield2.clear();
    dateOfBirthDatePicker.clear();
    currentLocationComboBox.clear();
    newLocationComboBox.clear();
    dateOfTransportDatePicker.clear();
    departmentComboBox.clear();
    first2.clear();
    middle2.clear();
    last2.clear();
    department2.clear();
    urgency.clear();
  }

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "AllTransport");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String departmentEnumString = departmentComboBox.getText().toUpperCase().replace(" ", "_");
      String departmentEnumString2 = department2.getText().toUpperCase().replace(" ", "_");
      String urgencyString = urgency.getText().toUpperCase().replace(" ", "_");

      // check
      if (firstNameTextfield.getText().equals("")
          || middleNameTextfield.getText().equals("")
          || lastNameTextfield.getText().equals("")
          || first2.getText().equals("")
          || middleNameTextfield2.getText().equals("")
          || lastNameTextfield2.getText().equals("")
          || firstNameTextfield2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")
          || departmentComboBox.getText().equals("")
          || dateOfTransportDatePicker.getText().equals("")
          || dateOfBirthDatePicker.getText().equals("")
          || currentLocationComboBox.getText().equals("")
          || newLocationComboBox.getText().equals("")) {
        throw new NullPointerException();
      }
      if (firstNameTextfield.getText().equals("")
          || middleNameTextfield.getText().equals("")
          || lastNameTextfield.getText().equals("")
          || first2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")
          || departmentComboBox.getText().equals("")
          || dateOfTransportDatePicker.getText().equals("")
          || dateOfBirthDatePicker.getText().equals("")
          || currentLocationComboBox.getText().equals("")
          || newLocationComboBox.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfTransport =
          Date.from(
              dateOfTransportDatePicker
                  .getValue()
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant());
      Date dateOfBirth =
          Date.from(
              dateOfBirthDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      InternalTransport transport = new InternalTransport();
      //      transport.setEmpFirstName(firstNameTextfield2.getText());
      //      transport.setEmpMiddleName(middleNameTextfield2.getText());
      //      transport.setEmpLastName(lastNameTextfield2.getText());
      //      transport.setAssignedEmpFirstName(first2.getText());
      //      transport.setAssignedEmpMiddleName(middle2.getText());
      //      transport.setAssignedEmpLastName(last2.getText());
      //      transport.setEmpDept(ServiceRequest.EmpDept.valueOf(departmentEnumString));
      //      transport.setAssignedEmpDept(ServiceRequest.EmpDept.valueOf(departmentEnumString2));
      //      transport.setDateOfBirth(dateOfBirth);
      //      transport.setTargetDate(dateOfTransport);
      //      transport.setDateOfSubmission(Date.from(Instant.now()));
      //      transport.setUrgency(ServiceRequest.Urgency.valueOf(urgencyString));
      //      transport.setNewLoc(session.find(LocationName.class, newLocationComboBox.getText()));
      //      transport.setOldLoc(session.find(LocationName.class,
      // currentLocationComboBox.getText()));
      //      transport.setPatientFirstName(firstNameTextfield.getText());
      //      transport.setPatientMiddleName(middleNameTextfield.getText());
      //      transport.setPatientLastName(lastNameTextfield.getText());
      try {
        session.persist(transport);
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

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "RequestsHome");
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQTransport();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
