package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class LoginAdministratorController {

  @FXML private MFXButton back;
  @FXML private TableView<UserLogin> tableView;

  @FXML private MFXButton addNewUser;

  @FXML private TableView<UserLogin> userLoginTable;
  @FXML private TableColumn<UserLogin, String> userName;
  @FXML private TableColumn<UserLogin, String> password;

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Login");
  }

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("../views/NewUser.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    NewUserController newUser = newLoad.getController();
    newUser.setPopOver(popOver);

    popOver.detach();
    Node node = (Node) actionEvent.getSource();
    popOver.show(node.getScene().getWindow());
  }

  public void initialize() throws Exception {

    // set columns userlogin

    userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
    password.setCellValueFactory(new PropertyValueFactory<>("hash"));

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
      // TODO Show a popup or something
      throw e;
    }
  }
}
