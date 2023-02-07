package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathFinder;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.hibernate.Session;

public class PathFindingController {

  @FXML private MFXTextField start;
  @FXML private MFXTextField end;
  @FXML private Text pathText;
  @FXML private MFXButton getPath;
  @FXML private MFXButton backButton;
  @FXML private MFXButton clearButton;
  @FXML private AnchorPane mapPane;

  private MapController mapController;

  @SneakyThrows
  public void initialize() {
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(getClass().getResource("../views/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    mapPane.getChildren().add(map); // Put the map loader into the editor box
    mapController = mapLoader.getController();
    mapController.setFloor(Node.Floor.L1);

    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    // Set the node creation processor
  }

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }

  public void handleButtonClear(ActionEvent event) throws IOException {
    start.clear();
    end.clear();
    pathText.setText("Path:");
  }

  public void handleGetPath(ActionEvent actionEvent) throws IOException {

    // reset all lines and circles to be black
    for (Circle circle : mapController.getNodes().values()) {
      if (circle != null) {
        circle.setFill(Paint.valueOf(Color.BLACK.toString()));
      }
    }
    for (Line line : mapController.getEdges().values()) {
      line.setStroke(Paint.valueOf(Color.BLACK.toString()));
    }

    // create session for PathFinder
    Session session = CONNECTION.getSessionFactory().openSession();
    // session.find(LocationName.class, start.getText());
    // LocationName startPath = session.find(LocationName.class, start.getText());
    // LocationName endPath = session.find(LocationName.class, end.getText());

    // get start and end locations from text fields
    String startPath = start.getText();
    String endPath = end.getText();
    // Transaction transaction = session.beginTransaction();
    PathFinder pathFinder = new PathFinder(CONNECTION.getSessionFactory());

    // display path as text
    try {
      pathText.setText(
          "Path:\n"
              + pathFinder.nodeListToLocation(pathFinder.findPath(startPath, endPath), session));
    } catch (NullPointerException e) {
      System.out.println("Error: No data in database");
    }

    // list of nodes that represent the shortest path
    List<Node> nodes = pathFinder.findPath(startPath, endPath);

    // color all circles that the mapController is displaying right now
    // and that are part of the path red
    for (Node node : nodes) {
      Circle circle = mapController.getNodes().get(node);
      if (circle != null) {

        circle.setFill(Paint.valueOf(Color.RED.toString()));
      }
    }

    for (int i = 1; i < nodes.size(); i++) {
      //find the edge related to each pair of nodes
      Edge edge = session.find(Edge.class, new Edge(nodes.get(i - 1), nodes.get(i)));

      //if it couldn't find the edge, reverse the direction and look again
      if (edge == null) {
        edge = session.find(Edge.class, new Edge(nodes.get(i), nodes.get(i - 1)));
      }


      System.out.println(mapController.getEdges().get(edge));
      System.out.println();

      //get the line on the map associated with the edge
      Line line = mapController.getEdges().get(edge);

      //if it is null, it is probably on another floor
      if (line != null) {
        line.setStroke(Paint.valueOf(Color.RED.toString()));
      }
      System.out.println(mapController.getEdges().get(edge));
    }

    session.close();
  }
}
