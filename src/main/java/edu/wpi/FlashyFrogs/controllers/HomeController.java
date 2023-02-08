package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

public class HomeController {
  @FXML private StackPane rootPane;
  @FXML private MFXButton serviceRequestsButton;
  @FXML private MFXButton mapDataEditorButton;
  @FXML private MFXButton pathfindingButton;
  @FXML private MFXButton question;
  @FXML private MFXButton exitButton;
  @FXML private MenuItem closeMenuItem;
  @FXML private MenuItem loadMapMenuItem;
  @FXML private MenuItem loadFeedbackMenuItem;
  @FXML private MenuItem logoutMenuItem;
  @FXML private MFXButton hiddneButton;
  @FXML private ImageView backgroundImage;
  @FXML private MFXButton secretButton;
  @FXML private TextArea AboutText;

  Stage stage;

  public void initialize() {
    stage = Fapp.getPrimaryStage();
    backgroundImage.fitHeightProperty().bind(stage.heightProperty());
    backgroundImage.fitWidthProperty().bind(stage.widthProperty());
    if (Fapp.isLightMode()) {
      changeToLightMode();
    } else {
      changeToDarkMode();
    }
  }

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("../views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQHome();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  @FXML
  public void handleClose(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  public void handleServiceRequestsButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("RequestsHome");
  }

  public void handleMapDataEditorButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("MapEditorView");
  }

  public void handlePathfindingButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("PathFinding");
  }

  public void handleSecurityMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SecurityService");
  }

  public void handleTransportMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Transport");
  }

  public void handleSanitationMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("SanitationService");
  }

  public void handleAudioVisualMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("AudioVisualService");
  }

  public void handleComputerMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ComputerService");
  }

  public void handleLoadMapMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("LoadMapPage");
  }

  public void handleFeedbackMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Feedback");
  }

  public void changeToLightModeFromButtonPress(ActionEvent actionEvent) throws IOException {
    changeToLightMode();
  }

  public void changeToLightMode() {
    rootPane.getStylesheets().clear();
    rootPane.getStylesheets().add("edu/wpi/FlashyFrogs/views/light-mode.css");
    AboutText.setBlendMode(BlendMode.DARKEN);
    rootPane.getStylesheets().add("edu/wpi/FlashyFrogs/views/label-override.css");
    Fapp.setLightMode(true);
  }

  // invoked only by the application
  public void changeToDarkModeFromButtonPress(ActionEvent actionEvent) throws IOException {
    changeToDarkMode();
  }

  public void changeToDarkMode() {
    rootPane.getStylesheets().clear();
    rootPane.getStylesheets().add("edu/wpi/FlashyFrogs/views/dark-mode.css");
    AboutText.setBlendMode(BlendMode.SOFT_LIGHT);
    AboutText.setStyle("-fx-text-fill: #2f2f2f;");
    Fapp.setLightMode(false);
  }

  public void handleLogOut(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Login");
  }

  public void secretMethod(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home2");
  }
}
