package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class AllRequestsController {
  @FXML
  private TableColumn<ServiceRequest, String> typeCol,
      empLastNameCol,
      submissionDateCol,
      submissionTimeCol,
      statusCol;

  public void initialize() {
    typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    empLastNameCol.setCellValueFactory(new PropertyValueFactory<>("empLastName"));
    submissionDateCol.setCellValueFactory(new PropertyValueFactory<>("subDate"));
    submissionTimeCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
    statusCol.setCellValueFactory(new PropertyValueFactory<>("update"));
  }
}
