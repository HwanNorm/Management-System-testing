package com.retailstore;

import com.retailstore.dao.DataAdapter;
import com.retailstore.dao.MongoDataAdapter;
import com.retailstore.dao.RedisDataAdapter;
import com.retailstore.controller.*;
import com.retailstore.view.*;
import com.retailstore.model.Users;

import javax.swing.*;
import java.sql.*;
import java.io.InputStream;
import java.util.Properties;

/**
 * Main Application class - Singleton pattern
 * Manages application initialization, database connections, and view coordination
 */
public class Application {
    private static Application instance;
    private static Connection connection;
    private DataAdapter dataAdapter;
    private Users currentUser = null;
    
    // Configuration
    private Properties config;
    private static final String CONFIG_FILE = "/application.properties";

    // Views
    private LoginScreen loginScreen;
    private CustomerView customerView;
    private CashierView cashierView;
    private CheckoutView checkoutScreen;
    private ManagerView managerView;

    // Controllers
    private LoginController loginController;
    private CustomerViewController customerViewController;
    private CashierController cashierController;
    private CheckoutController checkoutController;
    private ManagerController managerController;

    // Initialization flag
    private static boolean initializing = false;

    /**
     * Singleton instance getter
     */
    public static Application getInstance() {
        if (instance == null) {
            synchronized (Application.class) {
                if (instance == null && !initializing) {
                    initializing = true;
                    instance = new Application();
                    initializing = false;
                }
            }
        }
        return instance;
    }

    /**
     * Private constructor - enforces singleton pattern
     */
    private Application() {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        loadConfiguration();
        initializeDatabase();
        initializeViews();
        initializeControllers();
    }

