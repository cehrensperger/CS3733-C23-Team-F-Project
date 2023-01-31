package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Main {

  public static SessionFactory getFactory()
  {
    return factory;
  }

  static final StandardServiceRegistry registry =
      new StandardServiceRegistryBuilder()
          .configure("./edu/wpi/FlashyFrogs/hibernate.cfg.xml") // Load settings
          .build();
   static final SessionFactory factory =
      new MetadataSources(registry).buildMetadata().buildSessionFactory();

  public static void main(String[] args) {

    // App.launch(App.class, args);

    Fapp.launch(Fapp.class, args);
  }
}
