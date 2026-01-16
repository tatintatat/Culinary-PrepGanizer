package Solid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RegisterForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JLabel loginLabel;
    
    // Food-centered color palette matching RecipeApp
    private static final Color CREAM_BACKGROUND = new Color(255, 250, 240);
    private static final Color BURNT_ORANGE = new Color(230, 115, 50);
    private static final Color OLIVE_GREEN = new Color(138, 154, 91);
    private static final Color TERRACOTTA = new Color(204, 102, 68);
    private static final Color SOFT_BEIGE = new Color(245, 235, 220);
    private static final Color DEEP_BROWN = new Color(101, 67, 33);
    private static final Color ACCENT_RED = new Color(192, 57, 43);
    
    // Gmail credentials
    private static final String SENDER_EMAIL = "marcivancastro@gmail.com";
    private static final String APP_PASSWORD = "gykr eenb bmsd fxil";
    
    public RegisterForm() {
        setTitle("Culinary PrepGanizer - Register");
        setSize(550, 650);
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
        JLabel titleLabel = new JLabel("Join Recipe Manager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 28));
        titleLabel.setForeground(BURNT_ORANGE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Create your chef account today", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Georgia", Font.ITALIC, 14));
        subtitleLabel.setForeground(TERRACOTTA);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 20, 10);
        panel.add(subtitleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Email Label
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setForeground(DEEP_BROWN);
        emailLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        panel.add(emailLabel, gbc);
        
        // Email Field
        gbc.gridy = 3;
        emailField = new JTextField();
        emailField.setFont(new Font("Georgia", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(350, 40));
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(DEEP_BROWN);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OLIVE_GREEN, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(emailField, gbc);
        
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
        
        // Confirm Password Label
        gbc.gridy = 6;
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setForeground(DEEP_BROWN);
        confirmLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        panel.add(confirmLabel, gbc);
        
        // Confirm Password Field
        gbc.gridy = 7;
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Georgia", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(350, 40));
        confirmPasswordField.setBackground(Color.WHITE);
        confirmPasswordField.setForeground(DEEP_BROWN);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OLIVE_GREEN, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(confirmPasswordField, gbc);
        
        // Password Requirements
        gbc.gridy = 8;
        gbc.insets = new Insets(8, 10, 10, 10);
        JLabel requirementsLabel = new JLabel("<html><div style='text-align: center; color: #654321;'>" +
                "<b>Password Requirements:</b><br>" +
                "- At least 8 characters<br>" +
                "- Contains uppercase and lowercase letters<br>" +
                "- Contains at least one number</div></html>");
        requirementsLabel.setFont(new Font("Georgia", Font.PLAIN, 11));
        panel.add(requirementsLabel, gbc);
        
        // Register Button
        gbc.gridy = 9;
        gbc.insets = new Insets(15, 10, 10, 10);
        registerButton = new JButton("Create Account");
        registerButton.setFont(new Font("Georgia", Font.BOLD, 16));
        registerButton.setBackground(OLIVE_GREEN);
        registerButton.setForeground(Color.WHITE);
        registerButton.setPreferredSize(new Dimension(350, 50));
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(registerButton, gbc);
        
        // Login Link
        gbc.gridy = 10;
        gbc.insets = new Insets(15, 10, 5, 10);
        loginLabel = new JLabel("<HTML><U>Already have an account? Login here</U></HTML>");
        loginLabel.setForeground(TERRACOTTA);
        loginLabel.setFont(new Font("Georgia", Font.PLAIN, 13));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(loginLabel, gbc);
        
        mainPanel.add(panel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Register Action
        registerButton.addActionListener(e -> performRegistration());
        
        // Login Link Action
        loginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            }
            public void mouseEntered(MouseEvent evt) {
                loginLabel.setForeground(BURNT_ORANGE);
            }
            public void mouseExited(MouseEvent evt) {
                loginLabel.setForeground(TERRACOTTA);
            }
        });
        
        // Button Hover Effect
        registerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                registerButton.setBackground(BURNT_ORANGE);
            }
            public void mouseExited(MouseEvent evt) {
                registerButton.setBackground(OLIVE_GREEN);
            }
        });
    }
    
    private void performRegistration() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address!", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Password Mismatch", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            return;
        }
        if (password.length() < 8 || !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "Password must meet all requirements!", "Weak Password", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (emailExists(email)) {
            JOptionPane.showMessageDialog(this, "This email is already registered!", "Email Exists", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String hashedPassword = hashPassword(password);
        
        if (saveToDatabase(email, hashedPassword)) {
            sendWelcomeEmail(email);
            JOptionPane.showMessageDialog(this, "Registration successful!\n\nA welcome email has been sent to: " + email + "\n\nYou can now login!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
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
    
    private boolean emailExists(String email) {
        String sql = "SELECT 1 FROM tbl_registration WHERE TRIM(username)=?";
        try (Connection conn = DBconnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean saveToDatabase(String email, String hashedPassword) {
        String sql = "INSERT INTO tbl_registration (username, password, registration_date) VALUES (?, ?, ?)";
        try (Connection conn = DBconnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            int result = ps.executeUpdate();
            System.out.println("User registered: " + email);
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void sendWelcomeEmail(String recipientEmail) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                
                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                    }
                });   
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Welcome to Recipe Manager!");      
                String emailBody =
                    "============================================\n" +
                    "   WELCOME TO RECIPE MANAGER!\n" +
                    "============================================\n\n" +
                    "Dear Chef,\n\n" +
                    "Thank you for registering with Recipe Manager!\n" +
                    "Your culinary journey begins now!\n\n" +
                    "--------------------------------------------\n" +
                    "     ACCOUNT INFORMATION\n" +
                    "--------------------------------------------\n\n" +
                    "Email: " + recipientEmail + "\n\n" +
                    "--------------------------------------------\n\n" +
                    "What you can do:\n" +
                    "- Browse delicious recipes\n" +
                    "- Follow step-by-step instructions\n" +
                    "- Manage your ingredient stock\n" +
                    "- Track cooking costs\n\n" +
                    "Login now and start cooking!\n\n" +
                    "Happy Cooking!\n\n" +
                    "Best regards,\n" +
                    "The Recipe Manager Team\n\n" +
                    "============================================\n" +
                    "IMPORTANT: For your security, your password\n" +
                    "is safely encrypted and not sent via email.\n" +
                    "============================================";
                message.setText(emailBody);
                Transport.send(message);
                System.out.println("Welcome email sent to: " + recipientEmail);
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Failed to set Nimbus L&F.");
        }
        SwingUtilities.invokeLater(() -> new RegisterForm().setVisible(true));
    }
}