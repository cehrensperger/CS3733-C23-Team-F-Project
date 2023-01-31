package edu.wpi.FlashyFrogs;

import edu.wpi.FlashyFrogs.ORM.Node;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.*;

import java.util.stream.Stream;

/**
 * Tests for the PathFinder class, tests to ensure that it finds a valid path, and that it always finds
 * the shortest path. Uses transactions to ensure test atomicity where required, along with separate sessions
 * for each test. Uses a separate test schema and makes no changes to the actual database
 */
public class PathFinderTest {
    private static SessionFactory sessionFactory; // Session factory to be created before all tests have run
    private static StandardServiceRegistry serviceRegistry; // Service registry associated with the factory
    private Session testSession; // Session to be used for each individual test

    /**
     * Setup method to be run before all tests that creates the session factory and service registry
     */
    @BeforeAll
    public static void setupSessionFactory() {
        // Create the service registry we will use
        serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("./edu/wpi/FlashyFrogs/hibernate.cfg.xml") // Load settings
                .build();

        // Create the session factory from that
        sessionFactory = new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
    }

    /**
     * Teardown method to be run after all tests. Cleans up the service registry and
     * session factory
     */
    @AfterAll
    public static void closeSessionFactory() {
        sessionFactory.close(); // Close the session factory
        serviceRegistry.close(); // Close the service registry
    }

    /**
     * Setup method, sets up the session before each test
     */
    @BeforeEach
    public void setupSessionAndTransaction() {
        testSession = sessionFactory.openSession(); // Open a session
    }

    /**
     * Teardown method, closes the session
     */
    @AfterEach
    public void teardownSession() {
        testSession.close(); // Close the session
    }

    /**
     * Tests for a line of nodes, ensures that the algorithm reaches the end
     */
    @Test
    public void testLineOfNodes() {
        Node nodeOne = new Node();
    }
}
