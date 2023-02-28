package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
import edu.wpi.FlashyFrogs.Sound;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class NewUserController implements IController {
  public Pane toast;
  public Rectangle check2;
  public Rectangle check1;
  public Pane errtoast;
  public Rectangle errcheck2;
  public Rectangle errcheck1;
  public Pane errtoast1;
  public Rectangle errcheck21;
  public Rectangle errcheck11;
  public Pane errtoast2;
  public Rectangle errcheck22;
  public Rectangle errcheck12;
  public MFXButton newHospitalUser;
  private PopOver popOver;
  private LoginAdministratorController loginAdministratorController;
  @FXML private TextField username;
  @FXML private PasswordField pass1;
  @FXML private PasswordField pass2;

  @FXML private TextField rfid;
  @FXML private TextField firstName;
  @FXML private TextField middleName;
  @FXML private TextField lastName;
  @FXML private SearchableComboBox<Department> deptBox;
  @FXML private SearchableComboBox<HospitalUser.EmployeeType> employeeType;
  @FXML private Label errorMessage;

  public NewUserController() {}

  public void setPopOver(PopOver thePopOver) {
    this.popOver = thePopOver;
  }

  public void setLoginAdminController(LoginAdministratorController adminController) {
    this.loginAdministratorController = adminController;
  }

  public void initialize() {
    EditUserController.initDepartment_EmpType(deptBox, employeeType);
  }

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    if (username.getText().equals("")
        || pass1.getText().equals("")
        || pass2.getText().equals("")
        || firstName.getText().equals("")
        || lastName.getText().equals("")
        || deptBox.getValue() == null
        || employeeType.getValue() == null) {
      // One of the values is left null
      errortoastAnimation();
      Sound.ERROR.play();

    } else if (!pass1.getText().equals(pass2.getText())) {
      // Passwords do not match
      errortoastAnimation2();
      Sound.ERROR.play();
    } else {
      // Save Username and Password to db
      errorMessage.setVisible(false);
      HospitalUser userFK =
          new HospitalUser(
              firstName.getText(),
              middleName.getText(),
              lastName.getText(),
              employeeType.getValue(),
              deptBox.getValue()); // update department
      UserLogin newUser = new UserLogin(userFK, username.getText(), null, pass1.getText());
      Session ses = CONNECTION.getSessionFactory().openSession();
      Transaction transaction = ses.beginTransaction();
      try {
        ses.persist(userFK);
        toastAnimation();
        ses.persist(newUser);
        transaction.commit();
        loginAdministratorController.initialize();
      } catch (Exception e) {
        errortoastAnimation1();
        errorMessage.setVisible(true);
        transaction.rollback();
        return;
      }
      if (rfid != null && !rfid.getText().isEmpty()) {
        try {
          transaction = ses.beginTransaction();
          newUser.setRFIDBadge(rfid.getText());
          ses.merge(newUser);
          transaction.commit();
          loginAdministratorController.initialize();
        } catch (Exception e) {
          errorMessage.setText("That badge ID is already taken. User added without a badge ID.");
          toastAnimation();
          Sound.ERROR.play();
          errorMessage.setVisible(true);
          transaction.rollback();
        }
      }
      ses.close();
      popOver.hide();
    }
  }

  public void toastAnimation() {
    // Create a TranslateTransition to move the first rectangle to the left
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.1), toast);
    translate1.setByX(-280.0);
    translate1.setAutoReverse(true);
    check1.setFill(Color.web("#012D5A"));
    check2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), check1, Color.web("#012D5A"), Color.web("#F6BD38"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), check2, Color.web("#012D5A"), Color.web("#F6BD38"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.1), toast);
    translateBack1.setDelay(Duration.seconds(0.1));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.play();
  }

  public void errortoastAnimation() {
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
            newHospitalUser.setDisable(false);
          }
        });
  }

  public void errortoastAnimation1() {
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
            newHospitalUser.setDisable(false);
          }
        });
  }

  public void errortoastAnimation2() {
    errtoast2.getTransforms().clear();
    errtoast2.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast2);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck12.setFill(Color.web("#012D5A"));
    errcheck22.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck12, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck22, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast2);
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
            newHospitalUser.setDisable(false);
          }
        });
  }

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
