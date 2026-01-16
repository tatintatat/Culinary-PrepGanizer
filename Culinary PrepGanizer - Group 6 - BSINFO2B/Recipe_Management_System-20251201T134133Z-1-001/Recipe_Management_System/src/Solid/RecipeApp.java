package Solid;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Timer;

public class RecipeApp extends JFrame {
    private RecipeManager manager;
    private JTextField searchField;
    private JList<Recipe> recipeList;
    private JTextArea detailArea;
    private DefaultListModel<Recipe> listModel;
    private Recipe selectedRecipe;
    private JButton nextStepButton;
    private int currentStepIndex;
    private JTextArea stockArea;
    private JLabel recipeImageLabel;
    private JComboBox<String> sortComboBox;
    private String currentSortOption = "Name (A-Z)";
    
    private JTextField ingredientNameField;
    private JTextField ingredientQuantityField;
    private JTextField ingredientMeasurementField;
    private JTextField ingredientPriceField;	
    private JButton addIngredientButton;
    private JButton removeIngredientButton;
    private JButton logoutButton;
    private JToggleButton darkModeToggle;
    private JButton viewCostsButton;

    // Cost tracking
    private Map<String, Double> ingredientCosts = new HashMap<>();
    private double totalSpent = 0.0;

    // Light mode colors
    private static final Color LIGHT_CREAM_BG = new Color(255, 250, 240);
    private static final Color LIGHT_BURNT_ORANGE = new Color(230, 115, 50);
    private static final Color LIGHT_OLIVE_GREEN = new Color(138, 154, 91);
    private static final Color LIGHT_TERRACOTTA = new Color(204, 102, 68);
    private static final Color LIGHT_SOFT_BEIGE = new Color(245, 235, 220);
    private static final Color LIGHT_DEEP_BROWN = new Color(101, 67, 33);
    private static final Color LIGHT_ACCENT_RED = new Color(192, 57, 43);
    private static final Color LIGHT_WARM_YELLOW = new Color(241, 196, 15);

    // Dark mode colors
    private static final Color DARK_BG = new Color(30, 30, 35);
    private static final Color DARK_PANEL_BG = new Color(40, 40, 45);
    private static final Color DARK_ACCENT_ORANGE = new Color(255, 140, 70);
    private static final Color DARK_ACCENT_GREEN = new Color(120, 200, 120);
    private static final Color DARK_ACCENT_BLUE = new Color(100, 150, 255);
    private static final Color DARK_TEXT = new Color(230, 230, 230);
    private static final Color DARK_ACCENT_RED = new Color(220, 80, 80);
    private static final Color DARK_ACCENT_YELLOW = new Color(255, 200, 70);

    // Current theme colors (instance)
    private Color CREAM_BACKGROUND;
    private Color BURNT_ORANGE;
    private Color OLIVE_GREEN;
    private Color TERRACOTTA;
    private Color SOFT_BEIGE;
    private Color DEEP_BROWN;
    private Color ACCENT_RED;
    private Color WARM_YELLOW;
    private Color TEXT_COLOR;
    private Color CARD_BG;

    private boolean isDarkMode = false;
    private boolean isTransitioning = false;

    // Store current search text and selected recipe index across theme toggles
    private String currentSearchText = "";
    private int selectedRecipeIndex = -1;

    // Prevent consuming ingredients multiple times for the same cooking session
    private boolean ingredientsConsumed = false;
    private String consumptionDetails = "";
    
    // Track current animation timers to cancel them when switching recipes
    private Timer currentAnimationTimer = null;
    private Recipe currentlyAnimatingRecipe = null;

    public RecipeApp() {
        manager = new RecipeManager();
        applyLightMode();

        setTitle("Culinary PrepGanizer");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        getContentPane().setBackground(CREAM_BACKGROUND);

        setupNorthPanel();
        setupCenterPanel();
        setupSouthPanel();
        performSearch();
    }

    private void applyLightMode() {
        CREAM_BACKGROUND = LIGHT_CREAM_BG;
        BURNT_ORANGE = LIGHT_BURNT_ORANGE;
        OLIVE_GREEN = LIGHT_OLIVE_GREEN;
        TERRACOTTA = LIGHT_TERRACOTTA;
        SOFT_BEIGE = LIGHT_SOFT_BEIGE;
        DEEP_BROWN = LIGHT_DEEP_BROWN;
        ACCENT_RED = LIGHT_ACCENT_RED;
        WARM_YELLOW = LIGHT_WARM_YELLOW;
        TEXT_COLOR = LIGHT_DEEP_BROWN;
        CARD_BG = Color.WHITE;
    }

    private void applyDarkMode() {
        CREAM_BACKGROUND = DARK_BG;
        BURNT_ORANGE = DARK_ACCENT_ORANGE;
        OLIVE_GREEN = DARK_ACCENT_GREEN;
        TERRACOTTA = DARK_ACCENT_BLUE;
        SOFT_BEIGE = DARK_PANEL_BG;
        DEEP_BROWN = DARK_TEXT;
        ACCENT_RED = DARK_ACCENT_RED;
        WARM_YELLOW = DARK_ACCENT_YELLOW;
        TEXT_COLOR = DARK_TEXT;
        CARD_BG = DARK_PANEL_BG;
    }

    private void toggleDarkMode() {
        if (isTransitioning) return;
        
        // Save current state
        if (searchField != null) currentSearchText = searchField.getText();
        if (recipeList != null) selectedRecipeIndex = recipeList.getSelectedIndex();

        isDarkMode = !isDarkMode;
        
        // Animate the transition
        animateThemeTransition();
    }

    // Custom JPanel class for fade effect
    private class FadePanel extends JPanel {
        private float alpha = 0.0f;
        private Color fadeColor;
        private float rippleProgress = 0.0f;
        private Point rippleCenter;
        
        public FadePanel(Color color) {
            this.fadeColor = color;
            setOpaque(false);
            // Center the ripple effect
            this.rippleCenter = new Point(500, 350); // Will be updated with actual center
        }
        
        public void setAlpha(float alpha) {
            this.alpha = alpha;
            repaint();
        }
        
        public void setRippleProgress(float progress) {
            this.rippleProgress = progress;
            repaint();
        }
        
        public void setRippleCenter(Point center) {
            this.rippleCenter = center;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int width = getWidth();
            int height = getHeight();
            
            if (rippleProgress > 0 && rippleProgress < 1) {
                // Ripple effect - expanding circle from center
                int maxRadius = (int) Math.sqrt(width * width + height * height);
                int currentRadius = (int) (maxRadius * rippleProgress);
                
                // Create a radial gradient for smooth ripple
                float[] fractions = {0.0f, 0.7f, 1.0f};
                Color[] colors = {
                    new Color(fadeColor.getRed(), fadeColor.getGreen(), fadeColor.getBlue(), (int)(255 * alpha)),
                    new Color(fadeColor.getRed(), fadeColor.getGreen(), fadeColor.getBlue(), (int)(180 * alpha)),
                    new Color(fadeColor.getRed(), fadeColor.getGreen(), fadeColor.getBlue(), 0)
                };
                
                if (currentRadius > 0) {
                    RadialGradientPaint rippleGradient = new RadialGradientPaint(
                        rippleCenter,
                        currentRadius,
                        fractions,
                        colors
                    );
                    
                    g2d.setPaint(rippleGradient);
                    g2d.fillRect(0, 0, width, height);
                }
            } else {
                // Standard fade effect
                int centerX = width / 2;
                int centerY = height / 2;
                float radius = (float) Math.sqrt(centerX * centerX + centerY * centerY);
                
                RadialGradientPaint gradient = new RadialGradientPaint(
                    centerX, centerY, radius,
                    new float[]{0.0f, 1.0f},
                    new Color[]{fadeColor, fadeColor.darker()}
                );
                
                g2d.setPaint(gradient);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.fillRect(0, 0, width, height);
            }
            
            g2d.dispose();
        }
    }

