package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.utils.others.TriConsumer;
import jakarta.persistence.Tuple;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.kurobako.gesturefx.GesturePane;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

/**
 * Controller for the Map of the hospital. Provides utilities to make it useful for classes that may
 * want to use this. Listens for additions/removals of nodes/edges/locations in order to
 * automatically display changes to the map
 */
@GeneratedExclusion
public class MapController {
  @FXML private Label floorSelector; // Floor selector label
  @FXML private MFXButton floorSelectorButton; // Floor selector button
  @FXML private SearchableComboBox<Display> filterBox; // Box enabling selection of what is showing
  @FXML @Getter private GesturePane gesturePane; // Gesture pane, used to zoom to given locations
  @FXML private Group group; // Group that will be used as display in the gesture pane

  @Getter
  private final Pane currentDrawingPane =
      new Pane(); // The current drawing pane to use to draw nodes/edges

  @NonNull private final MapEntity mapEntity = new MapEntity(); // The entity the map will use

  /** Initialize, zooms to the middle of the map */
  @FXML
  private void initialize() {
    // Zooms to the middle of the map. This must be run later because currently the gesture pane
    // doesn't exist
    Platform.runLater(() -> gesturePane.zoomTo(0.15, new javafx.geometry.Point2D(2500, 1700)));

    // Set the initial filter box things
    filterBox.setItems(FXCollections.observableArrayList(Display.values())); // Set the items
    filterBox.setValue(Display.LOCATION_NAMES); // Set the value

    // When the map floor changes
    mapEntity
        .getMapFloor()
        .addListener(
            (observable) -> {
              redraw(); // Redraw
            });

    // Listener for the change box to update the labels on each node
    filterBox
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // this needs to be here for some reason (sometimes nulls are randomly here?)
              if (newValue != null) {
                setDisplayText(newValue); // Update the labels
              }
            });

    // Bind the floor text to be floor and then the floor
    floorSelector.textProperty().bind(Bindings.concat("Floor ", mapEntity.getMapFloor()));

    mapEntity.getMapFloor().setValue(Node.Floor.L1); // Set the starting to L1
  }

  /**
   * Gets the property representing the maps floor
   *
   * @return the property representing the maps floor
   */
  @NonNull
  public Property<Node.Floor> getMapFloorProperty() {
    return mapEntity.getMapFloor(); // Get the map floor
  }

  /**
   * Sets the node creation function
   *
   * @param function the function to set the node creation function to. May be null
   */
  public void setNodeCreation(BiConsumer<Node, Circle> function) {
    mapEntity.setNodeCreation(function);
    redraw(); // Make this take effect
  }

  /**
   * Set the edge creation function
   *
   * @param function the function to set the edge creation to. May be null
   */
  public void setEdgeCreation(BiConsumer<Edge, Line> function) {
    mapEntity.setEdgeCreation(function);
    redraw(); // Make this take effect
  }

  /**
   * Set the location creation function
   *
   * @param function the function to set the location creation to. May be null
   */
  public void setLocationCreation(TriConsumer<Node, LocationName, Text> function) {
    mapEntity.setLocationCreation(function);
    redraw(); // make this take effect
  }

  /**
   * Zooms the map to the given coordinates
   *
   * @param x the x-coordinate to zoom to
   * @param y the y-coordinate to zoom to
   */
  public void zoomToCoordinates(int x, int y) {
    gesturePane.centreOn(new Point2D(x, y));
  }

  /**
   * Adds a node to the map, including drawing it on the map and calling the associated callback.
   * NOTE: Assumes that the Node has no edges, and thus does not draw them
   *
   * @param node the node to draw on the map
   * @param addLocations whether locations should be added here. This is useful
   */
  public void addNode(@NonNull Node node, boolean addLocations) {
    Circle circleToDraw = new Circle(node.getXCoord(), node.getYCoord(), 5, Color.BLACK);
    currentDrawingPane.getChildren().add(circleToDraw); // Draw the circle

    mapEntity.addNode(node, circleToDraw); // Add the circle to the entity

    // Now that we have the box, get it
    VBox locationBox = getNodeToLocationBox().get(node);
    // Add the box to the map
    currentDrawingPane.getChildren().add(locationBox);

    // Set it's coordinates
    locationBox.setLayoutX(node.getXCoord() + 2.5);
    locationBox.setLayoutY(node.getYCoord() - 20);

    locationBox.setRotate(-45);

    if (addLocations) {
      // For each location belonging to this node
      for (LocationName nodeLocation :
          node.getCurrentLocation(getMapSession(), Date.from(Instant.now()))) {
        addLocationName(nodeLocation, node); // Add it
      }
    }
  }

  /**
   * Manages the deletion of a node, including removing the visual representation of it, removing it
   * from the current node mapping, and deleting any edges that are associated with it. Will throw
   * an exception if the node isn't mapped
   *
   * @param node the node to delete
   */
  public void deleteNode(@NonNull Node node) {
    currentDrawingPane
        .getChildren()
        .remove(getNodeToCircleMap().get(node)); // Remove the nodes circle

    // For each edge
    for (Edge edge : getEdgeToLineMap().keySet()) {
      // Check if it relates to the node we're deleting
      if (edge.getNode1().equals(node) || edge.getNode2().equals(node)) {
        currentDrawingPane
            .getChildren()
            .remove(getEdgeToLineMap().get(edge)); // Remove the edge visually
      }
    }

    // Remove the location box
    currentDrawingPane.getChildren().remove(getNodeToLocationBox().get(node));

    mapEntity.removeNode(
        node); // Remove the node, this also handles removing the edges and locations
  }

  /**
   * Replaces one node with another. Deletes the old node and replaces it with the new one. This
   * involves deleting the visual representation and creating a new one. This may be called on nodes
   * that may not belong to this floor, they will automatically be filtered
   *
   * @param oldNode the node to replace
   * @param newNode the new node
   */
  public void moveNode(@NonNull Node oldNode, @NonNull Node newNode) {
    deleteNode(oldNode); // Completely delete the old node

    if (newNode
        .getFloor()
        .equals(
            this.mapEntity
                .getMapFloor()
                .getValue())) { // If the floors are equal (we should draw this)
      addNode(newNode, true); // Add the new node

      // For each edge in the edges on this floor and associated with this node
      List<Edge> newEdges =
          getMapSession()
              .createQuery(
                  "FROM Edge "
                      + "WHERE node1.floor = :thisFloor AND node2.floor = :thisFloor AND "
                      + "(node1 = :thisNode OR node2 = :thisNode)",
                  Edge.class)
              .setCacheable(true)
              .setParameter("thisFloor", this.mapEntity.getMapFloor().getValue())
              .setParameter("thisNode", newNode)
              .getResultList();

      newEdges.forEach(this::addEdge); // Add them
    }
  }

  /**
   * Processing adding an edge to the map, including persistence and lines. Assumes the edge is
   * valid
   *
   * @param edge the edge to draw
   */
  public void addEdge(@NonNull Edge edge) {
    Line lineToDraw =
        new Line(
            edge.getNode1().getXCoord(),
            edge.getNode1().getYCoord(),
            edge.getNode2().getXCoord(),
            edge.getNode2().getYCoord());
    currentDrawingPane.getChildren().add(lineToDraw); // Add the line

    mapEntity.addEdge(edge, lineToDraw); // Add the line to the entity
  }

  /**
   * Adds a location name to the map, including setting its text
   *
   * @param locationName the location name to add
   * @param node the node to add the location onto
   */
  public void addLocationName(@NonNull LocationName locationName, @NonNull Node node) {
    Text locationToAdd = new Text(locationName.getShortName());
    locationToAdd.getStyleClass().add("map-location-name");

    // Add the text to the map
    mapEntity.addLocation(node, locationName, locationToAdd);
  }

  /**
   * Updates the text of a location name on the map
   *
   * @param oldLocation the old location
   * @param newLocation the new location
   * @param node the node the locations belong to
   */
  public void updateLocationName(
      @NonNull LocationName oldLocation, @NonNull LocationName newLocation, @NonNull Node node) {
    // Remove the old location
    removeLocationName(oldLocation);

    // Add the new location
    addLocationName(newLocation, node);
  }

  /**
   * Remove the location name from the map (physically). Does not handle DB issues
   *
   * @param locationName the location name
   */
  public void removeLocationName(@NonNull LocationName locationName) {
    // Remove the location name from the map backing
    mapEntity.removeLocationName(locationName);
  }

  public void fillServiceRequests() {
    HospitalUser currentUser = CurrentUserEntity.CURRENT_USER.getCurrentuser();

    List<Tuple> tuples =
        getMapSession()
            .createQuery(
                "Select s.id, m.node "
                    + "From ServiceRequest s, Move m "
                    + "WHERE s.assignedEmp = :user "
                    + "AND m.location = s.location "
                    + "AND m.moveDate = (Select max(m2.moveDate)"
                    + "                  FROM Move m2 "
                    + "                  WHERE s.location = m2.location "
                    + "                  GROUP BY m2.location "
                    + "                  HAVING max(m2.moveDate) <= s.dateOfSubmission)",
                Tuple.class)
            .setParameter("user", currentUser)
            .getResultList();

    for (Tuple t : tuples) {
      Node node = (Node) t.get(t.getElements().get(1));
      ServiceRequest sr = getMapSession().find(ServiceRequest.class, t.get(t.getElements().get(0)));

      if (node.getFloor().equals(mapEntity.getMapFloor().getValue())) {
        Text text = new Text(sr.toString());
        getNodeToLocationBox().get(node).getChildren().add(text);
      }
    }
  }

  /**
   * Gets the map relating nodes to circles on the map
   *
   * @return the map relating node to circles on the map
   */
  @NonNull
  public Map<Node, Circle> getNodeToCircleMap() {
    return mapEntity.getNodeToCircleMap();
  }

  /**
   * Gets the map relating edges to nodes on the map
   *
   * @return the map relating edges to lines on the map
   */
  @NonNull
  public Map<Edge, Line> getEdgeToLineMap() {
    return mapEntity.getEdgeToLineMap();
  }

  /**
   * Gets the map relating nodes to location names
   *
   * @return the map relating node to location names
   */
  @NonNull
  public Map<Node, Set<LocationName>> getNodeToLocationNameMap() {
    return mapEntity.getNodeToLocationNameMap();
  }

  /**
   * Gets the location name to text mapping
   *
   * @return the location name to text mapping
   */
  @NonNull
  public Map<LocationName, Text> getLocationNameToTextMap() {
    return mapEntity.getLocationNameToTextMap();
  }

  /**
   * Gets the node to location box mapping
   *
   * @return the node to location box mapping
   */
  @NonNull
  public Map<Node, VBox> getNodeToLocationBox() {
    return mapEntity.getNodeToLocationBox();
  }

  /**
   * Gets the session this map is using
   *
   * @return the Hibernate session this map is using
   */
  @NonNull
  public Session getMapSession() {
    return mapEntity.getMapSession();
  }

  /**
   * Redraws the entire map, from scratch. This is an EXPENSIVE operation, so should be avoided when
   * possible. This involves DB fetches to fetch data from the floor
   */
  public void redraw() {
    // Clear the gesture pane
    group.getChildren().clear();
    currentDrawingPane.getChildren().clear(); // Clear the drawing pane

    // If we have a floor to draw
    if (mapEntity.getMapFloor().getValue() != null) {
      Point2D currentCenter =
          this.gesturePane.targetPointAtViewportCentre(); // Get the current center

      // Fetch the map image from the resource dictionary, and then drop it at the root
      ImageView imageView =
          new ImageView(
              ResourceDictionary.valueOf(mapEntity.getMapFloor().getValue().name())
                  .resource); // image
      imageView.relocate(0, 0); // Relocate to 0, 0

      // Add the image to the group
      group.getChildren().add(imageView);

      // Create a pane to draw the nodes in
      group.getChildren().add(currentDrawingPane); // Add it to the group

      // Manually set the dimensions to the right size, so that dragging-out doesn't have issues
      currentDrawingPane.setPrefWidth(imageView.getImage().getWidth());
      currentDrawingPane.setPrefHeight(imageView.getImage().getHeight());

      // Get the list of nodes
      List<Node> nodes =
          getMapSession()
              .createQuery("FROM Node n WHERE n.floor = :floor", Node.class)
              .setCacheable(true)
              .setParameter("floor", mapEntity.getMapFloor().getValue())
              .getResultList();

      // Get the list of edges
      List<Edge> edges =
          getMapSession()
              .createQuery(
                  "FROM Edge WHERE node1.floor = :floor AND node2.floor = :floor", Edge.class)
              .setParameter("floor", mapEntity.getMapFloor().getValue())
              .setCacheable(true)
              .getResultList();

      // For each edge in the edges to draw
      for (Edge edge : edges) {
        // Create the edge
        addEdge(edge);
      }

      // For each node in the nodes to draw
      for (Node node : nodes) {
        addNode(node, false); // Add the node
      }

      Date now = new Date();

      // Get the moves before now
      List<Move> moves =
          getMapSession()
              .createQuery("FROM Move WHERE node.floor = :floor ORDER BY moveDate DESC", Move.class)
              .setParameter("floor", mapEntity.getMapFloor().getValue())
              .setCacheable(true)
              .stream()
              .filter((move) -> move.getMoveDate().before(now))
              .distinct()
              .toList();

      HashMap<Node, Integer> nodeToLocationCount = new HashMap<>(); // Node to location count map

      // For each location belonging to this node
      for (Move move : moves) {
        if (nodeToLocationCount.containsKey(move.getNode())
            && nodeToLocationCount.get(move.getNode()) == 1) {
          nodeToLocationCount.replace(move.getNode(), nodeToLocationCount.get(move.getNode()) + 1);

          addLocationName(move.getLocation(), move.getNode());
        } else if (!nodeToLocationCount.containsKey(move.getNode())) {
          nodeToLocationCount.put(move.getNode(), 1); // Save the node count initially

          addLocationName(move.getLocation(), move.getNode());
        }
      }

      fillServiceRequests(); // Fill the service requests, as set display text shows/hides

      // We need to re-handle updating the display text now that we've redrawn everything
      setDisplayText(filterBox.getValue());

      this.gesturePane.centreOn(currentCenter); // Re-zoom on the old center
      gesturePane.setMinScale(.001); // Set a scale that lets you go all the way out
      gesturePane.setMaxScale(10); // Set the max scale
    }
  }

  private void setDisplayText(Display display) {
    Collection<VBox> boxes = getNodeToLocationBox().values();

    switch (display.name()) {
      case "LOCATION_NAMES" -> {
        for (VBox box : boxes) {
          for (int i = 0; i < box.getChildren().size(); i++) {
            if (i == 0) box.getChildren().get(i).setOpacity(1);
            if (i == 1) box.getChildren().get(i).setOpacity(0);
          }
        }
      }
      case "SERVICE_REQUESTS" -> {
        for (VBox box : boxes) {
          for (int i = 0; i < box.getChildren().size(); i++) {
            if (i == 0) box.getChildren().get(i).setOpacity(0);
            if (i == 1) box.getChildren().get(i).setOpacity(1);
          }
        }
      }
      case "BOTH" -> {
        for (VBox box : boxes) {
          for (int i = 0; i < box.getChildren().size(); i++) {
            if (i == 0) box.getChildren().get(i).setOpacity(1);
            if (i == 1) box.getChildren().get(i).setOpacity(1);
          }
        }
      }
      case "NONE" -> {
        for (VBox box : boxes) {
          for (int i = 0; i < box.getChildren().size(); i++) {
            if (i == 0) box.getChildren().get(i).setOpacity(0);
            if (i == 1) box.getChildren().get(i).setOpacity(0);
          }
        }
      }
    }
  }

  /** Saves changes to the map */
  public void saveChanges() {
    this.mapEntity.commitMapChanges(); // Commit the map changes
  }

  /** Cancels changes to the map */
  public void cancelChanges() {
    // Get the coordinates, so we can restore view point on cancel
    Point2D currentCenter = this.gesturePane.targetPointAtViewportCentre();

    this.mapEntity.rollbackMapChanges(); // Abort the changes
    this.redraw(); // Redraw the map, as the map will now be in a different state

    this.gesturePane.centreOn(currentCenter); // Go to the previous center
  }

  /** Shuts down the map controller, ending the session it uses */
  public void exit() {
    this.mapEntity.closeMap(); // Close the map
  }

  /**
   * Handler for going up a floor
   *
   * @param actionEvent the event triggering this
   */
  public void upFloor(ActionEvent actionEvent) {
    int floorLevel = mapEntity.getMapFloor().getValue().ordinal() + 1;
    if (floorLevel > Node.Floor.values().length - 1) floorLevel = 0;

    mapEntity.getMapFloor().setValue(Node.Floor.values()[floorLevel]);
  }

  /**
   * Handler for going down a floor
   *
   * @param actionEvent the event triggering this
   */
  public void downFloor(ActionEvent actionEvent) {
    int floorLevel = mapEntity.getMapFloor().getValue().ordinal() - 1;
    if (floorLevel < 0) floorLevel = Node.Floor.values().length - 1;

    mapEntity.getMapFloor().setValue(Node.Floor.values()[floorLevel]);
  }

  /**
   * Floor selector opener binding, creates the pop-over with the floor selector and binds it to the
   * property
   *
   * @param actionEvent the event triggering this
   */
  @SneakyThrows
  public void openFloorSelector(ActionEvent actionEvent) {
    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("FloorSelectorPopUp.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    popOver.setHeaderAlwaysVisible(false); // Hide the header
    FloorSelectorController floorPopup = newLoad.getController();
    floorPopup.setFloorProperty(this.mapEntity.getMapFloor());

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node)
            actionEvent.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover

    // Set the button to be disabled until the window is closed
    floorSelectorButton.setDisable(true);
    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                floorSelectorButton.setDisable(false); // On close, re-enable
              }
            });
  }

  public enum Display {
    LOCATION_NAMES("Location Names"),
    SERVICE_REQUESTS("Service Requests"),
    BOTH("Both"),
    NONE("None");

    @NonNull public final String DisplayOption;

    /**
     * Creates a new floor with the given String backing
     *
     * @param displayOption the displayOption to create. Must not be null
     */
    Display(@NonNull String displayOption) {
      DisplayOption = displayOption;
    }
  }

  /**
   * Gets the current width of the drawing pane, AKA the image width
   *
   * @return the image width
   */
  public double getMapWidth() {
    return currentDrawingPane.getWidth();
  }

  /**
   * Gets the current height of the drawing pane, AKA the image height
   *
   * @return the image height
   */
  public double getMapHeight() {
    return currentDrawingPane.getHeight();
  }
}
