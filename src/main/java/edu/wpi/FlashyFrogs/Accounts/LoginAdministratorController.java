package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Department;
import edu.wpi.FlashyFrogs.ORM.User;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

@GeneratedExclusion
public class LoginAdministratorController implements IController {

  @FXML private TableView<UserLogin> tableView;
  @FXML private TableView<UserLogin> userLoginTable;
  @FXML private TableColumn<UserLogin, Number> idCol;
  @FXML private TableColumn<UserLogin, String> userNameCol;
  @FXML private TableColumn<UserLogin, String> nameCol;
  @FXML private TableColumn<UserLogin, User.EmployeeType> empTypeCol;
  @FXML private TableColumn<UserLogin, Department> deptCol;
  @FXML private Button addNewUser;
  @FXML private Button back;

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.handleBack();
  }

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("../Accounts/NewUser.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    NewUserController newUser = newLoad.getController();
    newUser.setPopOver(popOver);
    newUser.setLoginAdminController(this);
    popOver.detach();
    Node node = (Node) actionEvent.getSource();
    popOver.show(node.getScene().getWindow());

    addNewUser.setDisable(true);
    back.setDisable(true);
    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                addNewUser.setDisable(false);
                back.setDisable(false);
              }
            });
  }

  public void initialize() throws Exception {

    // Clear old table before init
    userLoginTable.getItems().clear();
    // set columns userlogin

    idCol.setCellValueFactory(
        data -> {
          User user = data.getValue().getUser();
          return new SimpleLongProperty(user.getId());
        });
    userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
    nameCol.setCellValueFactory(
        data -> {
          User user = data.getValue().getUser();
          return new SimpleStringProperty(
              user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
        });
    empTypeCol.setCellValueFactory(
        data -> {
          User user = data.getValue().getUser();
          return new SimpleObjectProperty(user.getEmployeeType());
        });
    deptCol.setCellValueFactory(
        data -> {
          User user = data.getValue().getUser();
          return new SimpleObjectProperty(user.getDepartment());
        });

    // create logIn table
    // open session
    ObservableList<UserLogin> userLoginObservableList = null; // convert list to ObservableList
    Session ses = CONNECTION.getSessionFactory().openSession();
    try {
      List<UserLogin> userLoginObjects =
          ses.createQuery("SELECT s FROM UserLogin s", UserLogin.class)
              .getResultList(); // select everything from userLogin table and add to list
      userLoginObservableList = FXCollections.observableList(userLoginObjects);
      ses.close();
      userLoginTable
          .getItems()
          .addAll(userLoginObservableList); // add every item in observable list to moveTable
    } catch (Exception e) {
      ses.close();
      throw e;
    }
  }

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }
}
