package edu.wpi.FlashyFrogs;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

  @Override
  public void init() {
    log.info("Starting Up");
  }

  @Override
  public void start(Stage primaryStage) throws IOException {
    /* primaryStage is generally only used if one of your components require the stage to display */
    Fapp.primaryStage = primaryStage;
    primaryStage.setResizable(false);
    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/Login.fxml"));

    final Pane root = loader.load();

    Fapp.rootPane = root;

    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Navigation.navigate(Screen.HOME);
  }

  @SneakyThrows
  public static void setScene(String sceneName) {
    Parent root =
        FXMLLoader.load(
            Objects.requireNonNull(Fapp.class.getResource("views/" + sceneName + ".fxml")));
    Scene scene = new Scene(root);
    // apply CSS styling to pages whenever we switch to them
    rootPane
        .getStylesheets()
        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off all
    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    if (isLightMode()) {
      scene
          .getStylesheets()
          .add(
              Fapp.class
                  .getResource("views/light-mode.css")
                  .toExternalForm()); // apply Light Mode styling
    } else { // we are not in Light Mode, so
      scene
          .getStylesheets()
          .add(
              Fapp.class
                  .getResource("views/dark-mode.css")
                  .toExternalForm()); // apply Dark Mode styling
    }
    // Scene scene = new Scene(root, 600, 400);
    primaryStage.setScene(scene);
    primaryStage.setMaximized(false);
    primaryStage.show();
  }

  @Override
  public void stop() {
    log.info("Shutting Down");
  }
}
