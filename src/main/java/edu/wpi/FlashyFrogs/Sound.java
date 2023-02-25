package edu.wpi.FlashyFrogs;

import java.io.File;
import javafx.scene.media.AudioClip;
import lombok.NonNull;

public enum Sound {
  ERROR(
      new AudioClip(
          new File("src/main/resources/edu/wpi/FlashyFrogs/Sounds/Error.wav").toURI().toString()));

  @NonNull public final AudioClip audioClip;

  /**
   * Creates the sound from an audio clip
   *
   * @param audioClip The Audio Clip object
   */
  Sound(AudioClip audioClip) {
    this.audioClip = audioClip;
  }

  /**
   * Play the sound effect
   */
  public void play() {
    if (Fapp.isSfxOn()) {
      this.audioClip.play();
    }
  }
}
