package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Node;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import org.hibernate.Session;

@GeneratedExclusion
public class NodeLocationNamePopUpController {
  @FXML Label label1;
  @FXML Label label2;

  public void setNode(Node node, Session session) {
    label1.setText(node.getId());
    label2.setText(node.getCurrentLocation(session).toString());
    label2.setTextAlignment(TextAlignment.CENTER);
  }
}
