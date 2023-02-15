package edu.wpi.FlashyFrogs;

import lombok.SneakyThrows;

@GeneratedExclusion
public class Main {

  @SneakyThrows
  public static void main(String[] args) {
    DBConnection.CONNECTION.connect(); // Connect the DB
    Fapp.launch(Fapp.class, args); // Launch the app
    DBConnection.CONNECTION.disconnect(); // Disconnect the DB
  }
}
