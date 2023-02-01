package edu.wpi.FlashyFrogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileData {
  private List<File> files;

  public FileData() {
    this.files = new ArrayList<>();
  }

  public void addFile(File file) {
    this.files.add(file);
  }

  public boolean correctNumOfFiles() {
    return files.size() == 4;
  }

  public List<File> getFiles() {
    return files;
  }
}
