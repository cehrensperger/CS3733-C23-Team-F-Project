package edu.wpi.FlashyFrogs.MoveVisualizer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinding.AStar;
import edu.wpi.FlashyFrogs.PathVisualizer.AbstractPathVisualizerController;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

/** Controller for the announcement visualizer */
public class MoveVisualizerController extends AbstractPathVisualizerController
    implements IController {
  @FXML private Text noLocationText;
  @FXML private TableView2<Move> moveTable; // Table for the moves
  @FXML private TableColumn2<Move, Node> nodeColumn; // Node column
  @FXML private TableColumn2<Move, LocationName> locationColumn; // Location column
  @FXML private TableColumn2<Move, Date> dateColumn; // Date column
  @FXML private AnchorPane mapPane; // Map pane for map display

  /** Sets up the move visualizer, including all tables, and the map */
  @SneakyThrows
  @FXML
  private void initialize() {
    // Set the columns to not be order-able anymore
    nodeColumn.setReorderable(false);
    locationColumn.setReorderable(false);
    dateColumn.setReorderable(false);

    mapPane.getChildren().add(map);

    // Anchor it to take up the whole map pane
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);

    // Set the value factories for the columns
    nodeColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getNode()));
    locationColumn.setCellValueFactory(
        row -> new SimpleObjectProperty<>(row.getValue().getLocation()));
    dateColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getMoveDate()));

    // Get the moves
    List<Move> moves =
        mapController.getMapSession().createQuery("FROM Move", Move.class).getResultList();
    moveTable.setItems(FXCollections.observableList(moves)); // set the items in the table

    pathFinder.setAlgorithm(new AStar()); // Select A* as what we will use

    // Add a listener to the selected table to regenerate the path
    moveTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              unColorFloor();

              // If something is selected
              if (newValue != null) {
                // Generate the path. Start at the node the location was at 1ms before the move
                // End at the node in the move

                // Get the old node for the location, 1ms before
                Node oldLocation =
                    newValue
                        .getLocation()
                        .getCurrentNode(
                            mapController.getMapSession(),
                            Date.from(newValue.getMoveDate().toInstant().minusMillis(1)));

                // If the location isn't null
                if (oldLocation != null) {
                  // Get and draw the path
                  currentPath = pathFinder.findPath(oldLocation, newValue.getNode(), false);
                } else {
                  noLocationText.setVisible(true);
                }
              }

              colorFloor(); // Redraw
            });
  }

  /** Help, shows the help menu for the visualizer */
  @Override
  public void help() {}

  /**
   * Handler for the back button, delegates to Fapp
   *
   * @param actionEvent the event triggering this
   */
  public void handleBackButton(ActionEvent actionEvent) {
    Fapp.handleBack();
  }
}
