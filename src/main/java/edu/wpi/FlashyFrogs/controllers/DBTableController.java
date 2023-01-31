package edu.wpi.FlashyFrogs.controllers;

import static edu.wpi.FlashyFrogs.Main.factory;

import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import java.net.URL;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;

public class DBTableController implements Initializable {

  public TableView<Move> moveTable;
  public TableColumn<Move, Node> colId;
  public TableColumn<Move, LocationName> colLongName;
  public TableColumn<Move, Date> colTimestamp;
  public TextField txtID;
  public TextField txtLongName;
  public Button btnadd;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // set columns
    colId.setCellValueFactory(new PropertyValueFactory<>("node"));
    colLongName.setCellValueFactory(new PropertyValueFactory<>("location"));
    colTimestamp.setCellValueFactory(new PropertyValueFactory<>("moveDate"));
    // open session
    Session ses = factory.openSession();
    List<Move> objects =
        ses.createQuery("SELECT s FROM Move s", Move.class)
            .getResultList(); // select everything from move table and add to list
    ObservableList<Move> observableList =
        FXCollections.observableList(objects); // convert list to ObservableList
    ses.close();
    moveTable.getItems().addAll(observableList); // add every item in observable list to moveTable
  }

  /**
   * @param actionEvent adds a new move object to the Tableview and Database from the input from UI
   * @throws Exception
   */
  @FXML
  public void addMove(javafx.event.ActionEvent actionEvent) throws Exception {
    Session session = factory.openSession();
    String colID = txtID.getText();
    String colLongName = txtLongName.getText();

    Node node = session.find(Node.class, colID);
    System.out.println(node.toString());
    LocationName locationName = session.find(LocationName.class, colLongName);
    Move move = new Move(node, locationName, Date.from(Instant.now()));
    System.out.println(move);
    // add info to the table
    moveTable.getItems().add(move);
    // add to database
    session.beginTransaction();
    session.persist(move);
    session.getTransaction().commit();
    session.close();
  }
}
