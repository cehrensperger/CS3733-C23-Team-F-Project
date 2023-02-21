package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class NewUserController implements IController {
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
    Session session = CONNECTION.getSessionFactory().openSession();
    List<Department> objects =
        session.createQuery("SELECT d FROM Department d", Department.class).getResultList();

    // objects.sort(String::compareTo);

    ObservableList<Department> observableList = FXCollections.observableList(objects);
    deptBox.setItems(observableList);
    employeeType
        .getItems()
        .addAll(
            HospitalUser.EmployeeType.ADMIN,
            HospitalUser.EmployeeType.MEDICAL,
            HospitalUser.EmployeeType.STAFF);
    session.close();
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
      errorMessage.setText("Please fill out all required fields!");
      errorMessage.setVisible(true);
    } else if (!pass1.getText().equals(pass2.getText())) {
      // Passwords do not match
      errorMessage.setText("Passwords do not match!");
      errorMessage.setVisible(true);
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
        ses.persist(newUser);
        transaction.commit();
        loginAdministratorController.initialize();
      } catch (Exception e) {
        errorMessage.setText("That username is already taken.");
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
          errorMessage.setVisible(true);
          transaction.rollback();
        }
      }
      ses.close();
      popOver.hide();
    }
  }

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
