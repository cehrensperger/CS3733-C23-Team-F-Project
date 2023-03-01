package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.tableview2.TableView2;

public class TrafficAnalyzerController implements IController {

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  boolean hDone = false;
  @FXML private Pane errtoast;
  @FXML private Rectangle errcheck2;
  @FXML private Rectangle errcheck1;
  @FXML private Button updateButton; // Update button so we can disable it
  @FXML private AnchorPane mapPane; // Map pane
  @FXML private DatePicker viewDate; // View date
  @FXML private TextField requestWeighting; // Weighting for requests
  @FXML private TableView2<MapItem> weightTable; // Weighting
  @FXML private TableColumn<MapItem, String> typeColumn; // Table column
  @FXML private TableColumn<MapItem, String> mapItemColumn; // Map items
  @FXML private TableColumn<MapItem, Number> usesColumn; // Uses
  @FXML private TableColumn<MapItem, Number> serviceRequestsColumn; // Service requests
  private MapController mapController; // The map controller

  // Max and min weights for the map
  private double maxWeight;
  private double minWeight;

  private Map<edu.wpi.FlashyFrogs.ORM.Node.Floor, Collection<MapItem>>
      floorToMapItems; // Floor to map items

  /** Initialize method for the controller, sets up the tables and info */
  @FXML
  @SneakyThrows
  private void initialize() {

    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    // Set the table up
    typeColumn.setCellValueFactory((row) -> new SimpleStringProperty(row.getValue().type()));
    typeColumn.setReorderable(false);
    mapItemColumn.setCellValueFactory(
        (row) -> new SimpleStringProperty(row.getValue().getMapItemString()));
    mapItemColumn.setReorderable(false);
    usesColumn.setCellValueFactory(
        (row) -> new SimpleIntegerProperty(row.getValue().getRelevantPaths().size()));
    usesColumn.setReorderable(false);
    serviceRequestsColumn.setCellValueFactory(
        (row) -> new SimpleIntegerProperty(row.getValue().getNumServiceRequests()));
    serviceRequestsColumn.setReorderable(false);

    // Load the map
    FXMLLoader loader = new FXMLLoader(Fapp.class.getResource("Map/Map.fxml"));

    Node map = loader.load(); // Load the map

    mapPane.getChildren().add(map); // Add the map to the children

    // Load scale (which indicates which colors indicate more activity)
    FXMLLoader loader2 = new FXMLLoader(Fapp.class.getResource("TrafficAnalyzer/Scale.fxml"));
    Node map2 = loader2.load();
    mapPane.getChildren().add(map2); // Add scale to the children

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

    // Set the circles to be thick
    mapController.setNodeCreation(
        (node, circle) -> {
          circle.setRadius(10);
        });

    // Set the edges to be thick
    mapController.setEdgeCreation(
        (edge, line) -> {
          line.setStrokeWidth(7);
        });

    // Disable all hallway text
    mapController.setLocationCreation(
        (node, location, text) -> {
          if (location.getLocationType().equals(LocationName.LocationType.HALL)) {
            text.setVisible(false);
          }
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

    // On date change, update the map controllers date and redraw
    viewDate.setOnAction(
        (action) -> {
          mapController.setDate(
              Date.from(viewDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
          mapController.redraw();
        });

    // Create a thread that waits for any FW backing updates to complete
    new Thread(
            () -> {
              // Disable the button, start an animation
              Platform.runLater(
                  () -> {
                    mapController.startAnimation();
                    updateButton.setDisable(true);
                  });

              try {
                FloydWarshallRunner.getReCalculationLock().acquire(); // Get the FW locks
              } catch (InterruptedException e) {
                throw new RuntimeException(e); // Thanks Java :)
              }

              // Stop the animation, re-enable the button
              Platform.runLater(
                  () -> {
                    mapController.stopAnimation();
                    updateButton.setDisable(false);
                  });
            })
        .start();
  }

  /** Handles coloring a floor with the heat map, based on the generated colors */
  private void colorFloor() {
    mapController.redraw(); // Redraw on the map, to clear styling

    if (floorToMapItems == null) return; // Do nothing if there is no map

    // For each item on this floor of the map
    for (MapItem mapItem : floorToMapItems.get(mapController.getMapFloorProperty().getValue())) {
      // Set its color to be the calculated color
      Color calculatedColor = calculateColor(mapItem); // Calculate the color
      mapItem.getMapBacking(mapController).setFill(calculatedColor);
      mapItem.getMapBacking(mapController).setStroke(calculatedColor);
    }
  }

  /** Processes an update on the traffic analyzer. Fills the table with the associated values */
  private void update(double serviceWeight, @NonNull Date date) {
    Collection<Thread> threads = new LinkedList<>();

    // Get the edges, so that we can check which direction they go without doing session.find
    Set<Edge> edges =
        new HashSet<>(
            mapController.getMapSession().createQuery("FROM Edge", Edge.class).getResultList());

    // Get the count of service requests for each location
    List<Object[]> serviceRequestsRaw =
        mapController
            .getMapSession()
            .createQuery(
                "SELECT loc, COUNT(*) FROM ServiceRequest sr JOIN sr.location loc GROUP BY loc",
                Object[].class)
            .getResultList();

    Map<LocationName, Integer> locationToServiceRequestCount = new HashMap<>();

    // Save the counts
    serviceRequestsRaw.forEach(
        (Object[] raw) -> {
          LocationName locationName = (LocationName) raw[0]; // Cast location name
          long count = (long) raw[1]; // Cast count

          locationToServiceRequestCount.put(locationName, (int) count); // Save it
        });

    // Create the queue, the comparator is comparing the number of uses
    Map<edu.wpi.FlashyFrogs.ORM.Node, MapItem> nodeMapItems =
        new ConcurrentHashMap<>(FloydWarshallRunner.getNextHops().size()); // Node items
    Map<Edge, MapItem> edgeMapItems = new ConcurrentHashMap<>(edges.size()); // Edge items

    floorToMapItems = new ConcurrentHashMap<>(); // Create the floor to map items list

    for (edu.wpi.FlashyFrogs.ORM.Node.Floor floor : edu.wpi.FlashyFrogs.ORM.Node.Floor.values()) {
      floorToMapItems.put(floor, new ConcurrentLinkedQueue<>()); // Create the map on the floor
    }

    Map<LocationName, edu.wpi.FlashyFrogs.ORM.Node> nodeToLocationName =
        getNodeToLocationNameMap(date);

    // For each location
    for (LocationName locationName : nodeToLocationName.keySet()) {
      if (locationName.getLocationType().equals(LocationName.LocationType.HALL)) {
        continue; // Skip all hallways
      }

      // Create a thread to process it
      Thread thread =
          new Thread(
              () -> {
                for (LocationName otherLocation : nodeToLocationName.keySet()) {
                  // Skip locations that are this one, and ignore if either are hallways
                  if (locationName.equals(otherLocation)
                      || otherLocation.getLocationType().equals(LocationName.LocationType.HALL)) {
                    continue;
                  }

                  // node to location name
                  edu.wpi.FlashyFrogs.ORM.Node nodeOne =
                      nodeToLocationName.get(locationName); // One
                  edu.wpi.FlashyFrogs.ORM.Node nodeTwo =
                      nodeToLocationName.get(otherLocation); // Two

                  edu.wpi.FlashyFrogs.ORM.Node nextHop = nodeOne; // The next hop in the path

                  // While the node isn't the target node
                  while (true) {
                    // If we haven't seen this node before
                    if (!nodeMapItems.containsKey(nextHop)) {
                      nodeMapItems.put(nextHop, new NodeMapItem(nextHop, serviceWeight)); // Save it

                      // Add this to its floor
                      floorToMapItems
                          .get(nodeMapItems.get(nextHop).getMapFloor())
                          .add(nodeMapItems.get(nextHop));
                    }

                    // Either way, add this to the relevant paths
                    nodeMapItems
                        .get(nextHop)
                        .getRelevantPaths()
                        .add(new Path(locationName, otherLocation));

                    // Do SRs too
                    nodeMapItems
                        .get(nextHop)
                        .addServiceRequest(
                            locationToServiceRequestCount.getOrDefault(otherLocation, 0));

                    // Stop here if we reached the target. Stop precisely here so that the last node
                    // also gets its weight covered
                    if (nextHop.equals(nodeTwo)) {
                      break;
                    }

                    edu.wpi.FlashyFrogs.ORM.Node nextNextHop =
                        FloydWarshallRunner.getNextHops()
                            .get(nextHop)
                            .get(nodeTwo); // Get the next hop on the path to the destination

                    // Try to find the edge in one order
                    Edge edge = new Edge(nextHop, nextNextHop);

                    // That failing
                    if (!edges.contains(edge)) {
                      // Try the other order
                      edge = new Edge(nextNextHop, nextHop);
                    }

                    // If we haven't seen the edge
                    if (!edgeMapItems.containsKey(edge)) {
                      edgeMapItems.put(edge, new EdgeMapItem(edge, serviceWeight)); // Save it

                      MapItem edgeItem = edgeMapItems.get(edge); // Get the edge item we just made

                      // If the edge item is valid floor-wise (can be cross floor and therefore
                      // invalid)
                      if (edgeItem.getMapFloor() != null) {
                        // Add this to the floor map
                        floorToMapItems.get(edgeItem.getMapFloor()).add(edgeItem);
                      }
                    }

                    // Either way, save this path as relevant
                    edgeMapItems
                        .get(edge)
                        .getRelevantPaths()
                        .add(new Path(locationName, otherLocation));

                    // Add service requests
                    edgeMapItems
                        .get(edge)
                        .addServiceRequest(
                            locationToServiceRequestCount.getOrDefault(otherLocation, 0));

                    nextHop = nextNextHop; // The next hop is this
                  }
                }
              });
      // Save and start the thread
      threads.add(thread);
      thread.start();
    }

    // Wait for each thred to finish
    threads.forEach(
        thread -> {
          try {
            thread.join();
          } catch (InterruptedException e) {
            throw new RuntimeException(e); // Thanks Java :)
          }
        });

    // Create the items
    ObservableList<MapItem> items = FXCollections.observableArrayList();

    // For each item
    // Add the item
    items.addAll(nodeMapItems.values());
    items.addAll(edgeMapItems.values());

    // Sort by the number of uses, descending
    items.sort(Comparator.comparingDouble(MapItem::getTotalWeight).reversed());

    maxWeight = items.get(0).getTotalWeight(); // Get the max weight
    minWeight = items.get(items.size() - 1).getTotalWeight(); // Get the min weight

    // Weight table
    Platform.runLater(() -> weightTable.setItems(items));
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
                "FROM Move WHERE moveDate < :providedDate " + "ORDER BY moveDate DESC", Move.class)
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
  public void help() {

    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      hDone = false;
    }
  }

  /**
   * Updates the map on the button press
   *
   * @param actionEvent the event triggering this
   */
  public void updateMap(ActionEvent actionEvent) {
    updateButton.setDisable(true); // Prevent duplicate actions
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

      // Start a thread that starts the animation, does the calculation, does the coloring
      new Thread(
              () -> {
                Platform.runLater(
                    () -> {
                      mapController.startAnimation();
                      updateButton.setDisable(true);
                    });
                update(
                    number,
                    Date.from(
                        viewDate
                            .getValue()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant())); // If everything worked, update the map

                Platform.runLater(
                    () -> {
                      mapController.stopAnimation();
                      updateButton.setDisable(false);
                      colorFloor();
                    });
              })
          .start();
    } catch (NumberFormatException err) {
      errortoastAnimation(); // Show the error on failure
    }
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
    double stepFactor = (maxWeight - minWeight) / 255.0; // Calculate the step

    // Calculate the weight for this, this - min / factor
    double weight = (mapItem.getTotalWeight() - minWeight) / stepFactor;

    int roundedWeight = (int) Math.round(weight); // Round the weight

    // Return the color, red - the rounded weight
    return Color.rgb(roundedWeight, 0, 255 - roundedWeight);
  }

  /** Shows the error toast animation, to show errors in the box */
  public void errortoastAnimation() {
    errtoast.getTransforms().clear();
    errtoast.setLayoutX(0);

    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck1.setFill(Color.web("#012D5A"));
    errcheck2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck1, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck2, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(event -> updateButton.setDisable(false));
  }
}
