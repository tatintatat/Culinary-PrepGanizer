package Solid;

import java.sql.*;

public class TestLoginQuery {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tbl_username_password",
                "root",
                ""
            );

            String sql = "SELECT * FROM tbl_registration WHERE TRIM(username)=? AND TRIM(password)=?";
            PreparedStatement ps = conn.prepareStatement(sql); // FIXED: was __sql__

            ps.setString(1, "marcivancastro@gmail.com");
            ps.setString(2, "12345");

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("✔ Login SUCCESS!");
                System.out.println("Found user: " + rs.getString("username"));
            } else {
                System.out.println("❌ Login FAILED!");
                
                // Debug: Check what's actually in the database
                String debug = "SELECT username, password FROM tbl_registration";
                Statement st = conn.createStatement();
                ResultSet debugRs = st.executeQuery(debug);
                
                System.out.println("\n--- Database contents ---");
                while (debugRs.next()) {
                    System.out.println("User: '" + debugRs.getString("username") + "'");
                    System.out.println("Pass: '" + debugRs.getString("password") + "'");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}