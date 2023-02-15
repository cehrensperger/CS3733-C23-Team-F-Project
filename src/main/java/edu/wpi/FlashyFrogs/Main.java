package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Edge;
import edu.wpi.FlashyFrogs.ORM.LocationName;
import edu.wpi.FlashyFrogs.ORM.Move;
import edu.wpi.FlashyFrogs.ORM.Node;
import org.hibernate.Session;

@GeneratedExclusion
public class Main {

  public static void main(String[] args) {
    DBConnection.CONNECTION.connect(); // Connect the DB

    // Pre-fill the L2 Cache
    Session fillCacheSession = DBConnection.CONNECTION.getSessionFactory().openSession();
    fillCacheSession.createQuery("FROM Node", Node.class).setHint("org.hibernate.cacheable", true).getResultList();
    fillCacheSession.createQuery("FROM Edge", Edge.class).setHint("org.hibernate.cacheable", true).getResultList();
    fillCacheSession.createQuery("FROM Move", Move.class).setHint("org.hibernate.cacheable", true).getResultList();
    fillCacheSession.createQuery("FROM LocationName", LocationName.class).setHint("org.hibernate.cacheable", true).getResultList();

    fillCacheSession.close();

    ResourceDictionary[] resources =
        ResourceDictionary.values(); // Pre-cache the dictionary, for performance
    Fapp.launch(Fapp.class, args); // Launch the app

    System.out.println(DBConnection.CONNECTION.getSessionFactory().getStatistics());

    DBConnection.CONNECTION.disconnect(); // Disconnect the DB
  }
}
