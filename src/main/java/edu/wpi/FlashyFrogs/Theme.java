package edu.wpi.FlashyFrogs;

import java.net.URL;
import java.util.Objects;
import lombok.NonNull;

/** Enumerated type for themes, including the resources associated with them */
public enum Theme {
  LIGHT_THEME("Light Theme", Objects.requireNonNull(Theme.class.getResource("views/NewStyle.css"))),
  DARK_THEME("Dark Theme", Objects.requireNonNull(Theme.class.getResource("views/darkMode.css")));

  @NonNull public final String description; // Description
  @NonNull public final URL resource; // Resource

  /**
   * Creates the theme from the description and resource
   *
   * @param description the description (short) of the theme
   * @param resource the resource
   */
  Theme(@NonNull String description, @NonNull URL resource) {
    this.description = description; // Set the description of the theme
    this.resource = resource; // The resource
  }
}
