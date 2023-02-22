package edu.wpi.FlashyFrogs.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;

public class AlertsController {
      @FXML private TextField summaryField;
      @FXML private TextArea descriptionField;
      @FXML private SearchableComboBox<Object> deptBox;
      @FXML private ComboBox<Object> severityBox;

}
