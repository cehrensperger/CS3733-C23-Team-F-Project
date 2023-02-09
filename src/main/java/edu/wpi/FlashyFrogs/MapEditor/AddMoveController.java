package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class AddMoveController {
  @FXML private MFXFilterComboBox<String> locationNameField;
  @FXML private MFXFilterComboBox<String> nodeIDField;
  @FXML private MFXDatePicker moveDatePicker;
  @FXML private Label errorMessage;

  @Setter private PopOver popOver;
  private Session session;

  void setSession(Session session) {
    this.session = session;
    fillBoxes(); // fill the dropdown boxes
  }

  /** Populates the dropdown boxes with the correct items */
  private void fillBoxes() {
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();
    locationNameField.setItems(FXCollections.observableList(objects));

    objects = session.createQuery("SELECT id FROM Node", String.class).getResultList();
    nodeIDField.setItems(FXCollections.observableList(objects));
  }

  /**
   * Persist the input node in the database
   *
   * @param event the event triggering this (unused)
   */
  @FXML
  private void saveMove(ActionEvent event) {
    try {
      if (locationNameField.getText().equals("") // if any fields are empty
          || nodeIDField.getText().equals("")
          || moveDatePicker.getText().equals("")) {
        throw new Exception(); // throw exception
      }
      // create the objects for the Move constructor
      LocationName location = session.find(LocationName.class, locationNameField.getText());
      Node node = session.find(Node.class, nodeIDField.getText());
      Date date = DateFormat.getDateInstance().parse(moveDatePicker.getText());

      // create the new move
      Move move = new Move(node, location, date);

      // if that move already exists, throw exception
      if (session.find(Move.class, move) != null) throw new NullPointerException();

      // else persist the move and close the popOver
      session.persist(move);
      popOver.hide();

      // tell the user what they did wrong
    } catch (NullPointerException e) {
      errorMessage.setText("This move already exists.");
    } catch (Exception e) {
      errorMessage.setText("Please fill all fields.");
    }
  }

  /**
   * do not persist the node and close the popOver
   *
   * @param event the event triggering this (unused)
   */
  @FXML
  private void cancelMove(ActionEvent event) {
    popOver.hide();
  }
}
