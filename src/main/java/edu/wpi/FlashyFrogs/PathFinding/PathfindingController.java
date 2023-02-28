package edu.wpi.FlashyFrogs.PathFinding;

import static edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity.CURRENT_USER;
import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.MapEditor.MapEditorController;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.PathVisualizer.AbstractPathVisualizerController;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

@GeneratedExclusion
public class PathfindingController extends AbstractPathVisualizerController implements IController {
  @FXML Pane errtoast;
  @FXML Rectangle errcheck2;
  @FXML Rectangle errcheck1;
  private int selectedIndex = -1;
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
  @FXML private SearchableComboBox<String> serviceRequestBox;
  @FXML private CheckBox accessibleBox;
  @FXML private AnchorPane mapPane;
  @FXML private MFXButton mapEditorButton;
  @FXML private DatePicker moveDatePicker;
  //  @FXML private Label error;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;

  boolean hDone = false;
  List<ServiceRequest> serviceRequests;

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
    h7.setVisible(false);

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

    // make the list of User's service requests
    long userID = CURRENT_USER.getCurrentUser().getId();
    serviceRequests =
        session
            .createQuery(
                "SELECT s FROM ServiceRequest s WHERE assignedEmp.id = :userID AND s.location IS NOT NULL",
                ServiceRequest.class)
            .setParameter("userID", userID)
            .getResultList();

    List<String> serviceRequestsStrings = new ArrayList<>();
    for (ServiceRequest request : serviceRequests) {
      serviceRequestsStrings.add(request.toString());
    }

    // Populate the boxes
    startingBox.setItems(FXCollections.observableList(objects));
    destinationBox.setItems(FXCollections.observableList(objects));
    algorithmBox.setItems(FXCollections.observableList(algorithms));
    if (serviceRequests.isEmpty()) {
      serviceRequestBox.setVisible(false);
    } else {
      serviceRequestBox.setItems(FXCollections.observableList(serviceRequestsStrings));
    }
    algorithmBox.setValue("A*");

    // Get whether the user is an admin
    boolean isAdmin = CURRENT_USER.getAdmin();

    // Decide what to do with the admin button based on that
    if (!isAdmin) {
      mapEditorButton.setDisable(true);
      mapEditorButton.setOpacity(0);
    } else {
      mapEditorButton.setDisable(false);
      mapEditorButton.setOpacity(1);
    }

    super.initialize(); // Call the supers intitialize

    serviceRequestBox
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              long selectedRequestId = -1;
              String srText = serviceRequestBox.getValue();
              for (ServiceRequest serviceRequest : serviceRequests) {
                if (Long.parseLong(srText.substring(srText.indexOf("_") + 1))
                    == serviceRequest.getId()) {
                  selectedRequestId = serviceRequest.getId();
                }
              }
              destinationBox
                  .getSelectionModel()
                  .select(session.find(ServiceRequest.class, selectedRequestId).getLocation());
            });

    // Only enables generatePathButton if something is selected for both startingBox and
    // destinationBox
    ChangeListener<Object> listener =
        (observable, oldValue, newValue) -> {
          // Check if both ComboBoxes have a selected value
          boolean isComboBox1Selected = startingBox.getValue() != null;
          boolean isComboBox2Selected = destinationBox.getValue() != null;

          // If both ComboBoxes have a selected value, enable the button, otherwise disable it
          generatePathButton.setDisable(!isComboBox1Selected || !isComboBox2Selected);
        };

    // Add the ChangeListener to both ComboBoxes
    startingBox.valueProperty().addListener(listener);
    destinationBox.valueProperty().addListener(listener);

    // Initially disable the button if either ComboBox is not selected
    generatePathButton.setDisable(
        startingBox.getValue() == null || destinationBox.getValue() == null);
  }

  /** Callback to handle the back button being pressed */
  @SneakyThrows
  @FXML
  public void handleBack() {
    Fapp.handleBack(); // Delegate to Fapp
  }

  @SneakyThrows
  public void handleGetPath() {
    try {
      if (destinationBox.getValue().equals("") && (startingBox.getValue().equals(""))) {
        generatePathButton.setDisable(true);
        throw new NullPointerException();
      }
      try {
        generatePathButton.setDisable(true);
        // start the animation
        Animation();

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
      } catch (RollbackException exception) {
        errortoastAnimation();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      errortoastAnimation();
    }
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
      mapController
          .getMapFloorProperty()
          .setValue(currentPath.get(0).getFloor()); // Go to the starting floor
      // Zoom to the coordinates of the starting node
      mapController.zoomToCoordinates(
          5, currentPath.get(0).getXCoord(), currentPath.get(0).getYCoord());
      colorFloor(); // Draw the path
      mapController.getMapFloorProperty().setValue(currentPath.get(0).getFloor());
      drawTable(
          Date.from(moveDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
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
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            generatePathButton.setDisable(false);
          }
        });
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
      h7.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      h7.setVisible(false);
      hDone = false;
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
      Platform.runLater(PathfindingController.this::unlock);
    }
  }
}
