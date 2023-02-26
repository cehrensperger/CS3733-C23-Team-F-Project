package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.controllers.IController;
import edu.wpi.FlashyFrogs.controllers.NavBarController;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
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

  @Getter private static Theme theme;

  public static IController iController;

  public static Stack<String> prevPage = new Stack<String>();

  @Override
  public void init() {
    log.info("Starting Up");
  }

  /**
   * Method to pre-load resources on program startup. Uses threads to improve resources without
   * making the app take forever to launch
   */
  private void preloadResources() {
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
    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/NavBar.fxml"));

    final BorderPane root = loader.load();
    final FXMLLoader homePage = new FXMLLoader(Fapp.class.getResource("Accounts/Login.fxml"));
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

    setTheme(Theme.LIGHT_THEME);
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

    AnchorPane mainAnchorPane = controller.getAnchorPane();

    mainAnchorPane.getChildren().clear();
    mainAnchorPane.getChildren().add(root);
    AnchorPane.setTopAnchor(root, 0.0);
    AnchorPane.setBottomAnchor(root, 0.0);
    AnchorPane.setLeftAnchor(root, 0.0);
    AnchorPane.setRightAnchor(root, 0.0);
  }

  @SneakyThrows
  public static void handleBack() {
    prevPage.pop();
    String[] page = prevPage.pop().split(",");
    Fapp.setScene(page[0], page[1]);
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
}
