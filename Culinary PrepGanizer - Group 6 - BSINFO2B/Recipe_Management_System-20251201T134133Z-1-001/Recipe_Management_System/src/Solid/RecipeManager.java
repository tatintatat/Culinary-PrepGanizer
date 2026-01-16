package Solid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all data access and core business logic (SRP).
 */
public class RecipeManager {

	 private final List<Recipe> allRecipes;
	    private final List<Ingredient> userStock;

	    public RecipeManager() {
	        allRecipes = new ArrayList<>();
	        userStock = new ArrayList<>();
	        initializeDefaultRecipes();
	        initializeDefaultStock();
	    }

	    private void initializeDefaultRecipes() {
	        allRecipes.add(new FilipinoRecipe("Chicken Adobo",
	                Arrays.asList(
	                        new Ingredient("Chicken", "1 kg"),
	                        new Ingredient("Soy Sauce", "1/2 cup"),
	                        new Ingredient("Vinegar", "1/4 cup"),
	                        new Ingredient("Garlic", "1 head")
	                ),
	                Arrays.asList(
	                        "Marinate chicken in soy sauce, vinegar, and crushed garlic.",
	                        "Sauté marinated chicken until brown.",
	                        "Simmer until the chicken is tender and sauce is reduced."
	                ), "Classic"));

	        allRecipes.add(new FilipinoRecipe("Pork Sinigang",
	                Arrays.asList(
	                        new Ingredient("Pork Belly", "1 kg"),
	                        new Ingredient("Tamarind Mix", "1 pack"),
	                        new Ingredient("Radish", "1 pc"),
	                        new Ingredient("Taro", "3 pcs"),
	                        new Ingredient("Kangkong", "1 bunch")
	                ),
	                Arrays.asList(
	                        "Boil pork until tender, removing scum.",
	                        "Add tamarind mix, radish, and taro.",
	                        "Add kangkong and simmer until vegetables are cooked."
	                ), "Iloilo Style"));

	        allRecipes.add(new Recipe("Beef Caldereta",
	                Arrays.asList(
	                        new Ingredient("Beef", "1 kg"),
	                        new Ingredient("Potatoes", "3 pcs"),
	                        new Ingredient("Carrots", "2 pcs"),
	                        new Ingredient("Tomato Sauce", "1 cup")
	                ),
	                Arrays.asList(
	                        "Brown beef, then add tomato sauce and water.",
	                        "Simmer until beef is tender.",
	                        "Add potatoes and carrots and cook until soft."
	                )));

	        allRecipes.add(new Recipe("American Hamburger",
	                Arrays.asList(
	                        new Ingredient("Ground Beef", "1 lb"),
	                        new Ingredient("Burger Buns", "4"),
	                        new Ingredient("Lettuce", "4 leaves"),
	                        new Ingredient("Tomato", "1 slice")
	                ),
	                Arrays.asList(
	                        "Form ground beef into patties.",
	                        "Grill patties to desired doneness.",
	                        "Assemble burgers with lettuce and tomato."
	                )));

	        allRecipes.add(new Recipe("Chicken Tinola",
	                Arrays.asList(
	                        new Ingredient("Chicken", "1 lb"),
	                        new Ingredient("Ginger", "1 pc"),
	                        new Ingredient("Papaya", "1 pc"),
	                        new Ingredient("Chayote", "1 pc")
	                ),
	                Arrays.asList(
	                        "Sauté ginger and onion until fragrant.",
	                        "Add chicken and cook until light brown.",
	                        "Pour water and simmer until chicken is tender. Add papaya and chayote."
	                )));
	    }

	    private void initializeDefaultStock() {
	        userStock.add(new Ingredient("Chicken", "2 kg"));
	        userStock.add(new Ingredient("Soy Sauce", "1 liter"));
	        userStock.add(new Ingredient("Pork Belly", "500 g"));
	        userStock.add(new Ingredient("Garlic", "5 heads"));
	        userStock.add(new Ingredient("Tamarind Mix", "2 packs"));
	    }

	    public List<Recipe> searchRecipes(String query) {
	        if (query == null || query.trim().isEmpty()) return allRecipes;
	        String lowerQuery = query.toLowerCase();
	        return allRecipes.stream()
	                .filter(r -> r.getName().toLowerCase().contains(lowerQuery))
	                .collect(Collectors.toList());
	    }

	    public List<Ingredient> getUserStock() { return userStock; }
	    public void addToStock(Ingredient ingredient) { userStock.add(ingredient); }

		public void addCustomRecipe(Recipe newRecipe, String selectedImagePath) {
			// TODO Auto-generated method stub
			
		}
	}
