package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.time.ZoneId;
import java.util.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.tableview2.TableView2;

public class TrafficAnalyzerController implements IController {
  @FXML private AnchorPane mapPane; // Map pane
  @FXML private DatePicker viewDate; // View date
  @FXML private TextField requestWeighting; // Weighting for requests
  @FXML private TableView2<MapItem> weightTable; // Weighting
  @FXML private TableColumn<MapItem, String> mapItemColumn; // Map items
  @FXML private TableColumn<MapItem, Number> usesColumn; // Uses
  private MapController mapController; // The map controller

  // Max and min weights for the map
  private int maxWeight;
  private int minWeight;

  private Map<edu.wpi.FlashyFrogs.ORM.Node.Floor, Collection<MapItem>>
      floorToMapItems; // Floor to map items

  /** Initialize method for the controller, sets up the tables and info */
  @FXML
  @SneakyThrows
  private void initialize() {
    // Set the table up
    mapItemColumn.setCellValueFactory(
        (row) -> new SimpleStringProperty(row.getValue().getMapItemString()));
    mapItemColumn.setReorderable(false);
    usesColumn.setCellValueFactory((row) -> new SimpleIntegerProperty(row.getValue().getNumUses()));
    usesColumn.setReorderable(false);

    FloydWarshallRunner.getReCalculationLock().acquire(); // Get the FW lock

    // Load the map
    FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("Map/Map.fxml"));

    Node map = loader.load(); // Load the map

    mapPane.getChildren().add(map); // Add the map to the children

    // Set the map to use the anchor pane to take all available space
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    mapController = loader.getController(); // Save the controller

