package com.retailstore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static AppConfig instance;
    private Properties properties;

    private AppConfig() {
        loadProperties();
    }

    public static AppConfig getInstance() {
        if (instance == null) {
            synchronized (AppConfig.class) {
                if (instance == null) {
                    instance = new AppConfig();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                System.err.println("Unable to find application.properties");
                // Load default values if properties file not found
                loadDefaultProperties();
                return;
            }
            properties.load(input);
            System.out.println("Configuration loaded successfully");
        } catch (IOException ex) {
            System.err.println("Error loading configuration: " + ex.getMessage());
            loadDefaultProperties();
        }
    }

    private void loadDefaultProperties() {
        // MySQL defaults
        properties.setProperty("mysql.url", "jdbc:mysql://localhost:3306/retail_store3?useSSL=false&allowPublicKeyRetrieval=true");
        properties.setProperty("mysql.username", "root");
        properties.setProperty("mysql.password", "fm92mhziczac");
        
        // MongoDB defaults
        properties.setProperty("mongodb.host", "localhost");
        properties.setProperty("mongodb.port", "27017");
        properties.setProperty("mongodb.database", "retail_store2");
        
        // Redis defaults
        properties.setProperty("redis.host", "redis-11107.c323.us-east-1-2.ec2.redns.redis-cloud.com");
        properties.setProperty("redis.port", "11107");
        properties.setProperty("redis.password", "fm92mhziczac");
        
        // Application defaults
        properties.setProperty("app.name", "Store Management System");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.tax.rate", "0.10");
        
        // UI defaults
        properties.setProperty("ui.theme", "system");
        properties.setProperty("ui.font.default", "Arial");
        properties.setProperty("ui.font.size.default", "12");
        properties.setProperty("ui.font.size.title", "24");
        properties.setProperty("ui.window.width.default", "1000");
        properties.setProperty("ui.window.height.default", "700");
        
        // Business logic defaults
        properties.setProperty("business.reorder.point.default", "10.0");
        properties.setProperty("business.auto.refresh.interval", "5000");
        properties.setProperty("business.order.timeout.minutes", "30");
        
        // Cache defaults
        properties.setProperty("cache.best.products.limit", "10");
        properties.setProperty("cache.recent.customers.limit", "10");
        properties.setProperty("cache.ttl.minutes", "60");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    // MySQL Configuration
    public String getMysqlUrl() {
        return getProperty("mysql.url");
    }

    public String getMysqlUsername() {
        return getProperty("mysql.username");
    }

    public String getMysqlPassword() {
        return getProperty("mysql.password");
    }

    // MongoDB Configuration
    public String getMongoHost() {
        return getProperty("mongodb.host");
    }

    public int getMongoPort() {
        return getIntProperty("mongodb.port", 27017);
    }

    public String getMongoDatabase() {
        return getProperty("mongodb.database");
    }

    // Redis Configuration
    public String getRedisHost() {
        return getProperty("redis.host");
    }

    public int getRedisPort() {
        return getIntProperty("redis.port", 6379);
    }

    public String getRedisPassword() {
        return getProperty("redis.password");
    }

    // Application Configuration
    public String getAppName() {
        return getProperty("app.name");
    }

    public String getAppVersion() {
        return getProperty("app.version");
    }

    public double getTaxRate() {
        return getDoubleProperty("app.tax.rate", 0.10);
    }

    // UI Configuration
    public String getUiTheme() {
        return getProperty("ui.theme");
    }

    public String getDefaultFont() {
        return getProperty("ui.font.default");
    }

    public int getDefaultFontSize() {
        return getIntProperty("ui.font.size.default", 12);
    }

    public int getTitleFontSize() {
        return getIntProperty("ui.font.size.title", 24);
    }

    public int getDefaultWindowWidth() {
        return getIntProperty("ui.window.width.default", 1000);
    }

    public int getDefaultWindowHeight() {
        return getIntProperty("ui.window.height.default", 700);
    }

    // Business Logic Configuration
    public double getDefaultReorderPoint() {
        return getDoubleProperty("business.reorder.point.default", 10.0);
    }

    public int getAutoRefreshInterval() {
        return getIntProperty("business.auto.refresh.interval", 5000);
    }

    public int getOrderTimeoutMinutes() {
        return getIntProperty("business.order.timeout.minutes", 30);
    }

    // Cache Configuration
    public int getBestProductsLimit() {
        return getIntProperty("cache.best.products.limit", 10);
    }

    public int getRecentCustomersLimit() {
        return getIntProperty("cache.recent.customers.limit", 10);
    }

    public int getCacheTtlMinutes() {
        return getIntProperty("cache.ttl.minutes", 60);
    }
}