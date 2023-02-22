package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
import edu.wpi.FlashyFrogs.controllers.IController;
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class EditUserController implements IController {
  private PopOver popOver;
  private LoginAdministratorController loginAdministratorController;
  private HospitalUser currentUser;
  private UserLogin currentUserLogin;
  @FXML private TextField firstName;
  @FXML private TextField middleName;
  @FXML private TextField lastName;
  @FXML private TextField username;
  @FXML private PasswordField pass1;
  @FXML private PasswordField pass2;
  @FXML private SearchableComboBox<Department> deptBox;
  @FXML private SearchableComboBox<HospitalUser.EmployeeType> employeeType;
  @FXML private TextField rfid;
  @FXML private Label errorMessage;

  public void setPopOver(PopOver thePopOver) {
    this.popOver = thePopOver;
  }

  public void setLoginAdminController(LoginAdministratorController adminController) {
    this.loginAdministratorController = adminController;
  }

  public void initialize(String userName, UserLogin selectedUserLogin) {
    this.currentUserLogin = selectedUserLogin;
    this.currentUser = selectedUserLogin.getUser();
    this.firstName.setText(currentUser.getFirstName());
    this.middleName.setText(currentUser.getMiddleName());
    this.lastName.setText(currentUser.getLastName());
    this.username.setText(userName);
    this.deptBox.getSelectionModel().select(currentUser.getDepartment());
    this.employeeType.getSelectionModel().select(currentUser.getEmployeeType());
    this.rfid.setText(currentUserLogin.getRFIDBadge());

    initDepartment_EmpType(deptBox, employeeType);
  }

  static void initDepartment_EmpType(
      SearchableComboBox<Department> deptBox,
      SearchableComboBox<HospitalUser.EmployeeType> employeeType) {
    Session session = CONNECTION.getSessionFactory().openSession();
    List<Department> objects =
        session.createQuery("SELECT d FROM Department d", Department.class).getResultList();

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

  public void saveChanges(ActionEvent actionEvent) throws Exception {
    if (username.getText().equals("")
        || pass1.getText().equals("")
        || pass2.getText().equals("")
        || firstName.getText().equals("")
        || lastName.getText().equals("")
        || deptBox.getValue() == null
        || employeeType.getValue() == null) {
      // One of the values is left null
      errorMessage.setText("Please fill out all fields!");
      errorMessage.setVisible(true);
    } else if (!pass1.getText().equals(pass2.getText())) {
      // Passwords do not match
      errorMessage.setText("Passwords do not match!");
      errorMessage.setVisible(true);
    } else {
      // Save Changes
      errorMessage.setVisible(false);

      currentUserLogin.setUserName(username.getText());
      currentUserLogin.setPassword(pass1.getText());

      Session ses;
      Transaction transaction;
      ses = CONNECTION.getSessionFactory().openSession();
      try {
        transaction = ses.beginTransaction();

        currentUser.setDepartment(deptBox.getValue());
        currentUser.setFirstName(firstName.getText());
        currentUser.setLastName(lastName.getText());
        currentUser.setMiddleName(middleName.getText());
        currentUser.setEmployeeType(employeeType.getValue());
        currentUserLogin.setUserName(username.getText());
        currentUserLogin.setPassword(pass1.getText());
        if (rfid.getText() == null || rfid.getText().equals("")) {
          currentUserLogin.setRFIDBadge(null);
        } else {
          currentUserLogin.setRFIDBadge(rfid.getText());
        }
        currentUserLogin.setUser(currentUser);

        ses.merge(currentUser);
        ses.merge(currentUserLogin);

        transaction.commit();
        ses.close();
      } catch (HibernateException e) {
        ses.close();
      }
      loginAdministratorController.initialize();
      popOver.hide();
    }
  }

  public void deleteUser(ActionEvent actionEvent) throws Exception {
    Session ses = CONNECTION.getSessionFactory().openSession();
    if (currentUserLogin == null) {
      errorMessage.setText("No user selected for deletion");
      errorMessage.setVisible(true);
    }
    if (currentUser.equals(CurrentUserEntity.CURRENT_USER.getCurrentUser())) {
      errorMessage.setText("Cannot delete current account");
      errorMessage.setVisible(true);
    } else {
      errorMessage.setVisible(false);
      ses.beginTransaction();
      ses.createMutationQuery("delete FROM HospitalUser user WHERE id=:ID")
          .setParameter("ID", currentUser.getId())
          .executeUpdate();
      ses.getTransaction().commit();
      ses.close();
      loginAdministratorController.initialize();
      popOver.hide();
    }
  }

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
