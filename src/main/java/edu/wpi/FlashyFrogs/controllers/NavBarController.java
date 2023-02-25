package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Theme;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

@GeneratedExclusion
public class NavBarController {

  @FXML private AnchorPane anchorPane;
  @FXML private HBox header;
  @FXML private MFXButton helpButton;

  @FXML private Pane toast;
  @FXML private MFXButton back;
  @FXML private MFXButton moveVisualizer;
  @FXML private MFXButton loginManager;
  @FXML private MFXButton csvManager;
  @FXML private MFXButton mapEditor;
  @FXML private MFXButton pathfinding;
  @FXML private MFXButton security;
  @FXML private MFXButton sanitation;
  @FXML private MFXButton religious;
  @FXML private MFXButton medicine;
  @FXML private MFXButton transport;
  @FXML private MFXButton itSupport;
  @FXML private MFXButton avService;
  @FXML private MFXButton srButton;
  @FXML private MFXButton homeButton;
  @FXML private MFXButton alertsButton;

  @FXML private MFXButton helpButton2;
  @FXML private MFXButton back2;
  @FXML private MFXButton moveVisualizer2;
  @FXML private MFXButton loginManager2;
  @FXML private MFXButton csvManager2;
  @FXML private MFXButton mapEditor2;
  @FXML private MFXButton pathfinding2;
  @FXML private MFXButton security2;
  @FXML private MFXButton sanitation2;
  @FXML private MFXButton religious2;
  @FXML private MFXButton medicine2;
  @FXML private MFXButton transport2;
  @FXML private MFXButton itSupport2;
  @FXML private MFXButton avService2;
  @FXML private Button srButton2;
  @FXML private Button homeButton2;
  @FXML private MFXButton alertsButton2;

  @FXML private StackPane mapStack;
  @FXML private StackPane mapStack2;
  @FXML private StackPane csvStack;
  @FXML private StackPane csvStack2;
  @FXML private StackPane loginStack;
  @FXML private StackPane loginStack2;
  @FXML private StackPane alertsStack;
  @FXML private StackPane alertsStack2;
  @FXML private StackPane moveStack;
  @FXML private StackPane moveStack2;

  @FXML private SVGPath SVGMap;
  @FXML private SVGPath SVGCSV;
  @FXML private SVGPath SVGLogin;
  @FXML private SVGPath SVGAlerts;
  @FXML private SVGPath SVGMove;

  @FXML private Text mapText;
  @FXML private Text CSVText;
  @FXML private Text loginText;
  @FXML private Text alertsText;
  @FXML private Text moveText;

  @FXML private VBox navButtons;
  @FXML private VBox navDescriptions;
  @FXML private MenuButton menu;
  @FXML private MenuButton loggedOutMenu;
  boolean isAdmin;

  @FXML
  public void initialize() {
    loggedOutMenu.setText("Welcome, Guest");
    loggedOutMenu.show();
    loggedOutMenu.setVisible(true);
    loggedOutMenu.setDisable(false);
    menu.setVisible(false);
    menu.setDisable(true);
    menu.hide();
    header.setDisable(true);
    header.setOpacity(0);

    navButtons.setVisible(false);
    navButtons.setDisable(true);
    navButtons.setOpacity(0);
  }

  public void logIn() {
    loggedOutMenu.setDisable(true);
    loggedOutMenu.setVisible(false);
    menu.setVisible(true);
    loggedOutMenu.hide();
    loggedOutMenu.setText("");
    menu.setDisable(false);
    header.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentUser().getFirstName());
    header.setOpacity(1);
    isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    navButtons.setVisible(true);
    navButtons.setDisable(false);
    navButtons.setOpacity(1);

