package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.controllers.IController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EditUserController implements IController {
  private PopOver popOver;
  private LoginAdministratorController loginAdministratorController;
  @FXML private TextField firstName;
  @FXML private TextField middleName;
  @FXML private TextField lastName;
  @FXML private TextField username;
  @FXML private PasswordField pass1;
  @FXML private PasswordField pass2;
  @FXML private SearchableComboBox<Department> deptBox;
  @FXML private SearchableComboBox<HospitalUser.EmployeeType> employeeType;
  @FXML private Label errorMessage;

  public void setPopOver(PopOver thePopOver) {
    this.popOver = thePopOver;
  }

  public void setLoginAdminController(LoginAdministratorController adminController) {
    this.loginAdministratorController = adminController;
  }

  public void initialize(String firstName, String middleName, String lastName, String userName) {
    this.firstName.setText(firstName);
    this.middleName.setText(middleName);
    this.lastName.setText(lastName);
    this.username.setText(userName);
    // this.deptBox.set
  }

  public void saveChanges(ActionEvent actionEvent) {
    if (username.getText().equals("")
        || pass1.getText().equals("")
        || pass2.getText().equals("")
        || firstName.getText().equals("")
        || middleName.getText().equals("")
        || lastName.getText().equals("")
        || deptBox.getValue() == null
        || employeeType.getValue() == null) {
      // One of the values is left null
      errorMessage.setText("Please fill out all fields!");
      errorMessage.setVisible(true);
    } /*else if (!pass1.getText().equals(pass2.getText())) {
      // Passwords do not match
      errorMessage.setText("Passwords do not match!");
      errorMessage.setVisible(true);}*/ else {
      // Save Username and Password to db
      errorMessage.setVisible(false);
      /*User userFK =
              new User(
                      firstName.getText(),
                      middleName.getText(),
                      lastName.getText(),
                      employeeType.getValue(),
                      deptBox.getValue()); // update department
      UserLogin newUser = new UserLogin(userFK, username.getText());*/
      Session ses = CONNECTION.getSessionFactory().openSession();
      Transaction transaction = ses.beginTransaction();
      try {
        // ses.persist(userFK);
        // ses.persist(newUser);
        transaction.commit();
        ses.close();
        loginAdministratorController.initialize();
        popOver.hide();
      } catch (Exception e) {
        errorMessage.setText("That username is already taken.");
        errorMessage.setVisible(true);
        transaction.rollback();
        ses.close();
      }
    }
  }

  public void deleteUser(ActionEvent actionEvent) {}

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
