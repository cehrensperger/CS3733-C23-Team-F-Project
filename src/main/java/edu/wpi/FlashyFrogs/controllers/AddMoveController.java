package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class AddMoveController {
  @FXML private MFXButton cancelButton;
  @FXML private MFXButton saveButton;
  @FXML private MFXComboBox locationNameField;
  @FXML private MFXComboBox nodeIDField;
  @FXML private MFXDatePicker moveDatePicker;
  @FXML private Label errorMessage;

  private PopOver popOver;
  private Session session;

  public void setPopOver(PopOver popOver) {
    this.popOver = popOver;
  }

  public void setSession(Session session) {
    this.session = session;
    fillBoxes();
  }

  public void fillBoxes() {
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();
    locationNameField.setItems(FXCollections.observableList(objects));

    objects = session.createQuery("SELECT id FROM Node", String.class).getResultList();
    nodeIDField.setItems(FXCollections.observableList(objects));
  }

  @FXML
  private void saveMove(ActionEvent event) {
    try {
      if (locationNameField.getText().equals("")
          || nodeIDField.getText().equals("")
          || moveDatePicker.getText().equals("")) {
        throw new NullPointerException();
      }
      LocationName location = session.find(LocationName.class, locationNameField.getText());
      Node node = session.find(Node.class, nodeIDField.getText());
      Date date = new Date(moveDatePicker.getText());

      Move move = new Move(node, location, date);
      if (session.find(Move.class, move) != null) throw new NullPointerException();
      session.persist(move);
      popOver.hide();

    } catch (NullPointerException e) {
      errorMessage.setText("This move already exists.");
    } catch (Exception e) {
      errorMessage.setText("Please fill all fields.");
    }
  }

  @FXML
  private void cancelMove(ActionEvent event) {
    popOver.hide();
  }
}
