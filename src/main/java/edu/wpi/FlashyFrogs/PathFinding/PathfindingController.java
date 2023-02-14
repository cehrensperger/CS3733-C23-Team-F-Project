package edu.wpi.FlashyFrogs.PathFinding;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.Map.NodeLocationNamePopUpController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ORM.User;
import edu.wpi.FlashyFrogs.controllers.FloorSelectorController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

public class PathfindingController {

  @FXML private SearchableComboBox<String> startingBox;
  @FXML private SearchableComboBox<String> destinationBox;
  @FXML private SearchableComboBox<String> algorithmBox;
  @FXML private AnchorPane mapPane;
  @FXML private Label floorSelector;
  @FXML private MFXButton mapEditorButton;
  //  @FXML private Label error;

  private MapController mapController;
  AtomicReference<PopOver> mapPopOver =
      new AtomicReference<>(); // The pop-over the map is using for node highlighting

  ObjectProperty<Node.Floor> floorProperty = new SimpleObjectProperty<>(Node.Floor.L1);

  // TODO: GENERATE CIRCLES AND LINES THE WAY MICHAEL SAID TO
  // TODO: TRUST
  @SneakyThrows
  public void initialize() {
    // set resizing behavior
    Fapp.getPrimaryStage().widthProperty().addListener((observable, oldValue, newValue) -> {});

    // load map page
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(Fapp.class.getResource("Map/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    mapPane.getChildren().add(0, map); // Put the map loader into the editor box
    mapController = mapLoader.getController();
    mapController.setFloor(Node.Floor.L1);
    floorSelector.setText("Floor " + Node.Floor.L1.name());

    // make the anchor pane resizable
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    // don't create a new session since the map is already using one
    Session session = mapController.getMapSession();

    // get the list of all location names from the database
    List<String> objects =
        session
            .createQuery(
                "SELECT l.longName FROM LocationName l, Move m WHERE m.location.longName = l.longName",
                String.class)
            .getResultList();

    // sort the locations alphabetically, algorithms already alphabetical
    objects.sort(String::compareTo);

    // make the list of algorithms
    List<String> algorithms = new LinkedList<>();
    algorithms.add("A*");
    algorithms.add("Breadth-first");
    algorithms.add("Depth-first");

    startingBox.setItems(FXCollections.observableList(objects));
    destinationBox.setItems(FXCollections.observableList(objects));
    algorithmBox.setItems(FXCollections.observableList(algorithms));

    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorProperty.addListener(
        (observable, oldValue, newValue) -> {
          mapController.setFloor(newValue);
          // drawNodesAndEdges(); // Re-draw pop-ups
          try {
            // If we have a valid path
            floorSelector.setText("Floor " + newValue.floorNum);
            hideAll();
            if (startingBox.valueProperty().getValue() != null
                && destinationBox.valueProperty().getValue() != null) {
              handleGetPath();
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

    User currentUser = CurrentUserEntity.CURRENT_USER.getCurrentuser();
    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    if (!isAdmin) {
      mapEditorButton.disarm();
      mapEditorButton.setOpacity(0);
    } else {
      mapEditorButton.arm();
      mapEditorButton.setOpacity(1);
    }

    // hide all nodes and edges on the map so that the user will only see the
    // nodes and edges relevant to the path they generate
    hideAll();
  }

  public void handleBack() throws IOException {
    mapController.exit();
    Fapp.setScene("views", "Home");
  }

  private void hideAll() {

    // set all opacities to 0
    for (Line line : mapController.getEdgeToLineMap().values()) {
      line.setOpacity(0);
    }

    for (Circle circle : mapController.getNodeToCircleMap().values()) {
      circle.setOpacity(0);
    }
  }

  // might not be necessary
  //  public void handleButtonClear(ActionEvent event) throws IOException {
  //    startingBox.valueProperty().set(null);
  //    destinationBox.valueProperty().set(null);
  //    algorithmBox.valueProperty().set(null);
  //    hideAll();
  //  }

  public void handleGetPath() throws IOException {
    // initially hide all nodes and edges
    // later show the nodes and edges that are part of the path
    hideAll();

    // get start and end locations from text fields
    String startPath = startingBox.valueProperty().get();
    String endPath = destinationBox.valueProperty().get();

    PathFinder pathFinder = new PathFinder(mapController.getMapSession());

    // get algorithm to use in pathfinding from algorithmBox
    if (algorithmBox.getValue() != null) {
      switch (algorithmBox.getValue()) {
        case "Breadth-first" -> pathFinder.setAlgorithm(new BreadthFirst());
        case "Depth-first" -> pathFinder.setAlgorithm(new DepthFirst());
        default -> pathFinder.setAlgorithm(new AStar());
      }
    }

    List<Node> nodes = pathFinder.findPath(startPath, endPath);

    if (nodes == null) {
      // if nodes is null, that means the there was no possible path
      //      error.setTextFill(Paint.valueOf(Color.RED.toString()));
      //      error.setText("No path found");
      System.out.println("no path found");
    } else {
      if (algorithmBox == null) {
        System.out.println("choose an algorithm");
      }
      if (nodes == null) {
        // if nodes is null, that means the there was no possible path
        //      error.setTextFill(Paint.valueOf(Color.RED.toString()));
        //      error.setText("No path found");
        System.out.println("no path found");
      } else {
        // color all circles that are part of the path red

        //      error.setText(""); // take away error message if there was one

        for (Node node : nodes) {
          // get the circle that represents the node from the mapController
          Circle circle = mapController.getNodeToCircleMap().get(node);

          if (circle != null) { // TODO: fix this garbage
            circle.setOpacity(0);

            // set hover behavior for each circle
            // TODO: change this to click behavior like in the map data editor
            circle
                .hoverProperty()
                .addListener(
                    (observable, oldValue, newValue) -> {
                      // If we're no longer hovering and the pop-over exists, delete it. We will
                      // either create a new one
                      // or, keep it deleted
                      if (mapPopOver.get() != null && (!mapPopOver.get().isFocused() || newValue)) {
                        mapPopOver.get().hide(); // Hide it
                        mapPopOver.set(null); // And delete it (set it to null)
                      }

                      // If we should draw a new pop-up
                      if (newValue) {
                        // Get the node info in FXML form
                        FXMLLoader nodeLocationNamePopUp =
                            new FXMLLoader(
                                Fapp.class.getResource("Map/NodeLocationNamePopUp.fxml"));

                        try {
                          // Try creating the pop-over
                          mapPopOver.set(new PopOver(nodeLocationNamePopUp.load()));
                        } catch (IOException e) {
                          throw new RuntimeException(e); // If it fails, throw an exception
                        }
                        NodeLocationNamePopUpController controller =
                            nodeLocationNamePopUp.getController();
                        controller.setNode(node, mapController.getMapSession());

                        mapPopOver.get().show(circle); // Show the pop-over
                      }
                    });
          }
          // get location name of the node in the path to check against the start and end locations
          // getCurrentLocation() creates its own session but map already has one running,
          // so we have to use that one

          LocationName nodeLocation = node.getCurrentLocation(mapController.getMapSession()).get(0);

          // if the node location is null, don't attempt to check it against the start and end text
          if (nodeLocation != null
              && nodeLocation.toString().equals(destinationBox.valueProperty().get())) {
            circle.setFill(Paint.valueOf(Color.GREEN.toString()));
            circle.setOpacity(1);
          } else if (nodeLocation != null
              && nodeLocation.toString().equals(startingBox.valueProperty().get())) {
            circle.setFill(Paint.valueOf(Color.BLUE.toString()));
            circle.setOpacity(1);
          }
        }
      }
    }

    for (int i = 1; i < nodes.size(); i++) {
      // find the edge related to each pair of nodes
      Edge edge =
          mapController.getMapSession().find(Edge.class, new Edge(nodes.get(i - 1), nodes.get(i)));

      // if it couldn't find the edge, reverse the direction and look again
      if (edge == null) {
        edge =
            mapController
                .getMapSession()
                .find(Edge.class, new Edge(nodes.get(i), nodes.get(i - 1)));
      }

      // get the line on the map associated with the edge
      Line line = mapController.getEdgeToLineMap().get(edge);
      // if it is null, it is probably on another floor
      if (line != null) {
        line.setOpacity(1);
        line.setStroke(Paint.valueOf(Color.BLUE.toString()));
        line.setStrokeWidth(5);
      }
    }
  }

  private void setHoverBehavior(Circle circle, Node node) {
    circle
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // If we're no longer hovering and the pop-over exists, delete it. We will
              // either create a new one
              // or, keep it deleted
              if (mapPopOver.get() != null && (!mapPopOver.get().isFocused() || newValue)) {
                mapPopOver.get().hide(); // Hide it
                mapPopOver.set(null); // And delete it (set it to null)
              }

              // If we should draw a new pop-up
              if (newValue) {
                // Get the node info in FXML form
                FXMLLoader nodeLocationNamePopUp =
                    new FXMLLoader(Fapp.class.getResource("Map/NodeLocationNamePopUp.fxml"));

                try {
                  // Try creating the pop-over
                  mapPopOver.set(new PopOver(nodeLocationNamePopUp.load()));
                } catch (IOException e) {
                  throw new RuntimeException(e); // If it fails, throw an exception
                }
                NodeLocationNamePopUpController controller = nodeLocationNamePopUp.getController();
                controller.setNode(node, mapController.getMapSession());

                mapPopOver.get().show(circle); // Show the pop-over
              }
            });
  }

  @FXML
  public void openMapEditor() {
    mapController.exit();
    Fapp.setScene("MapEditor", "MapEditorView");
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {
    // load the help page
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    // load a pop-over object with the help page in it
    PopOver popOver = new PopOver(newLoad.load());

    // get the controller of the help page
    HelpController help = newLoad.getController();
    // show the correct text for the path finding page specifically
    help.handleQPathFinding();

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  @FXML
  public void upFloor() {
    int floorLevel = floorProperty.getValue().ordinal() + 1;
    if (floorLevel > Node.Floor.values().length - 1) floorLevel = 0;

    floorProperty.setValue(Node.Floor.values()[floorLevel]);
  }

  @FXML
  public void downFloor() {
    int floorLevel = floorProperty.getValue().ordinal() - 1;
    if (floorLevel < 0) floorLevel = Node.Floor.values().length - 1;

    floorProperty.setValue(Node.Floor.values()[floorLevel]);
  }

  @FXML
  public void openFloorSelector(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/FloorSelectorPopUp.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    FloorSelectorController floorPopup = newLoad.getController();
    floorPopup.setFloorProperty(this.floorProperty);

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node) event.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover
  }
}
