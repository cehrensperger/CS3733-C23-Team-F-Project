package edu.wpi.FlashyFrogs.MapEditor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.Getter;

/** Exit confirmation controller */
public class ExitConfirmationController {
  @FXML @Getter private Button continueEditing;
  @FXML @Getter private Button discard;
  @FXML @Getter private Button save;
}
