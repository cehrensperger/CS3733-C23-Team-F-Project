package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import lombok.NonNull;
import org.hibernate.Session;

public class LocationNameInfoController {
  @FXML private MFXButton saveButton;
  @FXML private MFXButton deleteButton;
  @FXML private Text errorText;
  @FXML private MFXTextField locationNameField;
  @FXML private MFXComboBox<LocationName.LocationType> locationTypeField;
  @FXML private MFXTextField shortNameField;

  /**
   * Sets the location name and updates the associated
   *
   * @param locationName the location name to set everything to use
   * @param session the session to use to get information on the location name
   * @param handleDelete a function that should be called when this location is deleted
   * @param onUpdate function to be called when the location itself changes - passes the location
   *     back to the caller
   */
  public void setLocationName(
      @NonNull LocationName locationName,
      @NonNull Session session,
      @NonNull Runnable handleDelete,
      @NonNull Consumer<LocationName> onUpdate) {
    // Create properties representing the location names
    StringProperty longName = new SimpleStringProperty(locationName.getLongName());
    ObjectProperty<LocationName.LocationType> type =
        new SimpleObjectProperty<>(locationName.getLocationType());
    StringProperty shortName = new SimpleStringProperty(locationName.getShortName());

    // Set the text manually cuz MaterialFX is mean
    locationTypeField.setText(locationName.getLocationType().name());

    // Bind the items for the type field
    locationTypeField.setItems(
        FXCollections.observableArrayList(LocationName.LocationType.values()));

    // Bind all the properties
    locationNameField.textProperty().bindBidirectional(longName);
    locationTypeField.valueProperty().bindBidirectional(type);
    shortNameField.textProperty().bindBidirectional(shortName);

    // Bind the delete button
    deleteButton.setOnAction(
        event -> {
          // Do the deletion in the DB
          session
              .createMutationQuery("DELETE FROM LocationName WHERE longName = :originalName")
              .setParameter("originalName", locationName.getLongName());
          handleDelete.run(); // Run the deletion handler
        });

    saveButton.setOnAction(
        event -> {
          errorText.setText(""); // Clear the error text

          // Check to make sure that the location is unique. Uses a query because session.find
          // does not play nice with changing long names without committing
          if (!longName.get().equals(locationName.getLongName())
              && session
                      .createQuery(
                          "FROM LocationName WHERE longName = :newName", LocationName.class)
                      .setParameter("newName", longName.get())
                      .uniqueResult()
                  != null) {
            errorText.setText("A location with that name already exists! No changes saved.");
            return; // Short-circuit, don't run the update
          }

          // Run a query that updates the location name to be the new values, searching by the long
          // name PK
          session
              .createMutationQuery(
                  "UPDATE LocationName SET "
                      + "longName = :newLongName, locationType = :newType, shortName = :newShortName "
                      + "WHERE longName = :originalName")
              .setParameter("newLongName", longName.get())
              .setParameter("newType", type.get())
              .setParameter("newShortName", shortName.get())
              .setParameter("originalName", locationName.getLongName())
              .executeUpdate();

          session.flush(); // Force things to persist

          // If they haven't changed the long name, refresh the object so that we get the query
          // changes
          if (locationName.getLongName().equals(longName.get())) {
            session.refresh(locationName); // Refresh
          }

          // Call the updater with the newly-gotten location name
          onUpdate.accept(
              session
                  .createQuery(
                      "FROM LocationName WHERE longName = :newLongName", LocationName.class)
                  .setParameter("newLongName", longName.get())
                  .getSingleResult());
        });
  }
}