    if (!isAdmin) {
      mapEditor.setDisable(true);
      mapEditor2.setDisable(true);
      csvManager.setDisable(true);
      csvManager2.setDisable(true);
      loginManager.setDisable(true);
      loginManager2.setDisable(true);
      alertsButton.setDisable(true);
      alertsButton2.setDisable(true);
      moveVisualizer.setDisable(true);
      moveVisualizer2.setDisable(true);
      SVGMap.setOpacity(0);
      SVGCSV.setOpacity(0);

      SVGLogin.setOpacity(0);

      SVGAlerts.setOpacity(0);
      SVGMove.setOpacity(0);
      mapText.setOpacity(0);
      CSVText.setOpacity(0);
      loginText.setOpacity(0);
      alertsText.setOpacity(0);
      moveText.setOpacity(0);

    } else {
      mapEditor.setDisable(false);
      mapEditor2.setDisable(false);
      csvManager.setDisable(false);
      csvManager2.setDisable(false);
      loginManager.setDisable(false);
      loginManager2.setDisable(false);
      alertsButton.setDisable(false);
      alertsButton2.setDisable(false);
      moveVisualizer.setDisable(false);
      moveVisualizer2.setDisable(false);
      SVGMap.setOpacity(1);
      SVGCSV.setOpacity(1);
      SVGLogin.setOpacity(1);
      SVGAlerts.setOpacity(1);
      SVGMove.setOpacity(1);
      mapText.setOpacity(1);
      CSVText.setOpacity(1);
      loginText.setOpacity(1);
      alertsText.setOpacity(1);
      moveText.setOpacity(1);
    }
  }

  public AnchorPane getAnchorPane() {
    return anchorPane;
  }

  @FXML
  private void showDescriptions(MouseEvent event) throws IOException {
    System.out.println("in");
    toastAnimationForward();
  }

  @FXML
  private void hideDescriptions(MouseEvent event) throws IOException {
    toastAnimationBackward();
    System.out.println("out");
  }

  @FXML
  private void handleHomeButton(ActionEvent event) throws IOException {
    Fapp.setScene("views", "Home");
  }

  @FXML
  private void handleMoveVisualizer(ActionEvent event) throws IOException {
    Fapp.setScene("MoveVisualizer", "MoveVisualizer");
  }

  @FXML
  private void handleSignOut(ActionEvent event) throws IOException {
    Fapp.setScene("Account", "Login");
    navButtons.setVisible(false);
    navButtons.setDisable(true);
    navButtons.setOpacity(0);
  }

  @FXML
  private void handleServiceRequestsButton(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
  }

  @FXML
  private void handleLoginManager(ActionEvent event) throws IOException {
    Fapp.setScene("Accounts", "LoginAdministrator");
  }

  @FXML
  private void handleMapEditor(ActionEvent event) throws IOException {
    Fapp.setScene("MapEditor", "MapEditorView");
  }

  @FXML
  private void handlePathfinding(ActionEvent event) throws IOException {
    Fapp.setScene("Pathfinding", "Pathfinding");
  }

  @FXML
  private void handleSecurity(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  @FXML
  private void handleSanitation(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  @FXML
  private void handleReligious(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "ReligiousService");
  }

  @FXML
  private void handleMedicine(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "MedicineService");
  }

  @FXML
  private void handleTransport(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "TransportService");
  }

  @FXML
  private void handleITSupport(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "ComputerService");
  }

  @FXML
  private void handleAVService(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  @FXML
  private void handleCSVManager(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/CSVUpload.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover
    HomeController home = new HomeController();

    popOver.setTitle("CSV Manager");
    CSVUploadController controller = newLoad.getController();
    controller.setPopOver(popOver);

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node) event.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover

    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                home.refreshTable();
              }
            });
  }

  @FXML
  private void handleAlerts(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/AlertManager.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover
    HomeController home = new HomeController();

    AlertManagerController controller = newLoad.getController();
    controller.setPopOver(popOver);

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node) event.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover

    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                home.refreshAlerts();
              }
            });
  }

  @FXML
  private void handleHelpButton(ActionEvent event) throws IOException {
    if (Fapp.iController != null) {
      Fapp.iController.help();
    }
  }

  @FXML
  private void handleBack(ActionEvent event) throws IOException {
    Fapp.handleBack();
  }

  @FXML
  private void closeApp() {
    Stage stage = (Stage) anchorPane.getScene().getWindow();
    stage.close();
  }

  @FXML
  private void signOut() {
    CurrentUserEntity.CURRENT_USER.setCurrentUser(null);
    Fapp.setScene("Accounts", "Login");
    menu.setText("");
    menu.setDisable(true);
    loggedOutMenu.setVisible(true);
    menu.setVisible(false);
    menu.hide();
    loggedOutMenu.setDisable(false);
    loggedOutMenu.setText("Welcome, Guest");
    header.setDisable(true);
    header.setOpacity(0);
    navButtons.setVisible(false);
    navButtons.setDisable(true);
    navButtons.setOpacity(0);
  }

  /**
   * Launch About page when About This Application is clicked in the menu bar.
   *
   * @throws IOException
   */
  @FXML
  private void about() throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/About.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    popOver.detach();
    popOver.show(anchorPane.getScene().getWindow());
  }

  /**
   * Launch Acknowledgments page when Acknowledgments is clicked in the menu bar.
   *
   * @throws IOException
   */
  @FXML
  private void acknowledgments() throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/acknowledgmentsPage.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    popOver.detach();
    popOver.show(anchorPane.getScene().getWindow());
  }

  /**
   * Change the color theme between Dark and Light Mode when the Switch Color Scheme button is
   * clicked on NavBar.fxml.
   *
   * @throws IOException
   */
  public void changeMode() throws IOException {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      Fapp.setTheme(Theme.DARK_THEME);
    } else {
      Fapp.setTheme(Theme.LIGHT_THEME);
    }
  }

  public void toastAnimationForward() {
    // Create a TranslateTransition to move the first rectangle to the left
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.2), toast);
    translate1.setByX(210);
    navButtons.setOnMouseEntered(e -> {});
    translate1.setOnFinished(
        e -> {
          toast.setOnMouseExited(
              event -> {
                try {
                  hideDescriptions(event);
                } catch (IOException ex) {
                  throw new RuntimeException(ex);
                }
              });
        });

    // Play the animations in sequence

    translate1.play();
  }

  public void toastAnimationBackward() {

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.2), toast);

    toast.setOnMouseExited(e -> {});

    translateBack1.setOnFinished(
        e -> {
          navButtons.setOnMouseEntered(
              event -> {
                try {
                  showDescriptions(event);
                } catch (IOException ex) {
                  throw new RuntimeException(ex);
                }
              });
        });

    //    translateBack1.setDelay(Duration.seconds(2));
    translateBack1.setByX(-210);

    // Play the animations in sequence
    translateBack1.play();
  }
}
