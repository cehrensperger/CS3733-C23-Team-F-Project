package edu.wpi.FlashyFrogs.MapEditor;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

public class GroupSelectionContextMenuController {
  @FXML private MFXButton autoAlign;
  @FXML private MFXButton deleteLocations;
  @FXML private MFXButton deleteNodes;

  public void setOnAutoAlign(EventHandler<ActionEvent> eventHandler) {
    autoAlign.setOnAction(eventHandler);
  }

  public void setOnDeleteLocations(EventHandler<ActionEvent> eventHandler) {
    deleteLocations.setOnAction(eventHandler);
  }

  public void setOnDeleteNodes(EventHandler<ActionEvent> eventHandler) {
    deleteNodes.setOnAction(eventHandler);
  }
}
