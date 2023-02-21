package edu.wpi.FlashyFrogs.Map;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
import edu.wpi.FlashyFrogs.ResourceDictionary;
import io.github.palexdev.materialfx.utils.others.TriConsumer;
import jakarta.persistence.Tuple;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import net.kurobako.gesturefx.GesturePane;
import org.hibernate.Session;

/**
 * Controller for the Map of the hospital. Provides utilities to make it useful for classes that may
 * want to use this. Listens for additions/removals of nodes/edges/locations in order to
 * automatically display changes to the map
 */
@GeneratedExclusion
public class MapController {

  @FXML @Getter private GesturePane gesturePane; // Gesture pane, used to zoom to given locations
  @FXML private Group group; // Group that will be used as display in the gesture pane

  @Getter
  private final Pane currentDrawingPane =
      new Pane(); // The current drawing pane to use to draw nodes/edges

  @NonNull private final MapEntity mapEntity = new MapEntity(); // The entity the map will use

  public void initialize() {
    gesturePane.setScrollBarPolicy(GesturePane.ScrollBarPolicy.NEVER);
    Platform.runLater(() -> gesturePane.zoomTo(0.15, new javafx.geometry.Point2D(2500, 1700)));
  }

  /**
   * Sets the node creation function
   *
   * @param function the function to set the node creation function to. May be null
   */
  public void setNodeCreation(BiConsumer<Node, Circle> function) {
    mapEntity.setNodeCreation(function);
  }

  /**
   * Set the edge creation function
   *
   * @param function the function to set the edge creation to. May be null
   */
  public void setEdgeCreation(BiConsumer<Edge, Line> function) {
    mapEntity.setEdgeCreation(function);
  }

  /**
   * Set the location creation function
   * @param function the function to set the location creation to. May be null
   */
  public void setLocationCreation(TriConsumer<Node, LocationName, Text> function) {
    mapEntity.setLocationCreation(function);
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
    // TODO: remove because everyone hates print statements I guess
    // node.getChildren().forEach(node1 -> System.out.println(node1.getId()));

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

  public void deleteAndAutoRepair(Node node) {

    List<Node> allChildren = node.getChildren(getMapSession());

    ArrayList<Node> sameFloorChildren =
        new ArrayList<>(
            allChildren.stream()
                .filter(node1 -> node.getFloor().equals(node1.getFloor()))
                .toList());

    ArrayList<Node> differentFloorChildren =
        new ArrayList<>(
            allChildren.stream()
                .filter(node1 -> !node.getFloor().equals(node1.getFloor()))
                .toList());

    repairNodes(sameFloorChildren);
    repairNodes(differentFloorChildren);
    deleteNode(node);
  }

  private void repairNodes(List<Node> nodes) {
    if (nodes.size() == 0) {
      return;
    }
    List<Node> nodesLeftToRepair = nodes;

    // the last node should already be connected to something else, so we do not need
    // to connect it again

    while (nodesLeftToRepair.size() > 1) {

      // connect the first node in the list to the closest other node
      Node startingNode = nodesLeftToRepair.get(0);

      // find the closest other node
      // set the closest node to be the second one in the list to start
      Node closestNode = nodesLeftToRepair.get(1);
      double smallestDistance = startingNode.getDistanceFrom(closestNode);

      // if there are only two nodes left in the list, they by default are the closest to each other
      if (nodesLeftToRepair.size() > 2) {
        // start comparing distances with the third node in the list since we
        // already accounted for the second node in the list
        for (int i = 2; i < nodesLeftToRepair.size(); i++) {
          // update smallestDistance and closestNode if a closer node is found
          double newDistance = startingNode.getDistanceFrom(nodesLeftToRepair.get(i));
          // if the new distance is now the smallest AND the edge doesn't already exist (both ways)
          if (newDistance < smallestDistance
              && getMapSession().find(Edge.class, new Edge(startingNode, nodesLeftToRepair.get(i)))
                  == null
              && getMapSession().find(Edge.class, new Edge(nodesLeftToRepair.get(i), startingNode))
                  == null) {
            // update smallest and closest vars
            smallestDistance = newDistance;
            closestNode = nodesLeftToRepair.get(i);
          }
        }
      }

      Edge newEdge = new Edge(startingNode, closestNode);

      if (getMapSession().find(Edge.class, newEdge) == null
          && getMapSession().find(Edge.class, new Edge(closestNode, startingNode)) == null) {

        // node can be repaired and connected now to the closes node
        getMapSession().persist(newEdge);
        addEdge(newEdge);
        // nodesLeftToRepair.remove(0);
        getMapSession().flush();
      }

      // remove the node
      nodesLeftToRepair.remove(0);
    }
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
        .equals(this.getFloor())) { // If the floors are equal (we should draw this)
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
              .setParameter("thisFloor", this.getFloor())
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

      if (node.getFloor().equals(getFloor())) {
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
    if (mapEntity.getMapFloor() != null) {
      // Fetch the map image from the resource dictionary, and then drop it at the root
      ImageView imageView =
          new ImageView(
              ResourceDictionary.valueOf(mapEntity.getMapFloor().name()).resource); // image
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
              .setParameter("floor", mapEntity.getMapFloor())
              .getResultList();

      // Get the list of edges
      List<Edge> edges =
          getMapSession()
              .createQuery(
                  "FROM Edge WHERE node1.floor = :floor AND node2.floor = :floor", Edge.class)
              .setParameter("floor", mapEntity.getMapFloor())
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
              .setParameter("floor", getFloor())
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

      gesturePane.setMinScale(.001); // Set a scale that lets you go all the way out
    }
  }

  public void setDisplayText(Display display) {
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

  /**
   * Changes the floor that the map is displaying
   *
   * @param floor the new floor to display. Must not be null
   */
  public void setFloor(Node.Floor floor) {
    Point2D currentCenter =
        this.gesturePane.targetPointAtViewportCentre(); // Get the current center

    this.mapEntity.setMapFloor(floor); // Set the floor in the entity
    redraw(); // Force a redraw/re-fetch from scratch

    this.gesturePane.centreOn(currentCenter); // Re-zoom on the old center
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
   * Gets the floor the map is current on
   *
   * @return the floor the map is currently on, may be null
   */
  public Node.Floor getFloor() {
    return this.mapEntity.getMapFloor();
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
