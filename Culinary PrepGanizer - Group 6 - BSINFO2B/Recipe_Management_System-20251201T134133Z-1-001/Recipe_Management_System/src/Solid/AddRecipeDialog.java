package Solid;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddRecipeDialog extends JDialog {
    private JTextField recipeNameField;
    private JTextArea ingredientsArea;
    private JTextArea stepsArea;
    private JLabel imagePreviewLabel;
    private JButton selectImageButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    private String selectedImagePath;
    private RecipeManager manager;

    private static final Color DARK_BLUE = new Color(30, 45, 60);
    private static final Color ACCENT_COLOR = new Color(100, 181, 246);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);

    public AddRecipeDialog(JFrame parent, RecipeManager manager) {
        super(parent, "Add New Recipe", true);
        this.manager = manager;
        
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(DARK_BLUE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(DARK_BLUE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Add New Recipe");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Recipe Name
        JPanel namePanel = new JPanel(new BorderLayout(5, 5));
        namePanel.setBackground(DARK_BLUE);
        JLabel nameLabel = new JLabel("Recipe Name:");
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        recipeNameField = new JTextField();
        recipeNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recipeNameField.setPreferredSize(new Dimension(400, 35));
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(recipeNameField, BorderLayout.CENTER);
        mainPanel.add(namePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Ingredients
        JPanel ingredientsPanel = new JPanel(new BorderLayout(5, 5));
        ingredientsPanel.setBackground(DARK_BLUE);
        JLabel ingredientsLabel = new JLabel("Ingredients (one per line, format: 'name - quantity'):");
        ingredientsLabel.setForeground(TEXT_COLOR);
        ingredientsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ingredientsArea = new JTextArea(5, 40);
        ingredientsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScroll = new JScrollPane(ingredientsArea);
        ingredientsPanel.add(ingredientsLabel, BorderLayout.NORTH);
        ingredientsPanel.add(ingredientsScroll, BorderLayout.CENTER);
        mainPanel.add(ingredientsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Steps
        JPanel stepsPanel = new JPanel(new BorderLayout(5, 5));
        stepsPanel.setBackground(DARK_BLUE);
        JLabel stepsLabel = new JLabel("Cooking Steps (one per line):");
        stepsLabel.setForeground(TEXT_COLOR);
        stepsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stepsArea = new JTextArea(6, 40);
        stepsArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stepsArea.setLineWrap(true);
        stepsArea.setWrapStyleWord(true);
        JScrollPane stepsScroll = new JScrollPane(stepsArea);
        stepsPanel.add(stepsLabel, BorderLayout.NORTH);
        stepsPanel.add(stepsScroll, BorderLayout.CENTER);
        mainPanel.add(stepsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Image Selection
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setBackground(DARK_BLUE);
        
        selectImageButton = new JButton("📷 Select Recipe Image");
        selectImageButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        selectImageButton.setBackground(ACCENT_COLOR);
        selectImageButton.setForeground(DARK_BLUE);
        selectImageButton.addActionListener(e -> selectImage());
        
        imagePreviewLabel = new JLabel("No image selected", SwingConstants.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(200, 150));
        imagePreviewLabel.setBackground(new Color(55, 71, 79));
        imagePreviewLabel.setForeground(TEXT_COLOR);
        imagePreviewLabel.setOpaque(true);
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR, 2));
        
        imagePanel.add(selectImageButton, BorderLayout.NORTH);
        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);
        mainPanel.add(imagePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(DARK_BLUE);
        
        saveButton = new JButton("✅ Save Recipe");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(180, 45));
        saveButton.addActionListener(e -> saveRecipe());
        
        cancelButton = new JButton("❌ Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(180, 45));
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();
            
            // Show preview
            ImageIcon icon = new ImageIcon(selectedImagePath);
            Image image = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(image));
            imagePreviewLabel.setText(null);
            
            System.out.println("✅ Image selected: " + selectedImagePath);
        }
    }

    private void saveRecipe() {
        String recipeName = recipeNameField.getText().trim();
        String ingredientsText = ingredientsArea.getText().trim();
        String stepsText = stepsArea.getText().trim();

        // Validation
        if (recipeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a recipe name!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ingredientsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter at least one ingredient!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (stepsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter cooking steps!", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Parse ingredients
        List<Ingredient> ingredients = new ArrayList<>();
        String[] ingredientLines = ingredientsText.split("\n");
        
        for (String line : ingredientLines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split("-");
            if (parts.length >= 2) {
                String name = parts[0].trim();
                String quantity = parts[1].trim();
                ingredients.add(new Ingredient(name, quantity));
            } else {
                // If no dash, treat whole line as ingredient with "as needed" quantity
                ingredients.add(new Ingredient(line, "as needed"));
            }
        }

        // Parse steps
        List<String> steps = new ArrayList<>();
        String[] stepLines = stepsText.split("\n");
        
        for (String line : stepLines) {
            line = line.trim();
            if (!line.isEmpty()) {
                steps.add(line);
            }
        }

        // Create recipe
        Recipe newRecipe = new Recipe(recipeName, ingredients, steps);
        
        // Add to manager
        manager.addCustomRecipe(newRecipe, selectedImagePath);

        JOptionPane.showMessageDialog(this, 
            "✅ Recipe '" + recipeName + "' added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        dispose();
    }
}