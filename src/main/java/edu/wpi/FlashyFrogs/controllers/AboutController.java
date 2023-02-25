package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javax.swing.*;
import org.controlsfx.control.PopOver;

public class AboutController {
  private PopOver currentPopOver;

  @FXML private javafx.scene.layout.VBox VBox;

  /**
   * When the name of a team member is clicked, display a popup with their major and a fun fact about them.
   * @param event
   * @throws IOException
   */
  @FXML
  private void showTeamMemberInfo(MouseEvent event) throws IOException {
    // if there is already a popover, hide it
    if (currentPopOver != null) {
      currentPopOver.hide();
    }
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/TeamMemberPopup.fxml"));
    PopOver popOver = new PopOver(newLoad.load());
    currentPopOver = popOver;
    popOver.detach();
    TeamMemberPopupController controller = newLoad.getController();
    // get whatever Text node was clicked to call this function in the first place
    Text node = (Text) event.getSource();
    String text = ""; // this is the text that will show up in the popup
    // the Text nodes that can be clicked all have our names as the text content, set the text in
    // the popup accordingly
    switch (node.getText()) {
      case "Joseph Cardarelli":
        text = "A CS major who enjoys rock climbing";
        break;
      case "Connor Ehrensperger":
        text = "A CS major who plays trombone";
        break;
      case "Audrey Mongillo":
        text = "A CS major whose favorite food is pancakes";
        break;
      case "Kyla Driscoll":
        text = "An IMGD major from Maine";
        break;
      case "Jonathan Golden":
        text = "A CS major who has two pet sheep";
        break;
      case "Owen Krause":
        text = "";
        break;
      case "Lindsey Mraz":
        text = "A CS and Music major whose favorite animal is the rabbit";
        break;
      case "Rusen Sabaz":
        text = "A CS major from Turkey";
        break;
      case "Ian Wright":
        text = "";
        break;
    }
    controller.setContent(text);
    popOver.show(node);
  }
}
