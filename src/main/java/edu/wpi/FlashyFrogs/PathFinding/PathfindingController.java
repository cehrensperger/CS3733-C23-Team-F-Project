package edu.wpi.FlashyFrogs.PathFinding;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathVisualizer.AbstractPathVisualizerController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.MathUtils;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

@GeneratedExclusion
public class PathfindingController extends AbstractPathVisualizerController implements IController {
  @FXML private SearchableComboBox<LocationName> startingBox;
  @FXML private SearchableComboBox<LocationName> destinationBox;
  @FXML private SearchableComboBox<String> algorithmBox;
  @FXML private CheckBox accessibleBox;
  @FXML private AnchorPane mapPane;
  @FXML private MFXButton mapEditorButton;
  @FXML private DatePicker moveDatePicker;
  @FXML private TableView<Instruction> pathTable;
  @FXML private TableColumn<Instruction, String> pathCol;
  //  @FXML private Label error;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;

  boolean hDone = false;

  /**
   * Initializes the path finder, sets up the floor selector, and the map including default behavior
   */
  @SneakyThrows
  public void initialize() {
    moveDatePicker.setValue(LocalDate.now());
    moveDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setDate(
                  Date.from(newValue.atStartOfDay(ZoneId.of("America/Montreal")).toInstant()));
              mapController.redraw();
            });
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);
    pathTable.setVisible(false);
    // set resizing behavior
    Fapp.getPrimaryStage().widthProperty().addListener((observable, oldValue, newValue) -> {});

    mapPane.getChildren().add(0, map); // Put the map loader into the editor box

    // make the anchor pane resizable
    AnchorPane.setTopAnchor(map, 0.0);
    AnchorPane.setBottomAnchor(map, 0.0);
    AnchorPane.setLeftAnchor(map, 0.0);
    AnchorPane.setRightAnchor(map, 0.0);

    // don't create a new session since the map is already using one
    Session session = mapController.getMapSession();

    // get the list of all location names from the database
    List<LocationName> objects =
        session.createQuery("SELECT location FROM Move", LocationName.class).getResultList();

    // sort the locations alphabetically, algorithms already alphabetical
    objects.sort(Comparator.comparing(LocationName::getLongName));

    // make the list of algorithms
    List<String> algorithms = new LinkedList<>();
    algorithms.add("A*");
    algorithms.add("Breadth-first");
    algorithms.add("Depth-first");

    // Populate the boxes
    startingBox.setItems(FXCollections.observableList(objects));
    destinationBox.setItems(FXCollections.observableList(objects));
    algorithmBox.setItems(FXCollections.observableList(algorithms));

    algorithmBox.setValue("A*");

    // Get whether the user is an admin
    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    // Decide what to do with the admin button based on that
    if (!isAdmin) {
      mapEditorButton.setDisable(true);
      mapEditorButton.setOpacity(0);
    } else {
      mapEditorButton.setDisable(false);
      mapEditorButton.setOpacity(1);
    }

    pathCol.setCellValueFactory(new PropertyValueFactory<>("instruction"));

    pathTable.setRowFactory(
        param -> {
          TableRow<Instruction> row = new TableRow<>(); // Create a new table row to use

          // When the user selects a row, just un-select it to avoid breaking formatting
          row.selectedProperty()
              .addListener(
                  // Add a listener that does that
                  (observable, oldValue, newValue) -> row.updateSelected(false));

          // Add a listener to show the pop-up
          row.setOnMouseClicked(
              (event) -> {
                // If the pop over exists and is either not focused or we are showing a new
                // row
                if (row != null) {
                  setFloor(row.getItem().node.getFloor());
                  //                  Platform.runLater(
                  //                      () ->
                  //                          mapController
                  //                              .getGesturePane()
                  //                              .zoomTo(
                  //                                  .8,
                  //                                  new javafx.geometry.Point2D(
                  //                                      row.getItem().node.getXCoord(),
                  //                                      row.getItem().node.getYCoord())));
                }
              });
          return row;
        });
  }

  /** Callback to handle the back button being pressed */
  @SneakyThrows
  @FXML
  private void handleBack() {
    Fapp.handleBack(); // Delegate to Fapp
  }

  /** Method that generates table for textual path instructions */
  private void drawTable() {
    pathTable.setVisible(true);

    ObservableList<Instruction> instructions = FXCollections.observableArrayList();

    double curAngle = 0;

    pathTable.setItems(instructions);
    for (int i = 0; i < currentPath.size() - 1; i++) { // For each line in the path
      Node thisNode = currentPath.get(i);
      Node nextNode = currentPath.get(i + 1);

      double target =
          Math.atan2(
              (nextNode.getYCoord() - thisNode.getYCoord()),
              (nextNode.getXCoord() - thisNode.getXCoord()));
      double errorTheta = target - curAngle;
      curAngle = target;

      errorTheta = MathUtils.normalizeAngle(errorTheta, 0.0);

      int errorDeg = (int) Math.toDegrees(errorTheta);

      String nodeName =
          thisNode
              .getCurrentLocation(
                  mapController.getMapSession(),
                  Date.from(
                      moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()))
              .stream()
              .findFirst()
              .orElse(new LocationName("", LocationName.LocationType.HALL, ""))
              .getShortName();

      if (nodeName.equals("")) {
        if (errorDeg < -10) {
          instructions.add(new Instruction("Turn right " + -errorDeg + " degrees", thisNode));
        } else if (errorDeg > 10) {
          instructions.add(new Instruction("Turn left " + errorDeg + " degrees", thisNode));
        } else {
          instructions.add(new Instruction("Continue", thisNode));
        }
      } else {
        if (errorDeg < -10) {
          instructions.add(
              new Instruction("Turn right " + -errorDeg + " degrees at " + nodeName, thisNode));
        } else if (errorDeg > 10) {
          instructions.add(
              new Instruction("Turn left " + errorDeg + " degrees at " + nodeName, thisNode));
        } else {
          instructions.add(new Instruction("Continue at " + nodeName, thisNode));
        }
      }
    }

    instructions.add(
        new Instruction(
            "You have arrived at " + destinationBox.valueProperty().get(),
            currentPath.get(currentPath.size() - 1)));
  }

  /** Method that handles drawing a new path (AKA the submit button handler) */
  @SneakyThrows
  public void handleGetPath() {
    // get start and end locations from text fields
    LocationName startPath = startingBox.valueProperty().get();
    LocationName endPath = destinationBox.valueProperty().get();
    Boolean accessible = accessibleBox.isSelected();

    // get algorithm to use in pathfinding from algorithmBox
    if (algorithmBox.getValue() != null) {
      switch (algorithmBox.getValue()) {
        case "Breadth-first" -> pathFinder.setAlgorithm(new BreadthFirst());
        case "Depth-first" -> pathFinder.setAlgorithm(new DepthFirst());
        default -> pathFinder.setAlgorithm(new AStar());
      }
    }

    unColorFloor(); // hide the last drawn path

    // Get the new path from the PathFinder
    Node startNode =
        startPath.getCurrentNode(
            mapController.getMapSession(),
            Date.from(moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    Node endNode =
        endPath.getCurrentNode(
            mapController.getMapSession(),
            Date.from(moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    currentPath = pathFinder.findPath(startNode, endNode, accessible);

    // Check that we actually got a path
    if (currentPath == null) {
      // if nodes is null, that means the there was no possible path
      //      error.setTextFill(Paint.valueOf(Color.RED.toString()));
      //      error.setText("No path found");
      System.out.println("no path found");
    } else {
      setFloor(startNode.getFloor()); // Go to the starting floor
      // Zoom to the coordinates of the starting node
      mapController.zoomToCoordinates(5, startNode.getXCoord(), startNode.getYCoord());
      colorFloor(); // Draw the path
      setFloor(startNode.getFloor());
      drawTable();
    }
  }

  /** Callback to open the map editor from a button */
  @FXML
  public void openMapEditor() {
    Fapp.setScene("MapEditor", "MapEditorView");
  }

  /**
   * Callback to handle the help button being pressed
   *
   * @param event the event triggering this
   */
  @FXML
  @SneakyThrows
  public void handleQ(ActionEvent event) {
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

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      hDone = false;
    }
  }

  /**
   * Sets the floor for the map
   *
   * @param floor the new floort
   */
  public void setFloor(@NonNull Node.Floor floor) {
    mapController.getMapFloorProperty().setValue(floor);
  }

  public static class Instruction {
    @Getter @Setter private String instruction;
    @Getter @Setter private Node node;

    Instruction(String instruction, Node node) {
      this.instruction = instruction;
      this.node = node;
    }
  }
}