    // Enhanced theme transition with multiple animation stages
    private void animateThemeTransition() {
        if (isTransitioning) return;
        isTransitioning = true;
        
        // Get the center point of the toggle button for ripple effect
        Point buttonLocation = darkModeToggle.getLocationOnScreen();
        Point frameLocation = getLocationOnScreen();
        Point rippleCenter = new Point(
            buttonLocation.x - frameLocation.x + darkModeToggle.getWidth() / 2,
            buttonLocation.y - frameLocation.y + darkModeToggle.getHeight() / 2
        );
        
        // Set new colors
        if (isDarkMode) applyDarkMode();
        else applyLightMode();
        
        Color newBg = CREAM_BACKGROUND;
        
        // Create enhanced glass pane
        FadePanel fadePanel = new FadePanel(newBg);
        fadePanel.setRippleCenter(rippleCenter);
        setGlassPane(fadePanel);
        fadePanel.setVisible(true);
        
        // Stage 1: Ripple effect expanding from button
        Timer rippleTimer = new Timer(8, null);
        final long rippleStartTime = System.currentTimeMillis();
        final int rippleDuration = 500;
        
        rippleTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - rippleStartTime;
                float progress = Math.min(1.0f, (float) elapsed / rippleDuration);
                
                // Ease-out cubic for smooth deceleration
                float easedProgress = 1 - (float) Math.pow(1 - progress, 3);
                
                fadePanel.setRippleProgress(easedProgress);
                fadePanel.setAlpha(easedProgress);
                
                if (progress >= 1.0f) {
                    ((Timer) e.getSource()).stop();
                    
                    // Stage 2: Rebuild UI while fully covered
                    refreshUI();
                    
                    // Stage 3: Fade out with subtle pulse
                    Timer fadeOutTimer = new Timer(8, null);
                    final long outStartTime = System.currentTimeMillis();
                    final int outDuration = 450;
                    
                    fadeOutTimer.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e2) {
                            long outElapsed = System.currentTimeMillis() - outStartTime;
                            float outProgress = Math.min(1.0f, (float) outElapsed / outDuration);
                            
                            // Ease-in-out for smooth start and end
                            float easedOut = outProgress < 0.5f
                                ? 4 * outProgress * outProgress * outProgress
                                : 1 - (float) Math.pow(-2 * outProgress + 2, 3) / 2;
                            
                            float outAlpha = 1.0f - easedOut;
                            
                            // Add subtle pulse effect at the end
                            if (outProgress > 0.7f) {
                                float pulsePhase = (outProgress - 0.7f) / 0.3f;
                                float pulse = (float) (Math.sin(pulsePhase * Math.PI * 3) * 0.1f * (1 - pulsePhase));
                                outAlpha += pulse;
                                outAlpha = Math.max(0, Math.min(1, outAlpha));
                            }
                            
                            fadePanel.setRippleProgress(1.0f); // Keep ripple at full
                            fadePanel.setAlpha(outAlpha);
                            
                            if (outProgress >= 1.0f) {
                                ((Timer) e2.getSource()).stop();
                                fadePanel.setVisible(false);
                                isTransitioning = false;
                                
                                // Final touch: subtle glow on the toggle button
                                flashToggleButton();
                            }
                        }
                    });
                    fadeOutTimer.start();
                }
            }
        });
        rippleTimer.start();
    }

    private void refreshUI() {
        // Cancel any ongoing animations before rebuilding UI
        if (currentAnimationTimer != null && currentAnimationTimer.isRunning()) {
            currentAnimationTimer.stop();
            currentAnimationTimer = null;
        }
        
        getContentPane().removeAll();
        setupNorthPanel();
        setupCenterPanel();
        setupSouthPanel();
        getContentPane().setBackground(CREAM_BACKGROUND);

        // Restore search text
        if (searchField != null) searchField.setText(currentSearchText);

        // Restore selection & details
        performSearch();
        if (selectedRecipeIndex >= 0 && selectedRecipeIndex < listModel.getSize()) {
            recipeList.setSelectedIndex(selectedRecipeIndex);
            Recipe restoredRecipe = recipeList.getSelectedValue();
            if (restoredRecipe != null) {
                selectedRecipe = restoredRecipe;
                currentlyAnimatingRecipe = restoredRecipe;
                
                // Reload the correct recipe image for the selected recipe
                String imagePath = getRecipeImagePath(restoredRecipe.getName());
                if (imagePath != null) {
                    new ImageLoaderWorker(imagePath, recipeImageLabel).execute();
                } else {
                    recipeImageLabel.setIcon(null);
                    recipeImageLabel.setText("<html><body style='text-align: center;'>No image available for " + restoredRecipe.getName() + "</body></html>");
                }
                displayRecipeDetails(restoredRecipe);
            }
        }

        revalidate();
        repaint();
    }
    

    // Add a subtle flash effect to the toggle button after transition
    private void flashToggleButton() {
        Timer flashTimer = new Timer(50, null);
        final Color originalColor = darkModeToggle.getBackground();
        final Color glowColor = brightenColor(originalColor, 1.4f);
        
        flashTimer.addActionListener(new ActionListener() {
            int count = 0;
            float intensity = 1.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 8) {
                    intensity = (float) Math.sin((count / 8.0f) * Math.PI);
                    Color currentColor = interpolateColor(originalColor, glowColor, intensity * 0.5f);
                    darkModeToggle.setBackground(currentColor);
                    count++;
                } else {
                    darkModeToggle.setBackground(originalColor);
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        flashTimer.start();
    }

    // Custom panel class for slide effect
    private class SlidePanel extends JPanel {
        private float slideProgress = 0.0f;
        private Color slideColor;
        
        public SlidePanel(Color color) {
            this.slideColor = color;
            setOpaque(false);
        }
        
        public void setSlideProgress(float progress) {
            this.slideProgress = progress;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int width = getWidth();
            int height = getHeight();
            
            // Slide from left to right
            int slideWidth = (int) (width * slideProgress);
            
            // Create gradient for smooth transition
            GradientPaint gradient = new GradientPaint(
                slideWidth - 100, 0, new Color(slideColor.getRed(), slideColor.getGreen(), slideColor.getBlue(), 0),
                slideWidth, 0, slideColor
            );
            
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, slideWidth + 100, height);
            
            g2d.dispose();
        }
    }

    // Alternative: Slide transition effect (optional - can be added as a different mode)
    private void animateThemeTransitionSlide() {
        if (isTransitioning) return;
        isTransitioning = true;
        
        // Set new colors
        if (isDarkMode) applyDarkMode();
        else applyLightMode();
        
        Color newBg = CREAM_BACKGROUND;
        
        // Create slide panel
        SlidePanel slidePanel = new SlidePanel(newBg);
        setGlassPane(slidePanel);
        slidePanel.setVisible(true);
        
        Timer slideTimer = new Timer(10, null);
        final long startTime = System.currentTimeMillis();
        final int duration = 600;
        
        slideTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, (float) elapsed / duration);
                
                // Ease-in-out cubic
                float easedProgress = progress < 0.5f
                    ? 4 * progress * progress * progress
                    : 1 - (float) Math.pow(-2 * progress + 2, 3) / 2;
                
                slidePanel.setSlideProgress(easedProgress);
                
                if (progress >= 0.5f && progress < 0.55f) {
                    // Rebuild UI at midpoint
                    refreshUI();
                }
                
                if (progress >= 1.0f) {
                    ((Timer) e.getSource()).stop();
                    slidePanel.setVisible(false);
                    isTransitioning = false;
                }
            }
        });
        slideTimer.start();
    }
    private JButton createAnimatedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        // Store original color
        final Color originalBg = bgColor;
        final Color hoverBg = brightenColor(bgColor, 1.2f);
        
        // Add hover animation
        button.addMouseListener(new MouseAdapter() {
            Timer hoverTimer;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(20, null);
                hoverTimer.addActionListener(new ActionListener() {
                    float progress = 0.0f;
                    
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        progress += 0.1f;
                        if (progress >= 1.0f) {
                            progress = 1.0f;
                            ((Timer) ae.getSource()).stop();
                        }
                        
                        Color interpolated = interpolateColor(originalBg, hoverBg, progress);
                        button.setBackground(interpolated);
                    }
                });
                hoverTimer.start();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(20, null);
                hoverTimer.addActionListener(new ActionListener() {
                    float progress = 0.0f;
                    
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        progress += 0.1f;
                        if (progress >= 1.0f) {
                            progress = 1.0f;
                            ((Timer) ae.getSource()).stop();
                        }
                        
                        Color currentBg = button.getBackground();
                        Color interpolated = interpolateColor(currentBg, originalBg, progress);
                        button.setBackground(interpolated);
                    }
                });
                hoverTimer.start();
            }
        });
        
        return button;
    }

    private Color brightenColor(Color color, float factor) {
        int r = Math.min(255, (int)(color.getRed() * factor));
        int g = Math.min(255, (int)(color.getGreen() * factor));
        int b = Math.min(255, (int)(color.getBlue() * factor));
        return new Color(r, g, b);
    }

    private Color interpolateColor(Color start, Color end, float progress) {
        int r = (int)(start.getRed() + (end.getRed() - start.getRed()) * progress);
        int g = (int)(start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
        int b = (int)(start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
        return new Color(r, g, b);
    }

    //Pwesto at design ng asa itaas na panel
    private void setupNorthPanel() {
        JPanel northPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        northPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        northPanel.setBackground(CREAM_BACKGROUND);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(CREAM_BACKGROUND);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Georgia", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_COLOR);

        searchField = new JTextField("");
        searchField.setForeground(TEXT_COLOR);
        searchField.setCaretColor(BURNT_ORANGE);
        searchField.setBackground(CARD_BG);
        searchField.setFont(new Font("Georgia", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OLIVE_GREEN, 2),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { performSearch(); }
            public void removeUpdate(DocumentEvent e) { performSearch(); }
            public void insertUpdate(DocumentEvent e) { performSearch(); }
        });

        JButton searchButton = createAnimatedButton("Search", BURNT_ORANGE, Color.WHITE);
        searchButton.setFont(new Font("Georgia", Font.BOLD, 13));
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // New Sort Panel
        JPanel sortPanel = new JPanel(new BorderLayout(5, 0));
        sortPanel.setBackground(CREAM_BACKGROUND);
        
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Georgia", Font.BOLD, 12));
        sortLabel.setForeground(TEXT_COLOR);
        
        String[] sortOptions = {"Name (A-Z)", "Name (Z-A)", "Ingredient Count"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(new Font("Georgia", Font.PLAIN, 12));
        sortComboBox.setBackground(CARD_BG);
        sortComboBox.setForeground(TEXT_COLOR);
        sortComboBox.addActionListener(e -> {
            currentSortOption = (String) sortComboBox.getSelectedItem();
            performSearch();
        });
        
        sortPanel.add(sortLabel, BorderLayout.WEST);
        sortPanel.add(sortComboBox, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        buttonPanel.setBackground(CREAM_BACKGROUND);

        darkModeToggle = (JToggleButton) createAnimatedToggleButton(
            isDarkMode ? "Light Mode" : "Dark Mode", TERRACOTTA, Color.WHITE);
        darkModeToggle.setSelected(isDarkMode);
        darkModeToggle.setFont(new Font("Georgia", Font.BOLD, 12));
        darkModeToggle.addActionListener(e -> toggleDarkMode());

        viewCostsButton = createAnimatedButton("View Costs", WARM_YELLOW, DEEP_BROWN);
        viewCostsButton.setFont(new Font("Georgia", Font.BOLD, 12));
        viewCostsButton.addActionListener(e -> showCostReport());

        logoutButton = createAnimatedButton("Logout", ACCENT_RED, Color.WHITE);
        logoutButton.setFont(new Font("Georgia", Font.BOLD, 12));
        logoutButton.addActionListener(e -> performLogout());

        buttonPanel.add(darkModeToggle);
        buttonPanel.add(viewCostsButton);
        buttonPanel.add(logoutButton);

        northPanel.add(searchPanel);
        northPanel.add(sortPanel);  // Changed from empty JLabel
        northPanel.add(buttonPanel);

        add(northPanel, BorderLayout.NORTH);
    }
//------------------------------------------------------------------------------------------------
    
 // Insertion Sort Algorithm
    private void sortRecipes(List<Recipe> recipes, String sortOption) {
        if (recipes == null || recipes.size() <= 1) return;
        
        for (int i = 1; i < recipes.size(); i++) {
            Recipe key = recipes.get(i);
            int j = i - 1;
            
            // Compare based on sort option
            while (j >= 0 && shouldSwap(recipes.get(j), key, sortOption)) {
                recipes.set(j + 1, recipes.get(j));
                j--;
            }
            recipes.set(j + 1, key);
        }
    }
 //=================================================================================================
    private boolean shouldSwap(Recipe current, Recipe next, String sortOption) {
        switch (sortOption) {
            case "Name (A-Z)":
                return current.getName().compareToIgnoreCase(next.getName()) > 0;
            case "Name (Z-A)":
                return current.getName().compareToIgnoreCase(next.getName()) < 0;
            case "Ingredient Count":
                return current.getIngredients().size() > next.getIngredients().size();
            default:
                return false;
        }
    }
 //--------------------------------------------------------------------------------------------------
    private JToggleButton createAnimatedToggleButton(String text, Color bgColor, Color fgColor) {
        JToggleButton button = new JToggleButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        
        final Color originalBg = bgColor;
        final Color hoverBg = brightenColor(bgColor, 1.2f);
        
        button.addMouseListener(new MouseAdapter() {
            Timer hoverTimer;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(20, null);
                hoverTimer.addActionListener(new ActionListener() {
                    float progress = 0.0f;
                    
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        progress += 0.1f;
                        if (progress >= 1.0f) {
                            progress = 1.0f;
                            ((Timer) ae.getSource()).stop();
                        }
                        
                        Color interpolated = interpolateColor(originalBg, hoverBg, progress);
                        button.setBackground(interpolated);
                    }
                });
                hoverTimer.start();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(20, null);
                hoverTimer.addActionListener(new ActionListener() {
                    float progress = 0.0f;
                    
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        progress += 0.1f;
                        if (progress >= 1.0f) {
                            progress = 1.0f;
                            ((Timer) ae.getSource()).stop();
                        }
                        
                        Color currentBg = button.getBackground();
                        Color interpolated = interpolateColor(currentBg, originalBg, progress);
                        button.setBackground(interpolated);
                    }
                });
                hoverTimer.start();
            }
        });
        
        return button;
    }
    //Pwesto at design ng asa
    private void setupCenterPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        splitPane.setBackground(CREAM_BACKGROUND);

        listModel = new DefaultListModel<>();
        recipeList = new JList<>(listModel);
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        recipeList.setBackground(SOFT_BEIGE);
        recipeList.setForeground(TEXT_COLOR);
        recipeList.setFont(new Font("Georgia", Font.PLAIN, 14));
        recipeList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Recipe) {
                    setText(((Recipe) value).getName());
                    setFont(new Font("Georgia", Font.BOLD, 15));
                }
                setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                if (isSelected) {
                    c.setBackground(BURNT_ORANGE);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(index % 2 == 0 ? SOFT_BEIGE : CREAM_BACKGROUND);
                    c.setForeground(TEXT_COLOR);
                }
                return c;
            }
        });

        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Cancel any ongoing animations immediately
                if (currentAnimationTimer != null && currentAnimationTimer.isRunning()) {
                    currentAnimationTimer.stop();
                    currentAnimationTimer = null;
                }
                
                selectedRecipe = recipeList.getSelectedValue();
                if (selectedRecipe != null) {
                    // Update the currently animating recipe marker
                    currentlyAnimatingRecipe = selectedRecipe;
                    displayRecipeDetails(selectedRecipe);
                }
            }
        });

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TERRACOTTA, 3),
                "Recipe Collection",
                0, 0,
                new Font("Georgia", Font.BOLD, 16),
                TERRACOTTA));
        listPanel.add(new JScrollPane(recipeList), BorderLayout.CENTER);
        listPanel.setBackground(CREAM_BACKGROUND);

        recipeImageLabel = new JLabel("<html><body style='text-align: center;'>Select a recipe to view photo</body></html>", SwingConstants.CENTER);
        recipeImageLabel.setPreferredSize(new Dimension(300, 210));
        recipeImageLabel.setBackground(SOFT_BEIGE);
        recipeImageLabel.setForeground(TEXT_COLOR);
        recipeImageLabel.setOpaque(true);
        recipeImageLabel.setFont(new Font("Georgia", Font.ITALIC, 13));
        recipeImageLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(OLIVE_GREEN, 3),
                        "Recipe Photo",
                        0, 0,
                        new Font("Georgia", Font.BOLD, 15),
                        OLIVE_GREEN),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font("Georgia", Font.PLAIN, 13));
        detailArea.setBackground(CARD_BG);
        detailArea.setForeground(TEXT_COLOR);
        detailArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel detailPanelRight = new JPanel(new BorderLayout());
        detailPanelRight.setBackground(CREAM_BACKGROUND);

        JPanel detailTextPanel = new JPanel(new BorderLayout());
        detailTextPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(BURNT_ORANGE, 3),
                "Recipe Details & Instructions",
                0, 0,
                new Font("Georgia", Font.BOLD, 15),
                BURNT_ORANGE));
        detailTextPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);

        detailPanelRight.add(recipeImageLabel, BorderLayout.NORTH);
        detailPanelRight.add(detailTextPanel, BorderLayout.CENTER);

        splitPane.setLeftComponent(listPanel);
        splitPane.setRightComponent(detailPanelRight);
        splitPane.setDividerLocation(300);

        add(splitPane, BorderLayout.CENTER);
    }

    //--------------------------------------------------------------------------------
    //Pwesto at design ng asa Baba
    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        southPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        southPanel.setBackground(CREAM_BACKGROUND);

        JPanel instructionPanel = new JPanel(new BorderLayout());
        instructionPanel.setBackground(CREAM_BACKGROUND);
        nextStepButton = createAnimatedButton("Start Cooking", OLIVE_GREEN, Color.WHITE);
        nextStepButton.setEnabled(false);
        nextStepButton.setFont(new Font("Georgia", Font.BOLD, 15));
        nextStepButton.addActionListener(e -> showNextStep());
        instructionPanel.add(nextStepButton, BorderLayout.CENTER);

        JPanel managementPanel = new JPanel(new GridBagLayout());
        managementPanel.setBackground(SOFT_BEIGE);
        managementPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(TERRACOTTA, 3),
                "Manage Pantry",
                0, 0,
                new Font("Georgia", Font.BOLD, 15),
                TERRACOTTA));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setForeground(TEXT_COLOR);
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; managementPanel.add(nameLabel, gbc);

        ingredientNameField = new JTextField(8);
        ingredientNameField.setFont(new Font("Georgia", Font.PLAIN, 11));
        ingredientNameField.setBackground(CARD_BG);
        ingredientNameField.setForeground(TEXT_COLOR);
        ingredientNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OLIVE_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7; managementPanel.add(ingredientNameField, gbc);

        JLabel quantityLabel = new JLabel("Qty:");
        quantityLabel.setForeground(TEXT_COLOR);
        quantityLabel.setFont(new Font("Georgia", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3; managementPanel.add(quantityLabel, gbc);

        ingredientQuantityField = new JTextField(8);
        ingredientQuantityField.setFont(new Font("Georgia", Font.PLAIN, 11));
        ingredientQuantityField.setBackground(CARD_BG);
        ingredientQuantityField.setForeground(TEXT_COLOR);
        ingredientQuantityField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OLIVE_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7; managementPanel.add(ingredientQuantityField, gbc);

        JLabel measurementLabel = new JLabel("Unit:");
        measurementLabel.setForeground(TEXT_COLOR);
        measurementLabel.setFont(new Font("Georgia", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3; managementPanel.add(measurementLabel, gbc);

        ingredientMeasurementField = new JTextField(8);
        ingredientMeasurementField.setFont(new Font("Georgia", Font.PLAIN, 11));
        ingredientMeasurementField.setBackground(CARD_BG);
        ingredientMeasurementField.setForeground(TEXT_COLOR);
        ingredientMeasurementField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OLIVE_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7; managementPanel.add(ingredientMeasurementField, gbc);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(TEXT_COLOR);
        priceLabel.setFont(new Font("Georgia", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3; managementPanel.add(priceLabel, gbc);

        ingredientPriceField = new JTextField(8);
        ingredientPriceField.setFont(new Font("Georgia", Font.PLAIN, 11));
        ingredientPriceField.setBackground(CARD_BG);
        ingredientPriceField.setForeground(TEXT_COLOR);
        ingredientPriceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OLIVE_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        ));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7; managementPanel.add(ingredientPriceField, gbc);

        addIngredientButton = createAnimatedButton("Add to Pantry", OLIVE_GREEN, Color.WHITE);
        addIngredientButton.setFont(new Font("Georgia", Font.BOLD, 11));
        addIngredientButton.addActionListener(e -> addIngredientToStock());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; managementPanel.add(addIngredientButton, gbc);

        removeIngredientButton = createAnimatedButton("Remove from Pantry", WARM_YELLOW, DEEP_BROWN);
        removeIngredientButton.setFont(new Font("Georgia", Font.BOLD, 11));
        removeIngredientButton.addActionListener(e -> removeIngredientFromStock());
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; managementPanel.add(removeIngredientButton, gbc);

        stockArea = new JTextArea(5, 20);
        stockArea.setEditable(false);
        stockArea.setFont(new Font("Georgia", Font.PLAIN, 12));
        stockArea.setBackground(CARD_BG);
        stockArea.setForeground(TEXT_COLOR);
        stockArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(BURNT_ORANGE, 3),
                        "Pantry Stock",
                        0, 0,
                        new Font("Georgia", Font.BOLD, 15),
                        BURNT_ORANGE),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        updateStockArea();

        southPanel.add(instructionPanel);
        southPanel.add(managementPanel);
        southPanel.add(new JScrollPane(stockArea));

        add(southPanel, BorderLayout.SOUTH);
    }
    //--------------------------------------------------------------------------------
    //Logic para dun sa quantity
    private double parseQuantity(String quantityStr) throws NumberFormatException {
        quantityStr = quantityStr.trim();
        
        if (quantityStr.contains("/")) {
            String[] parts = quantityStr.split("/");
            if (parts.length == 2) {
                double numerator = Double.parseDouble(parts[0].trim());
                double denominator = Double.parseDouble(parts[1].trim());
                if (denominator == 0) {
                    throw new NumberFormatException("Cannot divide by zero");
                }
                return numerator / denominator;
            }
        }
        
        return Double.parseDouble(quantityStr);
    }

    private class ImageLoaderWorker extends SwingWorker<ImageIcon, Void> {
        private final String imagePath;
        private final JLabel label;

        public ImageLoaderWorker(String imagePath, JLabel label) {
            this.imagePath = imagePath;
            this.label = label;
            label.setIcon(null);
            label.setText("<html><body style='text-align: center;'>Loading image...</body></html>");
        }

        @Override
        protected ImageIcon doInBackground() {
            return scaleImageFromFile(imagePath, 300, 190);
        }

        @Override
        protected void done() {
            try {
                ImageIcon icon = get();
                if (icon != null) {
                    label.setIcon(icon);
                    label.setText(null);
                } else {
                    label.setIcon(null);
                    label.setText("<html><body style='text-align: center;'>Image not found</body></html>");
                }
            } catch (Exception e) {
                label.setIcon(null);
                label.setText("<html><body style='text-align: center;'>Error loading image</body></html>");
            }
        }
    }

    //Add Ingredients Logic
    private void addIngredientToStock() {
        String name = ingredientNameField.getText().trim();
        String quantity = ingredientQuantityField.getText().trim();
        String measurement = ingredientMeasurementField.getText().trim();
        String priceStr = ingredientPriceField.getText().trim();

        if (name.isEmpty() || quantity.isEmpty() || measurement.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name, quantity, and unit.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!quantity.matches("^[0-9]+([./][0-9]+)?$")) {
            JOptionPane.showMessageDialog(this, "Quantity must be numbers only (e.g., 2, 1.5, 1/2)", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!measurement.matches("^[a-zA-Z\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Unit must be letters only", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double price = 0.0;
        if (!priceStr.isEmpty()) {
            if (!priceStr.matches("^[0-9]+(\\.[0-9]+)?$")) {
                JOptionPane.showMessageDialog(this, "Price must be numbers only", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            price = Double.parseDouble(priceStr);
        }

        try {
            String normalizedName = normalizeIngredientName(name);
            double newQty = parseQuantity(quantity);
            
            Ingredient existingIngredient = null;
            for (Ingredient ing : manager.getUserStock()) {
                if (ing.getName().trim().equalsIgnoreCase(normalizedName)) {
                    String existingQtyStr = ing.getQuantity().trim();
                    String[] existingParts = existingQtyStr.split("\\s+", 2);
                    
                    if (existingParts.length > 1) {
                        String existingUnit = existingParts[1].trim();
                        
                        if (UnitConverter.areUnitsCompatible(existingUnit, measurement)) {
                            existingIngredient = ing;
                            break;
                        }
                    }
                }
            }

            if (existingIngredient != null) {
                String existingQtyStr = existingIngredient.getQuantity().trim();
                String[] existingParts = existingQtyStr.split("\\s+", 2);
                
                if (existingParts.length < 1) {
                    JOptionPane.showMessageDialog(this, "Error parsing existing quantity", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double existingQty = parseQuantity(existingParts[0]);
                String existingUnit = existingParts.length > 1 ? existingParts[1].trim() : "";

                double convertedQty = UnitConverter.convert(newQty, measurement, existingUnit);
                if (convertedQty < 0) {
                    JOptionPane.showMessageDialog(this, "Error converting units!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double totalQty = existingQty + convertedQty;

                manager.getUserStock().remove(existingIngredient);
                String updatedQuantity = String.format("%.2f", totalQty) + " " + existingUnit;
                Ingredient updatedIngredient = new Ingredient(normalizedName, updatedQuantity);
                manager.addToStock(updatedIngredient);

                if (price > 0) {
                    ingredientCosts.put(normalizedName.toLowerCase(), price);
                    totalSpent += price;
                }

                updateStockArea();

                ingredientNameField.setText("");
                ingredientQuantityField.setText("");
                ingredientMeasurementField.setText("");
                ingredientPriceField.setText("");

                String msg = "Updated " + normalizedName + "!\n\nAdded: " + newQty + " " + measurement;
                if (!existingUnit.equalsIgnoreCase(measurement)) {
                    msg += " (" + String.format("%.2f", convertedQty) + " " + existingUnit + ")";
                }
                msg += "\nNew total: " + String.format("%.2f", totalQty) + " " + existingUnit;
                
                if (price > 0) {
                    msg += "\nCost tracked: P" + String.format("%.2f", price);
                }
                
                JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);

            } else {
                String fullQuantity = quantity + " " + measurement;
                Ingredient newIngredient = new Ingredient(normalizedName, fullQuantity);
                manager.addToStock(newIngredient);

                if (price > 0) {
                    ingredientCosts.put(normalizedName.toLowerCase(), price);
                    totalSpent += price;
                }

                updateStockArea();

                ingredientNameField.setText("");
                ingredientQuantityField.setText("");
                ingredientMeasurementField.setText("");
                ingredientPriceField.setText("");

                String msg = normalizedName + " (" + fullQuantity + ") added to pantry!";
                if (price > 0) {
                    msg += "\nCost tracked: P" + String.format("%.2f", price);
                }
                JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            if (selectedRecipe != null) displayRecipeDetails(selectedRecipe);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //Remove Ingredients Logic
    private void removeIngredientFromStock() {
        String nameToRemove = ingredientNameField.getText().trim();
        String quantityToRemove = ingredientQuantityField.getText().trim();
        String measurementToRemove = ingredientMeasurementField.getText().trim();

        if (nameToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ingredient name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (quantityToRemove.isEmpty() || measurementToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter quantity and unit.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!quantityToRemove.matches("^[0-9]+([./][0-9]+)?$")) {
            JOptionPane.showMessageDialog(this, "Quantity must be numbers only (e.g., 2, 1.5, 1/2)", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!measurementToRemove.matches("^[a-zA-Z\\s]+$")) {
            JOptionPane.showMessageDialog(this, "Unit must be letters only", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ingredient foundIngredient = null;
        for (Ingredient ing : manager.getUserStock()) {
            if (ing.getName().trim().equalsIgnoreCase(nameToRemove)) {
                foundIngredient = ing;
                break;
            }
        }

        if (foundIngredient == null) {
            JOptionPane.showMessageDialog(this, nameToRemove + " not found in pantry.", "Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String currentQuantityStr = foundIngredient.getQuantity().trim();
        String[] parts = currentQuantityStr.split("\\s+", 2);
        if (parts.length < 1) {
            JOptionPane.showMessageDialog(this, "Error parsing quantity for " + nameToRemove, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double currentQuantity = parseQuantity(parts[0]);
            String currentUnit = parts.length > 1 ? parts[1].trim() : "";
            double removeQuantity = parseQuantity(quantityToRemove);

            if (!UnitConverter.areUnitsCompatible(currentUnit, measurementToRemove.trim())) {
                JOptionPane.showMessageDialog(this,
                        "Unit mismatch or incompatible units!\n\nStock has: " + currentUnit + "\nYou're removing: " + measurementToRemove + "\n\nPlease use compatible units.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double removeInPantryUnit = UnitConverter.convert(removeQuantity, measurementToRemove.trim(), currentUnit);
            
            if (removeInPantryUnit < 0) {
                JOptionPane.showMessageDialog(this,
                        "Error converting units!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (removeInPantryUnit > currentQuantity) {
                JOptionPane.showMessageDialog(this,
                        "Insufficient quantity!\n\nAvailable: " + currentQuantity + " " + currentUnit + 
                        "\nTrying to remove: " + removeQuantity + " " + measurementToRemove + 
                        " (" + String.format("%.2f", removeInPantryUnit) + " " + currentUnit + ")",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double remainingQuantity = currentQuantity - removeInPantryUnit;
            manager.getUserStock().remove(foundIngredient);

            if (remainingQuantity > 0.01) {
                String newQuantityStr = String.format("%.2f", remainingQuantity) + " " + currentUnit;
                Ingredient updatedIngredient = new Ingredient(nameToRemove, newQuantityStr);
                manager.addToStock(updatedIngredient);

                String msg = "Successfully removed " + removeQuantity + " " + measurementToRemove + " of " + nameToRemove;
                if (!currentUnit.equalsIgnoreCase(measurementToRemove.trim())) {
                    msg += "\n(" + String.format("%.2f", removeInPantryUnit) + " " + currentUnit + " from stock)";
                }
                msg += "\n\nRemaining: " + String.format("%.2f", remainingQuantity) + " " + currentUnit;
                
                JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Removed all " + nameToRemove + " from pantry\n\n(Removed " + currentQuantity + " " + currentUnit + ")",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            updateStockArea();
            ingredientNameField.setText("");
            ingredientQuantityField.setText("");
            ingredientMeasurementField.setText("");

            if (selectedRecipe != null) displayRecipeDetails(selectedRecipe);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error parsing quantity values.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Design, Pwesto at Logic ng Show Cost
    private void showCostReport() {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("     INGREDIENT COST TRACKER\n");
        report.append("========================================\n\n");

        if (ingredientCosts.isEmpty()) {
            report.append("No costs tracked yet.\n\n");
            report.append("Add ingredients with prices to start tracking your cooking expenses!");
        } else {
            report.append("TRACKED INGREDIENTS:\n");
            report.append("----------------------------------------\n");

            ingredientCosts.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> {
                        String name = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
                        report.append(String.format("%-25s PHP%.2f\n", name, entry.getValue()));
                    });

            report.append("\n========================================\n");
            report.append(String.format("TOTAL SPENT: PHP%.2f\n", totalSpent));
            report.append("========================================\n\n");
            report.append("Tip: Track prices when adding ingredients\n");
            report.append("to monitor your cooking expenses!");
        }

        JTextArea reportArea = new JTextArea(report.toString());
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Georgia", Font.PLAIN, 13));
        reportArea.setBackground(CARD_BG);
        reportArea.setForeground(TEXT_COLOR);
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Cost Tracker Report", JOptionPane.INFORMATION_MESSAGE);
    }

    //Para sa Logout
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
        }
    }
//-------------------------------------------------------------------------------------
    
    private ImageIcon scaleImageFromFile(String imagePath, int width, int height) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        try {
            File file = new File(imagePath);
            if (!file.exists()) return null;
            Image image = ImageIO.read(file);

            if (image != null) {
                Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (IOException e) {
            System.err.println("Error reading image file: " + imagePath + " - " + e.getMessage());
            return null;
        }
        return null;
    }

    private String getRecipeImagePath(String recipeName) {
        if (recipeName == null) return null;
        switch (recipeName.toLowerCase()) {
            case "chicken adobo": return "image/Adobo_DSCF4391.jpg";
            case "pork sinigang": return "image/sinigang-baboy-7.jpg";
            case "beef caldereta": return "image/spicy-beef-caldereta-2-1152x1536.jpg";
            case "lumpia shanghai": return "image/Lumpiang-Shanghai-Fried-Spring-Rolls-featured-image.jpg";
            case "chicken tinola": return "image/Chicken_Tinola.jpg";
            case "american hamburger": return "image/american-hamburger-foreman-grill.jpg";
            case "spaghetti bolognese": return "image/Spaghetti-Bolognese-square-FS-0204.jpg";
            case "chicken tacos": return "image/Crock-Pot-Chicken-Tacos-16.jpg";
            case "california roll sushi": return "image/japanese-california-sushi-roll-recipe-1749130724.jpg";
            case "french toast": return "image/french-toast.jpg";
            default: return null;
        }
    }

    private void performSearch() {
        listModel.clear();
        String concept = searchField.getText();
        List<Recipe> results = manager.searchRecipes(concept);
        
        // Apply sorting using insertion sort
        sortRecipes(results, currentSortOption);
        
        results.forEach(listModel::addElement);

        if (!results.isEmpty()) {
            // Only auto-select first recipe if we're not restoring state
            if (selectedRecipeIndex < 0 || selectedRecipeIndex >= results.size()) {
                recipeList.setSelectedIndex(0);
            }
            // If we're restoring, the selection will be set by refreshUI()
        } else {
            detailArea.setText("No recipes found for '" + concept + "'.\n\nTry searching for:\n- Adobo\n- Sinigang\n- Caldereta\n\nOr leave blank to see all recipes!");
            recipeImageLabel.setIcon(null);
            recipeImageLabel.setText("<html><body style='text-align: center;'>Select a recipe to view photo</body></html>");
            nextStepButton.setEnabled(false);
            nextStepButton.setText("Start Cooking");
        }
    }
    //Para sa recipe details
    private void displayRecipeDetails(Recipe recipe) {
        if (recipe == null) return;
        selectedRecipe = recipe;
        try {
            recipe.displayInstructions();
        } catch (Exception ignore) {
        }
        currentStepIndex = 0;
        ingredientsConsumed = false;
        consumptionDetails = "";
        nextStepButton.setEnabled(false);
        nextStepButton.setText("Loading recipe...");

        List<Ingredient> missing = recipe.checkMissingIngredients(manager.getUserStock());
        final boolean isCookable = missing.isEmpty();

        String imagePath = getRecipeImagePath(recipe.getName());
        if (imagePath != null) {
            new ImageLoaderWorker(imagePath, recipeImageLabel).execute();
        } else {
            recipeImageLabel.setIcon(null);
            recipeImageLabel.setText("<html><body style='text-align: center;'>No image available for " + recipe.getName() + "</body></html>");
        }
        detailArea.setText("");
        StringBuilder details = new StringBuilder();
        details.append("============================================\n");
        details.append("  ").append(recipe.getName().toUpperCase()).append("\n");
        details.append("============================================\n\n");
        if (recipe instanceof FilipinoRecipe fr) {
            details.append("REGIONAL VARIANT: ").append(fr.getRegionalVariant()).append("\n\n");
        }
        details.append("REQUIRED INGREDIENTS:\n");
        details.append("--------------------------------------------\n");
        recipe.getIngredients().forEach(ing -> details.append("  - ").append(ing).append("\n"));
        details.append("\n");
        if (!missing.isEmpty()) {
            details.append("MISSING FROM YOUR PANTRY:\n");
            details.append("--------------------------------------------\n");
            missing.forEach(ing -> details.append("  ✗ ").append(ing.getName()).append(" (need ").append(ing.getQuantity()).append(")\n"));
            details.append("\nTip: Add these ingredients to your pantry!\n");
            details.append("   (Compatible units like liters/cups are supported)\n\n");
        } else {
            details.append("✓ SUCCESS: You have all the ingredients!\n");
            details.append("✓ Ready to start cooking!\n\n");
        }
        details.append("============================================\n");
        details.append("COOKING INSTRUCTIONS\n");
        details.append("============================================\n\n");
        if (recipe.getSteps().isEmpty()) {
            details.append("No steps available for this recipe.\n");
            detailArea.setText(details.toString());
            nextStepButton.setEnabled(false);
            nextStepButton.setText("No Steps");
            return;
        } else {
            details.append("STEP 1:\n").append(recipe.getSteps().get(0)).append("\n");
        }
        animateTextReveal(details.toString(), recipe, isCookable);
    }
//--------------------------------------------------------------------------------------------
    
    private void animateTextReveal(String fullText, Recipe recipe, boolean cookable) {
        // Cancel any existing animation timer
        if (currentAnimationTimer != null && currentAnimationTimer.isRunning()) {
            currentAnimationTimer.stop();
            currentAnimationTimer = null;
        }
        
        Timer timer = new Timer(20, null);
        currentAnimationTimer = timer; // Store reference to current timer
        currentlyAnimatingRecipe = recipe; // Track which recipe is being animated
        final int chunkSize = 7;

        timer.addActionListener(new ActionListener() {
            int currentChar = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Check if this animation should still be running
                if (currentlyAnimatingRecipe != recipe) {
                    ((Timer) e.getSource()).stop();
                    return;
                }
                
                if (currentChar < fullText.length()) {
                    int endIndex = Math.min(currentChar + chunkSize, fullText.length());
                    detailArea.setText(fullText.substring(0, endIndex));
                    currentChar = endIndex;

                    detailArea.setCaretPosition(detailArea.getDocument().getLength());
                } else {
                    // Stop the timer FIRST to prevent any repetition
                    ((Timer) e.getSource()).stop();
                    
                    // Clear the timer reference
                    if (currentAnimationTimer == e.getSource()) {
                        currentAnimationTimer = null;
                    }

                    if (cookable) {
                        nextStepButton.setEnabled(true);
                        nextStepButton.setText("Start Cooking");
                        nextStepButton.setBackground(OLIVE_GREEN.brighter());
                        Timer flashTimer = new Timer(200, null);
                        flashTimer.addActionListener(new ActionListener() {
                            int count = 0;
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                if (count < 3) {
                                    nextStepButton.setBackground(count % 2 == 0 ? OLIVE_GREEN : OLIVE_GREEN.brighter());
                                    count++;
                                } else {
                                    nextStepButton.setBackground(OLIVE_GREEN);
                                    ((Timer) evt.getSource()).stop();
                                }
                            }
                        });
                        flashTimer.start();
                    } else {
                        // Enable the button but change appearance to indicate missing ingredients
                        nextStepButton.setEnabled(true);
                        nextStepButton.setText("Start Cooking (Missing Items)");
                        nextStepButton.setBackground(ACCENT_RED);
                    }
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }


    private void showNextStep() {
        if (selectedRecipe == null || selectedRecipe.getSteps().isEmpty()) return;

        // Check for missing ingredients when user first clicks "Start Cooking"
        if (currentStepIndex == 0) {
            List<Ingredient> missing = selectedRecipe.checkMissingIngredients(manager.getUserStock());
            
            // If there are missing ingredients, show the dialog and don't proceed
            if (!missing.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot start cooking ").append(selectedRecipe.getName()).append("\n\n");
                sb.append("Missing ingredients:\n\n");
                missing.forEach(ing -> 
                    sb.append(" ✗ ").append(ing.getName()).append(" - need ").append(ing.getQuantity()).append("\n"));
                sb.append("\nPlease add these ingredients to your pantry first.\n");
                sb.append("   Note: Compatible units (liters/cups, kg/g, etc.) work!");
                
                JOptionPane.showMessageDialog(this, sb.toString(), 
                    "Missing Ingredients", JOptionPane.WARNING_MESSAGE);
                
                // Don't proceed with cooking
                return;
            }
        }

        if (currentStepIndex < selectedRecipe.getSteps().size() - 1) {
            nextStepButton.setEnabled(false);
            nextStepButton.setText("Loading next step...");

            currentStepIndex++;

            String currentText = detailArea.getText();

            // Find where the steps section starts
            final String headerToken = "COOKING INSTRUCTIONS\n============================================\n\n";
            int stepSectionIndex = currentText.indexOf(headerToken);
            String header;
            if (stepSectionIndex >= 0) {
                header = currentText.substring(0, stepSectionIndex + headerToken.length());
            } else {
                int idx = currentText.indexOf("STEP 1:");
                header = idx >= 0 ? currentText.substring(0, idx) : "";
            }

            // Build the new steps text showing all steps up to current
            StringBuilder newSteps = new StringBuilder();
            for (int i = 0; i <= currentStepIndex; i++) {
                newSteps.append("STEP ").append(i + 1).append(":\n").append(selectedRecipe.getSteps().get(i)).append("\n\n");
            }

            final String targetText = header + newSteps.toString();

            // Animate only the new content being added
            animateStepTransition(currentText, targetText);

        } else {
            // Recipe is complete - NOW consume the ingredients
            nextStepButton.setText("Recipe Complete!");
            nextStepButton.setBackground(OLIVE_GREEN.darker());

            Timer completeTimer = new Timer(100, null);
            completeTimer.addActionListener(new ActionListener() {
                int pulseCount = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (pulseCount < 6) {
                        Color color = pulseCount % 2 == 0 ? OLIVE_GREEN.darker() : OLIVE_GREEN.brighter();
                        nextStepButton.setBackground(color);
                        pulseCount++;
                    } else {
                        ((Timer) e.getSource()).stop();
                        nextStepButton.setEnabled(false);
                        
                        // NOW consume ingredients after recipe is complete
                        if (!ingredientsConsumed) {
                            boolean consumed = consumeIngredientsIfAvailable(selectedRecipe);
                            if (consumed) {
                                StringBuilder finalMessage = new StringBuilder();
                                finalMessage.append("Congratulations! \n\n");
                                finalMessage.append(selectedRecipe.getName()).append(" is ready to serve!\n\n");
                                
                                if (!consumptionDetails.isEmpty()) {
                                    finalMessage.append("━━━━━━━━━━━━━━━━━━━━━━\n\n");
                                    finalMessage.append(consumptionDetails);
                                    finalMessage.append("\n━━━━━━━━━━━━━━━━━━━━━━\n\n");
                                }
                                
                                finalMessage.append("Enjoy your meal! 🍽");
                                
                                JOptionPane.showMessageDialog(RecipeApp.this,
                                        finalMessage.toString(),
                                        "Cooking Complete!",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            // Already consumed (shouldn't happen, but just in case)
                            JOptionPane.showMessageDialog(RecipeApp.this,
                                    " " + selectedRecipe.getName() + " is ready! \n\nEnjoy your meal! ",
                                    "Cooking Complete!",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            });
            completeTimer.start();
        }
    }
    
    private void animateStepTransition(String fromText, String toText) {
        // Simply append the new content without trying to find common prefix
        // This prevents the flickering issue
        
        final String newContent = toText.substring(fromText.length());

        Timer timer = new Timer(5, null);
        final int chunkSize = 3; // Increased chunk size for smoother animation
        timer.addActionListener(new ActionListener() {
            int currentChar = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentChar < newContent.length()) {
                    int endIndex = Math.min(currentChar + chunkSize, newContent.length());
                    detailArea.setText(fromText + newContent.substring(0, endIndex));
                    currentChar = endIndex;
                    detailArea.setCaretPosition(detailArea.getDocument().getLength());
                } else {
                    ((Timer) e.getSource()).stop();
                    nextStepButton.setEnabled(true);

                    if (currentStepIndex < selectedRecipe.getSteps().size() - 1) {
                        nextStepButton.setText("Next Step (" + (currentStepIndex + 1) + "/" + selectedRecipe.getSteps().size() + ")");
                        nextStepButton.setBackground(OLIVE_GREEN.brighter());
                        Timer readyTimer = new Timer(150, null);
                        readyTimer.addActionListener(new ActionListener() {
                            int count = 0;
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                if (count < 2) {
                                    nextStepButton.setBackground(count % 2 == 0 ? OLIVE_GREEN : OLIVE_GREEN.brighter());
                                    count++;
                                } else {
                                    nextStepButton.setBackground(OLIVE_GREEN);
                                    ((Timer) evt.getSource()).stop();
                                }
                            }
                        });
                        readyTimer.start();
                    } else {
                        nextStepButton.setText("Complete Recipe!");
                    }
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    private void updateStockArea() {
        String stockList = manager.getUserStock().stream()
                .map(ing -> "- " + ing.toString())
                .collect(Collectors.joining("\n"));
        if (stockList.isEmpty()) {
            stockArea.setText("Pantry is empty.\n\nAdd ingredients to get started!");
        } else {
            stockArea.setText(stockList);
        }
    }

    private boolean consumeIngredientsIfAvailable(Recipe recipe) {
        if (recipe == null) return false;

        List<String> insufficientItems = new ArrayList<>();
        List<String> incompatibleUnits = new ArrayList<>();
        
        for (Ingredient req : recipe.getIngredients()) {
            String reqName = req.getName().trim();
            String reqQtyStr = req.getQuantity().trim();
            String[] reqParts = reqQtyStr.split("\\s+", 2);
            if (reqParts.length < 1) continue;

            double reqQty;
            try {
                reqQty = parseQuantity(reqParts[0]);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cannot parse required quantity for: " + req.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String reqUnit = reqParts.length > 1 ? reqParts[1].trim() : "";

            Ingredient pantryMatch = null;
            for (Ingredient p : manager.getUserStock()) {
                if (p.getName().trim().equalsIgnoreCase(reqName)) {
                    pantryMatch = p;
                    break;
                }
            }

            if (pantryMatch == null) {
                insufficientItems.add(req.getName() + " (not in pantry)");
                continue;
            }

            String panQtyStr = pantryMatch.getQuantity().trim();
            String[] panParts = panQtyStr.split("\\s+", 2);
            if (panParts.length < 1) {
                JOptionPane.showMessageDialog(this, "Cannot parse pantry quantity for: " + pantryMatch.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            double panQty;
            try {
                panQty = parseQuantity(panParts[0]);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cannot parse pantry quantity for: " + pantryMatch.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String panUnit = panParts.length > 1 ? panParts[1].trim() : "";

            if (!UnitConverter.areUnitsCompatible(reqUnit, panUnit)) {
                incompatibleUnits.add(req.getName() + ": need " + reqUnit + ", have " + panUnit);
                continue;
            }

            if (!UnitConverter.hasEnough(reqQty, reqUnit, panQty, panUnit)) {
                double availableInReqUnit = UnitConverter.convert(panQty, panUnit, reqUnit);
                insufficientItems.add(String.format("%s: need %.2f %s, have %.2f %s (%.2f %s)", 
                    req.getName(), reqQty, reqUnit, panQty, panUnit, availableInReqUnit, reqUnit));
            }
        }

        if (!insufficientItems.isEmpty() || !incompatibleUnits.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Cannot start cooking — issues found:\n\n");
            
            if (!insufficientItems.isEmpty()) {
                sb.append("INSUFFICIENT QUANTITIES:\n");
                insufficientItems.forEach(item -> sb.append(" • ").append(item).append("\n"));
                sb.append("\n");
            }
            
            if (!incompatibleUnits.isEmpty()) {
                sb.append("INCOMPATIBLE UNITS:\n");
                incompatibleUnits.forEach(item -> sb.append(" • ").append(item).append("\n"));
                sb.append("\n(Tip: Use compatible volume units like ml/L/cups or weight units like g/kg/oz)\n\n");
            }
            
            sb.append("Please add or adjust ingredients before cooking.");
            JOptionPane.showMessageDialog(this, sb.toString(), "Cannot Start Cooking", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        StringBuilder consumptionLog = new StringBuilder();
        consumptionLog.append("Ingredients consumed:\n\n");
        
        for (Ingredient req : recipe.getIngredients()) {
            String reqName = req.getName().trim();
            String reqQtyStr = req.getQuantity().trim();
            String[] reqParts = reqQtyStr.split("\\s+", 2);
            double reqQty = parseQuantity(reqParts[0]);
            String reqUnit = reqParts.length > 1 ? reqParts[1].trim() : "";

            Ingredient pantryMatch = null;
            for (Ingredient p : new ArrayList<>(manager.getUserStock())) {
                if (p.getName().trim().equalsIgnoreCase(reqName)) {
                    pantryMatch = p;
                    break;
                }
            }

            if (pantryMatch == null) continue;

            String panQtyStr = pantryMatch.getQuantity().trim();
            String[] panParts = panQtyStr.split("\\s+", 2);
            double panQty = parseQuantity(panParts[0]);
            String panUnit = panParts.length > 1 ? panParts[1].trim() : "";

            double remaining = UnitConverter.calculateRemaining(panQty, panUnit, reqQty, reqUnit);

            if (remaining < 0) continue;

            if (!panUnit.equalsIgnoreCase(reqUnit)) {
                double consumedInPantryUnit = UnitConverter.convert(reqQty, reqUnit, panUnit);
                consumptionLog.append(String.format(" • %s: %.2f %s (%.2f %s from stock)\n", 
                    reqName, reqQty, reqUnit, consumedInPantryUnit, panUnit));
            } else {
                consumptionLog.append(String.format(" • %s: %.2f %s\n", reqName, reqQty, reqUnit));
            }

            manager.getUserStock().remove(pantryMatch);

            if (remaining > 0.01) {
                String newQtyStr = String.format("%.2f", remaining) + " " + panUnit;
                Ingredient updated = new Ingredient(pantryMatch.getName(), newQtyStr);
                manager.addToStock(updated);
            }
        }

        ingredientsConsumed = true;
        consumptionDetails = consumptionLog.toString();
        updateStockArea();
        if (selectedRecipe != null) displayRecipeDetails(selectedRecipe);

        return true;
    }
    
    private String normalizeIngredientName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return name;
        }
        
        String trimmed = name.trim();
        String[] words = trimmed.split("\\s+");
        StringBuilder normalized = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.length() > 0) {
                normalized.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    normalized.append(word.substring(1).toLowerCase());
                }
                
                if (i < words.length - 1) {
                    normalized.append(" ");
                }
            }
        }
        
        return normalized.toString();
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