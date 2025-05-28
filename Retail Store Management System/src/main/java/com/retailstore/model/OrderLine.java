// ===== OrderLine.java =====
package com.retailstore.model;

import com.retailstore.dao.DataAdapter;

/**
 * Order line item representing individual products in an order
 */
public class OrderLine {
    private Product product;
    private int productID;
    private int orderID;
    private double quantity;
    private double cost;
    private double unitPrice;
    private boolean scanned = false;

    /**
     * Default constructor
     */
    public OrderLine() {}

    /**
     * Constructor with essential fields
     */
    public OrderLine(int orderID, int productID, double quantity, double unitPrice) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.cost = quantity * unitPrice;
    }

    // Getters and setters
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { 
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.quantity = quantity; 
    }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { 
        if (unitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative");
        this.unitPrice = unitPrice; 
    }

    public double getCost() { 
        System.out.println("OrderLine getCost() called - cost: " + cost);
        return cost; 
    }
    public void setCost(double cost) { 
        if (cost < 0) throw new IllegalArgumentException("Cost cannot be negative");
        this.cost = cost; 
    }

    public boolean isScanned() { return scanned; }
    public void setScanned(boolean scanned) { this.scanned = scanned; }

    /**
     * Get associated product (lazy loading)
     */
    public Product getProduct(DataAdapter dataAdapter) {
        if (product == null) {
            product = dataAdapter.loadProduct(this.productID);
        }
        return product;
    }

    /**
     * Calculate total cost based on quantity and unit price
     */
    public void calculateCost() {
        this.cost = this.quantity * this.unitPrice;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "orderID=" + orderID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", cost=" + cost +
                ", scanned=" + scanned +
                '}';
    }
}