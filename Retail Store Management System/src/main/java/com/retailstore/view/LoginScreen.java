package com.retailstore.view;

import com.retailstore.Application;
import com.retailstore.AppConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginScreen extends JFrame {
    private JTextField txtUserName = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JButton btnLogin = new JButton("Login");
    private AppConfig config;

    public LoginScreen() {
        config = AppConfig.getInstance();
        
        this.setTitle(config.getAppName() + " - Login");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(config.getDefaultWindowWidth() / 2, config.getDefaultWindowHeight() / 2);
        this.setResizable(false);  // Prevent resizing to maintain layout

        // Create main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        // Add logo/title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setMaximumSize(new Dimension(400, 100));
        JLabel titleLabel = new JLabel(config.getAppName());
        titleLabel.setFont(new Font(config.getDefaultFont(), Font.BOLD, config.getTitleFontSize()));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Create form panel with GridBagLayout for better control
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setMaximumSize(new Dimension(400, 150));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font(config.getDefaultFont(), Font.PLAIN, config.getDefaultFontSize() + 2));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        txtUserName.setPreferredSize(new Dimension(200, 30));
        txtUserName.setFont(new Font(config.getDefaultFont(), Font.PLAIN, config.getDefaultFontSize() + 2));
        // Fix text visibility
        txtUserName.setForeground(Color.BLACK);
        txtUserName.setBackground(Color.WHITE);
        gbc.gridx = 1;
        formPanel.add(txtUserName, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font(config.getDefaultFont(), Font.PLAIN, config.getDefaultFontSize() + 2));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        txtPassword.setPreferredSize(new Dimension(200, 30));
        txtPassword.setFont(new Font(config.getDefaultFont(), Font.PLAIN, config.getDefaultFontSize() + 2));
        // Fix text visibility
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setBackground(Color.WHITE);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setMaximumSize(new Dimension(400, 50));
        btnLogin.setPreferredSize(new Dimension(200, 35));
        btnLogin.setFont(new Font(config.getDefaultFont(), Font.BOLD, config.getDefaultFontSize() + 2));
        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setMaximumSize(new Dimension(400, 60));

        JLabel infoLabel1 = new JLabel("Welcome to " + config.getAppName());
        infoLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel1.setFont(new Font(config.getDefaultFont(), Font.PLAIN, config.getDefaultFontSize()));

        JLabel infoLabel2 = new JLabel("Please enter your credentials to login");
        infoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel2.setFont(new Font(config.getDefaultFont(), Font.ITALIC, config.getDefaultFontSize()));

        infoPanel.add(infoLabel1);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(infoLabel2);

        mainPanel.add(infoPanel);

        // Add main panel to frame
        this.add(mainPanel);

        // Center on screen
        this.setLocationRelativeTo(null);

        // Set default button
        this.getRootPane().setDefaultButton(btnLogin);

        // Ensure proper rendering of all components
        this.validate();
    }

    // Getters
    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JTextField getTxtUserName() {
        return txtUserName;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }
}