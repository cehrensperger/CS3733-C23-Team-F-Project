package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.ServiceRequests.ServiceRequestController;
import edu.wpi.FlashyFrogs.Theme;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;
import lombok.Getter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class HomeController implements IController {
  @FXML protected FilteredTableColumn<ServiceRequest, String> requestTypeCol;
  @FXML protected FilteredTableColumn<ServiceRequest, String> requestIDCol;
  @FXML protected FilteredTableColumn<ServiceRequest, String> initEmpCol;
  @FXML protected FilteredTableColumn<ServiceRequest, String> assignedEmpCol;
  @FXML protected FilteredTableColumn<ServiceRequest, String> subDateCol;
  @FXML protected FilteredTableColumn<ServiceRequest, String> urgencyCol;
  @FXML protected FilteredTableColumn<ServiceRequest, LocationName> locationCol;
  @FXML protected FilteredTableColumn<ServiceRequest, ServiceRequest.Status> statusCol;

  @FXML protected FilteredTableView<ServiceRequest> requestTable;

  @FXML protected FilteredTableColumn<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> nodeIDCol;
  @FXML protected FilteredTableColumn<MoveWrapper, LocationName> locationNameCol;
  @FXML protected FilteredTableColumn<MoveWrapper, Date> dateCol;
  @FXML protected FilteredTableView<MoveWrapper> moveTable;
  @FXML protected MFXButton manageLoginsButton;
  @FXML protected MFXButton manageCSVButton;

  @FXML protected MFXButton manageAnnouncementsButton;
  @FXML protected Label tableText;
  @FXML protected Label tableText2;

  @FXML protected SearchableComboBox<String> filterBox;
  @FXML protected MFXButton editMovesButton;

  protected boolean canEditMoves = false;

  boolean filterCreated = false;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  boolean hDone = false;

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

    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);

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
    filterBox.valueProperty().setValue("All");

    moveTable = new FilteredTableView<MoveWrapper>();
    requestTable = new FilteredTableView<ServiceRequest>();

    // need to be the names of the fields
    requestTypeCol = new FilteredTableColumn<>("Request Type");
    requestTypeCol.setCellValueFactory(new PropertyValueFactory<>("requestType"));
    requestIDCol = new FilteredTableColumn<>("Request ID");
    requestIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
    initEmpCol = new FilteredTableColumn<>("Initiating Employee");
    initEmpCol.setCellValueFactory(new PropertyValueFactory<>("emp"));
    assignedEmpCol = new FilteredTableColumn<>("Assigned Employee");
    assignedEmpCol.setCellValueFactory(new PropertyValueFactory<>("assignedEmp"));
    subDateCol = new FilteredTableColumn<>("Submission Date");
    subDateCol.setCellValueFactory(new PropertyValueFactory<>("dateOfSubmission"));
    urgencyCol = new FilteredTableColumn<>("Urgency");
    urgencyCol.setCellValueFactory(new PropertyValueFactory<>("urgency"));
    locationCol = new FilteredTableColumn<>("Location");
    locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
    statusCol = new FilteredTableColumn<>("Status");
    statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    requestTable
        .getColumns()
        .setAll(
            requestTypeCol,
            requestIDCol,
            initEmpCol,
            assignedEmpCol,
            subDateCol,
            urgencyCol,
            locationCol,
            statusCol);

    nodeIDCol = new FilteredTableColumn<>("Node ID");
    nodeIDCol.setCellValueFactory(new PropertyValueFactory<>("node"));
    locationNameCol = new FilteredTableColumn<>("Location");
    locationNameCol.setCellValueFactory(new PropertyValueFactory<>("locationName"));
    dateCol = new FilteredTableColumn<>("Date Effective");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("moveDate"));
    moveTable.getColumns().setAll(nodeIDCol, locationNameCol, dateCol);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<edu.wpi.FlashyFrogs.ORM.Node> nodes =
        session.createQuery("FROM Node", edu.wpi.FlashyFrogs.ORM.Node.class).getResultList();
    List<LocationName> locationNames =
        session.createQuery("FROM LocationName ", LocationName.class).getResultList();
    List<ServiceRequest.Status> statuses = Arrays.asList(ServiceRequest.Status.values());

    session.close();
    // Status make it editable and combo box
    requestTable.setEditable(true);
    statusCol.setEditable(true);
    statusCol.setCellFactory(
        param -> new ComboBoxTableCell<>(FXCollections.observableList(statuses)));

    statusCol.setOnEditCommit(
        new EventHandler<
            FilteredTableColumn.CellEditEvent<ServiceRequest, ServiceRequest.Status>>() {
          @Override
          public void handle(
              FilteredTableColumn.CellEditEvent<ServiceRequest, ServiceRequest.Status> event) {
            try (Session session = CONNECTION.getSessionFactory().openSession()) {
              ServiceRequest request = event.getRowValue();
              request.setStatus(event.getNewValue());

              Transaction tx = session.beginTransaction();
              session.update(request);
              tx.commit();
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
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
        new EventHandler<
            FilteredTableColumn.CellEditEvent<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node>>() {
          @Override
          public void handle(
              FilteredTableColumn.CellEditEvent<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> event) {
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
        new EventHandler<FilteredTableColumn.CellEditEvent<MoveWrapper, LocationName>>() {
          @Override
          public void handle(FilteredTableColumn.CellEditEvent<MoveWrapper, LocationName> event) {
            Session session = CONNECTION.getSessionFactory().openSession();
            event.getRowValue().setLocationName(event.getNewValue(), session);
            session.close();
          }
        });

    dateCol.setCellFactory(TextFieldTableCell.forTableColumn(new DateStringConverter()));
    dateCol.setOnEditCommit(
        new EventHandler<FilteredTableColumn.CellEditEvent<MoveWrapper, Date>>() {
          @Override
          public void handle(FilteredTableColumn.CellEditEvent<MoveWrapper, Date> event) {
            Session session = CONNECTION.getSessionFactory().openSession();
            event.getRowValue().setMoveDate(event.getNewValue(), session);
            session.close();
          }
        });
    moveTable.setEditable(true);
    moveTable.getSelectionModel().setCellSelectionEnabled(true);

    requestTable.setRowFactory(
        param -> {
          TableRow<ServiceRequest> row = new TableRow<>(); // Create a new table row to use

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
                if (row != null && CurrentUserEntity.CURRENT_USER.getAdmin()) {
                  FXMLLoader newLoad =
                      new FXMLLoader(
                          Fapp.class.getResource(
                              "ServiceRequests/Editors/"
                                  + row.getItem().getRequestType()
                                  + "Editor.fxml"));

                  Parent root = null;
                  try {
                    root = newLoad.load();
                    PopOver popOver = new PopOver(root);
                    popOver.detach(); // Detach the pop-up, so it's not stuck to the button
                    Node node =
                        (Node) event.getSource(); // Get the node representation of what called this
                    popOver.show(node);
                  } catch (IOException e) {
                    throw new RuntimeException(e);
                  }

                  ServiceRequestController controller = newLoad.getController();
                  controller.setRequest(row.getItem());
                  controller.updateFields();
                }
              });
          return row;
        });

    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    if (!isAdmin) {
      tableText.setText("Assigned Service Requests");
      manageAnnouncementsButton.setDisable(true);
      manageAnnouncementsButton.setOpacity(0);
      manageLoginsButton.setDisable(true);
      manageLoginsButton.setOpacity(0);
      manageCSVButton.setDisable(true);
      manageCSVButton.setOpacity(0);
      editMovesButton.setDisable(true);
      editMovesButton.setOpacity(0);

      tableText2.setText("");
    } else {
      tableText.setText("All Service Requests");
      manageAnnouncementsButton.setDisable(false);
      manageAnnouncementsButton.setOpacity(1);
      manageLoginsButton.setDisable(false);
      manageLoginsButton.setOpacity(1);
      manageCSVButton.setDisable(false);
      manageCSVButton.setOpacity(1);
      editMovesButton.setDisable(false);
      editMovesButton.setOpacity(1);

      tableText2.setText("Future Moves");
    }
    refreshTable();
    setListener();
  }

  @FXML
  public void openPathfinding(ActionEvent event) throws IOException {
    System.out.println("opening pathfinding");
    Fapp.setScene("Pathfinding", "Pathfinding");
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
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      hDone = false;
    }
  }

  public void viewLogins(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Accounts", "LoginAdministrator");
  }

  public void refreshTable() {
    HospitalUser currentUser = CurrentUserEntity.CURRENT_USER.getCurrentuser();
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

      ObservableList<ServiceRequest> srList = FXCollections.observableList(serviceRequests);
      FilteredList<ServiceRequest> filteredSR = new FilteredList<>(srList);
      filteredSR.predicateProperty().bind(requestTable.predicateProperty());
      SortedList<ServiceRequest> sortedSR = new SortedList<>(filteredSR);
      sortedSR.comparatorProperty().bind(requestTable.comparatorProperty());
      requestTable.setItems(sortedSR);

      moveTable.setOpacity(0);
    } else {
      serviceRequests =
          session
              .createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class)
              .getResultList();
      ObservableList<ServiceRequest> srList = FXCollections.observableList(serviceRequests);
      FilteredList<ServiceRequest> filteredSR = new FilteredList<>(srList);
      filteredSR.predicateProperty().bind(requestTable.predicateProperty());
      SortedList<ServiceRequest> sortedSR = new SortedList<>(filteredSR);
      sortedSR.comparatorProperty().bind(requestTable.comparatorProperty());
      requestTable.setItems(sortedSR);

      String query = "SELECT m from Move m WHERE m.moveDate > current timestamp";
      if (canEditMoves) {
        query = "SELECT m from Move m";
      }
      moves = session.createQuery(query, Move.class).getResultList();
      List<MoveWrapper> moveWrappers = new ArrayList<>();
      for (Move move : FXCollections.observableList(moves)) {
        moveWrappers.add(new MoveWrapper(move));
      }
      ObservableList<MoveWrapper> moveList = FXCollections.observableList(moveWrappers);
      FilteredList<MoveWrapper> filteredMove = new FilteredList<>(moveList);
      filteredMove.predicateProperty().bind(moveTable.predicateProperty());
      SortedList<MoveWrapper> sortedMove = new SortedList<>(filteredMove);
      sortedMove.comparatorProperty().bind(moveTable.comparatorProperty());
      moveTable.setItems(sortedMove);
    }

    // refill based on filter
    if (!filterCreated) {

      session.close();
    }
  }

  public void setListener() {
    filterBox
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue.equals(null)) {
                Session session = CONNECTION.getSessionFactory().openSession();
                HospitalUser currentUser = CurrentUserEntity.CURRENT_USER.getCurrentuser();
                boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();
                if (!newValue.equals("All")) {
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
                session.close();
              }
            });
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
    canEditMoves = !canEditMoves;
    if (canEditMoves) {
      tableText2.setText("All Moves");
    } else {
      tableText2.setText("Future Moves");
    }
    refreshTable();
  }

  public void srEditorPopOver() {}
}
