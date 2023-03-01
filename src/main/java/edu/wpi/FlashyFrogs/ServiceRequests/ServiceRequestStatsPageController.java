package edu.wpi.FlashyFrogs.ServiceRequests;

import static edu.wpi.FlashyFrogs.DBConnection.CONNECTION;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.*;
import edu.wpi.FlashyFrogs.controllers.IController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.controlsfx.control.tableview2.FilteredTableColumn;
import org.controlsfx.control.tableview2.FilteredTableView;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupFilter;
import org.controlsfx.control.tableview2.filter.popupfilter.PopupStringFilter;
import org.hibernate.Session;

public class ServiceRequestStatsPageController implements IController {

  @FXML private AnchorPane anchorPane;
  @FXML private AnchorPane statsAnchorPane;
  @FXML private VBox sideBar;
  @FXML MFXButton downloadButton;

  @FXML protected FilteredTableColumn<ServiceRequest, String> requestTypeCol;
  @FXML protected FilteredTableColumn<ServiceRequest, Long> requestIDCol;
  @FXML protected FilteredTableColumn<ServiceRequest, HospitalUser> initEmpCol;
  @FXML protected FilteredTableColumn<ServiceRequest, HospitalUser> assignedEmpCol;
  @FXML protected FilteredTableColumn<ServiceRequest, Date> subDateCol;
  @FXML protected FilteredTableColumn<ServiceRequest, ServiceRequest.Urgency> urgencyCol;
  @FXML protected FilteredTableColumn<ServiceRequest, LocationName> locationCol;
  @FXML protected FilteredTableColumn<ServiceRequest, ServiceRequest.Status> statusCol;

  @FXML protected FilteredTableView<ServiceRequest> requestTable;
  @FXML private ComboBox<String> graphTypeComboBox;

