package src;
import java.sql.*;
public class connectPG {
    public static void main(String args []) throws SQLException {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            System.out.println("Found driver.");
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }


        String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";
        Connection con = DriverManager.getConnection(url, "cs421g09", "comp4212009");
        System.out.println("Connect to PG");
        Statement statement = con.createStatement();


    }
}
