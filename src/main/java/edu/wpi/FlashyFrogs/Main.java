package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Main {
  private static SessionFactory factory;

  public static SessionFactory getFactory() {
    return factory;
  }

  public static void main(String[] args) {

    // App.launch(App.class, args);

    final StandardServiceRegistry registry =
        new StandardServiceRegistryBuilder()
            .configure("./edu/wpi/FlashyFrogs/hibernate.cfg.xml") // Load settings
            .build();
    try {
      factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
      Session session = factory.openSession();
      // Transaction transaction = session.beginTransaction();

      session.close();
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      ex.printStackTrace();
      StandardServiceRegistryBuilder.destroy(registry);
    }
    Fapp.launch(Fapp.class, args);
    factory.close();
  }
}
