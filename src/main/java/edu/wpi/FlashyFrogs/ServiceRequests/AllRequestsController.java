package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.controllers.HelpController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AllRequestsController {
  @FXML protected TableColumn<ServiceRequest, String> typeCol;
  @FXML protected TableColumn<ServiceRequest, String> empLastNameCol;
  @FXML protected TableColumn<ServiceRequest, String> submissionDateCol;
  @FXML protected TableColumn<ServiceRequest, String> statusCol;

  @FXML protected TableView<ServiceRequest> tableView;
  @FXML private MFXButton back;

  @FXML private MFXButton question;

  @FXML
  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("views", "RequestsHome");
  }

  public void handleQ(ActionEvent event) throws IOException {

    FXMLLoader newLoad = new FXMLLoader(Fapp.class.getResource("views/Help.fxml"));
    PopOver popOver = new PopOver(newLoad.load());

    HelpController help = newLoad.getController();
    help.handleQAllRequests();

    popOver.detach();
    Node node = (Node) event.getSource();
    popOver.show(node.getScene().getWindow());
  }

  public void initialize() {
    System.out.println("initializing");

    typeCol.setCellValueFactory(
        new Callback<
            TableColumn.CellDataFeatures<ServiceRequest, String>, ObservableValue<String>>() {
          public ObservableValue<String> call(
              TableColumn.CellDataFeatures<ServiceRequest, String> p) {
            // p represents a single cell in the column (typeCol)
            // This method returns what should be displayed in the cell
            return new SimpleStringProperty(p.getValue().toString());
          }
        });
    empLastNameCol.setCellValueFactory(new PropertyValueFactory<>("empLastName"));
    submissionDateCol.setCellValueFactory(new PropertyValueFactory<>("dateOfSubmission"));
    statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    Session session = CONNECTION.getSessionFactory().openSession();

    // get all ServiceRequests of any type from the DB
    List<ServiceRequest> objects =
        session.createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class).getResultList();

    tableView.setItems(FXCollections.observableList(objects));
    tableView.setEditable(true);
    statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("BLANK", "PROCESSING", "DONE"));
    statusCol.setOnEditCommit(
        new EventHandler<TableColumn.CellEditEvent<ServiceRequest, String>>() {
          @Override
          public void handle(TableColumn.CellEditEvent<ServiceRequest, String> t) {

            Session editSession = CONNECTION.getSessionFactory().openSession();
            Transaction transaction = editSession.beginTransaction();

            ServiceRequest serviceRequest =
                (t.getTableView().getItems().get(t.getTablePosition().getRow()));
            serviceRequest.setStatus(ServiceRequest.Status.valueOf((t.getNewValue())));

            editSession.merge(serviceRequest);
            transaction.commit();
            editSession.close();
          }
        });

    session.close();
  }
}
