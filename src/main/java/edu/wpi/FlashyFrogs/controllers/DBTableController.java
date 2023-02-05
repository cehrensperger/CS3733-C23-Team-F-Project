package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.Fapp;
import edu.wpi.FlashyFrogs.Main;
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
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
  // Node TextBoxes
  public TextField nodeTxtID;
  public TextField nodeTxtX;
  public TextField nodeTxtY;
  public TextField nodeTxtFloor;
  public TextField nodeTxtBuilding;

  // Edge Table Stuff
  public TableView<Edge> edgeTable;
  public TableColumn<Edge, Node> edgeNode1;
  public TableColumn<Edge, Node> edgeNode2;
  // Location Table Stuff
  public TableView<LocationName> locationTable;
  public TableColumn<LocationName, String> lnLongName;
  public TableColumn<LocationName, String> lnShortName;
  public TableColumn<LocationName, LocationName.LocationType> lnLocationType;
  public TableColumn<Move, Node> colId;
  public TableColumn<Move, LocationName> colLongName;
  public TableColumn<Move, Date> colTimestamp;
  public TextField txtID;
  public TextField txtLongName;

  public Button backbutton;
  @FXML private MFXButton back;
  // Update Buttons
  public Button nodeUpdateID;
  public Button nodeUpdate;
  public Button lnSubmitBtn;
  public Button lnSubmitLongNameBtn;
  // Location Name TxtFields
  public TextField lnTxtLongName;
  public TextField lnTxtShortName;
  public TextField lnTxtLocationType;
  // checkers
  private String checkNodeID;
  private String checkLongName;

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
  private void addMove(javafx.event.ActionEvent actionEvent) throws Exception {
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

  @FXML
  private void nodeRowClicked(MouseEvent event) {
    Node clickedNode = nodeTable.getSelectionModel().getSelectedItem();
    nodeTxtID.setText(String.valueOf(clickedNode.getId()));
    checkNodeID = String.valueOf(clickedNode.getId());
    nodeTxtX.setText(String.valueOf(clickedNode.getXCoord()));
    nodeTxtY.setText(String.valueOf(clickedNode.getYCoord()));
    nodeTxtFloor.setText(String.valueOf(clickedNode.getFloor()));
    nodeTxtBuilding.setText(String.valueOf(clickedNode.getBuilding()));
  }

  /**
   * When a row is clicked on take the values of the row and paste it into the textFields
   *
   * @param event
   */
  @FXML
  private void lnRowClicked(MouseEvent event) {
    LocationName clickedLocationName = locationTable.getSelectionModel().getSelectedItem();
    checkLongName = String.valueOf(clickedLocationName.getLongName());
    lnTxtLongName.setText(String.valueOf(clickedLocationName.getLongName()));
    lnTxtShortName.setText(String.valueOf(clickedLocationName.getShortName()));
    lnTxtLocationType.setText(String.valueOf(clickedLocationName.getLocationType()));
  }

  @FXML
  private void updateID(ActionEvent event) {
    if (checkNodeID != nodeTxtID.getText()) {
      Session session = Main.factory.openSession();
      Transaction transaction = session.beginTransaction();

      String id = checkNodeID;
      String newID = nodeTxtID.getText();
      Node node = session.find(Node.class, id);

      Node newNode =
          new Node(newID, node.getBuilding(), node.getFloor(), node.getXCoord(), node.getYCoord());
      session.persist(newNode);
      transaction.commit();

      transaction = session.beginTransaction();

      List<Edge> edges1 =
          session
              .createQuery("Select e From Edge e Where node1 = :node", Edge.class)
              .setParameter("node", node)
              .getResultList();
      List<Edge> edges2 =
          session
              .createQuery("Select e From Edge e Where node2 = :node", Edge.class)
              .setParameter("node", node)
              .getResultList();
      List<Move> moves =
          session
              .createQuery("Select m From Move m Where node = :node", Move.class)
              .setParameter("node", node)
              .getResultList();

      if (edges1.size() != 0) {
        for (int i = 0; i < edges1.size(); i++) {
          Edge edge = new Edge(newNode, edges1.get(i).getNode2());
          session.persist(edge);
          session.remove(edges1.get(i));
        }
      }
      if (edges2.size() != 0) {
        for (int i = 0; i < edges2.size(); i++) {
          Edge edge = new Edge(edges2.get(i).getNode1(), newNode);
          session.persist(edge);
          session.remove(edges2.get(i));
        }
      }
      if (moves.size() != 0) {
        for (int i = 0; i < moves.size(); i++) {
          Move move = new Move(newNode, moves.get(i).getLocation(), moves.get(i).getMoveDate());
          session.persist(move);
          session.remove(moves.get(i));
        }
      }
      transaction.commit();
      transaction = session.beginTransaction();
      session.remove(node);
      transaction.commit();
      session.close();
    }
    createNodeTable();
  }

  private void createMoveTable() {
    // open session
    Session ses = factory.openSession();
    List<Move> moveObjects =
        ses.createQuery("SELECT s FROM Move s", Move.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Move> moveObservableList =
        FXCollections.observableList(moveObjects); // convert list to ObservableList
    ses.close();
    moveTable.getItems().clear();
    moveTable
        .getItems()
        .addAll(moveObservableList); // add every item in observable list to moveTable
  }

  private void createNodeTable() {
    // open session
    Session ses = factory.openSession();
    List<Node> nodeObjects =
        ses.createQuery("SELECT s FROM Node s", Node.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Node> nodeObservableList =
        FXCollections.observableList(nodeObjects); // convert list to ObservableList
    ses.close();
    nodeTable.getItems().clear();
    nodeTable
        .getItems()
        .addAll(nodeObservableList); // add every item in observable list to moveTable
  }

  private void createEdgeTable() {
    // open session
    Session ses = factory.openSession();
    List<Edge> edgeObjects =
        ses.createQuery("SELECT s FROM Edge s", Edge.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Edge> edgeObservableList =
        FXCollections.observableList(edgeObjects); // convert list to ObservableList
    ses.close();
    edgeTable.getItems().clear();
    edgeTable
        .getItems()
        .addAll(edgeObservableList); // add every item in observable list to moveTable
  }

  private void createLnTable() {
    // open session
    Session ses = factory.openSession();
    List<LocationName> lnObjects =
        ses.createQuery("SELECT s FROM LocationName s", LocationName.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<LocationName> lnObservableList =
        FXCollections.observableList(lnObjects); // convert list to ObservableList
    ses.close();
    locationTable.getItems().clear();
    locationTable
        .getItems()
        .addAll(lnObservableList); // add every item in observable list to moveTable
  }

  public void handleBackButton(ActionEvent actionEvent) throws IOException {
    Fapp.setScene("Home");
  }

  public void nodeUpdate(ActionEvent actionEvent) {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();
    Node node = session.find(Node.class, checkNodeID);
    node.setXCoord(Integer.parseInt(nodeTxtX.getText()));
    node.setYCoord(Integer.parseInt(nodeTxtY.getText()));
    node.setBuilding(nodeBuilding.getText());
    node.setFloor(Node.Floor.valueOf(nodeTxtFloor.getText()));
    session.merge(node);
    transaction.commit();
    session.close();
    createNodeTable();
  }

  public void submitLongName(ActionEvent actionEvent) {
    Session session = Main.factory.openSession();
    Transaction transaction = session.beginTransaction();
    LocationName location = session.find(LocationName.class, checkLongName);
    List<Move> moves =
        session
            .createQuery("Select m From Move m Where location = :location", Move.class)
            .setParameter("location", location)
            .getResultList();

    LocationName newLocation =
        new LocationName(
            lnTxtLongName.getText(), location.getLocationType(), location.getShortName());
    session.persist(newLocation);
    transaction.commit();
    transaction = session.beginTransaction();
    if (moves.size() != 0) {
      for (int i = 0; i < moves.size(); i++) {
        Move move = new Move(moves.get(i).getNode(), newLocation, moves.get(i).getMoveDate());
        session.persist(move);
        session.remove(moves.get(i));
      }
    }
    transaction.commit();
    transaction = session.beginTransaction();
    session.remove(location);
    transaction.commit();
    session.close();
    createLnTable();
  }

  public void lnUpdate(ActionEvent actionEvent) {
    Session session = factory.openSession();
    Transaction transaction = session.beginTransaction();
    LocationName locationName = session.find(LocationName.class, checkLongName);
    locationName.setLocationType(LocationName.LocationType.valueOf(lnTxtLocationType.getText()));
    locationName.setShortName(lnTxtShortName.getText());
    session.merge(locationName);
    transaction.commit();
    session.close();
    createLnTable();
  }
}
