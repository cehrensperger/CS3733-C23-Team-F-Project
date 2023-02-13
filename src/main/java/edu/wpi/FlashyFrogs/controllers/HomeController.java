package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ORM.User;
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

public class HomeController {
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

  @FXML protected MFXButton manageButton;
  @FXML protected Label tableText;
  @FXML protected Label tableText2;

  @FXML protected SearchableComboBox<String> filterBox;

  ObjectProperty<String> filterProperty = new SimpleObjectProperty<>("");

  public void initialize() {
    List<String> filters = new ArrayList<String>();
    filters.add("AudioVisual");
    filters.add("ComputerService");
    filters.add("InternalTransport");
    filters.add("Sanitation");
    filters.add("Security");
    filterBox.setItems(FXCollections.observableList(filters));

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

    Session session = CONNECTION.getSessionFactory().openSession();

    // todo: remove when login is implemented
    CurrentUserEntity.CURRENT_USER.setCurrentUser(session.find(User.class, 2));

    User currentUser = CurrentUserEntity.CURRENT_USER.getCurrentuser();
    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    if (!isAdmin) {
      tableText.setText("Assigned Service Requests");
      manageButton.disarm();
      manageButton.setOpacity(0);

      tableText2.setText("");
    } else {
      tableText.setText("All Service Requests");
      manageButton.arm();
      manageButton.setOpacity(1);

      tableText2.setText("Future Moves");
    }

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
                        .setParameter("emp", currentUser)
                        .getResultList()));
          }
        });
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

  @FXML
  public void handleClose(ActionEvent event) throws IOException {
    //    stage = (Stage) rootPane.getScene().getWindow();
    //    stage.close();
  }

  public void handleServiceRequestsButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "RequestsHome");
  }

  public void handleMapDataEditorButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "MapEditorView");
  }

  public void handlePathfindingButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "PathFinding");
  }

  public void handleSecurityMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SecurityService");
  }

  public void handleTransportMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Transport");
  }

  public void handleSanitationMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "SanitationService");
  }

  public void handleAudioVisualMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "AudioVisualService");
  }

  public void handleComputerMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "ComputerService");
  }

  public void handleLoadMapMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "LoadMapPage");
  }

  public void handleFeedbackMenuItem(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Feedback");
  }

  /**
   * Change the color theme to Light Mode when the Color Scheme > Light Mode option is selected on
   * EmployeeHome.fxml.
   *
   * @param actionEvent
   * @throws IOException
   */
  public void changeToLightMode(ActionEvent actionEvent) throws IOException {
    //    setToLightMode();
  }

  /**
   * Call to set EmployeeHome.fxml to light mode. Also makes some tweaks to JavaFX elements specific
   * to EmployeeHome.fxml, so not all of this method is generalizable to setting any page to light
   * mode.
   */
  public void setToLightMode() {
    //    rootPane
    //        .getStylesheets()
    //        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off
    // all
    //    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    //    rootPane
    //        .getStylesheets()
    //        .add("edu/wpi/FlashyFrogs/views/light-mode.css"); // add the light mode CSS
    //    AboutText.setBlendMode(
    //        BlendMode.DARKEN); // change the Blend Mode on the text box describing the hospital,
    // as the
    //    // Blend Mode used for Light Mode does not give the desired appearance
    //    rootPane
    //        .getStylesheets()
    //        .add("edu/wpi/FlashyFrogs/views/label-override.css"); // usually the text color in
    // label
    //    // elements is black in Light Mode, but the upper left menu on the Home page would be hard
    // to
    //    // read with black text,
    //    // so for this page we change the label text color to white.
    //    Fapp.setLightMode(true); // set the isLightMode variable to true, as we switched to Light
    // Mode
  }

  /**
   * Change the color theme to Dark Mode when the Color Scheme > Dark Mode option is selected on
   * EmployeeHome.fxml.
   *
   * @param actionEvent
   * @throws IOException
   */
  public void changeToDarkMode(ActionEvent actionEvent) {
    // setToDarkMode();

  }

  /**
   * Call to set EmployeeHome.fxml to dark mode. Also makes some tweaks to JavaFX elements specific
   * to EmployeeHome.fxml, so not all of this method is generalizable to setting any page to dark
   * mode.
   */
  public void setToDarkMode() {
    //    rootPane
    //        .getStylesheets()
    //        .clear(); // getStylesheets.add() is used frequently, so this line exists to clear off
    // all
    //    // stylesheets so we don't accumulate an infinite list of the same three stylesheets
    //    rootPane
    //        .getStylesheets()
    //        .add("edu/wpi/FlashyFrogs/views/dark-mode.css"); // add the dark mode CSS
    //    AboutText.setBlendMode(
    //        BlendMode.SOFT_LIGHT); // change the Blend Mode on the text box describing the
    // hospital, as
    //    // using Light Mode's Blend Mode (DARKEN) on this will make all the text in the box
    // invisible;
    //    // SOFT_LIGHT keeps it
    //    // visible and somewhat preserves the transparency idea shown in Light Mode
    //    AboutText.setStyle(
    //        "-fx-text-fill: #2f2f2f;"); // usually the text color in text-area elements is white
    // in Dark
    //    // Mode,
    //    // but the text-area element on this page, the one describing the hospital, would be hard
    // to
    //    // read with white, so for
    //    // this page we change the color to black/gray.
    //    Fapp.setLightMode(false); // set the isLightMode variable to false, as we switched to Dark
    // Mode
  }

  public void handleLogOut(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Login");
  }

  public void manageAnnouncements(ActionEvent event) throws IOException {}
}
