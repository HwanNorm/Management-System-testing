// ===== Orders.java =====
package com.retailstore.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Order entity representing customer purchases
 */
public class Orders {
    private int orderID;
    private int customerID;
    private int cashierID;
    private PaymentMethod paymentMethod;
    private double totalCost;
    private double totalTax;
    private String date;
    private String status;
    private List<OrderLine> lines;

    /**
     * Default constructor
     */
    public Orders() {
        lines = new ArrayList<>();
        status = "PENDING";
    }

    /**
     * Constructor with customer
     */
    public Orders(int customerID) {
        this();
        this.customerID = customerID;
    }

    // Getters and setters
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public int getCustomerID() { return customerID; }
    public void setCustomerID(int customerID) { this.customerID = customerID; }

    public int getCashierID() { return cashierID; }
    public void setCashierID(int cashierID) { this.cashierID = cashierID; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { 
        if (totalCost < 0) throw new IllegalArgumentException("Total cost cannot be negative");
        this.totalCost = totalCost; 
    }

    public double getTotalTax() { return totalTax; }
    public void setTotalTax(double totalTax) { 
        if (totalTax < 0) throw new IllegalArgumentException("Total tax cannot be negative");
        this.totalTax = totalTax; 
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<OrderLine> getLines() { return lines; }

    // Order line management
    public void addLine(OrderLine line) {
        if (line == null) throw new IllegalArgumentException("Order line cannot be null");
        lines.add(line);
        line.setOrderID(this.orderID);
    }

    public void removeLine(OrderLine line) {
        lines.remove(line);
    }

    public void clearLines() {
        lines.clear();
    }

    /**
     * Calculate subtotal (before tax) from order lines
     */
    public double getSubtotal() {
        double subtotal = 0.0;
        for (OrderLine line : lines) {
            subtotal += line.getCost();
        }
        return subtotal;
    }

    /**
     * Calculate total number of items in order
     */
    public double getTotalItems() {
        double total = 0.0;
        for (OrderLine line : lines) {
            total += line.getQuantity();
        }
        return total;
    }

    /**
     * Check if order is empty
     */
    public boolean isEmpty() {
        return lines.isEmpty();
    }

    /**
     * Check if all items are scanned (for cashier processing)
     */
    public boolean allItemsScanned() {
        for (OrderLine line : lines) {
            if (!line.isScanned()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate and set totals based on order lines
     */
    public void calculateTotals() {
        double subtotal = getSubtotal();
        this.totalTax = subtotal * 0.1; // 10% tax rate
        this.totalCost = subtotal + this.totalTax;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", customerID=" + customerID +
                ", cashierID=" + cashierID +
                ", paymentMethod=" + paymentMethod +
                ", totalCost=" + totalCost +
                ", totalTax=" + totalTax +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", lines=" + lines.size() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Orders order = (Orders) obj;
        return orderID == order.orderID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(orderID);
    }
}