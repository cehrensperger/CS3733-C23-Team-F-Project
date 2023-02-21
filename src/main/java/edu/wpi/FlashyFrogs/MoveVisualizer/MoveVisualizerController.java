package edu.wpi.FlashyFrogs.MoveVisualizer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import lombok.SneakyThrows;
import org.controlsfx.control.tableview2.TableColumn2;
import org.controlsfx.control.tableview2.TableView2;

/** */
public class MoveVisualizerController implements IController {
  @FXML private TableView2<Move> moveTable; // Table for the moves
  @FXML private TableColumn2<Move, Node> nodeColumn; // Node column
  @FXML private TableColumn2<Move, LocationName> locationColumn; // Location column
  @FXML private TableColumn2<Move, Date> dateColumn; // Date column
  @FXML private AnchorPane mapPane; // Map pane for map display
  private MapController mapController; // Map controller=

  /** Sets up the move visualizer, including all tables, and the map */
  @SneakyThrows
  @FXML
  private void initialize() {
    // Set the columns to not be order-able anymore
    nodeColumn.setReorderable(false);
    locationColumn.setReorderable(false);
    dateColumn.setReorderable(false);

    // Create the map loader
    FXMLLoader mapLoader = new FXMLLoader(Fapp.class.getResource("Map/Map.fxml"));

    Pane mapRoot = mapLoader.load(); // Load the map
    mapPane.getChildren().add(mapRoot); // Add it

    mapController = mapLoader.getController(); // Get the controller

    // Anchor it to take up the whole map pane
    AnchorPane.setLeftAnchor(mapRoot, 0.0);
    AnchorPane.setRightAnchor(mapRoot, 0.0);
    AnchorPane.setTopAnchor(mapRoot, 0.0);
    AnchorPane.setBottomAnchor(mapRoot, 0.0);

    // Set the value factories for the columns
    nodeColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getNode()));
    locationColumn.setCellValueFactory(
        row -> new SimpleObjectProperty<>(row.getValue().getLocation()));
    dateColumn.setCellValueFactory(row -> new SimpleObjectProperty<>(row.getValue().getMoveDate()));

    // Get the moves
    List<Move> moves =
        mapController.getMapSession().createQuery("FROM Move", Move.class).getResultList();
    moveTable.setItems(FXCollections.observableList(moves)); // set the items in the table
  }

  /** On close, in this case closes the map */
  @Override
  public void onClose() {
    mapController.exit();
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
