package com.retailstore;

import com.retailstore.AppConfig;
import com.retailstore.dao.DataAdapter;
import com.retailstore.dao.MongoDataAdapter;
import com.retailstore.dao.RedisDataAdapter;
import com.retailstore.model.Users;
import com.retailstore.view.*;
import com.retailstore.controller.*;

import javax.swing.*;
import java.sql.*;

public class Application {
    private static Application instance;   // Singleton pattern
    private static Connection connection;
    private DataAdapter dataAdapter;
    private Users currentUser = null;
    private AppConfig config;

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

    // Add a flag to track initialization
    private static boolean initializing = false;

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

    private Application() {
        // Prevent creation through reflection
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        try {
            config = AppConfig.getInstance();
            initializeDatabase();
            initializeViews();
            initializeControllers();
        } catch (Exception e) {
            System.err.println("Application initialization error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize application", e);
        }
    }

    private void initializeDatabase() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                connection = DriverManager.getConnection(
                    config.getMysqlUrl(),
                    config.getMysqlUsername(),
                    config.getMysqlPassword()
                );
                System.out.println("MySQL database connection established");
            }

            // MongoDB connection
            try {
                MongoDataAdapter.initialize(config.getMongoDatabase());
                if (!MongoDataAdapter.getInstance().isConnected()) {
                    throw new Exception("Could not connect to MongoDB database '" + config.getMongoDatabase() + "'");
                }
                System.out.println("MongoDB connection to '" + config.getMongoDatabase() + "' established");
            } catch (Exception ex) {
                System.err.println("MongoDB connection error: " + ex.getMessage());
                ex.printStackTrace();
                // Note: Not exiting application if MongoDB fails, since it's supplementary
            }

            dataAdapter = new DataAdapter(connection);
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Database connection failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
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

    public static Connection getConnection() {
        return connection;
    }

    private void initializeViews() {
        loginScreen = new LoginScreen();
        customerView = new CustomerView();
        cashierView = new CashierView();
        checkoutScreen = new CheckoutView();
        managerView = new ManagerView();

        System.out.println("Views initialized successfully");
    }

    private void initializeControllers() {
        try {
            loginController = new LoginController(loginScreen, dataAdapter);
            customerViewController = new CustomerViewController(customerView, dataAdapter);
            cashierController = null; // This will be initialized when a seller logs in
            checkoutController = new CheckoutController(checkoutScreen, dataAdapter);
            managerController = new ManagerController(managerView, dataAdapter);
            System.out.println("Controllers initialized successfully");
        } catch (Exception ex) {
            System.err.println("Controller initialization error: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize controllers: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public void cleanup() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }

            // Cleanup the manager controller if it exists
            if (managerController != null) {
                managerController.cleanup();
            }
        } catch (SQLException ex) {
            System.err.println("Error closing database connection: " + ex.getMessage());
        }
        RedisDataAdapter.getInstance().cleanup();
    }

    public Users getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Users user) {
        this.currentUser = user;

        SwingUtilities.invokeLater(() -> {
            // Hide all views first
            loginScreen.setVisible(false);
            customerView.setVisible(false);
            cashierView.setVisible(false);
            checkoutScreen.setVisible(false);
            managerView.setVisible(false);

            // Clear any existing cart/order data before showing new view
            if (cashierController != null) {
                cashierController.cleanup();
                cashierController = null;
            }

            // Then show appropriate view based on user role
            if (user != null) {
                switch (user.getRole()) {
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
            } else {
                loginScreen.setVisible(true);
            }
        });
    }

    // Getters
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
    public AppConfig getConfig() { return config; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                try {
                    Application app = Application.getInstance();
                    app.getLoginScreen().setVisible(true);

                    // Add shutdown hook to cleanup resources
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        app.cleanup();
                    }));
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