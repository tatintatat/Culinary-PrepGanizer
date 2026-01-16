package Solid;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to convert between different units of measurement.
 * Supports volume, weight, and count conversions.
 */
public class UnitConverter {
    
    // Base unit for volume: milliliters (ml)
    private static final Map<String, Double> VOLUME_TO_ML = new HashMap<>();
    
    // Base unit for weight: grams (g)
    private static final Map<String, Double> WEIGHT_TO_GRAMS = new HashMap<>();
    
    // Count-based units (head, piece, etc.) - stored for reference
    private static final String[] COUNT_UNITS = {
        "head", "heads", 
        "pc", "pcs", "piece", "pieces",
        "clove", "cloves",
        "bunch", "bunches",
        "pack", "packs",
        "can", "cans",
        "bottle", "bottles",
        "bag", "bags"
    };
    
    static {
        // Volume conversions to milliliters
        VOLUME_TO_ML.put("ml", 1.0);
        VOLUME_TO_ML.put("milliliter", 1.0);
        VOLUME_TO_ML.put("milliliters", 1.0);
        VOLUME_TO_ML.put("l", 1000.0);
        VOLUME_TO_ML.put("liter", 1000.0);
        VOLUME_TO_ML.put("liters", 1000.0);
        VOLUME_TO_ML.put("cup", 236.588);
        VOLUME_TO_ML.put("cups", 236.588);
        VOLUME_TO_ML.put("tbsp", 14.787);
        VOLUME_TO_ML.put("tablespoon", 14.787);
        VOLUME_TO_ML.put("tablespoons", 14.787);
        VOLUME_TO_ML.put("tsp", 4.929);
        VOLUME_TO_ML.put("teaspoon", 4.929);
        VOLUME_TO_ML.put("teaspoons", 4.929);
        VOLUME_TO_ML.put("fl oz", 29.574);
        VOLUME_TO_ML.put("fluid ounce", 29.574);
        VOLUME_TO_ML.put("fluid ounces", 29.574);
        VOLUME_TO_ML.put("gallon", 3785.41);
        VOLUME_TO_ML.put("gallons", 3785.41);
        VOLUME_TO_ML.put("quart", 946.353);
        VOLUME_TO_ML.put("quarts", 946.353);
        VOLUME_TO_ML.put("pint", 473.176);
        VOLUME_TO_ML.put("pints", 473.176);
        
        // Weight conversions to grams
        WEIGHT_TO_GRAMS.put("g", 1.0);
        WEIGHT_TO_GRAMS.put("gram", 1.0);
        WEIGHT_TO_GRAMS.put("grams", 1.0);
        WEIGHT_TO_GRAMS.put("kg", 1000.0);
        WEIGHT_TO_GRAMS.put("kilogram", 1000.0);
        WEIGHT_TO_GRAMS.put("kilograms", 1000.0);
        WEIGHT_TO_GRAMS.put("mg", 0.001);
        WEIGHT_TO_GRAMS.put("milligram", 0.001);
        WEIGHT_TO_GRAMS.put("milligrams", 0.001);
        WEIGHT_TO_GRAMS.put("oz", 28.3495);
        WEIGHT_TO_GRAMS.put("ounce", 28.3495);
        WEIGHT_TO_GRAMS.put("ounces", 28.3495);
        WEIGHT_TO_GRAMS.put("lb", 453.592);
        WEIGHT_TO_GRAMS.put("pound", 453.592);
        WEIGHT_TO_GRAMS.put("pounds", 453.592);
    }
    
