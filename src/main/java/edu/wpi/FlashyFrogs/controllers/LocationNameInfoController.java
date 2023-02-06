package edu.wpi.FlashyFrogs.controllers;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;

public class LocationNameInfoController {
  @FXML private MFXTextField locationNameField;
  @FXML private MFXTextField locationTypeField;
  @FXML private MFXTextField shortNameField;

  /**
   * Sets the location name and updates the associated
   *
   * @param locationName the location name to set everything to use
   */
  public void setLocationName(LocationName locationName) {
    locationNameField.setText(locationName.getLongName());
    locationTypeField.setText(locationName.getLocationType().name());
    shortNameField.setText(locationName.getShortName());
  }
}
