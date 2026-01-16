package Solid;
public class Ingredient {
    private String name;
    private String quantity;
    
    public Ingredient(String name, String quantity) {//constructor
        setName(name);
        setQuantity(quantity);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be empty.");
        }
        this.name = name.trim();
    }
    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        if (quantity == null) {
            this.quantity = "";
        } else {
            this.quantity = quantity.trim();
        }
    }
    @Override
    public String toString() {
        return name + " - " + quantity;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ingredient other = (Ingredient) obj;
        return name.equalsIgnoreCase(other.name);
    }
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}