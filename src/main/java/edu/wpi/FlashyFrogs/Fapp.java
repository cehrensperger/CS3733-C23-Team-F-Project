package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.controllers.IController;
import edu.wpi.FlashyFrogs.controllers.NavBarController;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

@Slf4j
@GeneratedExclusion
public class Fapp extends Application {
  @Setter @Getter
  private static boolean isLightMode =
      true; // keeps track of whether we are in Light Mode or Dark Mode

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static Pane rootPane;
  private static NavBarController controller;

  public static IController iController;

  public static Stack<String> prevPage = new Stack<String>();

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    // Pre-fill the L2 Cache for the map, so use the map FXML
    FXMLLoader fxmlLoader = new FXMLLoader(MapController.class.getResource("Map.fxml"));
    fxmlLoader.load(); // Load the FXML
    MapController mapController = fxmlLoader.getController(); // Load the map controller

    // For each floor
    for (Node.Floor floor : Node.Floor.values()) {
      mapController.setFloor(floor); // Cache it
    }

    Session session = mapController.getMapSession(); // Get the map session

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

    mapController.exit(); // Exit the map controller to free its resources

    ResourceDictionary[] resources =
        ResourceDictionary.values(); // Pre-cache the dictionary, for performance

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
    primaryStage.setScene(scene);
    // getStylesheets.add() is used frequently, so clear all stylesheets so we don't accumulate an
    // infinite list of them
    rootPane.getStylesheets().clear();
    // apply CSS styling to pages whenever we switch to them
    scene.getStylesheets().add(Fapp.class.getResource("views/style2.css").toExternalForm());
    primaryStage.setFullScreen(true);
    primaryStage.show();

    // apply Light Mode styling
    //    } else { // we are not in Light Mode, so
    //      scene
    //          .getStylesheets()
    //          .add(
    //              Fapp.class
    //                  .getResource("views/dark-mode.css")
    //                  .toExternalForm()); // apply Dark Mode styling
    //    }

    // Navigation.navigate(Screen.HOME);
  }

  @SneakyThrows
  public static void setScene(String packageName, String sceneName) {
    prevPage.push(packageName + "," + sceneName);

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

  public static void handleBack() throws IOException {
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
