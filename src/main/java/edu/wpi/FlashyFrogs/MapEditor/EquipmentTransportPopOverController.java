package edu.wpi.FlashyFrogs.MapEditor;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Node;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

public class EquipmentTransportPopOverController {

  @FXML MFXButton clear;
  @FXML MFXButton submit;

  @Getter @FXML TextField equipment;
  @Getter @FXML SearchableComboBox<LocationName> to;
  @Getter @FXML Label from;
  @Getter @FXML DatePicker date;
  @Getter @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @Getter @FXML TextField description;

  @Getter @FXML private MFXButton submitButton;

  private @Setter Node fromNode;

  public void initialize() {

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    to.setItems(FXCollections.observableArrayList(locations));
    // from.setItems();
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));

    urgency.setButtonCell(
        new ListCell<ServiceRequest.Urgency>() {
          @Override
          protected void updateItem(ServiceRequest.Urgency item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Urgency");
            } else {
              setText(item.toString());
            }
          }
        });

    to.setButtonCell(
        new ListCell<LocationName>() {
          @Override
          protected void updateItem(LocationName item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Transfer To");
            } else {
              setText(item.toString());
            }
          }
        });

    // from.setText("From Node: " + fromNode.toString());

    urgency.setButtonCell(
        new ListCell<ServiceRequest.Urgency>() {
          @Override
          protected void updateItem(ServiceRequest.Urgency item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Urgency");
            } else {
              setText(item.toString());
            }
          }
        });
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    equipment.setText("");
    to.valueProperty().set(null);
    from.setText("From Node: ");
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    description.setText("");
  }

  //  public void handleSubmit(ActionEvent actionEvent) throws IOException {
  //    Session session = CONNECTION.getSessionFactory().openSession();
  //    Transaction transaction = session.beginTransaction();
  //
  //    try {
  //      // check
  //      if (equipment.getText().equals("")
  //              || to.getValue().toString().equals("")
  //              || from.getValue().toString().equals("")
  //              || date.getValue().toString().equals("")
  //              || description.getText().equals("")) {
  //        throw new NullPointerException();
  //      }
  //      Date dateNeeded =
  // Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
  //
  //      EquipmentTransport equipmentTransport =
  //              new EquipmentTransport(
  //                      CurrentUserEntity.CURRENT_USER.getCurrentUser(),
  //                      dateNeeded,
  //                      Date.from(Instant.now()),
  //                      urgency.getValue(),
  //                      from.getValue(),
  //                      to.getValue(),
  //                      equipment.getText(),
  //                      description.getText());
  //
  //      try {
  //        session.persist(equipmentTransport);
  //        transaction.commit();
  //        session.close();
  //        handleClear(actionEvent);
  //        //toastAnimation();
  //        Sound.SUBMITTED.play();
  //      } catch (RollbackException exception) {
  //        session.clear();
  //        submit.setDisable(true);
  //
  //        session.close();
  //        Sound.ERROR.play();
  //      }
  //    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
  //      session.clear();
  //      submit.setDisable(true);
  //      //errortoastAnimation();
  //      session.close();
  //      Sound.ERROR.play();
  //    }
  //  }
}
