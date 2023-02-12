package edu.wpi.FlashyFrogs.ServiceRequests;

// import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.ComputerService;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ServiceRequests.ServiceRequestController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ComputerServiceController extends ServiceRequestController {

  @FXML private MFXComboBox<String> deviceType;
  @FXML private MFXTextField deviceModel;
  @FXML private MFXComboBox<String> serviceType;
  @FXML private MFXTextField issueDescription;
  @FXML private MFXComboBox<String> urgencyEntry;
  @FXML private MFXTextField firstEntry;
  @FXML private MFXTextField middleEntry;
  @FXML private MFXTextField lastEntry;
  @FXML private MFXComboBox<String> departmentEntry;
  @FXML private MFXTextField first2;
  @FXML private MFXTextField middle2;
  @FXML private MFXTextField last2;
  @FXML private MFXComboBox<String> department2;
  @FXML private Label errorMessage;

  @FXML private MFXButton question;

  @FXML
  public void initialize() {
    ServiceRequest.Urgency[] urgencies = ServiceRequest.Urgency.values();
    System.out.println(urgencies.length);
    for (int i = 0; i < urgencies.length; i++) {
      urgencyEntry.getItems().add(urgencies[i].toString().replace("_", " ").toLowerCase());
    }

    ComputerService.DeviceType[] deviceTypes = ComputerService.DeviceType.values();
    System.out.println(deviceTypes.length);
    for (int i = 0; i < deviceTypes.length; i++) {
      deviceType.getItems().add(deviceTypes[i].toString().replace("_", " ").toLowerCase());
    }

    ComputerService.ServiceType[] serviceTypes = ComputerService.ServiceType.values();
    System.out.println(serviceTypes.length);
    for (int i = 0; i < serviceTypes.length; i++) {
      serviceType.getItems().add(serviceTypes[i].toString().replace("_", " ").toLowerCase());
    }

    ServiceRequest.EmpDept[] depts = ServiceRequest.EmpDept.values();
    System.out.println(depts.length);
    for (int i = 0; i < depts.length; i++) {
      departmentEntry.getItems().add(depts[i].toString().replace("_", " ").toLowerCase());
      department2.getItems().add(depts[i].toString().replace("_", " ").toLowerCase());
    }
  }

  @FXML
  public void handleAllButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "AllComputerServiceRequest");
  }

  @FXML
  public void handleClear(ActionEvent actionEvent) throws IOException {
    deviceType.clear();
    deviceModel.clear();
    serviceType.clear();
    issueDescription.clear();
    urgencyEntry.clear();
    firstEntry.clear();
    middleEntry.clear();
    lastEntry.clear();
    departmentEntry.clear();
    first2.clear();
    middle2.clear();
    last2.clear();
    department2.clear();
  }

  @FXML
  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = DBConnection.CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      String[] parts = {};
      String departmentEnumString = departmentEntry.getText().toUpperCase();
      parts = urgencyEntry.getText().toUpperCase().split(" ");
      String urgencyEnumString = parts[0] + "_" + parts[1];

      if (deviceType.getText().equals("")
          || deviceModel.getText().equals("")
          || serviceType.getText().equals("")
          || issueDescription.getText().equals("")
          || urgencyEntry.getText().equals("")
          || firstEntry.getText().equals("")
          || middleEntry.getText().equals("")
          || lastEntry.getText().equals("")
          || departmentEntry.getText().equals("")
          || first2.getText().equals("")
          || middle2.getText().equals("")
          || last2.getText().equals("")
          || department2.getText().equals("")) {
        throw new NullPointerException();
      }

      String departmentEnumString2 = department2.getText().toUpperCase().replace(" ", "_");
      String deviceTypeEnumString = deviceType.getText().toUpperCase().replace(" ", "_");
      String serviceTypeEnumString = serviceType.getText().toUpperCase().replace(" ", "_");

      ComputerService computerService =
          new ComputerService(
              firstEntry.getText(),
              middleEntry.getText(),
              lastEntry.getText(),
              first2.getText(),
              middle2.getText(),
              last2.getText(),
              ComputerService.EmpDept.valueOf(departmentEnumString),
              ComputerService.EmpDept.valueOf(departmentEnumString2),
              Date.from(Instant.now()),
              Date.from(Instant.now()),
              ServiceRequest.Urgency.valueOf(urgencyEnumString),
              ComputerService.DeviceType.valueOf(deviceTypeEnumString),
              deviceModel.getText(),
              issueDescription.getText(),
              ComputerService.ServiceType.valueOf(serviceTypeEnumString));
      try {
        session.persist(computerService);
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
    help.handleQAudioVisual();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
