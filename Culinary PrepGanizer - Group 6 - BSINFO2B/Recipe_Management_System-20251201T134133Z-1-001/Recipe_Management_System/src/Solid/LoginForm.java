package Solid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;
    
    // Food-centered color palette matching RecipeApp
    private static final Color CREAM_BACKGROUND = new Color(255, 250, 240);
    private static final Color BURNT_ORANGE = new Color(230, 115, 50);
    private static final Color OLIVE_GREEN = new Color(138, 154, 91);
    private static final Color TERRACOTTA = new Color(204, 102, 68);
    private static final Color SOFT_BEIGE = new Color(245, 235, 220);
    private static final Color DEEP_BROWN = new Color(101, 67, 33);
    private static final Color ACCENT_RED = new Color(192, 57, 43);
    
    public LoginForm() {
        setTitle("Culinary PrepGanizer - Login");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CREAM_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CREAM_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        JLabel titleLabel = new JLabel("Welcome Back, Chef!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLabel.setForeground(BURNT_ORANGE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Login to access your recipes", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Georgia", Font.ITALIC, 14));
        subtitleLabel.setForeground(TERRACOTTA);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        panel.add(subtitleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Username Label
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel userLabel = new JLabel("Email Address:");
        userLabel.setForeground(DEEP_BROWN);
        userLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        panel.add(userLabel, gbc);
        
        // Username Field
        gbc.gridy = 3;
        usernameField = new JTextField();
        usernameField.setFont(new Font("Georgia", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(350, 40));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(DEEP_BROWN);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OLIVE_GREEN, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(usernameField, gbc);
        
        // Password Label
        gbc.gridy = 4;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(DEEP_BROWN);
        passLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        panel.add(passLabel, gbc);
        
        // Password Field
        gbc.gridy = 5;
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Georgia", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(350, 40));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(DEEP_BROWN);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OLIVE_GREEN, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(passwordField, gbc);
        
        // Login Button
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginButton = new JButton("Login to Recipe Manager");
        loginButton.setFont(new Font("Georgia", Font.BOLD, 16));
        loginButton.setBackground(BURNT_ORANGE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(350, 50));
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(loginButton, gbc);
        
        // Register Link
        gbc.gridy = 7;
        gbc.insets = new Insets(15, 10, 5, 10);
        registerLabel = new JLabel("<HTML><U>Don't have an account? Register here</U></HTML>");
        registerLabel.setForeground(TERRACOTTA);
        registerLabel.setFont(new Font("Georgia", Font.PLAIN, 13));
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(registerLabel, gbc);
        
        mainPanel.add(panel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Login Action
        loginButton.addActionListener(e -> performLogin());
        
        passwordField.addActionListener(e -> performLogin());
        
        // Register Link Action
        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        new RegisterForm().setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error opening registration form: " + e.getMessage());
                    }
                });
            }
            public void mouseEntered(MouseEvent evt) {
                registerLabel.setForeground(BURNT_ORANGE);
            }
            public void mouseExited(MouseEvent evt) {
                registerLabel.setForeground(TERRACOTTA);
            }
        });
        
        // Button Hover Effect
        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                loginButton.setBackground(TERRACOTTA);
            }
            public void mouseExited(MouseEvent evt) {
                loginButton.setBackground(BURNT_ORANGE);
            }
        });
    }
    
    	private void performLogin() {
    	    String username = usernameField.getText().trim();
    	    String password = new String(passwordField.getPassword());
    	    
    	    if (username.isEmpty() || password.isEmpty()) {
    	        JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
    	        return;
    	    }
    	    
    	    String hashedPassword = hashPassword(password);
    	    
    	    if (authenticateUser(username, hashedPassword)) {
    	        // REMOVE pop-up message
    	        dispose();
    	        SwingUtilities.invokeLater(() -> new RecipeApp().setVisible(true));
    	    } else {
    	        JOptionPane.showMessageDialog(this, "Invalid email or password.\nPlease try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
    	        passwordField.setText("");
    	    }
    	}

    
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private boolean authenticateUser(String email, String hashedPassword) {
        String sql = "SELECT 1 FROM tbl_registration WHERE TRIM(username)=? AND password=?";
        try (Connection conn = DBconnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Failed to set Nimbus L&F.");
        }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}