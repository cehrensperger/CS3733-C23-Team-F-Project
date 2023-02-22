package edu.wpi.FlashyFrogs.PathVisualizer;

import edu.wpi.FlashyFrogs.ORM.Node;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import lombok.NonNull;
import lombok.Setter;

/** Controller for the pop-up controller saying go to next floor */
public class NextFloorPopupController {
  @FXML private Text floorText;

  @Setter
  private AbstractPathVisualizerController
      pathfindingController; // The pathfinding controller this uses

  private Node destinationNode; // The node this is taking

  /**
   * Setter for the node that this pop-up is taking the user to. Also sets the text
   *
   * @param node the new destination node
   */
  void setDestination(@NonNull Node node) {
    this.destinationNode = node; // Save the node

    floorText.setText("Your path goes to floor " + node.getFloor());
  }

  public void initialize() {}

  /** Handles the yes button, instructs the path finding controller to go to the node */
  @FXML
  private void handleYesButton() {
    pathfindingController.goToNode(destinationNode);
  }
}
