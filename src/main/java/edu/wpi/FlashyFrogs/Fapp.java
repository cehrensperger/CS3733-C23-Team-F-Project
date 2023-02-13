package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.controllers.NavBarController;
import java.io.IOException;
import java.util.Objects;
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

@Slf4j
public class Fapp extends Application {
  @Setter @Getter
  private static boolean isLightMode =
      true; // keeps track of whether we are in Light Mode or Dark Mode

  @Setter @Getter private static Stage primaryStage;
  @Setter @Getter private static Pane rootPane;
  private static NavBarController controller;

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    Fapp.primaryStage = primaryStage;
    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/NavBar.fxml"));

    final Pane root = loader.load();
    final FXMLLoader homePage = new FXMLLoader(Fapp.class.getResource("views/Login.fxml"));
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
    primaryStage.setFullScreen(true);
    primaryStage.show();

    // Navigation.navigate(Screen.HOME);
  }

  @SneakyThrows
  public static void setScene(String packageName, String sceneName) {
    Parent root =
        FXMLLoader.load(
            Objects.requireNonNull(
                Fapp.class.getResource(packageName + "/" + sceneName + ".fxml")));
    AnchorPane mainAnchorPane = controller.getAnchorPane();

    mainAnchorPane.getChildren().clear();
    mainAnchorPane.getChildren().add(root);
    AnchorPane.setTopAnchor(root, 0.0);
    AnchorPane.setBottomAnchor(root, 0.0);
    AnchorPane.setLeftAnchor(root, 0.0);
    AnchorPane.setRightAnchor(root, 0.0);
    // Scene scene = new Scene(root);
    // apply CSS styling to pages whenever we switch to them
    //    rootPane
    //        .getStylesheets()
    //        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off
    // all
    //    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    //    if (isLightMode()) {
    //      scene
    //          .getStylesheets()
    //          .add(
    //              Fapp.class
    //                  .getResource("views/light-mode.css")
    //                  .toExternalForm()); // apply Light Mode styling
    //    } else { // we are not in Light Mode, so
    //      scene
    //          .getStylesheets()
    //          .add(
    //              Fapp.class
    //                  .getResource("views/dark-mode.css")
    //                  .toExternalForm()); // apply Dark Mode styling
    //    }
    //    // Scene scene = new Scene(root, 600, 400);
    //    primaryStage.setScene(scene);
    //    primaryStage.setMaximized(true);
    //    primaryStage.show();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
