package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ORM.User;
import edu.wpi.FlashyFrogs.ServiceRequests.ServiceRequestController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class SanitationEditorController extends ServiceRequestController implements IController {

  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;
  @FXML MFXButton back;
  @FXML MFXButton clear;
  @FXML MFXButton submit;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<Sanitation.SanitationType> sanitationType;
  @FXML DatePicker date;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML CheckBox isolation;
  @FXML SearchableComboBox<Sanitation.BiohazardLevel> biohazard;
  @FXML TextField description;
  @FXML SearchableComboBox<User> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
  boolean hDone = false;
  @FXML private Label errorMessage;
  private Connection connection = null;

  private Sanitation sanitationReq = new Sanitation();
  PopOver popOver;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    h7.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();
    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<User> users = session.createQuery("SELECT u FROM User u", User.class).getResultList();
    users.sort(Comparator.comparing(User::getFirstName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    sanitationType.setItems(FXCollections.observableArrayList(Sanitation.SanitationType.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    biohazard.setItems(FXCollections.observableArrayList(Sanitation.BiohazardLevel.values()));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    assignedBox.setItems(FXCollections.observableArrayList(users));

    session.close();
  }

  @Override
  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void updateFields() {
    System.out.println(sanitationReq.getLocation());
    locationBox.setValue(sanitationReq.getLocation());
    sanitationType.setValue(sanitationReq.getType());
    urgency.setValue(sanitationReq.getUrgency());
    biohazard.setValue(sanitationReq.getBiohazard());
    statusBox.setValue(sanitationReq.getStatus());
    date.setValue(
        Instant.ofEpochMilli(sanitationReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    isolation.setSelected(sanitationReq.getIsolation());
    description.setText(sanitationReq.getDescription());
    if (sanitationReq.getAssignedEmp() != null) {
      assignedBox.setValue(sanitationReq.getAssignedEmp());
    }
  }

  public void setRequest(ServiceRequest serviceRequest) {
    sanitationReq = (Sanitation) serviceRequest;
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {

    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      if (locationBox.getValue().toString().equals("")
          || sanitationType.getValue().toString().equals("")
          || date.getValue().toString().equals("")
          || biohazard.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateOfIncident =
          Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      sanitationReq.setDescription(description.getText());
      sanitationReq.setIsolation(isolation.isSelected());
      sanitationReq.setBiohazard(biohazard.getValue());
      sanitationReq.setAssignedEmp(assignedBox.getValue());
      sanitationReq.setUrgency(urgency.getValue());
      sanitationReq.setDate(dateOfIncident);
      sanitationReq.setType(sanitationType.getValue());
      sanitationReq.setStatus(statusBox.getValue());
      sanitationReq.setLocation(locationBox.getValue());

      try {
        session.merge(sanitationReq);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        popOver.hide();
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(Paint.valueOf("#b6000b"));
        errorMessage.setText("Please fill all fields.");
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#b6000b"));
      errorMessage.setText("Please fill all fields.");
      session.close();
    }
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    locationBox.valueProperty().set(null);
    sanitationType.valueProperty().set(null);
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    isolation.setSelected(false);
    biohazard.valueProperty().set(null);
    description.setText("");
  }

  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      h7.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      h7.setVisible(false);
      hDone = false;
    }
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ComputerService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.handleBack();
  }

  @Override
  public void onClose() {}
}
