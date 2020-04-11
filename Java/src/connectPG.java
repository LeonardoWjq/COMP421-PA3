package src;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class connectPG {
    private static final String url = "jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421";
    private static final String user = "cs421g09";
    private static final String password = "comp4212009";
    private static Connection con; //global connection object
    private static Statement stmt;//global statement object
    public static boolean start(){//set up a connection and initialize the fields
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            System.out.println("Connected to the PostgreSQL server successfully.");
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    //print greeting statements at the main page
    public static void greeting(){
        System.out.println("Welcome to use the official Cineplex online system.");
        System.out.println("Please choose your role:");
        System.out.println("1 Administrator");
        System.out.println("2 Customer");
        System.out.println("3 Exit");
    }
    //print operation options for administrators
    public static void printOptionAdministrator(){
        System.out.println("Please choose from the following options:");
        System.out.println("1 Create New Show Schedule");
        System.out.println("2 Sale Statistics");
        System.out.println("3 Review Duty");
    }
    //print operation options for customers
    public static void printOptionCustomer(){
        System.out.println("Please choose from the following options:");
        System.out.println("1 Book a Ticket");
        System.out.println("2 Recent Shows");
        System.out.println("3 View Your Transaction Records");
    }

    //main loop
    public static void main(String args []) throws SQLException {
    	
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());//register driver
            System.out.println("Found driver.");
            
            if(!start()){//if failed to establish connection then exit the program with an error message
                System.out.println("Failed to connect to the database.");
                System.exit(1);
            }
            
            Scanner scanner = new Scanner(System.in);//create a new scanner for user inputs
            while(true){
            	greeting();//print greeting
            	int option;
				try {
					System.out.print(">> ");
					option = scanner.nextInt();
				} catch (InputMismatchException e) {
					System.out.println("Please enter a valid integer as your option.");
					continue;
				}
				if(option==1) {//administrator branch
					while (true) {
						// Authenticate Admin
						
						//
						printOptionAdministrator();
						int option_adm = scanner.nextInt();
						switch (option_adm) {
							case 1:
								break;
							case 2:
								break;
							case 3:
								break;
							default:
							{
								System.out.println("Please enter a valid integer in the range 1-3 as your option.");
								break;
							}
						}
					}
				}else if(option==2) {//customer branch
					while (true) {
						printOptionCustomer();
						int option_cust = scanner.nextInt();
						switch (option_cust) {
							case 1:
								break;
							case 2:
								break;
							case 3:
								break;
							default:
							{
								System.out.println("Please enter a valid integer in the range 1-3 as your option.");
								break;
							}
						}
					}
				} else if (option == 3){// exit program
					stmt.close();
					con.close();
					scanner.close();
					System.out.println("Program exit successfully. Hope to see you again.");
					System.exit(0);//exit with success code
				} else {
					System.out.println("Please enter a valid integer in the range 1-3 as your option.");
				}
            }
        } catch (Exception cnfe) {
            System.out.println("Class not found");//catch exception for driver
            System.exit(2);
        }
    }
}
