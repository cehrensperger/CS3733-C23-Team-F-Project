package edu.wpi.FlashyFrogs;

import java.io.File;
import lombok.Getter;
import lombok.Setter;

public class FileData {
  @Getter @Setter private File nodesFile;
  @Getter @Setter private File edgesFile;
  @Getter @Setter private File locationsFile;
  @Getter @Setter private File movesFile;

  public FileData() {}

  public boolean allFilesChosen() {
    return nodesFile != null && edgesFile != null && locationsFile != null && movesFile != null;
  }
}
