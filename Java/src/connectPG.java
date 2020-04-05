package src;
import java.sql.*;
public class connectPG {
    public static void main(String args []) throws SQLException {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }

// This is the url you must use for Postgresql.
//Note: This url may not valid now !
        String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";
        Connection con = DriverManager.getConnection(url, "cs421g09", "comp4212009");
        Statement statement = con.createStatement();


    }
    }
