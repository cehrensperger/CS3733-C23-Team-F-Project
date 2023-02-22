package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.Announcement;
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

  public void insertAnnouncement(Announcement announcement) {
    summaryField.setText(announcement.getAnnouncement());
    String severity = "";
    for (int i = 0; i < announcement.getSeverity().ordinal(); i++) {
      severity = severity + "!";
    }
    severityField.setText(severity);
    dateField.setText(announcement.getCreationDate().toString());
    authorField.setText(
        announcement.getAuthor().getFirstName() + " " + announcement.getAuthor().getLastName());
    departmentField.setText(announcement.getDepartment().getLongName());
    descriptionField.setText(announcement.getDescription());
  }
}
