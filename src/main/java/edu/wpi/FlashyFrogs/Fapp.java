package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.Accounts.LoginController;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.TrafficAnalyzer.FloydWarshallRunner;
import edu.wpi.FlashyFrogs.controllers.IController;
import edu.wpi.FlashyFrogs.controllers.NavBarController;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

@Slf4j
@GeneratedExclusion
public class Fapp extends Application {
  @Setter @Getter
  public static boolean sfxOn = true; // keeps track of whether sound effects are turned on or off

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static Pane rootPane;
  private static NavBarController controller;
  @Getter private static Theme theme = Theme.LIGHT_THEME;

  @Getter public static IController iController;

  public static Stack<String> prevPage = new Stack<String>();

  // Last keypress time, used for things that require a timeout
  @Getter @Setter private static Instant lastKeyPressTime = Instant.now();
  private static String hold;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  /**
   * Method to pre-load resources on program startup. Uses threads to improve resources without
   * making the app take forever to launch
   */
  private void preloadResources() {
    FloydWarshallRunner.reCalculate(); // Have the floyd-warshall runner do its thing

    // Map loader
    new Thread(
            () -> {
              // Pre-fill the L2 Cache for the map, so use the map FXML
              FXMLLoader fxmlLoader = new FXMLLoader(MapController.class.getResource("Map.fxml"));
              try {
                fxmlLoader.load(); // Load the FXML
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              MapController mapController = fxmlLoader.getController(); // Load the map controller

              // For each floor
              for (Node.Floor floor : Node.Floor.values()) {
                mapController.getMapFloorProperty().setValue(floor); // Cache it
              }

              mapController.exit(); // Exit the map controller
            })
        .start();

    // Pre-load the various pathfinding elements
    new Thread(
            () -> {
              // Create a session
              Session session = DBConnection.CONNECTION.getSessionFactory().openSession();

              // Get all nodes
              List<Node> nodes = session.createQuery("FROM Node", Node.class).getResultList();

              // For each one
              for (Node node : nodes) {
                // Create a query (for the sake of caching it) that gets its neighbors
                session
                    .createQuery(
                        """
                                        SELECT node1
                                        FROM Edge
                                        WHERE node2 = :node
                                        UNION
                                        SELECT node2
                                        FROM Edge
                                        WHERE node1 = :node
""",
                        Node.class)
                    .setParameter("node", node)
                    .setCacheable(true)
                    .getResultList();
              }

              // End the session
              session.close();
            })
        .start();
  }

  /**
   * Sets the application theme
   *
   * @param themeName the theme to set
   */
  public static void setTheme(@NonNull Theme themeName) {
    // Get the sheets for the app
    ObservableList<String> sheets = Fapp.primaryStage.getScene().getStylesheets();

    sheets.clear(); // Clear the sheets

    // Add the sheets
    sheets.add(themeName.resource.toExternalForm());
    sheets.add(
        Objects.requireNonNull(Theme.class.getResource("views/NotColors.css")).toExternalForm());

    theme = themeName;
  }

  @SneakyThrows
  @Override
  public void start(Stage primaryStage) throws IOException {
    preloadResources();
    /* primaryStage is generally only used if one of your components require the stage to display */
    Fapp.primaryStage = primaryStage;
    final FXMLLoader homePage = new FXMLLoader(Fapp.class.getResource("Accounts/Login.fxml"));
    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/NavBar.fxml"));

    final BorderPane root = loader.load();
    controller = ((NavBarController) loader.getController());
    AnchorPane mainAnchorPane = controller.getAnchorPane();
    AnchorPane innerAnchorPane = homePage.load();
    mainAnchorPane.getChildren().add(innerAnchorPane);

    AnchorPane.setTopAnchor(innerAnchorPane, 0.0);
    AnchorPane.setBottomAnchor(innerAnchorPane, 0.0);
    AnchorPane.setLeftAnchor(innerAnchorPane, 0.0);
    AnchorPane.setRightAnchor(innerAnchorPane, 0.0);
    Fapp.rootPane = root;
    final Scene scene = new Scene(root);

    // Disable full-screen exit and combo
    primaryStage.setFullScreenExitHint("");
    primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

    primaryStage.setScene(scene);
    setTheme(Theme.LIGHT_THEME);
    primaryStage.setFullScreen(true);
    primaryStage.show();
  }

  /**
   * Sets the application root manually. Should only be used in special cases
   *
   * @param root the root of the app
   */
  public static void setRoot(@NonNull javafx.scene.Node root) {
    lastKeyPressTime =
        Instant.now(); // Update the last press time, so that it doesn't snap change after change to
    // login

    // Clear the root
    controller.getAnchorPane().getChildren().clear();

    // Set this to be the root
    controller.getAnchorPane().getChildren().add(root);

    // Make it take all available space
    AnchorPane.setTopAnchor(root, 0.0);
    AnchorPane.setBottomAnchor(root, 0.0);
    AnchorPane.setLeftAnchor(root, 0.0);
    AnchorPane.setRightAnchor(root, 0.0);
  }

  @SneakyThrows
  public static void setScene(String packageName, String sceneName) {
    try {
      if (!prevPage.peek().equals(packageName + "," + sceneName)) {
        prevPage.push(packageName + "," + sceneName);
      }
    } catch (EmptyStackException e) {
      prevPage.push(packageName + "," + sceneName);
    }

    if (iController != null) {
      iController.onClose();
    }

    FXMLLoader loader =
        new FXMLLoader(
            Objects.requireNonNull(
                Fapp.class.getResource(packageName + "/" + sceneName + ".fxml")));
    Parent root = loader.load();
    iController = loader.getController();

    setRoot(root);
  }

  @SneakyThrows
  public static void handleBack() {
    if (prevPage.size() > 1) {
      prevPage.pop();
      String[] page = prevPage.pop().split(",");
      Fapp.setScene(page[0], page[1]);
    } else {
      // If back is pressed on a page without full nav set and not the login page
      if (getIController() == null || getIController().getClass() != LoginController.class) {
        Fapp.logOutWithoutSceneChange(); // Ensure we're logged out
        Fapp.setScene("Accounts", "Login"); // Go to login
      } else {
        controller.closeApp(); // Close the app if it's the login page
      }
    }
  }

  public static void resetStack() {
    prevPage.clear();
    prevPage.push("views,Home");
  }

  public static void resetStackLogin() {
    prevPage.clear();
    prevPage.push("Accounts,Login");
  }

  public static void logIn() {
    controller.logIn();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }

  /** Signs the user out without changing the scene */
  public static void logOutWithoutSceneChange() {
    // Just have the controller do it
    controller.signUserOutWithoutSceneChange();
  }
}