  public void initialize() {

    // initialize filtered table
    // need to be the names of the fields
    requestTypeCol.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().getRequestType()));
    // requestTypeCol.setReorderable(false);
    requestIDCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getId()));
    // requestIDCol.setReorderable(false);
    initEmpCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getEmp()));
    // initEmpCol.setReorderable(false);
    assignedEmpCol.setCellValueFactory(
        p -> new SimpleObjectProperty<>(p.getValue().getAssignedEmp()));
    // assignedEmpCol.setReorderable(false);
    subDateCol.setCellValueFactory(
        p -> new SimpleObjectProperty<>(p.getValue().getDateOfSubmission()));
    // subDateCol.setReorderable(false);
    urgencyCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getUrgency()));
    // urgencyCol.setReorderable(false);
    locationCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getLocation()));
    // locationCol.setReorderable(false);
    statusCol.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getStatus()));
    // statusCol.setReorderable(false);

    PopupFilter<ServiceRequest, String> popupTypeFilter = new PopupStringFilter<>(requestTypeCol);
    requestTypeCol.setOnFilterAction(e -> popupTypeFilter.showPopup());
    PopupFilter<ServiceRequest, Long> popupIDFilter = new PopupStringFilter<>(requestIDCol);
    requestIDCol.setOnFilterAction(e -> popupIDFilter.showPopup());
    PopupFilter<ServiceRequest, HospitalUser> popupEmpFilter = new PopupStringFilter<>(initEmpCol);
    initEmpCol.setOnFilterAction(e -> popupEmpFilter.showPopup());
    PopupFilter<ServiceRequest, HospitalUser> popupAssignedFilter =
        new PopupStringFilter<>(assignedEmpCol);
    assignedEmpCol.setOnFilterAction(e -> popupAssignedFilter.showPopup());
    PopupFilter<ServiceRequest, Date> popupSubDateFilter = new PopupStringFilter<>(subDateCol);
    subDateCol.setOnFilterAction(e -> popupSubDateFilter.showPopup());
    PopupFilter<ServiceRequest, ServiceRequest.Urgency> popupUrgencyFilter =
        new PopupStringFilter<>(urgencyCol);
    urgencyCol.setOnFilterAction(e -> popupUrgencyFilter.showPopup());
    PopupFilter<ServiceRequest, LocationName> popupLocationFilter =
        new PopupStringFilter<>(locationCol);
    locationCol.setOnFilterAction(e -> popupLocationFilter.showPopup());
    PopupFilter<ServiceRequest, ServiceRequest.Status> popupStatusFilter =
        new PopupStringFilter<>(statusCol);
    statusCol.setOnFilterAction(e -> popupStatusFilter.showPopup());
    //
    //    // FILL TABLES
    List<ServiceRequest> serviceRequests;

    Session session = CONNECTION.getSessionFactory().openSession();

    serviceRequests =
        session.createQuery("SELECT s FROM ServiceRequest s", ServiceRequest.class).getResultList();

    session.close();
    ObservableList<ServiceRequest> srList = FXCollections.observableList(serviceRequests);
    //    //    srList.clear();
    //    //    srList.add(
    //    //        new Sanitation(
    //    //            Sanitation.SanitationType.MOPPING,
    //    //            new HospitalUser(
    //    //                "sd",
    //    //                "asdf",
    //    //                "asdf",
    //    //                HospitalUser.EmployeeType.ADMIN,
    //    //                new Department("asdfasdf", "vfggr")),
    //    //            new Date(),
    //    //            new Date(),
    //    //            ServiceRequest.Urgency.VERY_URGENT,
    //    //            new LocationName("asdfasdf", LocationName.LocationType.CONF, "asdfasdf"),
    //    //            false,
    //    //            Sanitation.BiohazardLevel.BSL1,
    //    //            "safdawfsgwrgfgfsfgdfg"));
    //    // FilteredTableView.configureForFiltering(requestTable, srList);
    requestTable.setItems(srList);
    //    // get rows of requestTable
    //    //    ObservableList<TablePosition> selectedCells =
    //    // requestTable.getSelectionModel().getSelectedCells();
    //    //    for (TablePosition pos : selectedCells) {
    //    //      int row = pos.getRow();
    //    //      ServiceRequest item = requestTable.getItems().get(row);
    //    //      System.out.println(item);
    //    //    }
    //
    //    srList.remove(srList.size() - 1);
    //
    //    // requestTable.refresh();
    ArrayList<String> graphTypes = new ArrayList<>();
    graphTypes.add("Total Requests by Type");
    graphTypes.add("Total Completed Requests by Type");
    graphTypes.add("Total Requests by Status");
    graphTypes.add("Completed vs Incomplete Service Requests Pie Chart");
    ObservableList<String> observableGraphTypes = FXCollections.observableList(graphTypes);
    graphTypeComboBox.setItems(observableGraphTypes);
  }

  // methods for drop down
  // TODO: add methods for each drop down item
  // each item should change the chart to show the data for that item
  // it should do this by removing any charts still in the scene graph and adding a new one

  public void handleAV(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "AudioVisualService");
  }

  public void handleEquipment(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "EquipmentTransport");
  }

  public void handleReligious(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ReligiousService");
  }

  public void handleMedicine(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "MedicineDeliveryService");
  }

  public void handleIT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "ComputerService");
  }

  public void handleIPT(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "TransportService");
  }

  public void handleSanitation(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SanitationService");
  }

  public void handleSecurity(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "SecurityService");
  }

  public void handleCredits(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("ServiceRequests", "Credits");
  }

  @Override
  public void onClose() {}

  @Override
  public void help() {}

  /**
   * Sets the chart to show the data for the selected graph type
   *
   * @param actionEvent
   */
  public void handleGraphTypeComboBox(ActionEvent actionEvent) throws IOException {

    Label label = new Label();

    // get all service requests from the table
    List<ServiceRequest> allServiceRequests = requestTable.getItems();
    if (graphTypeComboBox.getValue().equals("Total Requests by Type")) {

      Axis<String> xAxis = new CategoryAxis();
      Axis<Number> yAxis = new NumberAxis();
      BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
      // remove legend from chart
      chart.setLegendVisible(false);

      XYChart.Series<String, Number> series = new XYChart.Series<>();

      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Security",
                  allServiceRequests.stream().filter(s -> s instanceof Security).count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Sanitation",
                  allServiceRequests.stream().filter(s -> s instanceof Sanitation).count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Transport",
                  allServiceRequests.stream().filter(s -> s instanceof InternalTransport).count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "IT",
                  allServiceRequests.stream().filter(s -> s instanceof ComputerService).count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Audio/Visual",
                  allServiceRequests.stream().filter(s -> s instanceof AudioVisual).count()));

      chart.getData().add(series);
      AnchorPane.setTopAnchor(chart, 200.0);
      AnchorPane.setBottomAnchor(chart, 200.0);
      AnchorPane.setLeftAnchor(chart, sideBar.getWidth() + 50);
      AnchorPane.setRightAnchor(chart, 200.0);
      // set bar color
      chart
          .getData()
          .forEach(
              s -> {
                s.getData()
                    .forEach(
                        data -> {
                          data.getNode()
                              .setStyle(
                                  "-fx-bar-fill: #012D5A; "
                                      + "-fx-border-color: #000000; "
                                      + "-fx-border-width: 3px 3px 0px 3px;");
                        });
              });

      // get total of values in chart

      attachLabel(chart, series);

      replaceChart(chart);

      downloadButton.setOnAction(
          e -> {
            try {
              writeToExcelFile(series, graphTypeComboBox.getValue());
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          });

    } else if (graphTypeComboBox.getValue().equals("Total Completed Requests by Type")) {

      Axis<String> xAxis = new CategoryAxis();
      Axis<Number> yAxis = new NumberAxis();
      BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
      // make axis integer based
      // yAxis.setAutoRanging(false);

      XYChart.Series<String, Number> series = new XYChart.Series<>();
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Security",
                  allServiceRequests.stream()
                      .filter(
                          s ->
                              (s instanceof Security
                                  && s.getStatus().equals(ServiceRequest.Status.DONE)))
                      .count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Sanitation",
                  allServiceRequests.stream()
                      .filter(
                          s ->
                              (s instanceof Sanitation
                                  && s.getStatus().equals(ServiceRequest.Status.DONE)))
                      .count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Transport",
                  allServiceRequests.stream()
                      .filter(
                          s ->
                              (s instanceof InternalTransport
                                  && s.getStatus().equals(ServiceRequest.Status.DONE)))
                      .count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "IT",
                  allServiceRequests.stream()
                      .filter(
                          s ->
                              (s instanceof ComputerService
                                  && s.getStatus().equals(ServiceRequest.Status.DONE)))
                      .count()));
      series
          .getData()
          .add(
              new XYChart.Data<>(
                  "Audio/Visual",
                  allServiceRequests.stream()
                      .filter(
                          s ->
                              (s instanceof AudioVisual
                                  && s.getStatus().equals(ServiceRequest.Status.DONE)))
                      .count()));

      chart.getData().add(series);
      AnchorPane.setTopAnchor(chart, 200.0);
      AnchorPane.setBottomAnchor(chart, 200.0);
      AnchorPane.setLeftAnchor(chart, sideBar.getWidth() + 50);
      AnchorPane.setRightAnchor(chart, 200.0);

      chart
          .getData()
          .forEach(
              s -> {
                s.getData()
                    .forEach(
                        data -> {
                          data.getNode()
                              .setStyle(
                                  "-fx-bar-fill: #012D5A; "
                                      + "-fx-border-color: #000000; "
                                      + "-fx-border-width: 3px 3px 0px 3px;");
                        });
              });

      chart.setLegendVisible(false);
      attachLabel(chart, series);
      replaceChart(chart);
      downloadButton.setOnAction(
          e -> {
            try {
              writeToExcelFile(series, graphTypeComboBox.getValue());
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          });

    } else if (graphTypeComboBox.getValue().equals("Total Requests by Status")) {

      // set bar colors to be thicker

      Axis<String> xAxis = new CategoryAxis();
      NumberAxis yAxis = new NumberAxis();
      BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
      XYChart.Series<String, Number> series = new XYChart.Series<>();

      for (ServiceRequest.Status status : ServiceRequest.Status.values()) {

        series
            .getData()
            .add(
                new XYChart.Data<>(
                    status.toString(),
                    allServiceRequests.stream().filter(s -> s.getStatus().equals(status)).count()));
      }

      chart.getData().add(series);
      AnchorPane.setTopAnchor(chart, 200.0);
      AnchorPane.setBottomAnchor(chart, 200.0);
      AnchorPane.setLeftAnchor(chart, sideBar.getWidth() + 50);
      AnchorPane.setRightAnchor(chart, 200.0);

      // set bar color
      chart
          .getData()
          .forEach(
              s -> {
                s.getData()
                    .forEach(
                        data -> {
                          data.getNode()
                              .setStyle(
                                  "-fx-bar-fill: #012D5A; "
                                      + "-fx-border-color: #000000; "
                                      + "-fx-border-width: 3px 3px 0px 3px;");
                        });
              });

      chart.setLegendVisible(false);
      attachLabel(chart, series);
      // remove old chart and add new chart with same layout
      replaceChart(chart);
      downloadButton.setOnAction(
          e -> {
            try {
              writeToExcelFile(series, graphTypeComboBox.getValue());
            } catch (IOException ioException) {
              ioException.printStackTrace();
            }
          });
    } else if (graphTypeComboBox
        .getValue()
        .equals("Completed vs Incomplete Service Requests Pie Chart")) {
      PieChart chart = new PieChart();

      // fill pie chart with data from service requests
      ObservableList<PieChart.Data> pieChartData =
          FXCollections.observableArrayList(
              new PieChart.Data(
                  "Completed",
                  allServiceRequests.stream()
                      .filter(s -> s.getStatus().equals(ServiceRequest.Status.DONE))
                      .count()),
              new PieChart.Data(
                  "Incomplete",
                  allServiceRequests.stream()
                      .filter(
                          s ->
                              s.getStatus().equals(ServiceRequest.Status.BLANK)
                                  || s.getStatus().equals(ServiceRequest.Status.PROCESSING))
                      .count()));

      chart.setData(pieChartData);
      // set pie chart colors
      pieChartData.forEach(
          data -> {
            if (data.getName().equals("Completed")) {
              data.getNode().setStyle("-fx-pie-color: #012D5A;");
            } else {
              data.getNode().setStyle("-fx-pie-color: #F6BD38;");
            }
          });

      // remove legend
      chart.setLegendVisible(false);

      // get total
      double total = chart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();

      // show percentages on mouse moved along with the total in parentheses
      chart
          .getData()
          .forEach(
              data -> {
                data.getNode()
                    .setOnMouseEntered(
                        event -> {
                          // add label to scene
                          anchorPane.getChildren().add(label);
                        });
                data.getNode()
                    .setOnMouseMoved(
                        // make a label that shows the percentage and total and follows the mouse
                        // around
                        event -> {
                          label.setText(
                              String.format(
                                  "%.1f%% (%d)",
                                  ((data.getPieValue() / total) * 100), (int) data.getPieValue()));
                          label.setStyle(
                              "-fx-background-color: #D1D1D1; -fx-text-fill: #000000; -fx-font-size: 20px;");
                          label.setPadding(new Insets(5, 10, 5, 10));
                          // prevent label from going off screen

                          if (event.getScreenY() + label.getHeight()
                              <= anchorPane.getHeight() - 5) {
                            // label.setLayoutY(event.getSceneY() - label.getHeight());
                            label.setLayoutY(event.getScreenY());
                          }
                          label.setLayoutX(event.getScreenX());
                          // label.setLayoutY(event.getSceneY());

                          label.toFront();
                        });

                // anchorPane.getChildren().add(label);
                // remove the label when the mouse is no longer over the pie chart
                data.getNode()
                    .setOnMouseExited(
                        event -> {
                          anchorPane.getChildren().remove(label);
                        });
              });

      AnchorPane.setTopAnchor(chart, 200.0);
      AnchorPane.setBottomAnchor(chart, 200.0);
      AnchorPane.setLeftAnchor(chart, sideBar.getWidth() + 50);
      AnchorPane.setRightAnchor(chart, 200.0);

      // remove old chart and add new chart with same layout
      replaceChart(chart);
    }
  }

  private static void writeToExcelFile(XYChart.Series<String, Number> series, String title)
      throws IOException {
    // download chart to excel document
    // output stream for excel file

    // check if file already exists and increment the number at the end of the file name until it
    // doesn't exist
    int j = 1;
    while (new File("workbook" + j + ".xls").exists()) {
      j++;
    }

    FileOutputStream out = new FileOutputStream("workbook" + j + ".xls");

    Workbook workbook = new HSSFWorkbook();
    Sheet sheet = workbook.createSheet("Service Requests");
    Row row = sheet.createRow(0);
    // create title row
    row.createCell(0).setCellValue(title);

    // fill rows with data in chart
    for (int i = 0; i < series.getData().size(); i++) {
      row = sheet.createRow(i + 2);
      row.createCell(0).setCellValue(series.getData().get(i).getXValue());
      row.createCell(1).setCellValue(series.getData().get(i).getYValue().intValue());

      // System.out.println(series.getData().get(i).getXValue());
      // System.out.println(series.getData().get(i).getYValue().intValue());
    }

    // write to excel file
    workbook.write(out);
    out.close();
  }

  private void attachLabel(BarChart<String, Number> chart, XYChart.Series<String, Number> series) {
    double total =
        series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).sum();
    Label label = new Label();
    label.setLayoutX(chart.getLayoutX());
    label.setLayoutY(chart.getLayoutY());
    label.setVisible(false);
    anchorPane.getChildren().add(label);
    // System.out.println(anchorPane.getHeight());

    // put totals above bar

    chart
        .getData()
        .forEach(
            s -> {
              s.getData()
                  .forEach(
                      data -> {
                        data.getNode()
                            .setOnMouseEntered(
                                event -> {
                                  //
                                  // System.out.println(anchorPane.getHeight());
                                  if (event.getScreenY() + label.getHeight()
                                      <= anchorPane.getHeight() - 5) {
                                    // label.setLayoutY(event.getSceneY() - label.getHeight());
                                    label.setLayoutY(500);
                                  } else {
                                    label.setLayoutY(500);
                                    //
                                    // System.out.println(anchorPane.getHeight());
                                  }
                                  label.setMouseTransparent(true);
                                  // add label to scene
                                  label.setText(
                                      String.format(
                                          "%.1f%% (%d)",
                                          ((data.getYValue().doubleValue() / total) * 100),
                                          (int) data.getYValue().intValue()));
                                  label.setStyle(
                                      "-fx-background-color: #D1D1D1; -fx-text-fill: #000000; -fx-font-size: 20px;");
                                  label.setPadding(new Insets(5, 10, 5, 10));
                                  // prevent label from going off screen

                                  label.setLayoutX(event.getScreenX());
                                  // label.setLayoutY(event.getSceneY());

                                  label.toFront();
                                  label.setVisible(true);
                                  //
                                  // System.out.println(anchorPane.getHeight());
                                });
                        data.getNode()
                            .setOnMouseMoved(
                                // make a label that shows the percentage and total and follows
                                // the mouse
                                // around
                                event -> {
                                  if (event.getScreenY() + label.getHeight()
                                      <= anchorPane.getHeight() - 5) {
                                    label.setLayoutY(event.getSceneY() - label.getHeight());
                                    // label.setLayoutY(event.getScreenY());
                                  } else {
                                    label.setLayoutY(event.getScreenY() - label.getHeight() - 20);
                                  }
                                  label.setMouseTransparent(true);
                                  label.setText(
                                      String.format(
                                          "%.1f%% (%d)",
                                          ((data.getYValue().doubleValue() / total) * 100),
                                          (int) data.getYValue().intValue()));
                                  label.setStyle(
                                      "-fx-background-color: #D1D1D1; -fx-text-fill: #000000; -fx-font-size: 20px;");
                                  label.setPadding(new Insets(5, 10, 5, 10));
                                  // prevent label from going off screen

                                  label.setLayoutX(event.getScreenX());
                                  // label.setLayoutY(event.getSceneY());

                                  label.toFront();
                                });

                        data.getNode()
                            .setOnMouseExited(
                                event -> {
                                  label.setVisible(false);
                                });
                      });
            });
  }

  /**
   * Removes old chart(and all charts currently in the anchor pane) and adds new chart with same
   * layout
   *
   * @param chart new chart to be added
   */
  private void replaceChart(Chart chart) {
    for (Node node : anchorPane.getChildren()) {
      if (node instanceof Chart) {
        Chart oldChart = (Chart) node;
        anchorPane.getChildren().remove(node);
        AnchorPane.setTopAnchor(chart, AnchorPane.getTopAnchor(oldChart));
        AnchorPane.setBottomAnchor(chart, AnchorPane.getBottomAnchor(oldChart));
        AnchorPane.setLeftAnchor(chart, AnchorPane.getLeftAnchor(oldChart));
        AnchorPane.setRightAnchor(chart, AnchorPane.getRightAnchor(oldChart));

        chart.setLayoutX(oldChart.getLayoutX());
        chart.setLayoutY(oldChart.getLayoutY());
        chart.setMaxWidth(oldChart.getMaxWidth());
        chart.setMaxHeight(oldChart.getMaxHeight());
        chart.setMinWidth(oldChart.getMinWidth());
        chart.setMinHeight(oldChart.getMinHeight());
        chart.setPrefWidth(oldChart.getPrefWidth());
        chart.setPrefHeight(oldChart.getPrefHeight());
        chart.setTranslateX(oldChart.getTranslateX());
        chart.setTranslateY(oldChart.getTranslateY());
        chart.setRotate(oldChart.getRotate());
        chart.setScaleX(oldChart.getScaleX());
        chart.setScaleY(oldChart.getScaleY());
        chart.setOpacity(oldChart.getOpacity());
        chart.setBlendMode(oldChart.getBlendMode());
        chart.setCache(oldChart.isCache());
        chart.setCacheHint(oldChart.getCacheHint());
        chart.setCacheShape(oldChart.isCacheShape());
        chart.setDepthTest(oldChart.getDepthTest());
        chart.setEffect(oldChart.getEffect());
        chart.setMouseTransparent(oldChart.isMouseTransparent());
        chart.setPickOnBounds(oldChart.isPickOnBounds());
        chart.setRotate(oldChart.getRotate());
        chart.setScaleX(oldChart.getScaleX());
        chart.setScaleY(oldChart.getScaleY());
        chart.setTranslateX(oldChart.getTranslateX());
        chart.setTranslateY(oldChart.getTranslateY());
        anchorPane.getChildren().add(chart);
        break;
      }
    }
  }

  /**
   * Removes old chart and adds new chart with same layout
   *
   * @param oldChart chart to be removed
   * @param newChart new chart to be added
   */
  private void replaceChart(Chart oldChart, Chart newChart) {
    for (Node node : anchorPane.getChildren()) {
      if (node.equals(oldChart)) {
        anchorPane.getChildren().remove(node);
        AnchorPane.setTopAnchor(newChart, AnchorPane.getTopAnchor(oldChart));
        AnchorPane.setBottomAnchor(newChart, AnchorPane.getBottomAnchor(oldChart));
        AnchorPane.setLeftAnchor(newChart, AnchorPane.getLeftAnchor(oldChart));
        AnchorPane.setRightAnchor(newChart, AnchorPane.getRightAnchor(oldChart));

        newChart.setLayoutX(oldChart.getLayoutX());
        newChart.setLayoutY(oldChart.getLayoutY());
        newChart.setMaxWidth(oldChart.getMaxWidth());
        newChart.setMaxHeight(oldChart.getMaxHeight());
        newChart.setMinWidth(oldChart.getMinWidth());
        newChart.setMinHeight(oldChart.getMinHeight());
        newChart.setPrefWidth(oldChart.getPrefWidth());
        newChart.setPrefHeight(oldChart.getPrefHeight());
        newChart.setTranslateX(oldChart.getTranslateX());
        newChart.setTranslateY(oldChart.getTranslateY());
        newChart.setRotate(oldChart.getRotate());
        newChart.setScaleX(oldChart.getScaleX());
        newChart.setScaleY(oldChart.getScaleY());
        newChart.setOpacity(oldChart.getOpacity());
        newChart.setBlendMode(oldChart.getBlendMode());
        newChart.setCache(oldChart.isCache());
        newChart.setCacheHint(oldChart.getCacheHint());
        newChart.setCacheShape(oldChart.isCacheShape());
        newChart.setDepthTest(oldChart.getDepthTest());
        newChart.setEffect(oldChart.getEffect());
        newChart.setMouseTransparent(oldChart.isMouseTransparent());
        newChart.setPickOnBounds(oldChart.isPickOnBounds());
        newChart.setRotate(oldChart.getRotate());
        newChart.setScaleX(oldChart.getScaleX());
        newChart.setScaleY(oldChart.getScaleY());
        newChart.setTranslateX(oldChart.getTranslateX());
        newChart.setTranslateY(oldChart.getTranslateY());
        anchorPane.getChildren().add(newChart);
        break;
      }
    }
  }
}
