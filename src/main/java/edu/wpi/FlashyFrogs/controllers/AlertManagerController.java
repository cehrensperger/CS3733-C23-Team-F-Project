package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.Announcement;
import edu.wpi.FlashyFrogs.ORM.Department;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;

public class AlertManagerController {
      @FXML private TextField summaryField;
      @FXML private TextArea descriptionField;
      @FXML private SearchableComboBox<Department> deptBox;
      @FXML private ComboBox<Announcement.Severity> severityBox;

}
