package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;

/** Controller for the map editor, enables the user to add/remove/change Nodes */
public class MapEditorController implements Initializable {
  public TableView<LocationName> locationTable;
  public TableColumn<LocationName, String> longName;

  @FXML
  public void initialize(URL location, ResourceBundle resources) {
    longName.setCellValueFactory(new PropertyValueFactory<>("longName"));

    createLocationNameTable();
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
