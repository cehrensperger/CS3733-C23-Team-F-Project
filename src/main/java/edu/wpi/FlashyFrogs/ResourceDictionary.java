package edu.wpi.FlashyFrogs;

import java.util.Objects;
import javafx.scene.image.Image;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * Dictionary for devs to store resources that may be needed at runtime. Essentially allows
 * pre-loading of images, so that faster selection can happen at runtime. If an image can be used
 * directly in an FXML file, this is unnecessary as JFX will preload it (or load it) quickly anyway.
 * This is useful for runtime switching, where that cannot happen
 */
@GeneratedExclusion
public enum ResourceDictionary {
  DRAG_SVG("MapEditor/dragSVG.png"),
  L2("floors/L2.png"), // L2 hospital map image
  L1("floors/L1.png"), // , // L1 hospital map image
  // G("floors/G.png"), // Ground floor hospital map image
  ONE("floors/one.png"), // Floor one map image
  TWO("floors/two.png"), // , // Floor two map image
  THREE("floors/three.png"); // Floor three map image

  @NonNull public final Image resource; // The resource that is being stored in the dictionary

  /**
   * Constructs a new static resource
   *
   * @param URI the URI to use to load the resource. This can be a file path, resource path, or URL,
   *     as per image spec
   */
  @SneakyThrows
  ResourceDictionary(String URI) {
    resource = new Image(Objects.requireNonNull(Fapp.class.getResource(URI)).openStream());
  }
}
