package edu.wpi.FlashyFrogs.Alerts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.Alert;
import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.controllers.IController;
import jakarta.persistence.RollbackException;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EditAlertController implements IController {
  @Setter private PopOver popOver;
  @Setter private AlertManagerController alertManagerController;
  private Alert currentAlert;
  @FXML private TextField summaryField;
  @FXML private TextArea descriptionField;
  @FXML private SearchableComboBox<Department> deptBox;
  @FXML private ComboBox<Alert.Severity> severityBox;
  @FXML private DatePicker startDate;
  @FXML private DatePicker endDate;
  @FXML private Label errorMessage;

  public void initialize(Alert selectedAlert) {
    errorMessage.setVisible(false);
    this.currentAlert = selectedAlert;
    this.summaryField.setText(currentAlert.getDescription());
    this.descriptionField.setText(currentAlert.getAnnouncement());
    this.deptBox.getSelectionModel().select(currentAlert.getDepartment());
    this.severityBox.getSelectionModel().select(currentAlert.getSeverity());
    this.startDate.setValue(
        Instant.ofEpochMilli(currentAlert.getStartDisplayDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());
    this.endDate.setValue(
        Instant.ofEpochMilli(currentAlert.getStartDisplayDate().getTime())
            .atZone(ZoneId.systemDefault())
            .toLocalDate());

    Session session = CONNECTION.getSessionFactory().openSession();
    List<Department> departments =
        session.createQuery("FROM Department", Department.class).getResultList();

    deptBox.setItems(FXCollections.observableArrayList(departments));
    severityBox.setItems(FXCollections.observableArrayList(Alert.Severity.values()));
  }

  public void handleSave(ActionEvent actionEvent) {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      if (summaryField.getText().equals("")
          || deptBox.getValue().equals("")
          || severityBox.getValue().equals("")
          || descriptionField.getText().equals("")
          || endDate.getValue().toString().equals("")
          || startDate.getValue().toString().equals("")) {
        throw new NullPointerException();
      }

      currentAlert.setDescription(summaryField.getText());
      currentAlert.setAnnouncement(descriptionField.getText());
      currentAlert.setSeverity(severityBox.getValue());
      currentAlert.setDepartment(deptBox.getValue());
      currentAlert.setStartDisplayDate(
          Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
      currentAlert.setEndDisplayDate(
          Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));

      try {
        session.merge(currentAlert);
        transaction.commit();
        session.close();
        alertManagerController.initialize();
        popOver.hide();
        //        handleCancel(actionEvent);
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setVisible(true);
        errorMessage.setText("Rollback");
        session.close();
      } catch (Exception exception) {
        session.clear();
        errorMessage.setVisible(true);
        errorMessage.setText("exception");
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setVisible(true);
      errorMessage.setText("Fill out all fields");
      session.close();
    }
  }

  public void handleDelete(ActionEvent actionEvent) throws Exception {
    Session session = CONNECTION.getSessionFactory().openSession();

    session.beginTransaction();
    session
        .createMutationQuery("DELETE FROM Alert alert WHERE id=:ID")
        .setParameter("ID", currentAlert.getId())
        .executeUpdate();
    session.getTransaction().commit();
    session.close();

    alertManagerController.initialize();
    popOver.hide();
  }

  @Override
  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
