// ===== DataAdapter.java =====
package com.retailstore.dao;

import com.retailstore.model.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * MySQL Database Data Access Object
 * Handles all MySQL database operations for the retail store system
 */
public class DataAdapter {
    private Connection connection;

    public DataAdapter(Connection connection) {
        this.connection = connection;
    }

    /**
     * Prepare SQL statement with connection validation
     */
    private PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not available");
        }
        return connection.prepareStatement(sql);
    }

    /**
     * Prepare SQL statement with generated keys support
     */
    private PreparedStatement prepareStatement(String sql, int flag) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not available");
        }
        return connection.prepareStatement(sql, flag);
    }

    // ===== USER OPERATIONS =====

    /**
     * Authenticate user with username and password
     */
    public Users loadUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE UserName = ? AND Password = ?";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load user by ID
     */
    public Users loadUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE UserID = ?";
        
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Get all users for management interface
     */
    public Vector<Vector<Object>> getAllUsers() throws SQLException {
        Vector<Vector<Object>> userData = new Vector<>();
        String sql = "SELECT UserID, UserName, DisplayName, Role FROM Users ORDER BY UserID";

        try (PreparedStatement stmt = prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("UserID"));
                row.add(rs.getString("UserName"));
                row.add(rs.getString("DisplayName"));
                row.add(rs.getString("Role"));
                userData.add(row);
            }
        }
        return userData;
    }

    /**
     * Save new user
     */
    public void saveUser(Users user) throws SQLException {
        String sql = "INSERT INTO Users (UserName, Password, DisplayName, Role) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getRole().toString());
            stmt.executeUpdate();
        }
    }

    /**
     * Update existing user
     */
    public void updateUser(Users user) throws SQLException {
        String sql = "UPDATE Users SET UserName = ?, DisplayName = ?, Role = ? WHERE UserID = ?";
        
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getRole().toString());
            stmt.setInt(4, user.getUserID());
            stmt.executeUpdate();
        }
    }

    /**
     * Delete user (with relationship checks)
     */
    public void deleteUser(int userId) throws SQLException {
        if (hasRelatedRecords(userId)) {
            throw new SQLException("Cannot delete user with existing orders or products");
        }

        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Check if user has related records that prevent deletion
     */
    private boolean hasRelatedRecords(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Orders WHERE CustomerID = ?";
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // ===== PRODUCT OPERATIONS =====

    /**
     * Load product by ID
     */
    public Product loadProduct(int id) {
        String sql = "SELECT * FROM Products WHERE ProductID = ?";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading product: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load product by name
     */
    public Product loadProductByName(String name) {
        String sql = "SELECT * FROM Products WHERE Name = ?";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToProduct(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading product by name: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load product by barcode
     */
    public Product loadProductByBarcode(String barcode) throws SQLException {
        String sql = "SELECT * FROM Products WHERE Barcode = ?";
        
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setString(1, barcode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    /**
     * Save or update product
     */
    public boolean saveProduct(Product product) {
        try {
            if (product.getProductID() == 0) {
                return insertProduct(product);
            } else {
                return updateProduct(product);
            }
        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Insert new product
     */
    private boolean insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO Products (Name, Description, Price, Quantity, Category, Barcode) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription() != null ? product.getDescription() : "");
            statement.setDouble(3, product.getPrice());
            statement.setDouble(4, product.getQuantity());
            statement.setString(5, product.getCategory());
            statement.setString(6, product.getBarcode());
            
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Update existing product
     */
    private boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE Products SET Name = ?, Price = ?, Quantity = ?, Category = ? WHERE ProductID = ?";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setDouble(3, product.getQuantity());
            statement.setString(4, product.getCategory());
            statement.setInt(5, product.getProductID());
            
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Delete product and related records
     */
    public void deleteProduct(int productId) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Delete related inventory logs first
            String deleteLogsQuery = "DELETE FROM InventoryLog WHERE ProductID = ?";
            try (PreparedStatement stmt = prepareStatement(deleteLogsQuery)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }

            // Delete the product
            String deleteProductQuery = "DELETE FROM Products WHERE ProductID = ?";
            try (PreparedStatement stmt = prepareStatement(deleteProductQuery)) {
                stmt.setInt(1, productId);
                stmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    /**
     * Update product quantity
     */
    public void updateProductQuantity(int productId, double newQuantity) throws SQLException {
        String sql = "UPDATE Products SET Quantity = ? WHERE ProductID = ?";
        
        try (PreparedStatement stmt = prepareStatement(sql)) {
            stmt.setDouble(1, newQuantity);
            stmt.setInt(2, productId);
            
            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Failed to update product quantity");
            }
        }
    }

    // ===== ORDER OPERATIONS =====

    /**
     * Load complete order with order lines
     */
    public Orders loadOrder(int id) {
        Orders order = null;
        try {
            order = loadOrderHeader(id);
            if (order != null) {
                loadOrderLines(order);
            }
        } catch (SQLException e) {
            System.err.println("Error loading order: " + e.getMessage());
            e.printStackTrace();
        }
        return order;
    }

    /**
     * Load order header information
     */
    private Orders loadOrderHeader(int orderId) throws SQLException {
        String sql = "SELECT * FROM Orders WHERE OrderID = ?";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setInt(1, orderId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToOrder(resultSet);
                }
            }
        }
        return null;
    }

    /**
     * Load order lines for an order
     */
    private void loadOrderLines(Orders order) throws SQLException {
        String sql = "SELECT * FROM OrderLine WHERE OrderID = ?";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setInt(1, order.getOrderID());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderLine line = mapResultSetToOrderLine(resultSet);
                    order.addLine(line);
                }
            }
        }
    }

    /**
     * Save complete order with order lines
     */
    public boolean saveOrder(Orders order) {
        try {
            connection.setAutoCommit(false);
            
            // Save order header
            saveOrderHeader(order);
            
            // Save order lines
            for (OrderLine line : order.getLines()) {
                saveOrderLine(line);
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Error resetting auto-commit: " + ex.getMessage());
            }
        }
    }

    /**
     * Save order header
     */
    private void saveOrderHeader(Orders order) throws SQLException {
        String sql = "INSERT INTO Orders (CustomerID, CashierID, OrderDate, TotalCost, TotalTax, PaymentMethod, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, order.getCustomerID());
            statement.setInt(2, order.getCashierID());
            statement.setString(3, order.getDate());
            statement.setDouble(4, order.getTotalCost());
            statement.setDouble(5, order.getTotalTax());
            statement.setString(6, order.getPaymentMethod().toString());
            statement.setString(7, "PENDING");

            statement.executeUpdate();

            // Get generated OrderID
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setOrderID(rs.getInt(1));
                }
            }
        }
    }

    /**
     * Save order line
     */
    private void saveOrderLine(OrderLine line) throws SQLException {
        String sql = "INSERT INTO OrderLine (OrderID, ProductID, Quantity, UnitPrice, Cost) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = prepareStatement(sql)) {
            statement.setInt(1, line.getOrderID());
            statement.setInt(2, line.getProductID());
            statement.setDouble(3, line.getQuantity());
            statement.setDouble(4, line.getUnitPrice());
            statement.setDouble(5, line.getCost());
            statement.executeUpdate();
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Map ResultSet to User object
     */
    private Users mapResultSetToUser(ResultSet rs) throws SQLException {
        Users user = new Users();
        user.setUserID(rs.getInt("UserID"));
        user.setUsername(rs.getString("UserName"));
        user.setPassword(rs.getString("Password"));
        user.setFullName(rs.getString("DisplayName"));
        user.setRole(Users.Role.valueOf(rs.getString("Role")));
        return user;
    }

    /**
     * Map ResultSet to Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("ProductID"));
        product.setName(rs.getString("Name"));
        product.setPrice(rs.getDouble("Price"));
        product.setQuantity(rs.getDouble("Quantity"));
        product.setBarcode(rs.getString("Barcode"));
        product.setDescription(rs.getString("Description"));
        product.setCategory(rs.getString("Category"));
        
        // Handle optional SellerID
        try {
            product.setSellerID(rs.getInt("SellerID"));
        } catch (SQLException e) {
            // SellerID column might not exist in all queries
        }
        
        return product;
    }

    /**
     * Map ResultSet to Order object
     */
    private Orders mapResultSetToOrder(ResultSet rs) throws SQLException {
        Orders order = new Orders();
        order.setOrderID(rs.getInt("OrderID"));
        order.setCustomerID(rs.getInt("CustomerID"));
        order.setCashierID(rs.getInt("CashierID"));
        order.setTotalCost(rs.getDouble("TotalCost"));
        order.setTotalTax(rs.getDouble("TotalTax"));
        order.setDate(rs.getString("OrderDate"));
        order.setStatus(rs.getString("Status"));
        order.setPaymentMethod(PaymentMethod.valueOf(rs.getString("PaymentMethod")));
        return order;
    }

    /**
     * Map ResultSet to OrderLine object
     */
    private OrderLine mapResultSetToOrderLine(ResultSet rs) throws SQLException {
        OrderLine line = new OrderLine();
        line.setOrderID(rs.getInt("OrderID"));
        line.setProductID(rs.getInt("ProductID"));
        line.setQuantity(rs.getDouble("Quantity"));
        line.setUnitPrice(rs.getDouble("UnitPrice"));
        line.setCost(rs.getDouble("Cost"));
        return line;
    }

    /**
     * Get database connection
     */
    public Connection getConnection() {
        return this.connection;
    }

    // Additional methods for specific business operations would continue here...
    // (getPendingOrders, claimOrder, completeOrder, etc.)
}