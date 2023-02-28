package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.ORM.EquipmentTransport;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.Sound;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

public class EquipmentTransportPopOverController {

  @FXML MFXButton clear;
  @FXML MFXButton submit;

  @FXML
  TextField equipment;
  @FXML
  SearchableComboBox<LocationName> to;
  @FXML SearchableComboBox<LocationName> from;
  @FXML
  DatePicker date;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML TextField description;
  @FXML private MFXButton submitButton;

  public void handleClear(ActionEvent actionEvent) throws IOException {
    equipment.setText("");
    to.valueProperty().set(null);
    from.valueProperty().set(null);
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    description.setText("");
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // check
      if (equipment.getText().equals("")
              || to.getValue().toString().equals("")
              || from.getValue().toString().equals("")
              || date.getValue().toString().equals("")
              || description.getText().equals("")) {
        throw new NullPointerException();
      }
      Date dateNeeded = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      EquipmentTransport equipmentTransport =
              new EquipmentTransport(
                      CurrentUserEntity.CURRENT_USER.getCurrentUser(),
                      dateNeeded,
                      Date.from(Instant.now()),
                      urgency.getValue(),
                      from.getValue(),
                      to.getValue(),
                      equipment.getText(),
                      description.getText());

      try {
        session.persist(equipmentTransport);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        //toastAnimation();
        Sound.SUBMITTED.play();
      } catch (RollbackException exception) {
        session.clear();
        submit.setDisable(true);

        session.close();
        Sound.ERROR.play();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      submit.setDisable(true);
      //errortoastAnimation();
      session.close();
      Sound.ERROR.play();
    }
  }
}
