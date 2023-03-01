package edu.wpi.FlashyFrogs.Alerts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.ORM.Alert;
import edu.wpi.FlashyFrogs.ORM.Department;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.sql.Date;
import java.time.ZoneId;
import java.util.List;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import lombok.Setter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class NewAlertController {
  public Pane errtoast;
  public Rectangle errcheck2;
  public Rectangle errcheck1;
  public MFXButton submit;
  public Pane errtoast1;
  public Rectangle errcheck21;
  public Rectangle errcheck11;
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
      if (startDate.getValue().isAfter(endDate.getValue())) {
        throw new IllegalArgumentException();
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
        errortoastAnimation();
        session.close();
      } catch (Exception exception) {
        session.clear();
        errortoastAnimation();
        session.close();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errortoastAnimation();
      session.close();
    } catch (IllegalArgumentException exception) {
      session.clear();
      errortoastAnimation1();
      session.close();
    }
  }

  public void errortoastAnimation1() {
    submit.setDisable(true);
    errtoast1.getTransforms().clear();
    errtoast1.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast1);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck11.setFill(Color.web("#012D5A"));
    errcheck21.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck11, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck21, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast1);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            submit.setDisable(false);
          }
        });
  }

  public void errortoastAnimation() {
    submit.setDisable(true);
    errtoast.getTransforms().clear();
    errtoast.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck1.setFill(Color.web("#012D5A"));
    errcheck2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck1, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck2, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            submit.setDisable(false);
          }
        });
  }
}
