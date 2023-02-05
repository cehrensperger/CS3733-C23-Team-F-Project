package edu.wpi.FlashyFrogs.controllers;

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
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
public class MapEditorController {
  public MFXComboBox<Node.Floor> floorSelector;
  @FXML private HBox editorBox; // Editor box, map is appended to this on startup
  private MapController mapController; // Controller for the map

  @FXML
  private TableView<LocationName> locationTable;
  @FXML
  private TableColumn<LocationName, String> longName;

  /** Initializes the map editor, adds the map onto it */
  @SneakyThrows
  @FXML
  private void initialize() {
    longName.setCellValueFactory(new PropertyValueFactory<>("longName"));

    createLocationNameTable();

    // Load the map loader
    FXMLLoader mapLoader =
        new FXMLLoader(Objects.requireNonNull(getClass().getResource("../views/Map.fxml")));

    Pane map = mapLoader.load(); // Load the map
    editorBox.getChildren().add(map); // Put the map loader into the editor box
    mapController = mapLoader.getController(); // Load the map controller
    AtomicReference<PopOver> popOver =
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
                    if (popOver.get() != null && !popOver.get().isFocused()) {
                      popOver.get().hide(); // Hide it
                      popOver.set(null); // And delete it (set it to null)
                    }

                    // If we should draw a new pop-up
                    if (newValue) {
                      // Get the node info in FXML form
                      FXMLLoader nodeInfoLoader =
                          new FXMLLoader(getClass().getResource("../views/NodeInfo.fxml"));

                      try {
                        // Try creating the pop-over
                        popOver.set(new PopOver(nodeInfoLoader.load()));
                      } catch (IOException e) {
                        throw new RuntimeException(e); // If it fails, throw an exception
                      }

                      NodeInfoController controller =
                          nodeInfoLoader.getController(); // Get the controller to use
                      controller.setNode(node); // Set the node

                      popOver.get().show(circle); // Show the pop-over
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
        session.createQuery("SELECT s FROM LocationName s", LocationName.class).getResultList();
    ObservableList<LocationName> longNamesObservableList = FXCollections.observableList(longNames);
    session.close();
    locationTable.getItems().addAll(longNamesObservableList);
  }
}
