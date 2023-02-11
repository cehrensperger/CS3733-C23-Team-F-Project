package edu.wpi.FlashyFrogs;

import java.io.File;

public class FileData {
  private File nodesFile;
  private File edgesFile;
  private File locationsFile;
  private File movesFile;

  public FileData() {}

  public File getNodesFile() {
    return nodesFile;
  }

  public File getEdgesFile() {
    return edgesFile;
  }

  public File getLocationsFile() {
    return locationsFile;
  }

  public File getMovesFile() {
    return movesFile;
  }

  public void setNodesFile(File nodesFile) {
    this.nodesFile = nodesFile;
  }

  public void setEdgesFile(File edgesFile) {
    this.edgesFile = edgesFile;
  }

  public void setLocationsFile(File locationsFile) {
    this.locationsFile = locationsFile;
  }

  public void setMovesFile(File movesFile) {
    this.movesFile = movesFile;
  }

  public boolean allFilesChosen() {
    return nodesFile != null && edgesFile != null && locationsFile != null && movesFile != null;
  }
}
