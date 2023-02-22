package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
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
public class ComputerEditorController extends ServiceRequestController implements IController {

  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML TextField number;
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
  @FXML SearchableComboBox<ComputerService.ServiceType> service;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML SearchableComboBox<ComputerService.DeviceType> type;
  @FXML DatePicker date;
  @FXML TextField description;
  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;
  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;

  private ComputerService itReq = new ComputerService();
  private PopOver popOver;

  public void setRequest(ServiceRequest serviceRequest) {
    itReq = (ComputerService) serviceRequest;
  }

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    h7.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<HospitalUser> users =
        session.createQuery("FROM HospitalUser", HospitalUser.class).getResultList();

    users.sort(Comparator.comparing(HospitalUser::getFirstName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    assignedBox.setItems(FXCollections.observableArrayList(users));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    service.setItems(FXCollections.observableArrayList(ComputerService.ServiceType.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    type.setItems(FXCollections.observableArrayList(ComputerService.DeviceType.values()));
    session.close();
  }

  public void updateFields() {
    locationBox.setValue(itReq.getLocation());
    statusBox.setValue(itReq.getStatus());
    service.setValue(itReq.getServiceType());
    urgency.setValue(itReq.getUrgency());
    type.setValue(itReq.getDeviceType());
    description.setText(itReq.getDescription());
    date.setValue(
        Instant.ofEpochMilli(itReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    if (itReq.getAssignedEmp() != null) {
      assignedBox.setValue(itReq.getAssignedEmp());
    }
    number.setText(itReq.getBestContact());
  }

  @Override
  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // check
      if (number.getText().equals("")
          || locationBox.getValue().toString().equals("")
          || service.getValue().toString().equals("")
          || type.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }
      Date dateNeeded = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      itReq.setLocation(locationBox.getValue());
      itReq.setAssignedEmp(assignedBox.getValue());
      itReq.setStatus(statusBox.getValue());
      itReq.setServiceType(service.getValue());
      itReq.setUrgency(urgency.getValue());
      itReq.setDeviceType(type.getValue());
      itReq.setDescription(description.getText());
      itReq.setDate(dateNeeded);
      itReq.setBestContact(number.getText());

      try {
        session.merge(itReq);
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
    number.setText("");
    locationBox.valueProperty().set(null);
    service.valueProperty().set(null);
    type.valueProperty().set(null);
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    description.setText("");
  }

  @Override
  protected void handleBack(ActionEvent event) throws IOException {}

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

  public void onClose() {}
}
