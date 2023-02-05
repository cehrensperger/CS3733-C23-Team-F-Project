package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;

import java.util.Objects;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
public class MapEditorController {
  public MFXComboBox<Node.Floor> floorSelector;
  @FXML
  private HBox editorBox; // Editor box, map is appended to this on startup
  private MapController mapController; // Controller for the map

  /**
   * Initializes the map editor, adds the map onto it
   */
  @SneakyThrows
  @FXML
  private void initialize() {
    FXMLLoader mapLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("views/Map.fxml")));
    editorBox.getChildren().add()
  }
}
