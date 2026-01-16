package Solid;

import java.util.List;

/**
 * Handles all text formatting and presentation logic for the Recipe Detail Area (SRP).
 */
public class RecipeDetailPresenter {

    // --- REVERTED ORIGINAL COLOR CODES for HTML ---
    private static final String ACCENT_COLOR_HEX = "#64B5F6"; // Accent Blue
    private static final String DANGER_COLOR_HEX = "#F44336"; // Red for MISSING ingredients
    private static final String PRIMARY_TEXT_HEX = "#CCCCCC"; // Light Gray Text
    private static final String BACKGROUND_COLOR_HEX = "#1E2D3C"; // Dark Blue Background

    /**
     * Builds the static (non-step) part of the recipe details, including missing ingredient checks.
     */
    public static String buildRecipeDetailsText(Recipe recipe, List<Ingredient> userStock) {
        StringBuilder details = new StringBuilder();
        
        details.append("<h2 style='color:").append(ACCENT_COLOR_HEX).append(";'>").append(recipe.getName().toUpperCase()).append("</h2>");
        
        if (recipe instanceof FilipinoRecipe fr) {
            details.append("<p><b>REGIONAL VARIANT:</b> ").append(fr.getRegionalVariant()).append("</p>");
        }
        
        details.append("<h3>REQUIRED INGREDIENTS:</h3>");
        details.append("<ul>");
        // Ingredients List
        recipe.getIngredients().forEach(ing -> details.append("<li>").append(ing).append("</li>"));
        details.append("</ul>");
        
        List<Ingredient> missing = recipe.checkMissingIngredients(userStock);
        
        details.append("<hr>");
        
        // Check for missing ingredients
        if (!missing.isEmpty()) {
            details.append("<h3 style='color:").append(DANGER_COLOR_HEX).append(";'>!!! MISSING INGREDIENTS !!!</h3>");
            details.append("<ul style='color:").append(DANGER_COLOR_HEX).append(";'>");
            // CORRECTED LOOP: Ensure semicolon and closing tag logic is correct
            missing.forEach(ing -> details.append("<li>").append(ing.getName()).append("</li>")); 
            details.append("</ul>");
        } else {
            // FIX: This section appears to be the location of the syntax error in image_a882d5.jpg
            details.append("<h3 style='color:").append(ACCENT_COLOR_HEX).append(";'>✅ All Ingredients are in Stock!</h3>");
        }
        
        details.append("<hr><h3>INSTRUCTIONS:</h3>");
        
        return "<html><body style='padding: 10px; font-family: Arial; color: " + PRIMARY_TEXT_HEX + "; background-color: " + BACKGROUND_COLOR_HEX + ";'>" + details.toString() + "</body></html>";
    }
    
    /**
     * Appends the instructions up to the current step index, highlighting the current step.
     */
    public static String appendStepInstructions(String baseHtml, Recipe recipe, int currentStepIndex) {
        StringBuilder steps = new StringBuilder();
        
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            String stepText = recipe.getSteps().get(i);
            // Highlight steps that have been reached/passed (light color for reached, greyed out for future)
            String style = (i <= currentStepIndex) ? "style='font-weight: bold; color: " + PRIMARY_TEXT_HEX + ";'" : "style='color: #888888;'";
            steps.append("<li ").append(style).append(">").append(stepText).append("</li>");
        }
        
        String stepsHtml = "<h3>INSTRUCTIONS:</h3><ol>" + steps.toString() + "</ol>";
        
        int instructionIndex = baseHtml.indexOf("<h3>INSTRUCTIONS:</h3>");
        if (instructionIndex != -1) {
            return baseHtml.substring(0, instructionIndex) + stepsHtml + "</body></html>";
        }
        return baseHtml;
    }
}