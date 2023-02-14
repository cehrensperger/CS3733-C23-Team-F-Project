package edu.wpi.FlashyFrogs;

@GeneratedExclusion
public class Main {

  public static void main(String[] args) {
    DBConnection.CONNECTION.connect(); // Connect the DB
    ResourceDictionary[] resources =
        ResourceDictionary.values(); // Pre-cache the dictionary, for performance
    Fapp.launch(Fapp.class, args); // Launch the app
    DBConnection.CONNECTION.disconnect(); // Disconnect the DB
  }
}
