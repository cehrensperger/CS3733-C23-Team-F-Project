package edu.wpi.FlashyFrogs.Alerts;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.Alert;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.controllers.IController;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;

public class AlertManagerController implements IController {
  @FXML private Label errorMessage;
  @FXML private TableView<Alert> alertTable;
  @FXML private TableColumn<Alert, Number> idCol;
  @FXML private TableColumn<Alert, String> descriptionCol;
  @FXML private TableColumn<Alert, String> authorCol;
  @FXML private TableColumn<Alert, Date> dateCol;
  @FXML private TableColumn<Alert, Alert.Severity> severityCol;
  @FXML private Button addNewAlert;
  @FXML private Button back;

  @FXML Text h1;
  boolean hDone = false;

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.handleBack();
  }

  public void handleNewAlert(ActionEvent actionEvent) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("Alerts/NewAlert.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    NewAlertController newAlert = newLoad.getController();
    newAlert.setPopOver(popOver);
    newAlert.setAlertManagerController(this);

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    Node node = (Node) actionEvent.getSource(); // Get the node representation of what called this
    popOver.show(node.getScene().getWindow()); // display the popover

    addNewAlert.setDisable(true);
    back.setDisable(true);
    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                addNewAlert.setDisable(false);
                back.setDisable(false);
              }
            });
  }

  public void initialize() throws Exception {
    errorMessage.setVisible(false);
    h1.setVisible(false);

    // Clear old table before init
    alertTable.getItems().clear();

    // Set columns on table
    idCol.setCellValueFactory(
        data -> {
          Alert alert = data.getValue();
          return new SimpleLongProperty(alert.getId());
        });
    descriptionCol.setCellValueFactory(
        data -> {
          String description = data.getValue().getDescription();
          return new SimpleStringProperty(description);
        });
    authorCol.setCellValueFactory(
        data -> {
          HospitalUser user = data.getValue().getAuthor();
          return new SimpleStringProperty(
              user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName());
        });
    dateCol.setCellValueFactory(
        data -> {
          Date date = data.getValue().getStartDisplayDate();
          return new SimpleObjectProperty<>(date);
        });
    severityCol.setCellValueFactory(
        data -> {
          return new SimpleObjectProperty<>(data.getValue().getSeverity());
        });

    ObservableList<Alert> alertObservableList = null;
    Session session = CONNECTION.getSessionFactory().openSession();

    try {
      List<Alert> alerts =
          session.createQuery("SELECT s FROM Alert s", Alert.class).getResultList();
      alertObservableList = FXCollections.observableList(alerts);
      session.close();
      alertTable.getItems().addAll(alertObservableList);
    } catch (Exception e) {
      session.close();
      throw e;
    }
    alertTable.setOnMouseClicked(
        event -> {
          if (alertTable.getSelectionModel().getSelectedItem() != null) {
            Alert selectedAlert = alertTable.getSelectionModel().getSelectedItem();
            FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("Alerts/EditAlert.fxml"));
            PopOver popOver = null;
            try {
              popOver = new PopOver(newLoad.load());
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
            EditAlertController editAlert = newLoad.getController();
            editAlert.setPopOver(popOver);
            editAlert.setAlertManagerController(this);
            editAlert.initialize(selectedAlert);
            popOver.detach();
            Node node = (Node) event.getSource();
            popOver.show(node.getScene().getWindow());
            addNewAlert.setDisable(true);
            back.setDisable(true);
            popOver
                .showingProperty()
                .addListener(
                    (observable, oldValue, newValue) -> {
                      if (!newValue) {
                        addNewAlert.setDisable(false);
                        back.setDisable(false);
                      }
                    });
          }
          alertTable.getSelectionModel().clearSelection();
        });
  }

  public void onClose() {}

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      hDone = false;
    }
  }
}