    /**
     * Load configuration from properties file
     */
    private void loadConfiguration() {
        config = new Properties();
        try (InputStream input = getClass().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                config.load(input);
                System.out.println("Configuration loaded successfully");
            } else {
                System.out.println("Configuration file not found, using defaults");
                setDefaultConfiguration();
            }
        } catch (Exception ex) {
            System.err.println("Error loading configuration: " + ex.getMessage());
            setDefaultConfiguration();
        }
    }

    /**
     * Set default configuration values
     */
    private void setDefaultConfiguration() {
        config.setProperty("mysql.url", "jdbc:mysql://localhost:3306/retail_store3?useSSL=false&allowPublicKeyRetrieval=true");
        config.setProperty("mysql.username", "root");
        config.setProperty("mysql.password", "fm92mhziczac");
        config.setProperty("mongodb.database", "retail_store2");
        config.setProperty("redis.host", "redis-11107.c323.us-east-1-2.ec2.redns.redis-cloud.com");
        config.setProperty("redis.port", "11107");
        config.setProperty("redis.password", "fm92mhziczac");
    }

    /**
     * Initialize database connections
     */
    private void initializeDatabase() {
        initializeMySQL();
        initializeMongoDB();
        initializeRedis();
        
        dataAdapter = new DataAdapter(connection);
    }

    /**
     * Initialize MySQL connection
     */
    private void initializeMySQL() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = config.getProperty("mysql.url");
                String username = config.getProperty("mysql.username");
                String password = config.getProperty("mysql.password");

                connection = DriverManager.getConnection(url, username, password);
                System.out.println("MySQL database connection established");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("MySQL connection error: " + ex.getMessage());
            ex.printStackTrace();
            showErrorAndExit("Database connection failed: " + ex.getMessage());
        }
    }

    /**
     * Initialize MongoDB connection
     */
    private void initializeMongoDB() {
        try {
            String database = config.getProperty("mongodb.database");
            MongoDataAdapter.initialize(database);
            
            if (!MongoDataAdapter.getInstance().isConnected()) {
                throw new Exception("Could not connect to MongoDB database '" + database + "'");
            }
            System.out.println("MongoDB connection to '" + database + "' established");
        } catch (Exception ex) {
            System.err.println("MongoDB connection error: " + ex.getMessage());
            ex.printStackTrace();
            // Note: Not exiting application if MongoDB fails, since it's supplementary
        }
    }

    /**
     * Initialize Redis connection
     */
    private void initializeRedis() {
        try {
            RedisDataAdapter redisAdapter = RedisDataAdapter.getInstance();
            if (!redisAdapter.isConnected()) {
                throw new Exception("Could not connect to Redis database");
            }
            System.out.println("Redis connection established");
        } catch (Exception ex) {
            System.err.println("Redis connection error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Initialize all views
     */
    private void initializeViews() {
        try {
            loginScreen = new LoginScreen();
            customerView = new CustomerView();
            cashierView = new CashierView();
            checkoutScreen = new CheckoutView();
            managerView = new ManagerView();

            System.out.println("Views initialized successfully");
        } catch (Exception ex) {
            System.err.println("View initialization error: " + ex.getMessage());
            ex.printStackTrace();
            showErrorAndExit("Failed to initialize views: " + ex.getMessage());
        }
    }

    /**
     * Initialize all controllers
     */
    private void initializeControllers() {
        try {
            loginController = new LoginController(loginScreen, dataAdapter);
            customerViewController = new CustomerViewController(customerView, dataAdapter);
            cashierController = null; // Initialized when cashier logs in
            checkoutController = new CheckoutController(checkoutScreen, dataAdapter);
            managerController = new ManagerController(managerView, dataAdapter);
            
            System.out.println("Controllers initialized successfully");
        } catch (Exception ex) {
            System.err.println("Controller initialization error: " + ex.getMessage());
            ex.printStackTrace();
            showErrorAndExit("Failed to initialize controllers: " + ex.getMessage());
        }
    }

    /**
     * Show error message and exit application
     */
    private void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    /**
     * Get current database connection
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * Get current user
     */
    public Users getCurrentUser() {
        return currentUser;
    }

    /**
     * Set current user and switch views based on role
     */
    public void setCurrentUser(Users user) {
        this.currentUser = user;

        SwingUtilities.invokeLater(() -> {
            hideAllViews();
            cleanupControllers();

            if (user != null) {
                showViewForRole(user.getRole());
            } else {
                loginScreen.setVisible(true);
            }
        });
    }

    /**
     * Hide all views
     */
    private void hideAllViews() {
        loginScreen.setVisible(false);
        customerView.setVisible(false);
        cashierView.setVisible(false);
        checkoutScreen.setVisible(false);
        managerView.setVisible(false);
    }

    /**
     * Show appropriate view based on user role
     */
    private void showViewForRole(Users.Role role) {
        switch (role) {
            case MANAGER:
                managerView.setVisible(true);
                break;
            case CASHIER:
                cashierController = new CashierController(cashierView, dataAdapter);
                cashierView.setVisible(true);
                break;
            case CUSTOMER:
                customerView.setVisible(true);
                break;
        }
    }

    /**
     * Cleanup controllers
     */
    private void cleanupControllers() {
        if (cashierController != null) {
            cashierController.cleanup();
            cashierController = null;
        }
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }

            if (managerController != null) {
                managerController.cleanup();
            }

            RedisDataAdapter.getInstance().cleanup();
            
        } catch (SQLException ex) {
            System.err.println("Error closing database connection: " + ex.getMessage());
        }
    }

    // Getters for views and controllers
    public LoginScreen getLoginScreen() { return loginScreen; }
    public CustomerView getMainScreen() { return customerView; }
    public CashierView getCashierView() { return cashierView; }
    public CheckoutView getCheckoutScreen() { return checkoutScreen; }
    public ManagerView getManagerView() { return managerView; }
    public DataAdapter getDataAdapter() { return dataAdapter; }
    public CheckoutController getCheckoutController() { return checkoutController; }
    public CustomerViewController getMainScreenController() { return customerViewController; }
    public CashierController getCashierController() { return cashierController; }
    public ManagerController getManagerController() { return managerController; }

    /**
     * Main application entry point
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());

            SwingUtilities.invokeLater(() -> {
                try {
                    Application app = Application.getInstance();
                    app.getLoginScreen().setVisible(true);

                    // Add shutdown hook for cleanup
                    Runtime.getRuntime().addShutdownHook(new Thread(app::cleanup));
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Failed to start application: " + e.getMessage(),
                            "Startup Error",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}