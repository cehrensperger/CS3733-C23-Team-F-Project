package edu.wpi.FlashyFrogs.Navigation2;

import edu.wpi.FlashyFrogs.Fapp;
import java.io.IOException;
import javafx.fxml.FXMLLoader;

public class Navigation {

  public static void navigate(final Screen screen) {
    final String filename = screen.getFilename();

    try {
      final var resource = Fapp.class.getResource(filename);
      final FXMLLoader loader = new FXMLLoader(resource);

      Fapp.getRootPane().setCenterShape(loader.load());
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }
}
