package edu.wpi.FlashyFrogs.MapEditor;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.shape.Circle;
import lombok.Setter;

public class GroupSelectionContextMenuController {
  @FXML private MFXButton autoAlign;
  @FXML private MFXButton deleteLocations;
  @FXML private MFXButton deleteNodes;
  @Setter private Circle circle;
  @Setter private List<Circle> circlesToAlign;

  public void initialize() {
    circle = new Circle();
    circlesToAlign = new ArrayList<>();
  }

  public void handleAutoAlign() {
    for (int i = 0; i < circlesToAlign.size(); i++) {
      circlesToAlign.get(i).setCenterY(circle.getCenterY());
    }
  }

  public void handleDeleteLocations() {
    System.out.println("delete locations");
  }

  public void handleDeleteNodes() {
    System.out.println("handle delete nodes");
  }

  public void setOnAutoAlign(EventHandler<ActionEvent> eventHandler) {
    autoAlign.setOnAction(eventHandler);
  }
}
