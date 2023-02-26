package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.Sound;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import lombok.Setter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

@GeneratedExclusion
public class AddMoveController {
  @FXML private SearchableComboBox<String> locationNameField;
  @FXML private SearchableComboBox<String> nodeIDField;
  @FXML private DatePicker moveDatePicker;
  @FXML private Label errorMessage;

  @Setter private PopOver popOver;
  private Session session;

  // Function to call when the location is updated
  @Setter Runnable addMove;

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
  private void saveMove(ActionEvent event) throws Exception {

    // If there are two moves on the same move and same location,
    // show and error message and don't add.

    try {
      if (locationNameField.valueProperty().getValue().equals("") // if any fields are empty
          || nodeIDField.valueProperty().getValue().equals("")
          || moveDatePicker.valueProperty().getValue().toString().equals("")) {
        throw new Exception(); // throw exception
      }
      // create the objects for the Move constructor
      LocationName location =
          session.find(LocationName.class, locationNameField.valueProperty().getValue());
      Node node = session.find(Node.class, nodeIDField.valueProperty().getValue());
      Date date =
          Date.from(moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      // create the new move
      Move move = new Move(node, location, date);

      // if that move already exists, throw exception
      if (session.find(Move.class, move) != null) throw new NullPointerException();

      // else persist the move and close the popOver
      session.persist(move);

      addMove.run(); // handle adding the move

      popOver.hide();

      // tell the user what they did wrong
    } catch (NullPointerException e) {
      errorMessage.setText("This move already exists.");
      Sound.ERROR.play();
    } catch (Exception e) {
      errorMessage.setText("Please fill all fields.");
      Sound.ERROR.play();
      throw (e);
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