    // Floor change listener that re-draws the heats for the given items
    mapController
        .getMapFloorProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              colorFloor(); // Color the floor
            });

    // On weight table selection, zoom to on the map
    weightTable
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue == null) { // If the new value is null, don't do anything
                return;
              }

              if (newValue.getMapFloor() != null) {
                mapController.getMapFloorProperty().setValue(newValue.getMapFloor()); // Go to floor
                Point2D mapPoint = newValue.getMapCoordinates(); // Get the map coordinates

                // Go to those coordinates
                mapController.zoomToCoordinates(
                    2, (int) Math.round(mapPoint.getX()), (int) Math.round(mapPoint.getY()));
              }
            });
  }

  /** Handles coloring a floor with the heat map, based on the generated colors */
  private void colorFloor() {
    mapController.redraw(); // Redraw on the map, to clear styling

    // For each item on this floor of the map
    for (MapItem mapItem : floorToMapItems.get(mapController.getMapFloorProperty().getValue())) {
      // Set its color to be the calculated color
      mapItem.getMapBacking(mapController).setFill(calculateColor(mapItem));
    }
  }

  /** Processes an update on the traffic analyzer. Fills the table with the associated values */
  private void update(double serviceWeight, @NonNull Date date) {
    // Create the queue, the comparator is comparing the number of uses
    PriorityQueue<MapItem> mapQueue =
        new PriorityQueue<>(Comparator.comparingInt(MapItem::getNumUses));

    Map<LocationName, edu.wpi.FlashyFrogs.ORM.Node> nodeToLocationName =
        getNodeToLocationNameMap(date);

    for (LocationName locationName : nodeToLocationName.keySet()) {
      for (LocationName otherLocation : nodeToLocationName.keySet()) {
        // Skip locations that are this one
        if (locationName.equals(otherLocation)) {
          continue;
        }

        // node to location name
        edu.wpi.FlashyFrogs.ORM.Node nodeOne = nodeToLocationName.get(locationName); // One
        edu.wpi.FlashyFrogs.ORM.Node nodeTwo = nodeToLocationName.get(otherLocation); // Two

        edu.wpi.FlashyFrogs.ORM.Node nextHop = nodeOne; // The next hop in the path

        // While the node isn't the target node
        while (!nextHop.equals(nodeTwo)) {
          edu.wpi.FlashyFrogs.ORM.Node nextNextHop =
              FloydWarshallRunner.getNextHops()
                  .get(nextHop)
                  .get(nodeTwo); // Get the next hop on the path to the destination

          nextHop = nextNextHop;
        }
      }
    }

    // Weight table
    weightTable.setItems(FXCollections.observableList(mapQueue.stream().toList()));
  }

  /**
   * Gets the location name to node map
   *
   * @param date the date to get the nodes for the locations at
   * @return the node to location name map at the given date
   */
  @NonNull
  private Map<LocationName, edu.wpi.FlashyFrogs.ORM.Node> getNodeToLocationNameMap(
      @NonNull Date date) {
    // Get the moves before now
    List<Move> moves =
        mapController
            .getMapSession()
            .createQuery(
                "FROM Move WHERE node.floor = :floor AND moveDate < :providedDate "
                    + "ORDER BY moveDate DESC",
                Move.class)
            .setParameter("floor", mapController.getMapFloorProperty().getValue())
            .setParameter("providedDate", date)
            .getResultList();

    // Location maps
    Map<LocationName, edu.wpi.FlashyFrogs.ORM.Node> nodeToLocationName =
        new HashMap<>(); // Node to location
    Map<edu.wpi.FlashyFrogs.ORM.Node, Integer> nodeToLocationCount =
        new HashMap<>(); // Node to location count map
    Set<LocationName> placedLocations = new HashSet<>(); // Placed ones

    // For each location belonging to this node
    for (Move move : moves) {
      // If we haven't seen this before, and we haven't seen it too many times
      if (nodeToLocationCount.containsKey(move.getNode())
          && nodeToLocationCount.get(move.getNode()) == 1) {
        // Save that we've seen gotten another node
        nodeToLocationCount.replace(move.getNode(), nodeToLocationCount.get(move.getNode()) + 1);

        // If we haven't placed this
        if (!placedLocations.contains(move.getLocation())) {
          nodeToLocationName.put(move.getLocation(), move.getNode()); // Save it
          placedLocations.add(move.getLocation()); // Mark it as placed
        }
      } else if (!nodeToLocationCount.containsKey(move.getNode())) { // If we haven't seen this
        nodeToLocationCount.put(move.getNode(), 1); // Save the node count initially

        if (!placedLocations.contains(move.getLocation())) {
          nodeToLocationName.put(move.getLocation(), move.getNode());
          placedLocations.add(move.getLocation());
        }
      }
    }

    return nodeToLocationName;
  }

  /** Clears the traffic analyzer and all of its associated variables */
  private void clearHeatMap() {
    weightTable.getItems().clear(); // Clear the table items

    // Clear weights
    maxWeight = 0;
    minWeight = 0;

    // Clear items
    floorToMapItems = null;

    colorFloor(); // Re-color the floor (this will clear styling)
  }

  /** Close for the controller, disables releases the FW lock */
  @Override
  public void onClose() {
    FloydWarshallRunner.getReCalculationLock().release(); // Release the lock
    mapController.exit(); // Exit the map
  }

  /** Shows help for this page */
  @Override
  public void help() {}

  /**
   * Updates the map on the button press
   *
   * @param actionEvent the event triggering this
   */
  public void updateMap(ActionEvent actionEvent) {
    clearHeatMap(); // Clear to start with

    // Try converting the weighting to a positive number)
    try {
      // Handle getting the number
      double number = Double.parseDouble(requestWeighting.getText());

      // The number must be positive
      if (number < 0) {
        // If it's not, throw an exception
        throw new NumberFormatException();
      }

      update(
          number,
          Date.from(
              viewDate
                  .getValue()
                  .atStartOfDay(ZoneId.systemDefault())
                  .toInstant())); // If everything worked, update the map
    } catch (NumberFormatException err) {
      // TODO: error catching
    }

    colorFloor(); // Color the floor
  }

  /**
   * Calculates the color of a map item based on the min and max. Calculates a step by (max -
   * min)/255
   *
   * @param mapItem the map item
   * @return the color for the map item
   */
  @NonNull
  private Color calculateColor(@NonNull MapItem mapItem) {
    // Step factor, max-min over number possible
    double stepFactor = (maxWeight - minWeight) / 256.0; // Calculate the step

    // Calculate the weight for this, this - min / factor
    double weight = (mapItem.getNumUses() - minWeight) / stepFactor;

    int roundedWeight = (int) Math.round(weight); // Round the weight

    // Return the color, red - the rounded weight
    return Color.rgb(roundedWeight, 0, 255 - roundedWeight);
  }
}
