// ===== Product.java =====
package com.retailstore.model;

/**
 * Product entity representing store inventory items
 */
public class Product {
    private int productID;
    private int sellerID;
    private String name;
    private double price;
    private double quantity;
    private String barcode;
    private String category;
    private String description;

    /**
     * Default constructor
     */
    public Product() {}

    /**
     * Constructor with essential fields
     */
    public Product(String name, double price, double quantity, String category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Getters and setters
    public int getProductID() { return productID; }
    public void setProductID(int productID) { this.productID = productID; }

    public int getSellerID() { return sellerID; }
    public void setSellerID(int sellerID) { this.sellerID = sellerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { 
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.price = price; 
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { 
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        this.quantity = quantity; 
    }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Check if product is in stock
     */
    public boolean isInStock() {
        return quantity > 0;
    }

    /**
     * Check if product is low stock (less than 10 units)
     */
    public boolean isLowStock() {
        return quantity > 0 && quantity < 10;
    }

    /**
     * Get stock status as string
     */
    public String getStockStatus() {
        if (quantity == 0) return "Out of Stock";
        if (quantity < 10) return "Low Stock";
        return "In Stock";
    }

    @Override
    public String toString() {
        return "Product{" +
                "productID=" + productID +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return productID == product.productID;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(productID);
    }
}