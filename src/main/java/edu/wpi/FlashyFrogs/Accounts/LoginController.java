package edu.wpi.FlashyFrogs.Accounts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.DBConnection;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.User;
import edu.wpi.FlashyFrogs.ORM.UserLogin;
// import edu.wpi.FlashyFrogs.controllers.ForgotPassController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class LoginController implements IController {

  @FXML private AnchorPane rootPane;
  @FXML private TextField username;
  @FXML private PasswordField password;
  @FXML private MFXButton login;
  @FXML private MFXButton clear;
  @FXML Text forgot;
  @FXML private Label errorMessage;

  public void initialize() {
    rootPane
        .getStylesheets()
        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off all
    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    if (Fapp.isLightMode()) {
      rootPane
          .getStylesheets()
          .add("edu/wpi/FlashyFrogs/views/Css.css"); // apply Light Mode styling
      rootPane
          .getStylesheets()
          .add("edu/wpi/FlashyFrogs/views/label-override.css"); // usually the text color in label
      // elements is black in Light Mode, but the upper left menu on the Login page would be hard to
      // read with black text,
      // so for this page we change the label text color to white.
    } else { // we are not in Dark Mode, so
      rootPane
          .getStylesheets()
          .add("edu/wpi/FlashyFrogs/views/dark-mode.css"); // apply Dark Mode styling
    }
  }

  public void loginButton(ActionEvent actionEvent) throws Exception {
    if (username.getText().equals("") || password.getText().equals("")) {
      // One of the values is left null
      errorMessage.setText("Please fill out all fields!");
      errorMessage.setVisible(true);
    } else {
      Session ses = CONNECTION.getSessionFactory().openSession();
      try {
        UserLogin logIn =
            ses.createQuery("FROM UserLogin where userName = :username", UserLogin.class)
                .setParameter("username", username.getText())
                .getSingleResult();
        if (logIn == null) { // Username does not exist in database
          throw new Exception();
        } else if (!logIn.checkPasswordEqual(
            password.getText())) { // Username's Password is not equal to what was inputted
          throw new Exception();
        } else { // Username and Password match database
          Fapp.setScene("views", "Home");
          Fapp.logIn();
          CurrentUserEntity.CURRENT_USER.setCurrentUser(logIn.getUser());
        }
        ses.close();
      } catch (Exception e) {
        errorMessage.setText("Invalid Username or Password.");
        errorMessage.setVisible(true);
        ses.close();
      }
    }
  }

  public void forgotPass(MouseEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/ForgotPass.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    //    ForgotPassController forgotPass = newLoad.getController();
    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  public void handleClose(ActionEvent actionEvent) throws IOException {
    Stage stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    username.clear();
    password.clear();
  }

  public void handleNewUser(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "LoginAdministrator");
  }

  public void onClose() {}
}
