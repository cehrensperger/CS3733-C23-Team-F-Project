package edu.wpi.FlashyFrogs.ServiceRequests.Editors;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
import edu.wpi.FlashyFrogs.ServiceRequests.ServiceRequestController;
import edu.wpi.FlashyFrogs.controllers.IController;
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
public class AudioVisualEditorController extends ServiceRequestController implements IController {

  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<HospitalUser> assignedBox;
  @FXML SearchableComboBox<ServiceRequest.Status> statusBox;
  @FXML TextField device;
  @FXML TextField reason;
  @FXML DatePicker date;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML TextField description;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;

  private AudioVisual avReq = new AudioVisual();
  PopOver popOver;

  public void setRequest(ServiceRequest serviceRequest) {
    avReq = (AudioVisual) serviceRequest;
  }

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();

    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    List<HospitalUser> hospitalUsers =
        session.createQuery("FROM HospitalUser", HospitalUser.class).getResultList();

    hospitalUsers.sort(Comparator.comparing(HospitalUser::getFirstName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    assignedBox.setItems(FXCollections.observableArrayList(hospitalUsers));
    statusBox.setItems(FXCollections.observableArrayList(ServiceRequest.Status.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    session.close();
  }

  @Override
  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void updateFields() {
    locationBox.setValue(avReq.getLocation());
    urgency.setValue(avReq.getUrgency());
    statusBox.setValue(avReq.getStatus());
    date.setValue(
        Instant.ofEpochMilli(avReq.getDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    description.setText(avReq.getDescription());
    device.setText(avReq.getDeviceType());
    reason.setText(avReq.getReason());
    if (avReq.getAssignedEmp() != null) {
      assignedBox.setValue(avReq.getAssignedEmp());
    }
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // check
      if (locationBox.getValue().toString().equals("")
          || device.getText().equals("")
          || reason.getText().equals("")
          || date.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateNeeded = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      avReq.setDescription(description.getText());
      avReq.setAssignedEmp(assignedBox.getValue());
      avReq.setUrgency(urgency.getValue());
      avReq.setDate(dateNeeded);
      avReq.setStatus(statusBox.getValue());
      avReq.setLocation(locationBox.getValue());
      avReq.setDeviceType(device.getText());
      avReq.setReason(reason.getText());

      try {
        session.merge(avReq);
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
    device.setText("");
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
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      hDone = false;
    }
  }

  @Override
  public void onClose() {}
}
