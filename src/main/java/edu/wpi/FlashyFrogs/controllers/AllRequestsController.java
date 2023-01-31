package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.Sanitation;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;

public class AllRequestsController {
  @FXML
  private TableColumn<ServiceRequest, String> typeCol,
      empLastNameCol,
      submissionDateCol,
      submissionTimeCol,
      statusCol;

  @FXML private TableView tableView;
  @FXML private MFXButton back;

  @FXML
  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  public void initialize() {
    System.out.println("initializing");
    typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    empLastNameCol.setCellValueFactory(new PropertyValueFactory<>("empFirstName"));
    submissionDateCol.setCellValueFactory(new PropertyValueFactory<>("dateOfSubmission"));
    submissionTimeCol.setCellValueFactory(new PropertyValueFactory<>("idk"));
    statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    Session session = factory.openSession();
    // Transaction transaction = session.beginTransaction();
    Sanitation sanitationRequest = new Sanitation();

    List<ServiceRequest> objects =
        session.createQuery("SELECT s FROM Sanitation s", ServiceRequest.class).getResultList();
    System.out.println(objects.size());
    System.out.println(FXCollections.observableList(objects).size());
    tableView.setItems(FXCollections.observableList(objects));
  }
}
