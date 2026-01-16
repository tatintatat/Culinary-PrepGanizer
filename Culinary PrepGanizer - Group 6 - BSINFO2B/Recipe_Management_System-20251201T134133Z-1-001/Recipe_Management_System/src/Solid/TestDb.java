package Solid;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestDb {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/tbl_username_password",
                    "root",
                    ""
                );
            if (conn != null) {
                System.out.println("✔ Connected to database successfully!");
            }

        } catch (Exception e) {
            System.out.println("❌ Failed: " + e.getMessage());
        }
    }
}
