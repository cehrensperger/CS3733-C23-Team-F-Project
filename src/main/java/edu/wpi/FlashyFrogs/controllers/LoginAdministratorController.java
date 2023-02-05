package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import org.controlsfx.control.PopOver;

public class LoginAdministratorController {

  @FXML private MFXButton back;
  @FXML private TableView tableView;

  @FXML private MFXButton addNewUser;

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

    // username.setCellValueFactory(new PropertyValueFactory<>("Username"));
    // password.setCellValueFactory(new PropertyValueFactory<>("Password"));
  }
}
