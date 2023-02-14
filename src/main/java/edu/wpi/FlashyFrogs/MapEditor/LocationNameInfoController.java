package edu.wpi.FlashyFrogs.MapEditor;

import edu.wpi.FlashyFrogs.GeneratedExclusion;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import lombok.NonNull;
import org.controlsfx.control.SearchableComboBox;
import org.hibernate.Session;

@GeneratedExclusion
public class LocationNameInfoController {
  @FXML private Button saveButton;
  @FXML private Button deleteButton;
  @FXML private Text errorText;
  @FXML private TextField locationNameField;
  @FXML private SearchableComboBox<LocationName.LocationType> locationTypeField;
  @FXML private TextField shortNameField;

  /**
   * Sets the location name and updates the associated
   *
   * @param locationName the location name to set everything to use
   * @param session the session to use to get information on the location name
   * @param handleDelete a function that should be called when this location is deleted. The
   *     location to delete should be provided
   * @param onUpdate function to be called when the location itself changes - passes the old
   *     location and the new one. Must be prepared to accept null in cases where a location is
   *     being created
   */
  void setLocationName(
      @NonNull LocationName locationName,
      @NonNull Session session,
      @NonNull Consumer<LocationName> handleDelete,
      @NonNull BiConsumer<LocationName, LocationName> onUpdate,
      boolean isNewLocation) {
    // Place to store the last "safe" location name, only useful for cases when that changes
    final String[] originalName = new String[1]; // String array (pointer) for the original name
    originalName[0] = locationName.getLongName(); // Original name to reference

    // Create properties representing the location names
    StringProperty longName = new SimpleStringProperty(locationName.getLongName());
    ObjectProperty<LocationName.LocationType> type =
        new SimpleObjectProperty<>(locationName.getLocationType());
    StringProperty shortName = new SimpleStringProperty(locationName.getShortName());

    // Runnable that should be called to validate input parameters
    Runnable onFieldChange =
        () -> {
          errorText.setText(""); // Clear the error text

          // If there are empty fields
          if (longName.get().equals("") || shortName.get().equals("")) {
            errorText.setText("Fill in all fields before submitting!"); // Show that
          }
        };

    // Error listener in the long name
    longName.addListener(
        (observable, oldValue, newValue) -> {
          onFieldChange.run();
        });

    // Error name in the short name
    shortName.addListener(
        (observable, oldValue, newValue) -> {
          onFieldChange.run();
        });
    // Set the text manually cuz MaterialFX is mean
    locationTypeField
        .valueProperty()
        .set(LocationName.LocationType.valueOf(locationName.getLocationType().name()));

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
          // Original location name for the deletion handler
          LocationName oldName =
              session
                  .createQuery(
                      "FROM LocationName WHERE longName = :originalName", LocationName.class)
                  .setParameter("originalName", originalName[0])
                  .uniqueResult();

          // Do the deletion in the DB
          session
              .createMutationQuery("DELETE FROM LocationName WHERE longName = :originalName")
              .setParameter("originalName", originalName[0])
              .executeUpdate();

          session.flush();
          handleDelete.accept(oldName); // Run the deletion handler
        });

    saveButton.setOnAction(
        event -> {
          onFieldChange.run(); // Run the validator

          // If that failed, we have an error message
          if (!errorText.getText().equals("")) {
            return; // So block the return value
          }

          // Check to make sure that the location is unique. Uses a query because session.find
          // does not play nice with changing long names without committing.
          // Done here and not every time a field is changed to make validation faster
          if (!longName.get().equals(originalName[0])
              && session
                      .createQuery(
                          "FROM LocationName WHERE longName = :newName", LocationName.class)
                      .setParameter("newName", longName.get())
                      .uniqueResult()
                  != null) {

            // Show an error
            errorText.setText("A location with that name already exists! No changes saved.");
            return; // Short-circuit, prevent submit
          }

          LocationName oldLocation; // The old location to pass into the handler

          if (isNewLocation) {
            oldLocation = null; // There is no old location if this is new

            // If it's new, we can just persist
            session.persist(new LocationName(longName.get(), type.get(), shortName.get()));
          } else {
            // Old name, for the update handler
            oldLocation =
                session
                    .createQuery("FROM LocationName WHERE longName = :oldName", LocationName.class)
                    .setParameter("oldName", originalName[0])
                    .getSingleResult();

            // Run a query that updates the location name to be the new values, searching by the
            // long
            // name PK
            session
                .createMutationQuery(
                    "UPDATE LocationName SET "
                        + "longName = :newLongName, locationType = :newType, shortName = :newShortName "
                        + "WHERE longName = :originalName")
                .setParameter("newLongName", longName.get())
                .setParameter("newType", type.get())
                .setParameter("newShortName", shortName.get())
                .setParameter("originalName", originalName[0])
                .executeUpdate();
          }

          session.flush(); // Force things to persist

          // Get the new location based on the above
          originalName[0] = longName.get(); // Update the name
          LocationName newLocation =
              session
                  .createQuery(
                      "FROM LocationName WHERE longName = :newLongName", LocationName.class)
                  .setParameter("newLongName", longName.get())
                  .getSingleResult();
          session.refresh(newLocation); // Update the new location object

          // Run the updater
          onUpdate.accept(oldLocation, newLocation); // Accept the new location reference for update
        });
  }

  void setDeleteButtonText(@NonNull String text) {
    deleteButton.setText(text);
  }
}
