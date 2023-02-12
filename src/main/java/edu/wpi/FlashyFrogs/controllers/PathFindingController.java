package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Map.MapController;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinder;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class PathFindingController {

  @FXML private MFXFilterComboBox<String> start;
  @FXML private MFXFilterComboBox<String> end;
  @FXML private AnchorPane mapPane;
  @FXML private MFXComboBox<Node.Floor> floorSelector;
  @FXML private HBox buttonsHBox;
  @FXML private Label error;

  private MapController mapController;
  AtomicReference<PopOver> mapPopOver =
      new AtomicReference<>(); // The pop-over the map is using for node highlighting

  @SneakyThrows
  public void initialize() {
    // set resizing behavior
    Fapp.getPrimaryStage()
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {

              // TODO: figure out how to get rid of magic numbers
              buttonsHBox.setMaxWidth(newValue.doubleValue() - 30.0);
              buttonsHBox.setMinWidth(newValue.doubleValue() - 30.0);
            });

    // load map page
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(Fapp.class.getResource("Map/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    mapPane.getChildren().add(map); // Put the map loader into the editor box
    mapController = mapLoader.getController();
    mapController.setFloor(Node.Floor.L1);
    floorSelector
        .getItems()
        .addAll(Node.Floor.values()); // Add all the floors to the floor selector
    floorSelector.setText("L1");

    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorSelector
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setFloor(newValue);

              // drawNodesAndEdges(); // Re-draw pop-ups

              try {
                // If we have a valid path
                if (!start.getText().equals("") && !end.getText().equals("")) {
                  handleGetPath(null);
                }
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });

    // make the anchor pane resizable
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    // don't create a new session since the map is already using one
    Session session = mapController.getMapSession();

    // get the list of all location names from the database
    List<String> objects =
        session.createQuery("SELECT longName FROM LocationName", String.class).getResultList();

    // sort the locations alphabetically
    objects.sort(String::compareTo);

    // set the items of the dropdowns to be the location names
    start.setItems(FXCollections.observableList(objects));
    end.setItems(FXCollections.observableList(objects));

    // hide all nodes and edges on the map so that the user will only see the
    // nodes and edges relevant to the path they generate
    hideAll();
  }

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
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

  public void handleButtonClear(ActionEvent event) throws IOException {
    start.setText("");
    end.setText("");
    hideAll();
  }

  public void handleGetPath(ActionEvent actionEvent) throws IOException {
    // initially hide all nodes and edges
    // later show the nodes and edges that are part of the path
    hideAll();

    // get start and end locations from text fields
    String startPath = start.getText();
    String endPath = end.getText();

    PathFinder pathFinder = new PathFinder(mapController.getMapSession());

    // list of nodes that represent the shortest path
    List<Node> nodes = pathFinder.findPath(startPath, endPath);

    if (nodes == null) {
      // if nodes is null, that means the there was no possible path
      error.setTextFill(Paint.valueOf(Color.RED.toString()));
      error.setText("No path found");
    } else {
      // color all circles that are part of the path red

      error.setText(""); // take away error message if there was one

      for (Node node : nodes) {
        // get the circle that represents the node from the mapController
        Circle circle = mapController.getNodeToCircleMap().get(node);

        if (circle != null) {
          circle.setOpacity(1);

          // set hover behavior for each circle
          // TODO: change this to click behavior like in the map data editor
          circle
              .hoverProperty()
              .addListener(
                  (observable, oldValue, newValue) -> {
                    // If we're no longer hovering and the pop over exists, delete it. We will
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
                              Fapp.class.getResource("views/NodeLocationNamePopUp.fxml"));

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

          // get location name of the node in the path to check against the start and end locations
          // getCurrentLocation() creates its own session but map already has one running,
          // so we have to use that one

          LocationName nodeLocation = node.getCurrentLocation(mapController.getMapSession());

          // if the node location is null, don't attempt to check it against the start and end text
          if (nodeLocation != null && nodeLocation.toString().equals(start.getText())) {
            // blue for start node
            circle.setFill(Paint.valueOf(Color.BLUE.toString()));
          } else if (nodeLocation != null && nodeLocation.toString().equals(end.getText())) {
            // green for end node
            circle.setFill(Paint.valueOf(Color.GREEN.toString()));
          } else {
            // red for in-between nodes
            circle.setFill(Paint.valueOf(Color.RED.toString()));
          }
        }
      }

      for (int i = 1; i < nodes.size(); i++) {
        // find the edge related to each pair of nodes
        Edge edge =
            mapController
                .getMapSession()
                .find(Edge.class, new Edge(nodes.get(i - 1), nodes.get(i)));

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
          line.setStroke(Paint.valueOf(Color.RED.toString()));
        }
      }
    }
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {
    // load the help page
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    // load a pop-over object with the help page in it
    PopOver popOver = new PopOver(newLoad.load());

    // get the contoller of the help page
    HelpController help = newLoad.getController();
    // show the correct text for the path finding page specifically
    help.handleQPathFinding();

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
