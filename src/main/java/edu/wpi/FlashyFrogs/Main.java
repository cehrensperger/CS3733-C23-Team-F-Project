package edu.wpi.FlashyFrogs;

public class Main {

  public static void main(String[] args) {
    DBConnection.CONNECTION.connect(); // Connect the DB
    Fapp.launch(Fapp.class, args); // Launch the app
    DBConnection.CONNECTION.disconnect(); // Disconnect the DB
  }
}
