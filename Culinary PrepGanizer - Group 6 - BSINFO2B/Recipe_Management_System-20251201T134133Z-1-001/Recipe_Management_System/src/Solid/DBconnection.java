package Solid;

import java.sql.*;

public class DBconnection {

	public static Connection getConnection() {
	    Connection conn = null;

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");

	        String url = "jdbc:mysql://localhost:3306/tbl_username_password";
	        String user = "root";
	        String pass = "";

	        conn = DriverManager.getConnection(url, user, pass);
	        System.out.println("Connected to database!");
	    } 
	    catch (Exception e) {
	        System.out.println("❌ Database connection failed:");
	        e.printStackTrace();  // THIS SHOWS THE REAL ERROR
	    }

	    return conn;
	}

}
