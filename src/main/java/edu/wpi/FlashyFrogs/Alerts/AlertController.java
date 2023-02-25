package edu.wpi.FlashyFrogs.Alerts;

import edu.wpi.FlashyFrogs.ORM.Alert;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class AlertController {
  @FXML private Label summaryField;
  @FXML private Label departmentField;
  @FXML private Label severityField;
  @FXML private TextArea descriptionField;
  @FXML private Label authorField;
  @FXML private Label dateField;

  public void initialize() {}

  public void insertAnnouncement(Alert alert) {
    summaryField.setText(alert.getDescription());
    String severity = "";
    for (int i = 0; i < alert.getSeverity().ordinal(); i++) {
      severity = severity + "!";
    }
    severityField.setText(severity);
    dateField.setText(alert.getDisplayDate().toString());
    authorField.setText(alert.getAuthor().getFirstName() + " " + alert.getAuthor().getLastName());
    departmentField.setText(alert.getDepartment().getLongName());
    descriptionField.setText(alert.getAnnouncement());
  }
}
