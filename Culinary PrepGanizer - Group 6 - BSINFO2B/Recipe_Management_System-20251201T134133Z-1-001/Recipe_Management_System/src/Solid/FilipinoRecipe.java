package Solid;

import java.util.List;

/**
 * Specialized recipe class for Filipino cuisine (Inheritance).
 */
public class FilipinoRecipe extends Recipe {
    
    private final String regionalVariant;
    
    public FilipinoRecipe(String name, List<Ingredient> ingredients, List<String> steps, String regionalVariant) {
        super(name, ingredients, steps);
        this.regionalVariant = regionalVariant;
    }
    
    public FilipinoRecipe(String name, List<Ingredient> ingredients, List<String> steps, String regionalVariant, String imagePath) {
        super(name, ingredients, steps, imagePath);
        this.regionalVariant = regionalVariant;
    }
    
    public String getRegionalVariant() {
        return regionalVariant;
    }
    
    @Override
    public void displayInstructions() {
        // Overridden method demonstrates POLYMORPHISM
        System.out.println("Starting instructions for " + getName() + " (" + regionalVariant + " style).");
    }
}