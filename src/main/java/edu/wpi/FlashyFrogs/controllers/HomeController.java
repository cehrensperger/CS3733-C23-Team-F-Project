package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ORM.User;
import edu.wpi.FlashyFrogs.Theme;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

@GeneratedExclusion
public class HomeController implements IController {
  @FXML protected TableColumn<ServiceRequest, String> requestTypeCol;
  @FXML protected TableColumn<ServiceRequest, String> requestIDCol;
  @FXML protected TableColumn<ServiceRequest, String> initEmpCol;
  @FXML protected TableColumn<ServiceRequest, String> assignedEmpCol;
  @FXML protected TableColumn<ServiceRequest, String> subDateCol;
  @FXML protected TableColumn<ServiceRequest, String> urgencyCol;
  @FXML protected TableColumn<ServiceRequest, String> summaryCol;
  @FXML protected TableView<ServiceRequest> requestTable;

  @FXML protected TableColumn<Move, String> nodeIDCol;
  @FXML protected TableColumn<Move, String> locationNameCol;
  @FXML protected TableColumn<Move, Date> dateCol;
  @FXML protected TableView<Move> moveTable;
  @FXML protected MFXButton manageLoginsButton;

  @FXML protected MFXButton manageAnnouncementsButton;
  @FXML protected Label tableText;
  @FXML protected Label tableText2;

  @FXML protected SearchableComboBox<String> filterBox;

  ObjectProperty<String> filterProperty = new SimpleObjectProperty<>("All");

  public void initialize() {
    Fapp.resetStack();

    List<String> filters = new ArrayList<String>();
    filters.add("All");
    filters.add("AudioVisual");
    filters.add("ComputerService");
    filters.add("InternalTransport");
    filters.add("Sanitation");
    filters.add("Security");
    filterBox.setItems(FXCollections.observableList(filters));
    filterBox.setValue("All");

    // need to be the names of the fields
    requestTypeCol.setCellValueFactory(new PropertyValueFactory<>("requestType"));
    requestIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    initEmpCol.setCellValueFactory(new PropertyValueFactory<>("emp"));
    assignedEmpCol.setCellValueFactory(new PropertyValueFactory<>("assignedEmp"));
    subDateCol.setCellValueFactory(new PropertyValueFactory<>("dateOfSubmission"));
    urgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
    summaryCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    nodeIDCol.setCellValueFactory(new PropertyValueFactory<>("node"));
    locationNameCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    dateCol.setCellValueFactory(new PropertyValueFactory<>("moveDate"));

    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    if (!isAdmin) {
      tableText.setText("Assigned Service Requests");
      manageAnnouncementsButton.disarm();
      manageAnnouncementsButton.setOpacity(0);
      manageLoginsButton.disarm();
      manageLoginsButton.setOpacity(0);

      tableText2.setText("");
    } else {
      tableText.setText("All Service Requests");
      manageAnnouncementsButton.arm();
      manageAnnouncementsButton.setOpacity(1);
      manageLoginsButton.arm();
      manageLoginsButton.setOpacity(1);

      tableText2.setText("Future Moves");

      refreshTable();
    }
  }

  @FXML
  public void openPathfinding(ActionEvent event) throws IOException {
    System.out.println("opening pathfinding");
    Fapp.setScene("Pathfinding", "Pathfinding");
  }

  @FXML
  public void handleExitButton(ActionEvent event) throws IOException {
    //    stage = (Stage) rootPane.getScene().getWindow();
    //    stage.close();
  }

  @FXML
  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQHome();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  /**
   * Change the color theme between Dark and Light Mode when the Switch Color Scheme button is
   * clicked on Home.fxml.
   *
   * @param actionEvent
   * @throws IOException
   */
  public void changeMode(ActionEvent actionEvent) throws IOException {
    if (Fapp.getTheme().equals(Theme.LIGHT_THEME)) {
      Fapp.setTheme(Theme.DARK_THEME);
      System.out.println("switch to dark");
    } else {
      Fapp.setTheme(Theme.LIGHT_THEME);
      System.out.println("switch to light");
    }
  }

  public void handleLogOut(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Login");
  }

  public void manageAnnouncements(ActionEvent event) throws IOException {}

  public void onClose() {}

  @Override
  public void help() {
    // TODO: help for this page
  }

  public void viewLogins(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Accounts", "LoginAdministrator");
  }

  public void refreshTable() {
    User currentUser = CurrentUserEntity.CURRENT_USER.getCurrentuser();
    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    Session session = CONNECTION.getSessionFactory().openSession();

    // FILL TABLES
    List<ServiceRequest> serviceRequests;
    List<Move> moves;
    if (!isAdmin) {
      serviceRequests =
          session
              .createQuery(
                  "SELECT s FROM ServiceRequest s WHERE s.assignedEmp = :emp", ServiceRequest.class)
              .setParameter("emp", currentUser)
              .getResultList();
      moveTable.setOpacity(0);
    } else {
      serviceRequests =
          session
              .createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class)
              .getResultList();

      moves =
          session
              .createQuery("SELECT m from Move m WHERE m.moveDate > current timestamp", Move.class)
              .getResultList();
      moveTable.setItems(FXCollections.observableList(moves));
    }

    // refill based on filter
    filterProperty.addListener(
        (observable, oldValue, newValue) -> {
          if (newValue.equals("All")) {
            if (!isAdmin) {
              requestTable.setItems(
                  FXCollections.observableList(
                      session
                          .createQuery(
                              "SELECT s FROM ServiceRequest s WHERE s.requestType = :type AND s.assignedEmp = :emp",
                              ServiceRequest.class)
                          .setParameter("type", newValue)
                          .setParameter("emp", currentUser)
                          .getResultList()));
            } else {
              requestTable.setItems(
                  FXCollections.observableList(
                      session
                          .createQuery(
                              "SELECT s FROM ServiceRequest s WHERE s.requestType = :type",
                              ServiceRequest.class)
                          .setParameter("type", newValue)
                          .getResultList()));
            }
          } else {
            if (!isAdmin) {
              requestTable.setItems(
                  FXCollections.observableList(
                      session
                          .createQuery(
                              "SELECT s FROM ServiceRequest s WHERE s.assignedEmp = :emp",
                              ServiceRequest.class)
                          .setParameter("emp", currentUser)
                          .getResultList()));
            } else {
              requestTable.setItems(
                  FXCollections.observableList(
                      session
                          .createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class)
                          .getResultList()));
            }
          }
        });
    session.close();
  }

  public void handleManageCSV(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/CSVUpload.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    popOver.setTitle("CSV Manager");
    CSVUploadController controller = newLoad.getController();
    controller.setPopOver(popOver);

    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
    javafx.scene.Node node =
        (javafx.scene.Node) event.getSource(); // Get the node representation of what called this
    popOver.show(node); // display the popover

    popOver
        .showingProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue) {
                refreshTable();
              }
            });
  }
}
