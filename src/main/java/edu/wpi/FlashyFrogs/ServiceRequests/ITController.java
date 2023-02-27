package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Accounts.CurrentUserEntity;
import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.ComputerService;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

@GeneratedExclusion
public class ITController implements IController {
  @FXML Pane errtoast;
  @FXML Rectangle errcheck2;
  @FXML Rectangle errcheck1;
  @FXML Rectangle check2;
  @FXML Rectangle check1;
  @FXML Pane toast;
  @FXML MFXButton AV;
  @FXML MFXButton IT;
  @FXML MFXButton IPT;
  @FXML MFXButton sanitation;
  @FXML MFXButton security;
  @FXML MFXButton credits;
  @FXML MFXButton equipmentButton;
  @FXML MFXButton clear;
  @FXML MFXButton submit;
  @FXML TextField number;
  @FXML SearchableComboBox<LocationName> locationBox;
  @FXML SearchableComboBox<ComputerService.ServiceType> service;
  @FXML SearchableComboBox<ServiceRequest.Urgency> urgency;
  @FXML SearchableComboBox<ComputerService.DeviceType> type;
  @FXML DatePicker date;
  @FXML TextField description;
  @FXML Text h1;
  @FXML Text h2;
  @FXML Text h3;
  @FXML Text h4;
  @FXML Text h5;
  @FXML Text h6;
  @FXML Text h7;
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
    h7.setVisible(false);

    Session session = CONNECTION.getSessionFactory().openSession();
    List<LocationName> locations =
        session.createQuery("FROM LocationName", LocationName.class).getResultList();

    locations.sort(Comparator.comparing(LocationName::getShortName));

    locationBox.setItems(FXCollections.observableArrayList(locations));
    service.setItems(FXCollections.observableArrayList(ComputerService.ServiceType.values()));
    urgency.setItems(FXCollections.observableArrayList(ServiceRequest.Urgency.values()));
    type.setItems(FXCollections.observableArrayList(ComputerService.DeviceType.values()));
    session.close();

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

    service.setButtonCell(
        new ListCell<ComputerService.ServiceType>() {
          @Override
          protected void updateItem(ComputerService.ServiceType item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Service Type");
            } else {
              setText(item.toString());
            }
          }
        });

    type.setButtonCell(
        new ListCell<ComputerService.DeviceType>() {
          @Override
          protected void updateItem(ComputerService.DeviceType item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
              setText("Device Type");
            } else {
              setText(item.toString());
            }
          }
        });
  }

  public void handleSubmit(ActionEvent actionEvent) throws IOException {
    Session session = CONNECTION.getSessionFactory().openSession();
    Transaction transaction = session.beginTransaction();

    try {
      // check
      if (number.getText().equals("")
          || locationBox.getValue().toString().equals("")
          || service.getValue().toString().equals("")
          || type.getValue().toString().equals("")
          || description.getText().equals("")) {
        throw new NullPointerException();
      }
      Date dateNeeded = Date.from(date.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());

      ComputerService informationTechnology =
          new ComputerService(
              CurrentUserEntity.CURRENT_USER.getCurrentUser(),
              locationBox.getValue(),
              dateNeeded,
              Date.from(Instant.now()),
              urgency.getValue(),
              type.getValue(),
              "temp",
              description.getText(),
              service.getValue(),
              number.getText());

      try {
        session.persist(informationTechnology);
        transaction.commit();
        session.close();
        handleClear(actionEvent);
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#012D5A"));
        errorMessage.setText("Successfully submitted.");
        toastAnimation();
      } catch (RollbackException exception) {
        session.clear();
        errorMessage.setTextFill(javafx.scene.paint.Paint.valueOf("#b6000b"));
        errorMessage.setText("Please fill all fields.");
        session.close();
        Sound.ERROR.play();
      }
    } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
      session.clear();
      errorMessage.setTextFill(Paint.valueOf("#b6000b"));
      errorMessage.setText("Please fill all fields.");
      session.close();
      Sound.ERROR.play();
    }
  }

  public void handleClear(ActionEvent actionEvent) throws IOException {
    number.setText("");
    locationBox.valueProperty().set(null);
    service.valueProperty().set(null);
    type.valueProperty().set(null);
    date.valueProperty().set(null);
    urgency.valueProperty().set(null);
    description.setText("");
  }

  public void help() {
    if (!hDone) {
      h1.setVisible(true);
      h2.setVisible(true);
      h3.setVisible(true);
      h4.setVisible(true);
      h5.setVisible(true);
      h6.setVisible(true);
      h7.setVisible(true);
      hDone = true;
    } else if (hDone) {
      h1.setVisible(false);
      h2.setVisible(false);
      h3.setVisible(false);
      h4.setVisible(false);
      h5.setVisible(false);
      h6.setVisible(false);
      h7.setVisible(false);
      hDone = false;
    }
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
    errtoast.getTransforms().clear();
    errtoast.setLayoutX(0);

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

  public void onClose() {}
}
