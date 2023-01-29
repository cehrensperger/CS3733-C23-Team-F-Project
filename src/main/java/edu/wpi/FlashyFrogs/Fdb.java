package edu.wpi.FlashyFrogs;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import lombok.NonNull;
import org.hibernate.*;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.*;

public class Fdb {

  static Connection connection; // database connection

  public static void main(String[] args)
      throws SQLException, ClassNotFoundException, InvocationTargetException,
          IllegalAccessException {
    // dbInit();
    // tableInit();
    // insertFromCSV();

    final StandardServiceRegistry registry =
        new StandardServiceRegistryBuilder()
            .configure() // configures settings from hibernate.cfg.xml
            .build();

    try {
      SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
      Session session = factory.openSession();
      Transaction transaction = session.beginTransaction();
      session.close();
      factory.close();
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      ex.printStackTrace();
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }

  /**
   * Initializes the database connection
   *
   * @throws SQLException if an sql error occurs
   */
  public static void dbInit() throws SQLException {
    String username = "teamf";
    String password = "QZsMCabe9faRjyhHQphIYHRnLI0no3N7";

    try {
      Class.forName("org.postgresql.Driver");
      connection =
          DriverManager.getConnection(
              "jdbc:postgresql://wpi-softeng-postgres-db.coyfss2f91ba.us-east-1.rds.amazonaws.com:2112/dbf",
              username,
              password);
    } catch (SQLException | ClassNotFoundException e) {
      System.out.println("Connection Failed! Check output console.");
      e.printStackTrace();
      return;
    }
    if (connection != null) {
      System.out.println("Connection established.");
    } else {
      System.out.println("Failed to make connection.");
    }
  }

  /**
   * initializes the database by creating the required tables
   *
   * @throws SQLException if an sql error occurs
   */
  //  @NonNull
  //  public static void tableInit() throws SQLException {
  //    Statement stmt = connection.createStatement(); // begin statement
  //    if (tableExists("edge")) {
  //      stmt.execute("drop table edge"); // drop existing tables to be repopulated
  //    }
  //    if (tableExists("node")) {
  //      stmt.execute("drop table node"); // drop existing tables to be repopulated
  //    }
  //    if (tableExists("move")) {
  //      stmt.execute("drop table move"); // drop existing tables to be repopulated
  //    }
  //    if (tableExists("locationname")) {
  //      stmt.execute("drop table locationname"); // drop existing tables to be repopulated
  //    }
  //
  //    // create new node and edge tables
  //    stmt.execute(
  //        "Create table node(\n"
  //            + "id char(100) primary key,\n"
  //            + "xcoord int not null,\n"
  //            + "ycoord int not null,\n"
  //            + "floor char(100) not null,\n"
  //            + "building varchar(100) not null);");
  //
  //    stmt.execute(
  //        "Create table edge(\n"
  //            + "nodeID_1 char(100),\n"
  //            + "nodeID_2 char(100),\n"
  //            + "foreign key(nodeID_1) references node(id),\n"
  //            + "foreign key(nodeID_2) references node(id),\n"
  //            + "primary key(nodeID_1, nodeID_2));");
  //
  //    stmt.execute(
  //        "Create table locationName (\n"
  //            + "longName char(100) primary key,\n"
  //            + "shortName char(100) not null,\n"
  //            + "locationType char(4) not null);");
  //
  //    stmt.execute(
  //        "Create table move(\n"
  //            + "nodeID char(100),\n"
  //            + "longName char(100),\n"
  //            + "moveDate date,\n"
  //            + "foreign key(nodeID) references node(id),\n"
  //            + "foreign key(longName) references locationName(longName),\n"
  //            + "primary key(nodeID, longName, moveDate));");
  //
  //    stmt.close();
  //  }

  //  @NonNull
  //    public static MapCSVParser insertFromCSV() {
  //        Scanner nodeInput;
  //
  //        try {
  //            nodeInput = new Scanner(new File("L1Nodes.csv"));
  //        } catch (FileNotFoundException e) {
  //            System.out.println("Please place L1Nodes in the same folder as the .jar file.");
  //            throw new RuntimeException(e);
  //        }
  //
  //        Scanner edgeInput;
  //
  //        try {
  //            edgeInput = new Scanner(new File("L1Edges.csv"));
  //        } catch (FileNotFoundException e) {
  //            System.out.println("Please place L1Nodes in the same folder as the .jar file.");
  //            throw new RuntimeException(e);
  //        }
  //        Scanner[] nodeFiles = {nodeInput};
  //        Scanner[] edgeFiles = {edgeInput};
  //        try {
  //            parser = new MapCSVParser(nodeFiles, edgeFiles);
  //        } catch (SQLException e) {
  //            throw new RuntimeException(e);
  //        }
  //        return parser;
  //    }

  /**
   * Process inserts into a given table
   *
   * @param table the table to insert into
   * @param values values to insert into that table, must be arranged with single quotes around and
   *     a comma between each value
   * @return boolean whether the statement executes correctly
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static void processInsert(String table, String values) throws SQLException {
    Statement stmt = connection.createStatement(); // begin statement
    String insert = "INSERT INTO " + table + " VALUES(" + values + ")"; // insert node into database
    stmt.execute(insert); // execute statement
  }

  /**
   * processes a given update statement
   *
   * @param update the sql statement to be used for the update
   * @return boolean whether the tuple was updated correctly
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static boolean processUpdate(String update) throws SQLException {
    Statement stmt = connection.createStatement(); // begin statement
    int numUpdated = stmt.executeUpdate(update); // execute update
    return (numUpdated > 0);
  }

  /**
   * processes a given query statement
   *
   * @param query the query to be executed
   * @return the result set of the query
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static ResultSet processQuery(String query) throws SQLException {
    Statement stmt = connection.createStatement(); // begin statement
    return stmt.executeQuery(query); // execute query and save the result set
  }

  /**
   * Convert the result set from a query statement on the node table into formatted system output
   *
   * @param resultSet the result set to be processed
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static void processNodeResult(ResultSet resultSet) throws SQLException {
    while (resultSet.next()) { // while there are still more items in the result set
      String id = resultSet.getString("id"); // the first item in the tuple is the id
      String xcoord = resultSet.getString("xcoord"); // the second item is the x coordinate
      String ycoord = resultSet.getString("ycoord"); // the third item is the y coordinate
      String floor = resultSet.getString("floor"); // the fourth item is the floor
      String building = resultSet.getString("building"); // the fifth item is the building
      String nodeType = resultSet.getString("nodeType"); // the sixth item is the node type
      String longname =
          resultSet.getString("longname"); // the seventh item is the long name of the node
      String shortname =
          resultSet.getString("shortname"); // the eighth item is the shortened name of the node
      System.out.println("Node:"); // print before each tuple
      System.out.println( // print with formatting, one tab in and each attribute on a new line
          "\tNode ID: "
              + id
              + "\n\tX Coordinate: "
              + xcoord
              + "\n\tY Coordinate: "
              + ycoord
              + "\n\tFloor: "
              + floor
              + "\n\tBuilding: "
              + building
              + "\n\tNode Type: "
              + nodeType
              + "\n\tLong Name: "
              + longname
              + "\n\tShort Name: "
              + shortname
              + "\n"); // space one line for the next tuple
    }
  }

  /**
   * Convert the result set from a query statement on the edge table into formatted system output
   *
   * @param resultSet the result set to be processed
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static void processEdgeResult(ResultSet resultSet) throws SQLException {
    while (resultSet.next()) { // while there are still more items in the result set
      String id = resultSet.getString("id"); // first item in the tuple is the id
      String node1ID = resultSet.getString("nodeid_1"); // second item is the id of the start node
      String node2ID = resultSet.getString("nodeid_2"); // third item is the id of the end node
      System.out.println("Edge:");
      System.out.println( // print with formatting, one tab in and each attribute on a new line
          "\tEdge ID: "
              + id
              + "\n\tStart Node: "
              + node1ID
              + "\n\tEnd Node: "
              + node2ID
              + "\n"); // space one line for the next tuple
    }
  }

  /**
   * processes a delete statement in a given table
   *
   * @param table the table to delete from
   * @param deleteID the id of the item to delete
   * @return boolean whether the statement executed
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static boolean processDelete(String table, String deleteID) throws SQLException {
    Statement stmt = connection.createStatement(); // begin statement
    String delete =
        "Delete from "
            + table
            + " where "
            + table
            + ".id = '"
            + deleteID
            + "'"; // create delete statement using parameters
    return stmt.execute(delete); // execute delete statement
  }

  /**
   * checks in the given table exists in the database
   *
   * @param tableName the table to check for
   * @return boolean whether the table exists
   * @throws SQLException if an sql error occurs
   */
  public static boolean tableExists(String tableName) throws SQLException {
    String query =
        "SELECT EXISTS (\n"
            + "SELECT FROM\n"
            + "   pg_tables\n"
            + "WHERE\n"
            + "   schemaname = 'public' AND\n"
            + "   tablename  = ?\n"
            + ");\n";
    PreparedStatement stmt = connection.prepareStatement(query);
    stmt.setString(1, tableName);
    ResultSet rs = stmt.executeQuery();
    rs.next();

    return rs.getBoolean(1);
  }

  /**
   * end the database connection at the end of the program
   *
   * @throws SQLException if an sql error occurs
   */
  @NonNull
  public static void closeConnection() throws SQLException {
    connection.close(); // close the connection
  }
}
