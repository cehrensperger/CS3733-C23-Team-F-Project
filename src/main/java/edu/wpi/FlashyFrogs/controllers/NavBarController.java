package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Theme;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

@GeneratedExclusion
public class NavBarController {

  @FXML private MenuItem menuToggleSFX;
  @FXML private MenuItem loggedOutMenuToggleSFX;
  @FXML private AnchorPane anchorPane;
  // @FXML private HBox header;
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
  @FXML private MenuButton menu;
  @FXML private MenuButton loggedOutMenu;
  @FXML private Label clockLabel;
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
    // header.setDisable(true);
    // header.setOpacity(0);
    updateToggleSFX();
    navButtons.setVisible(false);
    navButtons.setDisable(true);
    navButtons.setOpacity(0);
    //    srButton.setOpacity(0);
    //    srButton.setDisable(true);
    //    homeButton.setOpacity(0);
    //    homeButton.setDisable(true);
    //    helpButton.setOpacity(0);
    //    helpButton.setDisable(true);
    //    line1.setOpacity(0);
    //    line2.setOpacity(0);

    dateAndTime();
    // clockLabel.setTextFill(Paint.valueOf("white"));
  }

  /**
   * Updates the text on the menu item that toggles sound effects on and off to reflect whether
   * clicking the item will turn sound effects on or off: clicking the menu item will turn sound
   * effects on if sound effects are currently set to off, and vice versa
   */
  private void updateToggleSFX() {
    // if sounds are turned on, make menu option say that it turns sound off
    if (Fapp.isSfxOn()) {
      loggedOutMenuToggleSFX.setText("Turn Sound Effects Off");
      menuToggleSFX.setText("Turn Sound Effects Off");
      // if sounds are turned off, make menu option say that it turns sound on
    } else {
      loggedOutMenuToggleSFX.setText("Turn Sound Effects On");
      menuToggleSFX.setText("Turn Sound Effects On");
    }
  }

  public void logIn() {
    loggedOutMenu.setDisable(true);
    loggedOutMenu.setVisible(false);
    menu.setVisible(true);
    loggedOutMenu.hide();
    loggedOutMenu.setText("");
    updateToggleSFX();
    menu.setDisable(false);
    // header.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentUser().getFirstName());
    // header.setOpacity(1);
    // header.setOpacity(1);
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
    //    System.out.println("in");
    toastAnimationForward();
  }

  @FXML
  private void hideDescriptions(MouseEvent event) throws IOException {
    toastAnimationBackward();
    //    System.out.println("out");
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
    Fapp.setScene("Alerts", "AlertManager");
    //    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/AlertManager.fxml"));
    //    PopOver popOver = new PopOver(newLoad.load()); // create the popover
    //    HomeController home = new HomeController();
    //
    //    AlertManagerController controller = newLoad.getController();
    //    controller.setPopOver(popOver);
    //
    //    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    //    javafx.scene.Node node =
    //        (javafx.scene.Node) event.getSource(); // Get the node representation of what called
    // this
    //    popOver.show(node); // display the popover
    //
    //    popOver
    //        .showingProperty()
    //        .addListener(
    //            (observable, oldValue, newValue) -> {
    //              if (!newValue) {
    //                home.refreshAlerts();
    //              }
    //            });
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
    Fapp.setScene("Accounts", "Login");
    signUserOutWithoutSceneChange();
  }

  /** */
  public void signUserOutWithoutSceneChange() {
    CurrentUserEntity.CURRENT_USER.setCurrentUser(null);
    menu.setText("");
    menu.setDisable(true);
    loggedOutMenu.setVisible(true);
    menu.setVisible(false);
    menu.hide();
    loggedOutMenu.setDisable(false);
    loggedOutMenu.setText("Welcome, Guest");
    // header.setDisable(true);
    // header.setOpacity(0);
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
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("About/About.fxml"));
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
  @FXML
  private void changeMode() throws IOException {
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

  /**
   * If sound effects were off, turn them on and say that clicking the menu option again will turn
   * them off. If sound effects were on, turn them off and say that clicking the menu option again
   * will turn them on.
   *
   * @param actionEvent
   */
  @FXML
  private void toggleSFX(ActionEvent actionEvent) {
    MenuItem menu = (MenuItem) actionEvent.getSource();
    if (Fapp.isSfxOn()) {
      Fapp.setSfxOn(false);
      menu.setText("Turn Sound Effects On");
    } else {
      Fapp.setSfxOn(true);
      menu.setText("Turn Sound Effects Off");
    }
  }

  public void dateAndTime() {
    Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.seconds(0),
                event -> {
                  SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm a");
                  Date date = new Date();
                  String formattedDate = sdf.format(date);
                  clockLabel.setText(formattedDate);
                }),
            new KeyFrame(Duration.seconds(1)));
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
  }
}
