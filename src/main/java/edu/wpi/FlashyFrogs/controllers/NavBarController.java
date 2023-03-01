package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.Theme;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

  @FXML private AnchorPane sidePane;
  @FXML private MenuItem menuToggleSFX;
  @FXML private MenuItem loggedOutMenuToggleSFX;

  @FXML private MenuItem menuToggleTheme;
  @FXML private MenuItem loggedOutMenuToggleTheme;

  @FXML private AnchorPane anchorPane;
  // @FXML private HBox header;
  @FXML private Button helpButton;

  @FXML private VBox toast;
  @FXML private Text backText;
  @FXML private Text heatText;
  @FXML private SVGPath heatSVG;
  @FXML Button heat;
  @FXML Button heat2;
  @FXML Button stats;
  @FXML Button stats2;
  @FXML SVGPath statsSVG;
  @FXML Text statsText;

  @FXML private Button back;
  @FXML private SVGPath backSVG;
  @FXML private Button moveVisualizer;
  @FXML private Button loginManager;
  @FXML private Button csvManager;
  @FXML private Button mapEditor;
  @FXML private Button pathfinding;
  @FXML private Button security;
  @FXML private Button sanitation;
  @FXML private Button religious;
  @FXML private Button medicine;
  @FXML private Button transport;
  @FXML private Button itSupport;
  @FXML private Button avService;
  @FXML private Button equipmentTransport;
  @FXML private Button equipmentTransport2;
  @FXML private Button srButton;
  @FXML private Button homeButton;
  @FXML private Button alertsButton;

  @FXML private Button helpButton2;
  @FXML private Button back2;
  @FXML private Button moveVisualizer2;
  @FXML private Button loginManager2;
  @FXML private Button csvManager2;
  @FXML private Button mapEditor2;
  @FXML private Button pathfinding2;
  @FXML private Button security2;
  @FXML private Button sanitation2;
  @FXML private Button religious2;
  @FXML private Button medicine2;
  @FXML private Button transport2;
  @FXML private Button itSupport2;
  @FXML private Button avService2;
  @FXML private Button srButton2;
  @FXML private Button homeButton2;
  @FXML private Button alertsButton2;

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

    menu.hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {

              } else {

              }
            });
    // header.setDisable(true);
    // header.setOpacity(0);
    updateToggleSFX();
    updateMode();
    navButtons.setVisible(false);
    navButtons.setDisable(true);
    navButtons.setOpacity(0);
    toast.setVisible(false);
    toast.setDisable(true);
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

    // Setup the nav button hover handlers
    for (int i = 0; i < navButtons.getChildren().size(); i++) {
      // If it's not a VBox (separate stuff)
      if (navButtons.getChildren().get(i).getClass()
          != VBox.class) { // If it's an actual nav button
        int finalI = i; // thanks java :)

        // Set the nav button hover
        navButtons
            .getChildren()
            .get(i)
            .hoverProperty()
            .addListener(
                (observable, oldValue, newValue) ->
                    handleHoverChange(newValue, finalI, -1)); // hover change

        // Set the toast hover
        toast
            .getChildren()
            .get(i)
            .hoverProperty()
            .addListener(
                (observable, oldValue, newValue) ->
                    handleHoverChange(newValue, finalI, -1)); // Hover change
      } else {
        // It's more complicated if it's a VBox - we need to figure out the children of that
        VBox child = (VBox) navButtons.getChildren().get(i); // Get the VBox

        int finalI1 = i; // Thanks Java :)

        // For each of the children in the VBox
        for (int j = 0; j < child.getChildren().size(); j++) {
          int finalJ = j; // Thanks Java :)

          // Set the hover on the child
          child
              .getChildren()
              .get(j)
              .hoverProperty()
              .addListener(
                  // Since the VBox is at the end, we add the indexes so the hover handle change
                  // listener
                  // can find both the VBox and the property
                  (observable, oldValue, newValue) -> handleHoverChange(newValue, finalJ, finalI1));

          // Do the same with the toast
          ((VBox) toast.getChildren().get(i))
              .getChildren()
              .get(j)
              .hoverProperty()
              .addListener(
                  (observable, oldValue, newValue) -> handleHoverChange(newValue, finalJ, finalI1));
        }
      }
    }

    sidePane
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                toastAnimationForward();
              } else {
                toastAnimationBackward();
              }
            });
  }

  /**
   * Listener for hover changing
   *
   * @param hover the new hover state
   * @param itemNumber the index of the item within the pane
   * @param paneNumber the index of the pane in the super. If this is -1, the root panes will be
   *     used
   */
  private void handleHoverChange(boolean hover, int itemNumber, int paneNumber) {
    // By default, use the root icon and root text to get the icons
    VBox iconRoot = navButtons;
    VBox textRoot = toast;

    // If we have a pane number set
    if (paneNumber != -1) {
      iconRoot = (VBox) navButtons.getChildren().get(paneNumber); // Get the icon pane at that index
      textRoot = (VBox) toast.getChildren().get(paneNumber); // Get the text pane at that index
    }

    // Stack panes for the thing that's having hover changed
    StackPane iconPane = (StackPane) iconRoot.getChildren().get(itemNumber); // Icon pane
    StackPane textPane = (StackPane) textRoot.getChildren().get(itemNumber); // Text pane

    // Concrete types of the relevant children
    SVGPath svg = null; // SVG path
    Text text = null; // Text

    // Check each icon child
    for (Node child : iconPane.getChildren()) {
      if (child.getClass() == SVGPath.class) {
        svg = (SVGPath) child;
      }
    }

    // Check each node child
    for (Node child : textPane.getChildren()) {
      if (child.getClass() == Text.class) {
        text = (Text) child;
      }
    }

    // Assert we found the stuff
    assert svg != null; // Assert SVG isn't null
    assert text != null; // Assert text isn't null

    // Clear SVG style
    svg.getStyleClass().clear();
    svg.getStyleClass()
        .add(hover ? "yellowNav" : "navSlide"); // Add either yellow nav or nav based on hover

    // Clear SVG style
    text.getStyleClass().clear();
    text.getStyleClass()
        .add(hover ? "yellowNav" : "navSlide"); // Add either yellow nav or nav based on hover

    // Scale transition
    ScaleTransition iconTransition = new ScaleTransition(Duration.seconds(.05), iconPane);
    iconTransition.setToX(hover ? 1.3 : 1.0);
    iconTransition.setToY(hover ? 1.3 : 1.0);

    iconTransition.play();
  }

  public void refresh() {

    if (Fapp.getIController() != null) {
      IController current = Fapp.getIController();
      if (current.getClass() != HomeController.class) {
        backSVG.setOpacity(1);
        back2.setDisable(false);
        back.setDisable(false);
        backText.setVisible(true);
      } else {
        back.setDisable(true);
        backSVG.setOpacity(0);
        back2.setDisable(true);
        backText.setVisible(false);
      }
    }
  }

  /**
   * If sound effects were off, turn them on and say that clicking the menu option again will turn
   * them off. If sound effects were on, turn them off and say that clicking the menu option again
   * will turn them on.
   */
  @FXML
  private void toggleSFX() {
    if (Fapp.isSfxOn()) {
      Fapp.setSfxOn(false);
      loggedOutMenuToggleSFX.setText("Turn Sound Effects On");
      menuToggleSFX.setText("Turn Sound Effects On");
    } else {
      Fapp.setSfxOn(true);
      loggedOutMenuToggleSFX.setText("Turn Sound Effects Off");
      menuToggleSFX.setText("Turn Sound Effects Off");
    }
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

  /**
   * Change the color theme between Dark and Light Mode when the menu option to do so is clicked on
   * NavBar.fxml. Also changes message on the menu option based on whether clicking it will change
   * to Light or Dark Mode.
   *
   * @throws IOException
   */
  @FXML
  private void changeMode() throws IOException {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      Fapp.setTheme(Theme.DARK_THEME);
      menuToggleTheme.setText("Switch to Light Mode");
      loggedOutMenuToggleTheme.setText("Switch to Light Mode");
    } else {
      Fapp.setTheme(Theme.LIGHT_THEME);
      menuToggleTheme.setText("Switch to Dark Mode");
      loggedOutMenuToggleTheme.setText("Switch to Dark Mode");
    }
  }

  /**
   * If Light Mode is on, say that clicking the menu option again will switch to Dark Mode. If Dark
   * Mode is on, say that clicking the menu option again will switch to Light Mode.
   */
  private void updateMode() {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      menuToggleTheme.setText("Switch to Dark Mode");
      loggedOutMenuToggleTheme.setText("Switch to Dark Mode");
    } else {
      menuToggleTheme.setText("Switch to Light Mode");
      loggedOutMenuToggleTheme.setText("Switch to Light Mode");
    }
  }

  public void logIn() {
    loggedOutMenu.setDisable(true);
    loggedOutMenu.setVisible(false);
    menu.setVisible(true);
    loggedOutMenu.hide();
    loggedOutMenu.setText("");
    menu.setDisable(false);
    // header.setDisable(false);
    menu.setText("Welcome, " + CurrentUserEntity.CURRENT_USER.getCurrentUser().getFirstName());
    // header.setOpacity(1);
    // header.setOpacity(1);
    isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    navButtons.setVisible(true);
    navButtons.setDisable(false);
    navButtons.setOpacity(1);
    toast.setVisible(true);
    toast.setDisable(false);

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
      heat.setDisable(true);
      heat2.setDisable(true);
      heatText.setOpacity(0);
      heatSVG.setOpacity(0);
      stats.setDisable(true);
      stats2.setDisable(true);
      statsText.setOpacity(0);
      statsSVG.setOpacity(0);

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
      heat.setDisable(false);
      heat2.setDisable(false);
      heatText.setOpacity(1);
      heatSVG.setOpacity(1);
      stats.setDisable(false);
      stats2.setDisable(false);
      statsText.setOpacity(1);
      statsSVG.setOpacity(1);
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
  private void handleHeat(ActionEvent event) throws IOException {
    Fapp.setScene("TrafficAnalyzer", "trafficAnalyzer");
  }


  @FXML
  private void handleStats(ActionEvent event) throws IOException {
    //Fapp.setScene("TrafficAnalyzer", "trafficAnalyzer");
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
  private void handleEquipment(ActionEvent event) throws IOException {
    Fapp.setScene("ServiceRequests", "EquipmentTransport");
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
    toast.setVisible(false);
    toast.setDisable(true);
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

  public void toastAnimationForward() {
    // Create a TranslateTransition to move the first rectangle to the left
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.2), toast);
    translate1.setToX(260);

    // Play the animation
    translate1.play();
  }

  public void toastAnimationBackward() {

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.2), toast);

    //    translateBack1.setDelay(Duration.seconds(2));
    translateBack1.setToX(-260);

    // Play the animations in sequence
    translateBack1.play();
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

  public void guestPathfinding() {
    navButtons.setVisible(true);
  }
}
