package Solid;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements RecipeActions {    
    private final String name;
    private final List<Ingredient> ingredients;
    private final List<String> steps;
    private String imagePath; // Local file path for image
    
    public Recipe(String name, List<Ingredient> ingredients, List<String> steps) {
        this(name, ingredients, steps, null);
    }
    public Recipe(String name, List<Ingredient> ingredients, List<String> steps, String imagePath) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imagePath = imagePath;
    }
    // --- Core Implementations (Polymorphism) ---
    @Override
    public List<Ingredient> checkMissingIngredients(List<Ingredient> userStock) {
        List<Ingredient> missing = new ArrayList<>();
        
        for (Ingredient req : ingredients) {
            String reqName = req.getName().trim();
            String reqQtyStr = req.getQuantity().trim();
            String[] reqParts = reqQtyStr.split("\\s+", 2);
            
            if (reqParts.length < 1) {
                missing.add(req);
                continue;
            }
            double reqQty;
            try {
                reqQty = Double.parseDouble(reqParts[0]);
            } catch (NumberFormatException ex) {
                // If quantity is not a number (e.g., "1 pc", "to taste"), 
                // fall back to simple name matching
                boolean found = userStock.stream()
                    .anyMatch(s -> s.getName().trim().equalsIgnoreCase(reqName));
                if (!found) {
                    missing.add(req);
                }
                continue;
            }
            String reqUnit = reqParts.length > 1 ? reqParts[1].trim() : "";

            // Find matching ingredient in pantry
            Ingredient pantryMatch = null;
            for (Ingredient p : userStock) {
                if (p.getName().trim().equalsIgnoreCase(reqName)) {
                    pantryMatch = p;
                    break;
                }
            }
            if (pantryMatch == null) {
                missing.add(req);
                continue;
            }
            String panQtyStr = pantryMatch.getQuantity().trim();
            String[] panParts = panQtyStr.split("\\s+", 2);
            
            if (panParts.length < 1) {
                missing.add(req);
                continue;
            }
            double panQty;
            try {
                panQty = Double.parseDouble(panParts[0]);
            } catch (NumberFormatException ex) {
                // Pantry has the item but quantity format is not parseable
                // Consider it as available
                continue;
            }
            String panUnit = panParts.length > 1 ? panParts[1].trim() : "";

            // Check with unit conversion using UnitConverter
            if (!UnitConverter.hasEnough(reqQty, reqUnit, panQty, panUnit)) {
                missing.add(req);
            }
        } 
        return missing;
    }
    
    @Override
    public void displayInstructions() {
        System.out.println("Displaying instructions for: " + name);
    }
    // --- Getters and Setters (Encapsulation) ---
    public String getName() {
        return name;
    }
    public List<Ingredient> getIngredients() {
        return ingredients;
    }
    
    public List<String> getSteps() {
        return steps;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    // Allows AddRecipeForm to set the image path after construction
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    @Override
    public String toString() {
        return name;
    }
}