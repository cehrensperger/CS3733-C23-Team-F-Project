package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;

public class DBTableController implements Initializable {
  // Move Table Stuff
  public TableView<Move> moveTable;
  public TableColumn<Move, Node> moveColId;
  public TableColumn<Move, LocationName> moveColLongName;
  public TableColumn<Move, Date> moveColTimestamp;
  public TextField moveTxtID;
  public TextField moveTxtLongName;
  public Button moveBtnadd;
  // Node Table Stuff
  public TableView<Node> nodeTable;
  public TableColumn<Node, String> nodeId;
  public TableColumn nodeX;
  public TableColumn nodeY;
  public TableColumn<Node, Node.Floor> nodeFloor;
  public TableColumn<Node, String> nodeBuilding;
  // Edge Table Stuff
  public TableView<Edge> edgeTable;
  public TableColumn<Edge, Node> edgeNode1;
  public TableColumn<Edge, Node> edgeNode2;
  // Location Table Stuff
  public TableView<LocationName> locationTable;
  public TableColumn<LocationName, String> lnLongName;
  public TableColumn<LocationName, String> lnShortName;
  public TableColumn<LocationName, LocationName.LocationType> lnLocationType;
  @FXML private MFXButton back;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // set columns movetable
    moveColId.setCellValueFactory(new PropertyValueFactory<>("node"));
    moveColLongName.setCellValueFactory(new PropertyValueFactory<>("location"));
    moveColTimestamp.setCellValueFactory(new PropertyValueFactory<>("moveDate"));
    // set collumns Node
    nodeId.setCellValueFactory(new PropertyValueFactory<>("id"));
    nodeX.setCellValueFactory(new PropertyValueFactory<>("xCoord"));
    nodeY.setCellValueFactory(new PropertyValueFactory<>("yCoord"));
    nodeFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
    nodeBuilding.setCellValueFactory(new PropertyValueFactory<>("building"));
    // set collumns Edge
    edgeNode1.setCellValueFactory(new PropertyValueFactory<>("node1"));
    edgeNode2.setCellValueFactory(new PropertyValueFactory<>("node2"));
    // set collumns LocationName
    lnLongName.setCellValueFactory(new PropertyValueFactory<>("longName"));
    lnShortName.setCellValueFactory(new PropertyValueFactory<>("shortName"));
    lnLocationType.setCellValueFactory(new PropertyValueFactory<>("locationType"));

    // Create all the Tables
    createLnTable();
    createEdgeTable();
    createNodeTable();
    createMoveTable();
  }

  /**
   * @param actionEvent adds a new move object to the Tableview and Database from the input from UI
   * @throws Exception
   */
  @FXML
  public void addMove(javafx.event.ActionEvent actionEvent) throws Exception {
    Session session = factory.openSession();
    String colID = moveTxtID.getText();
    String colLongName = moveTxtLongName.getText();

    Node node = session.find(Node.class, colID);
    System.out.println(node.toString());
    LocationName locationName = session.find(LocationName.class, colLongName);
    Move move = new Move(node, locationName, Date.from(Instant.now()));
    System.out.println(move);

    // add to database
    session.beginTransaction();
    session.persist(move);
    session.getTransaction().commit();
    session.close();
    // add info to the table
    createMoveTable();
  }

  public void createMoveTable() {
    // open session
    Session ses = factory.openSession();
    List<Move> moveObjects =
        ses.createQuery("SELECT s FROM Move s", Move.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Move> moveObservableList =
        FXCollections.observableList(moveObjects); // convert list to ObservableList
    ses.close();
    moveTable
        .getItems()
        .addAll(moveObservableList); // add every item in observable list to moveTable
  }

  public void createNodeTable() {
    // open session
    Session ses = factory.openSession();
    List<Node> nodeObjects =
        ses.createQuery("SELECT s FROM Node s", Node.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Node> nodeObservableList =
        FXCollections.observableList(nodeObjects); // convert list to ObservableList
    ses.close();
    nodeTable
        .getItems()
        .addAll(nodeObservableList); // add every item in observable list to moveTable
  }

  public void createEdgeTable() {
    // open session
    Session ses = factory.openSession();
    List<Edge> edgeObjects =
        ses.createQuery("SELECT s FROM Edge s", Edge.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Edge> edgeObservableList =
        FXCollections.observableList(edgeObjects); // convert list to ObservableList
    ses.close();
    edgeTable
        .getItems()
        .addAll(edgeObservableList); // add every item in observable list to moveTable
  }

  public void createLnTable() {
    // open session
    Session ses = factory.openSession();
    List<LocationName> lnObjects =
        ses.createQuery("SELECT s FROM LocationName s", LocationName.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<LocationName> lnObservableList =
        FXCollections.observableList(lnObjects); // convert list to ObservableList
    ses.close();
    locationTable
        .getItems()
        .addAll(lnObservableList); // add every item in observable list to moveTable
  }

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }
}
