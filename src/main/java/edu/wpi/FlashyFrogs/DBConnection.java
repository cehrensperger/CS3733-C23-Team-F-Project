package edu.wpi.FlashyFrogs;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Singleton class representing the Database connection via Hibernate. Prevents duplicate connections
 * or changing the connection once it is established
 */
public enum DBConnection {
  CONNECTION; // The connection
  private SessionFactory sessionFactory; // The session factory to generate connections with
  private StandardServiceRegistry registry; // The registry to create the sessions

  /**
   * Getter for the session factory, returns the session factory
   *
   * @return the session factory. MAY BE NULL if the connection hasn't been established or was
   *     disconnected
   */
  public SessionFactory getSessionFactory() {
    return this.sessionFactory;
  }

  /**
   * Connects the DB connection
   *
   * @throws IllegalStateException if the connection is already setup
   */
  public void connect() {
    // Check the session factory
    if (this.sessionFactory != null) {
      throw new IllegalStateException(
          "Connection already connected!"); // If it's null, disconnect it
    }

    // Setup the registry
    registry =
        new StandardServiceRegistryBuilder()
            .configure("edu/wpi/FlashyFrogs/hibernate.cfg.xml") // Load settings
            .build(); // Build

    // Setup the session factory
    sessionFactory =
        new MetadataSources(registry)
            .buildMetadata()
            .buildSessionFactory(); // Build from the registry
  }

  /**
   * Disconnects the DB connection
   *
   * @throws IllegalStateException if the connection is not currently connected
   */
  public void disconnect() {
    // Check if the session is actually disconnectable (not null)
    if (this.sessionFactory == null) {
      throw new IllegalStateException(
          "Connection already disconnected!"); // Throw an illegal state if not
    }

    // Close the system
    sessionFactory.close(); // Close the session factory
    registry.close(); // Close the registry

    // Delete everything
    sessionFactory = null; // Delete the session factory
    registry = null; // Delete the registry
  }
}
