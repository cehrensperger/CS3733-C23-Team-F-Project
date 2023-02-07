package edu.wpi.FlashyFrogs;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Fapp extends Application {

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

    final FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("views/MapEditorView.fxml"));
    final Pane root = loader.load();

    Fapp.rootPane = root;

    final Scene scene = new Scene(root);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Navigation.navigate(Screen.HOME);
  }

  public static void setScene(String sceneName) throws IOException {
    Parent root = FXMLLoader.load(Fapp.class.getResource("views/" + sceneName + ".fxml"));
    Scene scene = new Scene(root);
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
