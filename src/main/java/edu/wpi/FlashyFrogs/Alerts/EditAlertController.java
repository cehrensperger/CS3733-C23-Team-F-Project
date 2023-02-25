package edu.wpi.FlashyFrogs.Alerts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.Announcement;
import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.controllers.IController;
import jakarta.persistence.RollbackException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Setter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EditAlertController implements IController {
  @Setter private PopOver popOver;
  @Setter private AlertManagerController alertManagerController;
  private Announcement currentAlert;
  @FXML private TextField summaryField;
  @FXML private TextArea descriptionField;
  @FXML private SearchableComboBox<Department> deptBox;
  @FXML private ComboBox<Announcement.Severity> severityBox;

  public void initialize(Announcement selectedAlert) {
    this.currentAlert = selectedAlert;
    this.summaryField.setText(currentAlert.getDescription());
    this.descriptionField.setText(currentAlert.getAnnouncement());
    this.deptBox.getSelectionModel().select(currentAlert.getDepartment());
    this.severityBox.getSelectionModel().select(currentAlert.getSeverity());

    Session session = CONNECTION.getSessionFactory().openSession();
    List<Department> departments =
        session.createQuery("FROM Department", Department.class).getResultList();

    deptBox.setItems(FXCollections.observableArrayList(departments));
    severityBox.setItems(FXCollections.observableArrayList(Announcement.Severity.values()));
  }

  public void handleSave(ActionEvent actionEvent) {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      if (summaryField.getText().equals("")
          || descriptionField.getText().equals("")) { // TODO check dropdowns
        throw new NullPointerException();
      }

      currentAlert.setDescription(summaryField.getText());
      currentAlert.setAnnouncement(descriptionField.getText());
      currentAlert.setSeverity(severityBox.getValue());
      currentAlert.setDepartment(deptBox.getValue());

      try {
        session.merge(currentAlert);
        transaction.commit();
        session.close();
        alertManagerController.initialize();
        popOver.hide();
        //        handleCancel(actionEvent);
      } catch (RollbackException exception) {
        session.clear();
        // TODO Do something smart and throw an error maybe
        session.close();
      } catch (Exception exception) {
        session.clear();
        // TODO Do something smart also
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      // TODO Do something smart and throw an error maybe
      session.close();
    }
  }

  public void handleDelete(ActionEvent actionEvent) throws Exception {
    Session session = CONNECTION.getSessionFactory().openSession();

    session.beginTransaction();
    session
        .createMutationQuery("DELETE FROM Announcement alert WHERE id=:ID")
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
