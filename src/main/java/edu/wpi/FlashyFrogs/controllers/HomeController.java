package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;
import lombok.Getter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

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

  @FXML protected TableColumn<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> nodeIDCol;
  @FXML protected TableColumn<MoveWrapper, LocationName> locationNameCol;
  @FXML protected TableColumn<MoveWrapper, Date> dateCol;
  @FXML protected TableView<MoveWrapper> moveTable;
  @FXML protected MFXButton manageLoginsButton;

  @FXML protected MFXButton manageAnnouncementsButton;
  @FXML protected Label tableText;
  @FXML protected Label tableText2;

  @FXML protected SearchableComboBox<String> filterBox;

  protected boolean canEditMoves = false;

  ObjectProperty<String> filterProperty = new SimpleObjectProperty<>("All");

  public static class MoveWrapper {
    @Getter public edu.wpi.FlashyFrogs.ORM.Node node;

    @Getter private LocationName locationName;

    @Getter private Date moveDate;

    public MoveWrapper(Move move) {
      node = move.getNode();
      locationName = move.getLocation();
      moveDate = move.getMoveDate();
    }

    public void setNode(edu.wpi.FlashyFrogs.ORM.Node node, Session session) {
      Transaction transaction = session.beginTransaction();
      session
          .createMutationQuery(
              "UPDATE Move SET node=:newNode where node=:oldNode and location=:oldLocation and moveDate=:oldDate")
          .setParameter("oldNode", this.node)
          .setParameter("oldLocation", this.locationName)
          .setParameter("oldDate", this.moveDate)
          .setParameter("newNode", node)
          .executeUpdate();

      transaction.commit();

      this.node = node;
    }

    public void setLocationName(LocationName locationName, Session session) {
      Transaction transaction = session.beginTransaction();
      session
          .createMutationQuery(
              "UPDATE Move SET location=:newLocation where node=:oldNode and location=:oldLocation and moveDate=:oldDate")
          .setParameter("oldNode", this.node)
          .setParameter("oldLocation", this.locationName)
          .setParameter("oldDate", this.moveDate)
          .setParameter("newLocation", locationName)
          .executeUpdate();
      transaction.commit();
      this.locationName = locationName;
    }

    public void setMoveDate(Date moveDate, Session session) {
      Transaction transaction = session.beginTransaction();
      session
          .createMutationQuery(
              "UPDATE Move SET moveDate=:newDate where node=:oldNode and location=:oldLocation and moveDate=:oldDate")
          .setParameter("oldNode", this.node)
          .setParameter("oldLocation", this.locationName)
          .setParameter("oldDate", this.moveDate)
          .setParameter("newDate", moveDate)
          .executeUpdate();
      transaction.commit();
      this.moveDate = moveDate;
    }
  }

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
    locationNameCol.setCellValueFactory(new PropertyValueFactory<>("locationName"));
    dateCol.setCellValueFactory(new PropertyValueFactory<>("moveDate"));
    Session session = CONNECTION.getSessionFactory().openSession();
    List<edu.wpi.FlashyFrogs.ORM.Node> nodes =
        session.createQuery("FROM Node", edu.wpi.FlashyFrogs.ORM.Node.class).getResultList();
    List<LocationName> locationNames =
        session.createQuery("FROM LocationName ", LocationName.class).getResultList();
    session.close();

    nodeIDCol.setCellFactory(
        new Callback<
            TableColumn<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node>,
            TableCell<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node>>() {
          @Override
          public TableCell<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> call(
              TableColumn<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> param) {
            return new ComboBoxTableCell<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node>(
                (ObservableList<edu.wpi.FlashyFrogs.ORM.Node>) FXCollections.observableList(nodes));
          }
        });

    nodeIDCol.setEditable(true);
    nodeIDCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node>>() {
          @Override
          public void handle(
              TableColumn.CellEditEvent<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> event) {
            Session session = CONNECTION.getSessionFactory().openSession();
            event.getRowValue().setNode(event.getNewValue(), session);
            session.close();
          }
        });

    locationNameCol.setCellFactory(
        new Callback<
            TableColumn<MoveWrapper, LocationName>, TableCell<MoveWrapper, LocationName>>() {
          @Override
          public TableCell<MoveWrapper, LocationName> call(
              TableColumn<MoveWrapper, LocationName> param) {
            return new ComboBoxTableCell<MoveWrapper, LocationName>(
                (ObservableList<LocationName>) FXCollections.observableList(locationNames));
          }
        });

    locationNameCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<MoveWrapper, LocationName>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<MoveWrapper, LocationName> event) {
            Session session = CONNECTION.getSessionFactory().openSession();
            event.getRowValue().setLocationName(event.getNewValue(), session);
            session.close();
          }
        });

    dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
    dateCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<MoveWrapper, Date>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<MoveWrapper, Date> event) {
            Session session = CONNECTION.getSessionFactory().openSession();
            event.getRowValue().setMoveDate(event.getNewValue(), session);
            session.close();
          }
        });
    moveTable.setEditable(true);
    moveTable.getSelectionModel().setCellSelectionEnabled(true);

    User currentUser = new User("a", "a", "a", User.EmployeeType.ADMIN, new Department());
    boolean isAdmin = true;

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
    //        .add("edu/wpi/FlashyFrogs/views/Css.css"); // add the light mode CSS
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

      String query = "SELECT m from Move m WHERE m.moveDate > current timestamp";
      if (canEditMoves) {
        query = "SELECT m from Move m";
      }
      moves = session.createQuery(query, Move.class).getResultList();
      List<MoveWrapper> moveWrappers = new ArrayList<>();
      for (Move move : FXCollections.observableList(moves)) {
        moveWrappers.add(new MoveWrapper(move));
      }
      moveTable.setItems(FXCollections.observableList(moveWrappers));
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

  @FXML
  public void handleEditMovesButton() {
    canEditMoves = true;
    refreshTable();
  }
}
