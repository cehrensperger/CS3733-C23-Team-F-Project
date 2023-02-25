package edu.wpi.FlashyFrogs;

import java.util.Objects;
import javafx.scene.media.AudioClip;
import lombok.NonNull;

public enum Sound {
  ERROR(
      new AudioClip(
          Objects.requireNonNull(Sound.class.getResource("Sounds/Error.wav")).toString()));

  @NonNull public final AudioClip audioClip;

  /**
   * Creates the sound from an audio clip
   *
   * @param audioClip The Audio Clip object
   */
  Sound(AudioClip audioClip) {
    this.audioClip = audioClip;
  }

  /** Play the sound effect */
  public void play() {
    if (Fapp.isSfxOn()) {
      this.audioClip.play();
    }
  }
}
