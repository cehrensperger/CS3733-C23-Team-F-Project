package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;

public class PathFindingController {

  @FXML private MFXTextField start;
  @FXML private MFXTextField end;
  @FXML private Text pathText;
  @FXML private MFXButton getPath;
  @FXML private MFXButton backButton;
  @FXML private MFXButton clearButton;
  @FXML private AnchorPane mapPane;
  @FXML private MFXComboBox<Node.Floor> floorSelector;
  @FXML private HBox buttonsHBox;

  private MapController mapController;
  AtomicReference<PopOver> mapPopOver =
      new AtomicReference<>(); // The pop-over the map is using for node highlighting

  @SneakyThrows
  public void initialize() {
    Fapp.getPrimaryStage()
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {

              //should figure out how to get rid of magic numbers
              buttonsHBox.setMaxWidth(newValue.doubleValue() - 30.0);
              buttonsHBox.setMinWidth(newValue.doubleValue() - 30.0);

              // for debugging
              // buttonsHBox.setBackground(Background.fill(Color.RED));
            });
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(getClass().getResource("../views/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    mapPane.getChildren().add(map); // Put the map loader into the editor box
    mapController = mapLoader.getController();
    mapController.setFloor(Node.Floor.L1);
    floorSelector
        .getItems()
        .addAll(Node.Floor.values()); // Add all the floors to the floor selector

    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorSelector
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setFloor(newValue);
              try {
                handleGetPath(null);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
    drawNodesAndEdges();

    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    // Set the node creation processor
  }

  @FXML private MFXButton question;

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    mapController.exit();
    Fapp.setScene("Home");
  }

  public void handleButtonClear(ActionEvent event) throws IOException {
    start.clear();
    end.clear();
    drawNodesAndEdges();
    // pathText.setText("Path:");
  }

  public void handleGetPath(ActionEvent actionEvent) throws IOException {
    drawNodesAndEdges();

    // get start and end locations from text fields
    String startPath = start.getText();
    String endPath = end.getText();
    // Transaction transaction = session.beginTransaction();

    PathFinder pathFinder = new PathFinder(mapController.getMapSession());

    // display path as text
    try {
      pathText.setText(
          "Path:\n"
              + pathFinder.nodeListToLocation(
                  pathFinder.findPath(startPath, endPath), mapController.getMapSession()));
    } catch (NullPointerException e) {
      System.out.println("Error: No data in database");
    }

    // list of nodes that represent the shortest path
    List<Node> nodes = pathFinder.findPath(startPath, endPath);

    // color all circles that the mapController is displaying right now
    // and that are part of the path red
    for (Node node : nodes) {
      Circle circle = mapController.getNodeToCircleMap().get(node);
      if (circle != null) {

        circle.setFill(Paint.valueOf(Color.RED.toString()));
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

      System.out.println(mapController.getEdgeToLineMap().get(edge));
      System.out.println();

      // get the line on the map associated with the edge
      Line line = mapController.getEdgeToLineMap().get(edge);

      // if it is null, it is probably on another floor
      if (line != null) {
        line.setStroke(Paint.valueOf(Color.RED.toString()));
      }
      System.out.println(mapController.getEdgeToLineMap().get(edge));
    }

    // session.close();
  }

  private void drawNodesAndEdges() {
    for (Node node : mapController.getNodeToCircleMap().keySet()) {
      Circle circle = mapController.getNodeToCircleMap().get(node);
      if (circle != null) {
        circle.setFill(Paint.valueOf(Color.BLACK.toString()));
        circle
            .hoverProperty()
            .addListener(
                (observable, oldValue, newValue) -> {
                  System.out.println("hovering");
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
                            getClass().getResource("../views/NodeLocationNamePopUp.fxml"));

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
    }
    for (Line line : mapController.getEdgeToLineMap().values()) {
      line.setStroke(Paint.valueOf(Color.BLACK.toString()));
    }
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(getClass().getResource("../views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQPathFinding();

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }
}