    /**
     * Check if a unit is a count-based unit (head, piece, etc.)
     */
    public static boolean isCountUnit(String unit) {
        if (unit == null) return false;
        String u = unit.trim().toLowerCase();
        
        for (String countUnit : COUNT_UNITS) {
            if (u.equals(countUnit)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if two count units match (handles singular/plural)
     */
    public static boolean countUnitsMatch(String unit1, String unit2) {
        if (unit1 == null || unit2 == null) return false;
        
        String u1 = unit1.trim().toLowerCase();
        String u2 = unit2.trim().toLowerCase();
        
        // Exact match
        if (u1.equals(u2)) return true;
        
        // Check singular/plural variations
        if (u1.equals(u2 + "s") || u2.equals(u1 + "s")) return true;
        
        // Special cases
        Map<String, String> singularPlural = new HashMap<>();
        singularPlural.put("piece", "pieces");
        singularPlural.put("head", "heads");
        singularPlural.put("clove", "cloves");
        singularPlural.put("bunch", "bunches");
        singularPlural.put("can", "cans");
        singularPlural.put("bottle", "bottles");
        singularPlural.put("bag", "bags");
        singularPlural.put("pack", "packs");
        
        for (Map.Entry<String, String> entry : singularPlural.entrySet()) {
            if ((u1.equals(entry.getKey()) && u2.equals(entry.getValue())) ||
                (u2.equals(entry.getKey()) && u1.equals(entry.getValue()))) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if two units are compatible (can be converted between each other)
     */
    public static boolean areUnitsCompatible(String unit1, String unit2) {
        if (unit1 == null || unit2 == null) return false;
        
        String u1 = unit1.trim().toLowerCase();
        String u2 = unit2.trim().toLowerCase();
        
        // Exact match
        if (u1.equals(u2)) return true;
        
        // Check if both are count units
        if (isCountUnit(u1) && isCountUnit(u2)) {
            return countUnitsMatch(u1, u2);
        }
        
        // Both are volume units
        if (VOLUME_TO_ML.containsKey(u1) && VOLUME_TO_ML.containsKey(u2)) return true;
        
        // Both are weight units
        if (WEIGHT_TO_GRAMS.containsKey(u1) && WEIGHT_TO_GRAMS.containsKey(u2)) return true;
        
        return false;
    }
    
    /**
     * Convert a quantity from one unit to another.
     * Returns the converted quantity, or -1 if conversion is not possible.
     */
    public static double convert(double quantity, String fromUnit, String toUnit) {
        if (fromUnit == null || toUnit == null) return -1;
        
        String from = fromUnit.trim().toLowerCase();
        String to = toUnit.trim().toLowerCase();
        
        // Same unit, no conversion needed
        if (from.equals(to)) return quantity;
        
        // Count units - check if compatible
        if (isCountUnit(from) && isCountUnit(to)) {
            if (countUnitsMatch(from, to)) {
                return quantity; // Same count, just different singular/plural
            }
            return -1; // Different count units, can't convert
        }
        
        // Try volume conversion
        if (VOLUME_TO_ML.containsKey(from) && VOLUME_TO_ML.containsKey(to)) {
            double inML = quantity * VOLUME_TO_ML.get(from);
            return inML / VOLUME_TO_ML.get(to);
        }
        
        // Try weight conversion
        if (WEIGHT_TO_GRAMS.containsKey(from) && WEIGHT_TO_GRAMS.containsKey(to)) {
            double inGrams = quantity * WEIGHT_TO_GRAMS.get(from);
            return inGrams / WEIGHT_TO_GRAMS.get(to);
        }
        
        // Conversion not possible
        return -1;
    }
    
    /**
     * Check if pantry has enough of an ingredient, considering unit conversions.
     * 
     * @param requiredQty Required quantity
     * @param requiredUnit Required unit
     * @param availableQty Available quantity in pantry
     * @param availableUnit Available unit in pantry
     * @return true if pantry has enough (after conversion), false otherwise
     */
    public static boolean hasEnough(double requiredQty, String requiredUnit, 
                                     double availableQty, String availableUnit) {
        if (!areUnitsCompatible(requiredUnit, availableUnit)) {
            return false;
        }
        
        // For count units, simple comparison
        if (isCountUnit(requiredUnit) && isCountUnit(availableUnit)) {
            return availableQty >= requiredQty;
        }
        
        // Convert available quantity to required unit
        double availableInRequiredUnit = convert(availableQty, availableUnit, requiredUnit);
        
        if (availableInRequiredUnit < 0) return false;
        
        return availableInRequiredUnit >= requiredQty;
    }
    
    /**
     * Calculate remaining quantity after consumption, in the original unit.
     * 
     * @param availableQty Available quantity
     * @param availableUnit Available unit
     * @param consumedQty Quantity to consume
     * @param consumedUnit Unit of quantity to consume
     * @return Remaining quantity in available unit, or -1 if conversion fails
     */
    public static double calculateRemaining(double availableQty, String availableUnit,
                                             double consumedQty, String consumedUnit) {
        if (!areUnitsCompatible(availableUnit, consumedUnit)) {
            return -1;
        }
        
        // For count units, simple subtraction
        if (isCountUnit(availableUnit) && isCountUnit(consumedUnit)) {
            return availableQty - consumedQty;
        }
        
        // Convert consumed quantity to available unit
        double consumedInAvailableUnit = convert(consumedQty, consumedUnit, availableUnit);
        
        if (consumedInAvailableUnit < 0) return -1;
        
        return availableQty - consumedInAvailableUnit;
    }
    
    /**
     * Format a unit conversion message for user display
     */
    public static String getConversionMessage(double qty, String fromUnit, String toUnit) {
        double converted = convert(qty, fromUnit, toUnit);
        if (converted < 0) {
            return "Cannot convert " + fromUnit + " to " + toUnit;
        }
        return String.format("%.2f %s = %.2f %s", qty, fromUnit, converted, toUnit);
    }
}