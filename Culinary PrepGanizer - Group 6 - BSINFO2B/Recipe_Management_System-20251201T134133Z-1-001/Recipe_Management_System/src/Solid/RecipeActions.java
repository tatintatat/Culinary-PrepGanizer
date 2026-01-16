package Solid;

import java.util.List;
/**
 * Interface defining core recipe behaviors (Abstraction).
 */
public interface RecipeActions {
    
    List<Ingredient> checkMissingIngredients(List<Ingredient> userStock);
    
    void displayInstructions(); // Example method for base class behavior
}