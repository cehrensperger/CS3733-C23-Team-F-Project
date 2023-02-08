package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
public class MapEditorController {
  @FXML private MFXButton backButton;
  @FXML private MFXButton cancelButton;
  @FXML private MFXButton saveButton;
  @FXML private MFXComboBox<Node.Floor> floorSelector;
  @FXML private BorderPane editorBox; // Editor box, map is appended to this on startup
  private MapController mapController; // Controller for the map
  @FXML private TableView<LocationName> locationTable; // Attribute for the location table

  @FXML
  private TableColumn<LocationName, String> longName; // Attribute for the name column of the table

  /** Initializes the map editor, adds the map onto it */
  @SneakyThrows
  @FXML
  private void initialize() {
    // Exit listener
    backButton.setOnAction(
        event -> {
          mapController.exit(); // Should go back

          Fapp.setScene("Home");
        });

    // Cancel listener
    cancelButton.setOnAction(
        event -> {
          mapController.cancelChanges(); // Cancel changes
          createLocationNameTable(mapController.getMapSession()); // Reload the table
        });

    // Save listener
    saveButton.setOnAction(event -> mapController.saveChanges());

    longName.setCellValueFactory(new PropertyValueFactory<>("longName"));

    AtomicReference<PopOver> tablePopOver =
        new AtomicReference<>(); // The pop-over the map is using for node highlighting

    locationTable.setRowFactory(
        param -> {
          TableRow<LocationName> row = new TableRow<>(); // Create a new table row to use

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
                if (tablePopOver.get() != null) {
                  tablePopOver.get().hide(); // Hide the pop-over
                  tablePopOver.set(null); // Delete the pop-over
                }

                // Load the location name info view
                FXMLLoader locationNameLoader =
                    new FXMLLoader(Fapp.class.getResource("views/LocationNameInfo.fxml"));

                // Load the resource
                try {
                  tablePopOver.set(new PopOver(locationNameLoader.load())); // Create the pop-over
                } catch (IOException e) {
                  throw new RuntimeException(e); // If anything goes wrong, just re-throw
                }

                LocationNameInfoController controller =
                    locationNameLoader.getController(); // Get the controller

                // We need to cache the row num, as when we do the setter, this will change
                int rowNum = row.getIndex();

                // Set the location name to the value
                controller.setLocationName(
                    row.getItem(), // Set it to the rows item
                    mapController.getMapSession(),
                    () -> {
                      locationTable.getItems().remove(row.getItem());
                      tablePopOver.get().hide();
                    },
                    // Set the original saved row number to be the new location nam,e
                    (locationName) -> locationTable.getItems().set(rowNum, locationName),
                    false);

                tablePopOver.get().show(row); // Show the pop-over on the row
              });

          return row; // Return the generated row
        });

    // Load the map loader
    FXMLLoader mapLoader = new FXMLLoader(Fapp.class.getResource("views/Map.fxml"));

    Pane map = mapLoader.load(); // Load the map
    editorBox.setCenter(map); // Put the map loader into the editor box

    mapController = mapLoader.getController(); // Load the map controller

    createLocationNameTable(
        mapController.getMapSession()); // Create the table using the map session

    AtomicReference<PopOver> mapPopOver =
        new AtomicReference<>(); // The pop-over the map is using for node highlighting

    // Set the node creation processor
    mapController.setNodeCreation(
        (node, circle) -> {
          // Set the on-click processor
          circle.setOnMouseClicked(
              (event) -> {
                // If we're no longer hovering and the pop over exists, delete it. We will
                // either create a new one
                // or, keep it deleted
                if (mapPopOver.get() != null) {
                  mapPopOver.get().hide(); // Hide it
                  mapPopOver.set(null); // And delete it (set it to null)
                }

                // Get the node info in FXML form
                FXMLLoader nodeInfoLoader =
                    new FXMLLoader(Fapp.class.getResource("views/NodeInfo.fxml"));

                try {
                  // Try creating the pop-over
                  mapPopOver.set(new PopOver(nodeInfoLoader.load()));
                } catch (IOException e) {
                  throw new RuntimeException(e); // If it fails, throw an exception
                }

                NodeInfoController controller =
                    nodeInfoLoader.getController(); // Get the controller to use
                controller.setNode(
                    node,
                    mapController.getMapSession(),
                    () -> {
                      mapPopOver.getAndSet(null).hide(); // hide the pop-over
                      mapController.redraw(); // Redraw the map
                    }); // Set the node

                mapPopOver.get().show(circle); // Show the pop-over
              });
        });

    floorSelector
        .getItems()
        .addAll(Node.Floor.values()); // Add all the floors to the floor selector

    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorSelector
        .valueProperty()
        .addListener((observable, oldValue, newValue) -> mapController.setFloor(newValue));
  }

  /**
   * Creates the location name table, first clearing existing entries, then updating them
   *
   * @param session the session to use in querying
   */
  private void createLocationNameTable(@NonNull Session session) {
    locationTable.getItems().clear(); // Clear the table

    List<LocationName> longNames =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    ObservableList<LocationName> longNamesObservableList =
        FXCollections.observableList(longNames); // Create the list

    // Add the list to the table
    locationTable.getItems().addAll(longNamesObservableList);
  }

  /**
   * Handler for the help menu for the map
   *
   * @param event unsued event
   */
  @SneakyThrows
  @FXML
  public void handleQ(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the new popOver

    HelpController help = newLoad.getController(); // get the controller
    help.handleQMapEditor(); // display the correct help text

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popOver
  }

  /**
   * Creates a pop-up for a new move to be created
   *
   * @param event the unused creation event
   */
  @FXML
  @SneakyThrows
  private void popupMove(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/AddMove.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the new popOver

    AddMoveController addMove = newLoad.getController(); // get the controllers
    addMove.setPopOver(popOver); // pass the popOver
    addMove.setSession(mapController.getMapSession()); // pass the session

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popover
  }

  /**
   * Creates a pop-up to create a new location
   *
   * @param event the unused event calling this
   */
  @SneakyThrows
  @FXML
  private void popupLocation(ActionEvent event) {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/LocationNameInfo.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the new popover

    LocationNameInfoController addLoc = newLoad.getController(); // get the controller
    addLoc.setDeleteButtonText("Cancel"); // change the original text of the delete button
    addLoc.setLocationName( // create a new location to pass in param
        new LocationName("", LocationName.LocationType.HALL, ""),
        mapController.getMapSession(),
        popOver::hide,
        (locationName) -> {
          popOver.hide();
          locationTable.getItems().add(0, locationName);
        },
        true);

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popover
  }

  /**
   * opens the popup for adding a new node
   *
   * @param event the event triggering this (unused)
   * @throws IOException
   */
  @FXML
  private void popupNode(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/AddNode.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    AddNodeController addNode = newLoad.getController(); // get the controller
    addNode.setPopOver(popOver); // pass the popover
    addNode.setMapController(mapController); // pass the session

    popOver.detach();
    javafx.scene.Node node = (javafx.scene.Node) event.getSource();
    popOver.show(node.getScene().getWindow()); // display the popover
  }
}
