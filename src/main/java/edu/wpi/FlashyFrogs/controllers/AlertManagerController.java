package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.ORM.Announcement;
import edu.wpi.FlashyFrogs.ORM.Department;
import jakarta.persistence.RollbackException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

public class AlertManagerController {
      @FXML private TextField summaryField;
      @FXML private TextArea descriptionField;
      @FXML private SearchableComboBox<Department> deptBox;
      @FXML private ComboBox<Announcement.Severity> severityBox;

      public void initialize() {
            Session session = CONNECTION.getSessionFactory().openSession();
            List<Department> departments = session.createQuery("FROM Department", Department.class).getResultList();

            deptBox.setItems(FXCollections.observableArrayList(departments));
            severityBox.setItems(FXCollections.observableArrayList(Announcement.Severity.values()));
      }

      public void handleSubmit(ActionEvent actionEvent) throws IOException {
            Session session = CONNECTION.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            try{
                  if (summaryField.getText().equals("") || descriptionField.getText().equals("")) {
                        throw new NullPointerException();
                  }

                  Announcement announcement = new Announcement(Date.from(Instant.now()), CurrentUserEntity.CURRENT_USER.getCurrentUser(), summaryField.getText(), descriptionField.getText(), deptBox.getValue(), severityBox.getValue());

                  try {
                        session.persist(announcement);
                        transaction.commit();
                        session.close();
                        handleClear(actionEvent);
                  } catch (RollbackException exception) {
                        session.clear();
                        // TODO Do something smart and throw an error maybe
                        session.close();
                  }
            } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
                  session.clear();
                  // TODO Do something smart and throw an error maybe
                  session.close();
            }
      }

      public void handleClear(ActionEvent actionEvent) throws IOException {
            summaryField.setText("");
            descriptionField.setText("");
            deptBox.valueProperty().set(null);
            severityBox.valueProperty().set(null);
      }
}
