package edu.wpi.FlashyFrogs.Alerts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.ORM.Alert;
import edu.wpi.FlashyFrogs.ORM.Department;
import jakarta.persistence.RollbackException;
import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NewAlertController {
  @Setter private PopOver popOver;
  @Setter private AlertManagerController alertManagerController;
  @FXML private TextField summaryField;
  @FXML private TextArea descriptionField;
  @FXML private SearchableComboBox<Department> deptBox;
  @FXML private ComboBox<Alert.Severity> severityBox;
  @FXML private DatePicker startDate;
  @FXML private DatePicker endDate;
  @FXML private Label errorMessage;

  public void initialize() {
    errorMessage.setVisible(false);
    Session session = CONNECTION.getSessionFactory().openSession();
    List<Department> departments =
        session.createQuery("FROM Department", Department.class).getResultList();

    deptBox.setItems(FXCollections.observableArrayList(departments));
    severityBox.setItems(FXCollections.observableArrayList(Alert.Severity.values()));
  }

  public void handleSubmit(javafx.event.ActionEvent actionEvent) {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      if (summaryField.getText().equals("")
          || deptBox.getValue().equals("")
          || severityBox.getValue().equals("")
          || descriptionField.getText().equals("")
          || startDate.getValue().toString().equals("")
          || endDate.getValue().toString().equals("")) {
        throw new NullPointerException();
      }

      Alert alert =
          new Alert(
              Date.from(startDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
              Date.from(endDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
              CurrentUserEntity.CURRENT_USER.getCurrentUser(),
              summaryField.getText(),
              descriptionField.getText(),
              deptBox.getValue(),
              severityBox.getValue());

      try {
        session.persist(alert);
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
}
