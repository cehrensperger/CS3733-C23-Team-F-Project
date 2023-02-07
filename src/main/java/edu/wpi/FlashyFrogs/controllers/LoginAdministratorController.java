package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

public class LoginAdministratorController {

  @FXML private MFXButton back;
  @FXML private TableView tableView;

  @FXML private MFXButton addNewUser;

  private TableColumn username;

  private TableColumn password;

  private TableColumn salt;

  private TableView<UserLogin> userLoginTable;

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Login");
  }

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    PopOver popOver =
        new PopOver(
            FXMLLoader.load(
                Objects.requireNonNull(getClass().getResource("../views/NewUser.fxml"))));
    popOver.detach();
    Node node = (Node) actionEvent.getSource();
    popOver.show(node.getScene().getWindow());
  }

  public void initialize() {

    // set columns userlogin

    username.setCellValueFactory(new PropertyValueFactory<>("userName"));
    password.setCellValueFactory(new PropertyValueFactory<>("password"));
    salt.setCellValueFactory(new PropertyValueFactory<>("salt"));

    // create logIn table
    // open session
    Session ses = CONNECTION.getSessionFactory().openSession();
    List<edu.wpi.FlashyFrogs.ORM.UserLogin> userLoginObjects =
            ses.createQuery("SELECT s FROM UserLogin s", edu.wpi.FlashyFrogs.ORM.UserLogin.class)
                    .getResultList(); // select everything from userLogin table and add to list
    ObservableList<edu.wpi.FlashyFrogs.ORM.UserLogin> userLoginObservableList =
            FXCollections.observableList(userLoginObjects); // convert list to ObservableList
    ses.close();
    userLoginTable
            .getItems()
            .addAll(userLoginObservableList); // add every item in observable list to moveTable
  }
}
