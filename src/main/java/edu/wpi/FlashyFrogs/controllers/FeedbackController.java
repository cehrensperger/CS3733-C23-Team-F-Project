package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.controlsfx.control.PopOver;

public class FeedbackController {

  @FXML private MFXTextField feedback;
  @FXML private MFXTextField first;
  @FXML private MFXTextField middle;
  @FXML private MFXTextField last;
  @FXML private MFXComboBox department;
  @FXML private MFXTextField email;
  @FXML private MFXTextField phone;
  @FXML private MFXButton clear;
  @FXML private MFXButton back;
  @FXML private MFXButton submit;
  @FXML private MFXButton question;

  public void initialize() {
    department.getItems().addAll("Nursing", "Cardiology", "Radiology", "Maintenance");
  }

  public void handleClearButton(ActionEvent actionEvent) throws IOException {

    feedback.clear();
    first.clear();
    middle.clear();
    last.clear();
    department.clear();
    email.clear();
    phone.clear();
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQFeedback();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  @FXML
  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home", "views");
  }

  @FXML
  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Confirmation", "views");
  }
}
