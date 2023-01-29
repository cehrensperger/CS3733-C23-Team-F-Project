package edu.wpi.FlashyFrogs.navigation;

public enum Screen {
  ROOT("views/RequestsHome.fxml"),
  HOME("views/RequestsHome.fxml"),
  SERVICE_REQUEST("views/ServiceRequest.fxml");

  private final String filename;

  Screen(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }
}
