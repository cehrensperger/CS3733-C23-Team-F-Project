package edu.wpi.FlashyFrogs;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Main {

  public static void main(String[] args) {

    // App.launch(App.class, args);

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

    // shortcut: psvm
  }
}
