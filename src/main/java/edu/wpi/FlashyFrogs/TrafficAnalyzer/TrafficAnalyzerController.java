package edu.wpi.FlashyFrogs.TrafficAnalyzer;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import org.controlsfx.control.tableview2.TableView2;

public class TrafficAnalyzerController {
  @FXML private DatePicker viewDate;
  @FXML private TextField requestWeighting;
  @FXML private TableView2 weightTable;
  @FXML private TableColumn mapItemColumn;
  @FXML private TableColumn usesColumn;
}
