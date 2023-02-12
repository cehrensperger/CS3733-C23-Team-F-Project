package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

public class HomeController {
  @FXML private StackPane rootPane;
  @FXML private ImageView backgroundImage;
  @FXML private TextArea AboutText;

  Stage stage;

  public void initialize() {
    stage = Fapp.getPrimaryStage();
    backgroundImage.fitHeightProperty().bind(stage.heightProperty());
    backgroundImage.fitWidthProperty().bind(stage.widthProperty());
    // ensure that Home doesn't lose its styling upon leaving the page and returning to it
    if (Fapp.isLightMode()) {
      setToLightMode();
    } else {
      setToDarkMode();
    }
  }

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    stage = (Stage) rootPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
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
    Fapp.setScene("views", "RequestsHome");
  }

  public void handleMapDataEditorButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("MapEditor", "MapEditorView");
  }

  public void handlePathfindingButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "PathFinding");
  }

  public void handleSecurityMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SecurityService");
  }

  public void handleTransportMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Transport");
  }

  public void handleSanitationMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SanitationService");
  }

  public void handleAudioVisualMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "AudioVisualService");
  }

  public void handleComputerMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "ComputerService");
  }

  public void handleLoadMapMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "LoadMapPage");
  }

  public void handleFeedbackMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Feedback");
  }

  /**
   * Change the color theme to Light Mode when the Color Scheme > Light Mode option is selected on
   * Home.fxml.
   *
   * @param actionEvent
   * @throws IOException
   */
  public void changeToLightMode(ActionEvent actionEvent) throws IOException {
    setToLightMode();
  }

  /**
   * Call to set Home.fxml to light mode. Also makes some tweaks to JavaFX elements specific to
   * Home.fxml, so not all of this method is generalizable to setting any page to light mode.
   */
  public void setToLightMode() {
    rootPane
        .getStylesheets()
        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off all
    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    rootPane
        .getStylesheets()
        .add("edu/wpi/FlashyFrogs/views/light-mode.css"); // add the light mode CSS
    AboutText.setBlendMode(
        BlendMode.DARKEN); // change the Blend Mode on the text box describing the hospital, as the
    // Blend Mode used for Light Mode does not give the desired appearance
    rootPane
        .getStylesheets()
        .add("edu/wpi/FlashyFrogs/views/label-override.css"); // usually the text color in label
    // elements is black in Light Mode, but the upper left menu on the Home page would be hard to
    // read with black text,
    // so for this page we change the label text color to white.
    Fapp.setLightMode(true); // set the isLightMode variable to true, as we switched to Light Mode
  }

  /**
   * Change the color theme to Dark Mode when the Color Scheme > Dark Mode option is selected on
   * Home.fxml.
   *
   * @param actionEvent not used
   */
  public void changeToDarkMode(ActionEvent actionEvent) {
    setToDarkMode();
  }

  /**
   * Call to set Home.fxml to dark mode. Also makes some tweaks to JavaFX elements specific to
   * Home.fxml, so not all of this method is generalizable to setting any page to dark mode.
   */
  public void setToDarkMode() {
    rootPane
        .getStylesheets()
        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off all
    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    rootPane
        .getStylesheets()
        .add("edu/wpi/FlashyFrogs/views/dark-mode.css"); // add the dark mode CSS
    AboutText.setBlendMode(
        BlendMode.SOFT_LIGHT); // change the Blend Mode on the text box describing the hospital, as
    // using Light Mode's Blend Mode (DARKEN) on this will make all the text in the box invisible;
    // SOFT_LIGHT keeps it
    // visible and somewhat preserves the transparency idea shown in Light Mode
    AboutText.setStyle(
        "-fx-text-fill: #2f2f2f;"); // usually the text color in text-area elements is white in Dark
    // Mode,
    // but the text-area element on this page, the one describing the hospital, would be hard to
    // read with white, so for
    // this page we change the color to black/gray.
    Fapp.setLightMode(false); // set the isLightMode variable to false, as we switched to Dark Mode
  }

  public void handleLogOut(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Login");
  }

  public void secretMethod(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Home2");
  }
}
