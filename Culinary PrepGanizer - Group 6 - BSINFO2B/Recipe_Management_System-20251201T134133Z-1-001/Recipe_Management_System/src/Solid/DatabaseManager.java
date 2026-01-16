package Solid;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private Connection conn;

    public DatabaseManager(String dbURL) {
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("✅ Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }

    // Fetch all dishes
  

    // Fetch recipe by dishId
    public Recipe fetchRecipe(int dishId) {
        String sql = "SELECT ingredient, quantity, step_number, instruction " +
                     "FROM tbl_recipes WHERE dish_id = ? ORDER BY step_number";

        List<Ingredient> ingredients = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ingredients.add(new Ingredient(
                    rs.getString("ingredient"),
                    rs.getString("quantity")
                ));
                steps.add(rs.getString("instruction"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching recipe: " + e.getMessage());
        }

        if (ingredients.isEmpty() && steps.isEmpty()) {
            return null;
        }

        // Get dish name
        String dishName = "";
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT dish_name FROM tbl_dishes WHERE dish_id = ?")) {

            stmt.setInt(1, dishId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) dishName = rs.getString("dish_name");

        } catch (SQLException e) {
            System.err.println("Error fetching dish name: " + e.getMessage());
        }

        return new Recipe(dishName, ingredients, steps);
    }

    // Add dish
    public int addDish(String name, String imagePath) {
        String sql = "INSERT INTO tbl_dishes (dish_name, dish_image) VALUES (?, ?)";

        try (PreparedStatement stmt =
                     conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, name);
            stmt.setString(2, imagePath);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✅ Added dish ID: " + id);
                return id;
            }

        } catch (SQLException e) {
            System.err.println("Error adding dish: " + e.getMessage());
        }

        return -1;
    }

    // Add recipe step
    public void addRecipeStep(int dishId, String ingredient, String quantity,
                              int stepNumber, String instruction) {

        String sql = "INSERT INTO tbl_recipes (dish_id, ingredient, quantity, step_number, instruction) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dishId);
            stmt.setString(2, ingredient);
            stmt.setString(3, quantity);
            stmt.setInt(4, stepNumber);
            stmt.setString(5, instruction);
            stmt.executeUpdate();

            System.out.println("✅ Step added to dish ID: " + dishId);

        } catch (SQLException e) {
            System.err.println("Error adding recipe step: " + e.getMessage());
        }
    }

    // Close connection
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing DB: " + e.getMessage());
        }
    }
}
