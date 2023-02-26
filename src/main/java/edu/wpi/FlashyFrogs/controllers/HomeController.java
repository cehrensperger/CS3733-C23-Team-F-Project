package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.*;
import edu.wpi.FlashyFrogs.ServiceRequests.ServiceRequestController;
import java.io.IOException;
import java.util.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DateStringConverter;
import lombok.Getter;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class HomeController implements IController {
  @FXML protected FilteredTableColumn<ServiceRequest, String> requestTypeCol;
  @FXML protected FilteredTableColumn<ServiceRequest, Long> requestIDCol;
  @FXML protected FilteredTableColumn<ServiceRequest, HospitalUser> initEmpCol;
  @FXML protected FilteredTableColumn<ServiceRequest, HospitalUser> assignedEmpCol;
  @FXML protected FilteredTableColumn<ServiceRequest, Date> subDateCol;
  @FXML protected FilteredTableColumn<ServiceRequest, ServiceRequest.Urgency> urgencyCol;
  @FXML protected FilteredTableColumn<ServiceRequest, LocationName> locationCol;
  @FXML protected FilteredTableColumn<ServiceRequest, ServiceRequest.Status> statusCol;

  @FXML protected FilteredTableView<ServiceRequest> requestTable;

  @FXML protected FilteredTableColumn<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> nodeIDCol;
  @FXML protected FilteredTableColumn<MoveWrapper, LocationName> locationNameCol;
  @FXML protected FilteredTableColumn<MoveWrapper, Date> dateCol;
  @FXML protected FilteredTableView<MoveWrapper> moveTable;

  @FXML protected Label tableText;
  @FXML protected Label tableText2;

  @FXML protected ScrollPane scrollPane;
  @FXML protected VBox alertBox;

  protected boolean canEditMoves = false;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
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

    Fapp.resetStack();

    // need to be the names of the fields
    requestTypeCol.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().getRequestType()));
    requestTypeCol.setReorderable(false);
    requestIDCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getId()));
    requestIDCol.setReorderable(false);
    initEmpCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getEmp()));
    initEmpCol.setReorderable(false);
    assignedEmpCol.setCellValueFactory(
        p -> new SimpleObjectProperty<>(p.getValue().getAssignedEmp()));
    assignedEmpCol.setReorderable(false);
    subDateCol.setCellValueFactory(
        p -> new SimpleObjectProperty<>(p.getValue().getDateOfSubmission()));
    subDateCol.setReorderable(false);
    urgencyCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getUrgency()));
    urgencyCol.setReorderable(false);
    locationCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getLocation()));
    locationCol.setReorderable(false);
    statusCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStatus()));
    statusCol.setReorderable(false);

    PopupFilter<ServiceRequest, String> popupTypeFilter = new PopupStringFilter<>(requestTypeCol);
    requestTypeCol.setOnFilterAction(e -> popupTypeFilter.showPopup());
    PopupFilter<ServiceRequest, Long> popupIDFilter = new PopupStringFilter<>(requestIDCol);
    requestIDCol.setOnFilterAction(e -> popupIDFilter.showPopup());
    PopupFilter<ServiceRequest, HospitalUser> popupEmpFilter = new PopupStringFilter<>(initEmpCol);
    initEmpCol.setOnFilterAction(e -> popupEmpFilter.showPopup());
    PopupFilter<ServiceRequest, HospitalUser> popupAssignedFilter =
        new PopupStringFilter<>(assignedEmpCol);
    assignedEmpCol.setOnFilterAction(e -> popupAssignedFilter.showPopup());
    PopupFilter<ServiceRequest, Date> popupSubDateFilter = new PopupStringFilter<>(subDateCol);
    subDateCol.setOnFilterAction(e -> popupSubDateFilter.showPopup());
    PopupFilter<ServiceRequest, ServiceRequest.Urgency> popupUrgencyFilter =
        new PopupStringFilter<>(urgencyCol);
    urgencyCol.setOnFilterAction(e -> popupUrgencyFilter.showPopup());
    PopupFilter<ServiceRequest, LocationName> popupLocationFilter =
        new PopupStringFilter<>(locationCol);
    locationCol.setOnFilterAction(e -> popupLocationFilter.showPopup());
    PopupFilter<ServiceRequest, ServiceRequest.Status> popupStatusFilter =
        new PopupStringFilter<>(statusCol);
    statusCol.setOnFilterAction(e -> popupStatusFilter.showPopup());

    nodeIDCol.setCellValueFactory(new PropertyValueFactory<>("node"));
    nodeIDCol.setReorderable(false);
    locationNameCol.setCellValueFactory(new PropertyValueFactory<>("locationName"));
    locationNameCol.setReorderable(false);
    dateCol.setCellValueFactory(new PropertyValueFactory<>("moveDate"));
    dateCol.setReorderable(false);

    PopupFilter<MoveWrapper, edu.wpi.FlashyFrogs.ORM.Node> popupNodeFilter =
        new PopupStringFilter<>(nodeIDCol);
    nodeIDCol.setOnFilterAction(e -> popupNodeFilter.showPopup());
    PopupFilter<MoveWrapper, LocationName> popupLocationNameFilter =
        new PopupStringFilter<>(locationNameCol);
    locationNameCol.setOnFilterAction(e -> popupLocationNameFilter.showPopup());
    PopupFilter<MoveWrapper, Date> popupDateFilter = new PopupStringFilter<>(dateCol);
    dateCol.setOnFilterAction(e -> popupDateFilter.showPopup());

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
    TableRow<ServiceRequest> row1 = new TableRow<>();
    requestTable.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          @SneakyThrows
          public void handle(MouseEvent event) {
            ServiceRequest selectedItem =
                requestTable.getSelectionModel().selectedItemProperty().get();
            if (selectedItem != null) {

              if (CurrentUserEntity.CURRENT_USER.getAdmin()) {
                FXMLLoader newLoad =
                    new FXMLLoader(
                        Fapp.class.getResource(
                            "ServiceRequests/Editors/"
                                + selectedItem.getRequestType()
                                + "Editor.fxml"));

                Parent root = null;
                root = newLoad.load();
                PopOver popOver = new PopOver(root);
                popOver.detach(); // Detach the pop-up, so it's not stuck to the button
                Node node =
                    (Node) event.getSource(); // Get the node representation of what called this
                popOver.show(node);
                ServiceRequestController controller = newLoad.getController();
                controller.setRequest(selectedItem);
                controller.updateFields();
                controller.setPopOver(popOver);

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
            requestTable.getSelectionModel().clearSelection();
          }
        });

    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    if (!isAdmin) {
      tableText.setText("Assigned Service Requests");
      tableText2.setText("");
    } else {
      tableText.setText("All Service Requests");
      tableText2.setText("Future Moves");
    }
    refreshTable();

    refreshAlerts();
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

  public void handleLogOut(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "Login");
  }

  public void manageAnnouncements(ActionEvent event) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/AlertManager.fxml"));
    PopOver popOver = new PopOver(newLoad.load()); // create the popover

    AlertManagerController controller = newLoad.getController();
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
                refreshAlerts();
              }
            });
  }

  public void onClose() {}

  @Override
  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      hDone = false;
    }
  }

  public void viewLogins(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Accounts", "LoginAdministrator");
  }

  public void refreshTable() {
    HospitalUser currentUser = CurrentUserEntity.CURRENT_USER.getCurrentUser();
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
      FilteredTableView.configureForFiltering(requestTable, srList);

      moveTable.setOpacity(0);
    } else {
      serviceRequests =
          session
              .createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class)
              .getResultList();
      ObservableList<ServiceRequest> srList = FXCollections.observableList(serviceRequests);
      FilteredTableView.configureForFiltering(requestTable, srList);

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
      FilteredTableView.configureForFiltering(moveTable, moveList);
    }

    moveTable.refresh();
    requestTable.refresh();
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

  public void handleResetFilters() {
    requestTable.resetFilter();
    moveTable.resetFilter();
  }

  public void insertAlert(Announcement announcement) throws IOException {
    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Alert.fxml"));

    Parent root = newLoad.load();
    alertBox.getChildren().add(root);

    AlertController controller = newLoad.getController();
    controller.insertAnnouncement(announcement);
  }

  @SneakyThrows
  public void refreshAlerts() {
    alertBox.getChildren().clear();

    Session session = CONNECTION.getSessionFactory().openSession();
    List<Announcement> list =
        session.createQuery("Select a from Announcement a", Announcement.class).getResultList();

    for (Announcement a : list) {
      insertAlert(a);
    }
  }
  /** Callback to open the map editor from a button */
  @FXML
  public void openMapEditor() {
    Fapp.setScene("MapEditor", "MapEditorView");
  }

  public void srEditorPopOver() {}
}
