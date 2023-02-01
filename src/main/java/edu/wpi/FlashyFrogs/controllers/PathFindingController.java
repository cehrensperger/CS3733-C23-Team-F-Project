package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.PathFinder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.hibernate.Session;

public class PathFindingController {

  @FXML private MFXTextField start;
  @FXML private MFXTextField end;
  @FXML private Text pathText;
  @FXML private MFXButton getPath;
  @FXML private MFXButton backButton;
  @FXML private MFXButton clearButton;

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }

  public void handleButtonClear(ActionEvent event) throws IOException {
    start.clear();
    end.clear();
    pathText.setText("Path:");
  }

  public void handleGetPath(ActionEvent actionEvent) throws IOException {
    Session session = factory.openSession();
    // session.find(LocationName.class, start.getText());
    LocationName startPath = session.find(LocationName.class, start.getText());
    LocationName endPath = session.find(LocationName.class, end.getText());

    // String startPath = start.getText();
    // String endPath = end.getText();
    // Transaction transaction = session.beginTransaction();
    PathFinder pathFinder = new PathFinder(factory);
    try {
      pathText.setText("Path:\n" + pathFinder.findPath(startPath, endPath).toString());
    } catch (NullPointerException e) {
      System.out.println("Error: No data in database");
    }

    // = pathFinder.findPath(startPath, endPath);
    //    transaction.commit();
    //    session.close();

  }
}
