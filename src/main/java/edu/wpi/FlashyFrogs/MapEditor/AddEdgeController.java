package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javax.swing.*;
import lombok.NonNull;
import lombok.Setter;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

/** Controller to add edges */
@GeneratedExclusion
public class AddEdgeController {
  @FXML private Text errorText;
  @FXML private SearchableComboBox<Node> edgeOneField;
  @FXML private SearchableComboBox<Node> edgeTwoField;
  @Setter private Runnable onAdd; // On save callback

  @Setter private Runnable onCancel; // On cancel callback

  private Session session;

  /**
   * Populates the drop-downs
   *
   * @param session the session
   */
  public void populate(@NonNull Session session) {
    this.session = session; // Save the session

    List<Node> nodes = session.createQuery("FROM Node", Node.class).getResultList();
    ObservableList<Node> observableNodes =
        FXCollections.observableList(nodes); // Observable node list

    // Set the items for both
    edgeOneField.setItems(observableNodes);
    edgeTwoField.setItems(observableNodes);
  }

  /**
   * Callback to cancel adding
   *
   * @param actionEvent callback triggering this
   */
  @FXML
  private void cancel(ActionEvent actionEvent) {
    onCancel.run(); // Run the cancel handler
  }

  /**
   * Callback to submit
   *
   * @param actionEvent callback triggering this
   */
  @FXML
  private void save(ActionEvent actionEvent) {
    errorText.setText(""); // Clear the error text

    // Check both fields are filled
    if (edgeOneField.getValue() == null || edgeTwoField.getValue() == null) {
      errorText.setText("Fill all fields!"); // Prompt to fill all
      return; // If not, exit
    }

    // Create the edge
    Edge edge = new Edge(edgeOneField.getValue(), edgeTwoField.getValue());

    // Say that it's a duplicate if it is
    if (this.session.find(Edge.class, edge) != null) {
      errorText.setText("This is a duplicate edge!");
      return; // Exit
    }

    session.persist(edge); // Save the edge
    onAdd.run(); // Run the add handler
  }
}
