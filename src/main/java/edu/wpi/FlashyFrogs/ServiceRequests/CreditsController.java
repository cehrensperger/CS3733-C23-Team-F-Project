package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.HospitalUser;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.List;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import org.hibernate.Session;

@GeneratedExclusion
public class CreditsController implements IController {

  @FXML MFXButton MD;
  @FXML protected FilteredTableView<ServiceRequest> requestTable;
  @FXML protected FilteredTableColumn<ServiceRequest, String> requestTypeCol;
  @FXML protected FilteredTableColumn<ServiceRequest, Long> requestIDCol;
  @FXML MFXButton religious;
  @FXML MFXButton credits;
  @FXML MFXButton equipmentButton;
  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML Text h1;
  boolean hDone = false;

  public void initialize() {
    h1.setVisible(false);

    requestTypeCol.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().getRequestType()));
    requestTypeCol.setReorderable(false);
    requestIDCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getId()));
    requestIDCol.setReorderable(false);

    PopupFilter<ServiceRequest, String> popupTypeFilter = new PopupStringFilter<>(requestTypeCol);
    requestTypeCol.setOnFilterAction(e -> popupTypeFilter.showPopup());
    PopupFilter<ServiceRequest, Long> popupIDFilter = new PopupStringFilter<>(requestIDCol);
    requestIDCol.setOnFilterAction(e -> popupIDFilter.showPopup());

    requestTable.setOnMouseClicked(
        new EventHandler<MouseEvent>() {
          @Override
          @SneakyThrows
          public void handle(MouseEvent event) {
            ServiceRequest selectedItem =
                requestTable.getSelectionModel().selectedItemProperty().get();
            if (selectedItem != null) {

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
            requestTable.getSelectionModel().clearSelection();
          }
        });
    refreshTable();
  }

  public void refreshTable() {
    HospitalUser currentUser = CurrentUserEntity.CURRENT_USER.getCurrentUser();
    boolean isAdmin = CurrentUserEntity.CURRENT_USER.getAdmin();

    Session session = CONNECTION.getSessionFactory().openSession();

    // FILL TABLES
    List<ServiceRequest> serviceRequests;
    if (!isAdmin) {
      serviceRequests =
          session
              .createQuery(
                  "SELECT s FROM ServiceRequest s WHERE s.assignedEmp = :emp", ServiceRequest.class)
              .setParameter("emp", currentUser)
              .getResultList();

      ObservableList<ServiceRequest> srList = FXCollections.observableList(serviceRequests);
      FilteredTableView.configureForFiltering(requestTable, srList);
    } else {
      serviceRequests =
          session
              .createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class)
              .getResultList();
      ObservableList<ServiceRequest> srList = FXCollections.observableList(serviceRequests);
      FilteredTableView.configureForFiltering(requestTable, srList);
    }

    requestTable.refresh();
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleEquipment(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "EquipmentTransport");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ComputerService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
  }

  public void handleBack(ActionEvent actionEvent) throws IOException {
    Fapp.handleBack();
  }

  public void handleReligious(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ReligiousService");
  }

  public void handleMedicine(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "MedicineDeliveryService");
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
