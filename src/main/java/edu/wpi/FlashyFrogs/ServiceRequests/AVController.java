package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.AudioVisual;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.ServiceRequest;
import edu.wpi.FlashyFrogs.Sound;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import jakarta.persistence.RollbackException;
import java.io.IOException;
import java.sql.Connection;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class AVController implements IController {

  @FXML Pane errtoast;
  @FXML Rectangle errcheck2;
  @FXML Rectangle errcheck1;
  @FXML Rectangle check2;
  @FXML Rectangle check1;
  @FXML Pane toast;
  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML MFXButton credits;
  @FXML MFXButton equipmentButton;
  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML TextField device;
  @FXML TextField reason;
  @FXML DatePicker date;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML TextField description;

  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML private Label errorMessage;

  boolean hDone = false;
  private Connection connection = null;

  public void initialize() {
    h1.setVisible(false);
    h2.setVisible(false);
    h3.setVisible(false);
    h4.setVisible(false);
    h5.setVisible(false);
    h6.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();

    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
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

    locationBox.setButtonCell(
        new ListCell<LocationName>() {
          @Override
          protected void updateItem(LocationName item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Location of Request");
            } else {
              setText(item.toString());
            }
          }
        });

    session.close();
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException, InterruptedException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // check
      if (locationBox.getValue().toString().equals("")
          || device.getText().equals("")
          || reason.getText().equals("")
          || date.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }

      Date dateNeeded = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      AudioVisual audioVisual =
          new AudioVisual(
              CurrentUserEntity.CURRENT_USER.getCurrentUser(),
              dateNeeded,
              Date.from(Instant.now()),
              urgency.getValue(),
              device.getText(),
              reason.getText(),
              description.getText(),
              locationBox.getValue());

      try {
        session.persist(audioVisual);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        toastAnimation();
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
      errortoastAnimation();
      session.close();
      Sound.ERROR.play();
    }
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    device.setText("");
    date.valueProperty().set(null);
    description.setText("");
    reason.setText("");
    urgency.valueProperty().set(null);
    locationBox.valueProperty().set(null);
  }

  public void help() {

    locationBox.setTooltip(new Tooltip("Select the location of the request"));

    locationBox
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                locationBox
                    .getTooltip()
                    .show(
                        locationBox,
                        // get screen coordinates of the node
                        locationBox.localToScreen(locationBox.getBoundsInLocal()).getMinX(),
                        locationBox.localToScreen(locationBox.getBoundsInLocal()).getMinY()
                            - locationBox.getTooltip().getHeight());

              } else {
                locationBox.getTooltip().hide();
              }
            });
    // make hover properties for all the other fields

    device
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              Tooltip tooltip = new Tooltip("Enter the device name");
              if (newValue) {
                tooltip.show(
                    device,
                    // get screen coordinates of the node
                    device.localToScreen(device.getBoundsInLocal()).getMinX(),
                    device.localToScreen(device.getBoundsInLocal()).getMinY()
                        - tooltip.getHeight());

              } else {
                device.setTooltip(null);
                tooltip.hide();
              }
            });

    reason
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                new Tooltip("Enter the reason for the request")
                    .show(
                        reason,
                        // get screen coordinates of the node
                        reason.localToScreen(reason.getBoundsInLocal()).getMinX(),
                        reason.localToScreen(reason.getBoundsInLocal()).getMinY()
                            - reason.getTooltip().getHeight());

              } else {
                reason.getTooltip().hide();
              }
            });

    date.hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                new Tooltip("Enter the date needed")
                    .show(
                        date,
                        // get screen coordinates of the node
                        date.localToScreen(date.getBoundsInLocal()).getMinX(),
                        date.localToScreen(date.getBoundsInLocal()).getMinY()
                            - date.getTooltip().getHeight());

              } else {
                date.getTooltip().hide();
              }
            });
    description
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                new Tooltip("Enter the description of the request")
                    .show(
                        description,
                        // get screen coordinates of the node
                        description.localToScreen(description.getBoundsInLocal()).getMinX(),
                        description.localToScreen(description.getBoundsInLocal()).getMinY()
                            - description.getTooltip().getHeight());

              } else {
                description.getTooltip().hide();
              }
            });
    urgency
        .hoverProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (newValue) {
                new Tooltip("Select the urgency of the request")
                    .show(
                        urgency,
                        // get screen coordinates of the node
                        urgency.localToScreen(urgency.getBoundsInLocal()).getMinX(),
                        urgency.localToScreen(urgency.getBoundsInLocal()).getMinY()
                            - urgency.getTooltip().getHeight());

              } else {
                urgency.getTooltip().hide();
              }
            });

    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      hDone = false;
    }
  }

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleReligious(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ReligiousService");
  }

  public void handleMedicine(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "MedicineDeliveryService");
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

  public void toastAnimation() {
    // Create a TranslateTransition to move the first rectangle to the left
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), toast);
    translate1.setByX(-280.0);
    translate1.setAutoReverse(true);
    check1.setFill(Color.web("#012D5A"));
    check2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), check1, Color.web("#012D5A"), Color.web("#F6BD38"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), check2, Color.web("#012D5A"), Color.web("#F6BD38"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), toast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.play();
  }

  public void errortoastAnimation() {
    TranslateTransition translate1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translate1.setByX(-280);
    translate1.setAutoReverse(true);
    errcheck1.setFill(Color.web("#012D5A"));
    errcheck2.setFill(Color.web("#012D5A"));
    // Create FillTransitions to fill the second and third rectangles in sequence
    FillTransition fill2 =
        new FillTransition(
            Duration.seconds(0.1), errcheck1, Color.web("#012D5A"), Color.web("#B6000B"));
    FillTransition fill3 =
        new FillTransition(
            Duration.seconds(0.1), errcheck2, Color.web("#012D5A"), Color.web("#B6000B"));
    SequentialTransition fillSequence = new SequentialTransition(fill2, fill3);

    // Create a TranslateTransition to move the first rectangle back to its original position
    TranslateTransition translateBack1 = new TranslateTransition(Duration.seconds(0.5), errtoast);
    translateBack1.setDelay(Duration.seconds(0.5));
    translateBack1.setByX(280.0);

    // Play the animations in sequence
    SequentialTransition sequence =
        new SequentialTransition(translate1, fillSequence, translateBack1);
    sequence.setCycleCount(1);
    sequence.setAutoReverse(false);
    sequence.jumpTo(Duration.ZERO);
    sequence.playFromStart();
    sequence.setOnFinished(
        new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent event) {
            submit.setDisable(false);
          }
        });
  }

  @Override
  public void onClose() {}
}
