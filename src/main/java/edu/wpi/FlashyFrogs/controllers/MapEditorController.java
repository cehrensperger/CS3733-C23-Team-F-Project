package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
public class MapEditorController {
  public MFXComboBox<Node.Floor> floorSelector;
  @FXML private HBox editorBox; // Editor box, map is appended to this on startup
  private MapController mapController; // Controller for the map
  @FXML private TableView<LocationName> locationTable; // Attribute for the location table

  @FXML
  private TableColumn<LocationName, String> longName; // Attribute for the name column of the table

  /** Initializes the map editor, adds the map onto it */
  @SneakyThrows
  @FXML
  private void initialize() {
    longName.setCellValueFactory(new PropertyValueFactory<>("longName"));

    AtomicReference<PopOver> tablePopOver =
        new AtomicReference<>(); // The pop-over the map is using for node highlighting

    locationTable.setRowFactory(
        new Callback<>() {
          @Override
          public TableRow<LocationName> call(TableView<LocationName> param) {
            TableRow<LocationName> row = new TableRow<>(); // Create a new table row to use

            // Add a listener to show the pop-up
            row.hoverProperty()
                .addListener(
                    (observable, oldValue, newValue) -> {
                      // If the pop over exists and is either not focused or we are showing a new
                      // row
                      if (tablePopOver.get() != null
                          && (!tablePopOver.get().isFocused() || newValue)) {
                        tablePopOver.get().hide(); // Hide the pop-over
                        tablePopOver.set(null); // Delete the pop-over
                      }

                      // If we have a new value to show
                      if (newValue) {
                        // Load the location name info view
                        FXMLLoader locationNameLoader =
                            new FXMLLoader(
                                getClass().getResource("../views/LocationNameInfo.fxml"));

                        // Load the resource
                        try {
                          tablePopOver.set(
                              new PopOver(locationNameLoader.load())); // Create the pop-over
                        } catch (IOException e) {
                          throw new RuntimeException(e); // If anything goes wrong, just re-throw
                        }

                        LocationNameInfoController controller =
                            locationNameLoader.getController(); // Get the controller
                        controller.setLocationName(
                            row.getItem()); // Set the location name to the value

                        tablePopOver.get().show(row); // Show the pop-over on the row
                      }
                    });

            return row; // Return the generated row
          }
        });

    createLocationNameTable();

    // Load the map loader
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(getClass().getResource("../views/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    editorBox.getChildren().add(map); // Put the map loader into the editor box
    mapController = mapLoader.getController(); // Load the map controller
    AtomicReference<PopOver> mapPopOver =
        new AtomicReference<>(); // The pop-over the map is using for node highlighting

    // Set the node creation processor
    mapController.setNodeCreation(
        (node, circle) -> {
          // Set the on-click processor
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
                      FXMLLoader nodeInfoLoader =
                          new FXMLLoader(getClass().getResource("../views/NodeInfo.fxml"));

                      try {
                        // Try creating the pop-over
                        mapPopOver.set(new PopOver(nodeInfoLoader.load()));
                      } catch (IOException e) {
                        throw new RuntimeException(e); // If it fails, throw an exception
                      }

                      NodeInfoController controller =
                          nodeInfoLoader.getController(); // Get the controller to use
                      controller.setNode(node); // Set the node

                      mapPopOver.get().show(circle); // Show the pop-over
                    }
                  });
        });

    // Set the map to take all available space
    HBox.setHgrow(map, Priority.ALWAYS);

    floorSelector
        .getItems()
        .addAll(Node.Floor.values()); // Add all the floors to the floor selector

    // Add a listener so that when the floor is changed, the map  controller sets the new floor
    floorSelector
        .valueProperty()
        .addListener((observable, oldValue, newValue) -> mapController.setFloor(newValue));
  }

  private void createLocationNameTable() {
    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> longNames =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    // Remove all locations that are have a node associated with them
    ObservableList<LocationName> longNamesObservableList = FXCollections.observableList(longNames);

    session.close();
    locationTable.getItems().addAll(longNamesObservableList);
  }
}
