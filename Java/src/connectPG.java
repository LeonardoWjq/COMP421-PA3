package src;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class connectPG {
    private final String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";
    private final String user = "cs421g09";
    private final String password = "comp4212009";
    public Connection connect(){
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return con;
    }
    public static void greeting(){
        System.out.println("Welcome to use the official Cineplex online system.");
    }
    public static void printOption(){
        System.out.println("Please choose from the following options:");
        System.out.println("Option1");
        System.out.println("Option2");
        System.out.println("Option3");
        System.out.println("Option4");
        System.out.println("Option5");
    }

    public static void main(String args []) throws SQLException {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            System.out.println("Found driver.");
            connectPG connection = new connectPG();
            Connection con = connection.connect();
            if(con==null){
                System.out.println("Failed to connect to the database.");
                System.exit(1);
            }
            greeting();
            Scanner scanner = new Scanner(System.in);
            while (true){
                printOption();
                String input;
                try{
                    System.out.print(">> ");
                    input = scanner.nextLine();
                }catch(InputMismatchException e){
                    System.out.println("Please enter a valid option.");
                    continue;
                }
                switch(input){
                    case "Option1":
                        System.out.println("option 1");
                        break;
                    case "Option2":
                        System.out.println("option 2");
                        break;
                    case "Quit":
                        con.close();
                        return;
                    default:
                        System.out.println("Please enter a valid option.");
                }
            }
        } catch (Exception cnfe) {
            System.out.println("Class not found");
            System.exit(2);
        }


//        Connection con = DriverManager.getConnection(url, "cs421g09", "comp4212009");
//        System.out.println("Connect to PG");
//        Statement statement = con.createStatement();


    }
}
