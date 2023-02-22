package edu.wpi.FlashyFrogs.PathFinding;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.MapEditor.MapEditorController;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.PathVisualizer.AbstractPathVisualizerController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
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
  @FXML private MFXButton generatePathButton;
  @FXML private Pane animationPane;
  @FXML private Circle cir1;
  @FXML private Circle cir6;
  @FXML private Circle cir5;
  @FXML private Circle cir4;
  @FXML private Circle cir3;
  @FXML private Circle cir2;
  private final ReentrantLock lock = new ReentrantLock();
  private final MyRunnable myRunnable = new MyRunnable();
  private ParallelTransition parallelTransition = new ParallelTransition();
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
    // hide the circles
    cir1.setVisible(false);
    cir2.setVisible(false);
    cir3.setVisible(false);
    cir4.setVisible(false);
    cir5.setVisible(false);
    cir6.setVisible(false);
    animationPane.setVisible(false);
    moveDatePicker.setValue(LocalDate.now());
    moveDatePicker
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              mapController.setDate(
                  MapEditorController.add(
                      Date.from(newValue.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                      Calendar.MILLISECOND,
                      1));
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
                  mapController.zoomToCoordinates(
                      2, row.getItem().node.getXCoord(), row.getItem().node.getYCoord());
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

  @SneakyThrows
  public void handleGetPath() {
    generatePathButton.setDisable(true);
    // start the animation
    Animation();
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
    // acquire the lock
    lock.lock();

    // create a new thread with myRunnable
    Thread thread = new Thread(myRunnable);
    thread.start();
  }

  public void unlock() {
    // release the lock
    lock.unlock();
    // Check that we actually got a path
    if (currentPath == null) {
      // if nodes is null, that means the there was no possible path
      //      error.setTextFill(Paint.valueOf(Color.RED.toString()));
      //      error.setText("No path found");
      System.out.println("no path found");
    } else {
      setFloor(currentPath.get(0).getFloor()); // Go to the starting floor
      // Zoom to the coordinates of the starting node
      mapController.zoomToCoordinates(
          5, currentPath.get(0).getXCoord(), currentPath.get(0).getYCoord());
      colorFloor(); // Draw the path
      setFloor(currentPath.get(0).getFloor());
      drawTable();
    }
    // stop the animation
    parallelTransition.jumpTo(Duration.ZERO);
    parallelTransition.stop();

    //    animationPane.setTranslateX(2000);
    //    animationPane.setTranslateY(2000);

    // hide the circles
    cir1.setVisible(false);
    cir2.setVisible(false);
    cir3.setVisible(false);
    cir4.setVisible(false);
    cir5.setVisible(false);
    cir6.setVisible(false);
    animationPane.setVisible(false);
    generatePathButton.setDisable(false);
  }

  public void Animation() {
    parallelTransition.stop();
    //    animationPane.setTranslateX(0);
    //    animationPane.setTranslateY(0);
    cir1.setVisible(true);
    cir2.setVisible(true);
    cir3.setVisible(true);
    cir4.setVisible(true);
    cir5.setVisible(true);
    cir6.setVisible(true);
    animationPane.setVisible(true);
    //    cir1.setTranslateY(0);
    //    cir2.setTranslateY(0);
    //    cir3.setTranslateY(0);
    //    cir4.setTranslateY(0);
    //    cir5.setTranslateY(0);
    //    cir6.setTranslateY(0);
    cir1.setTranslateX(300);
    cir2.setTranslateX(300);
    cir3.setTranslateX(300);
    cir4.setTranslateX(300);
    cir5.setTranslateX(300);
    cir6.setTranslateX(300);
    // Create a TranslateTransition for each circle and add a delay
    TranslateTransition tt1 = new TranslateTransition(Duration.seconds(0.2), cir1);
    tt1.setInterpolator(Interpolator.EASE_BOTH);
    tt1.setByY(-50);
    tt1.setAutoReverse(true);
    tt1.setCycleCount(2);
    tt1.setDelay(Duration.seconds(0.0));
    TranslateTransition tt2 = new TranslateTransition(Duration.seconds(0.2), cir2);
    tt2.setInterpolator(Interpolator.EASE_BOTH);
    tt2.setByY(-50);
    tt2.setAutoReverse(true);
    tt2.setCycleCount(2);
    tt2.setDelay(Duration.seconds(0.2));
    TranslateTransition tt3 = new TranslateTransition(Duration.seconds(0.2), cir3);
    tt3.setInterpolator(Interpolator.EASE_BOTH);
    tt3.setByY(-50);
    tt3.setAutoReverse(true);
    tt3.setCycleCount(2);
    tt3.setDelay(Duration.seconds(0.4));
    TranslateTransition tt4 = new TranslateTransition(Duration.seconds(0.2), cir4);
    tt4.setInterpolator(Interpolator.EASE_BOTH);
    tt4.setByY(-50);
    tt4.setAutoReverse(true);
    tt4.setCycleCount(2);
    tt4.setDelay(Duration.seconds(0.6));
    TranslateTransition tt5 = new TranslateTransition(Duration.seconds(0.2), cir5);
    tt5.setInterpolator(Interpolator.EASE_BOTH);
    tt5.setByY(-50);
    tt5.setAutoReverse(true);
    tt5.setCycleCount(2);
    tt5.setDelay(Duration.seconds(0.8));
    TranslateTransition tt6 = new TranslateTransition(Duration.seconds(0.2), cir6);
    tt6.setInterpolator(Interpolator.EASE_BOTH);
    tt6.setByY(-50);
    tt6.setAutoReverse(true);
    tt6.setCycleCount(2);
    tt6.setDelay(Duration.seconds(1.0));

    // Create a ParallelTransition to play the animations in parallel
    ParallelTransition parallelTransition = new ParallelTransition(tt1, tt2, tt3, tt4, tt5, tt6);
    parallelTransition.setAutoReverse(true);
    parallelTransition.setCycleCount(ParallelTransition.INDEFINITE);
    // Start the animation
    parallelTransition.playFromStart();
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
  /** Method that handles drawing a new path (AKA the submit button handler) */
  class MyRunnable implements Runnable {

    public void run() {
      Session session = CONNECTION.getSessionFactory().openSession();

      // Get the new path from the PathFinder
      Node startNode =
          startingBox
              .getValue()
              .getCurrentNode(
                  mapController.getMapSession(),
                  Date.from(
                      moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
      Node endNode =
          destinationBox
              .getValue()
              .getCurrentNode(
                  mapController.getMapSession(),
                  Date.from(
                      moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
      currentPath = pathFinder.findPath(startNode, endNode, accessibleBox.isSelected());

      session.close();
      // Call unlock() on the UI thread when finished
      Platform.runLater(() -> unlock());
    }
  }
}
