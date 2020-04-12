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
    private static Scanner scanner;//global scanner object
    public static boolean start(){//set up a connection and initialize the fields
        try {
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            scanner = new Scanner(System.in);
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
        System.out.println("4 Go Back");
    }
    public static int navigate(){
        while(true){
            System.out.println("Continue or Go Back?");
            System.out.println("1 Continue");
            System.out.println("2 Go Back");
            try{
                int option = scanner.nextInt();
                if(option<1 || option >2)
                    throw new InputMismatchException();
                return option;

            }catch(InputMismatchException e){
                System.out.println("Please choose between 1 (Continue) and 2 (Go Back) with the corresponding option number.");
            }
        }
    }
    public static boolean authenticate() throws SQLException{
        while(true) {
            //print login options
            System.out.println("Please login or sign up:");
            System.out.println("1 Login");
            System.out.println("2 Sign Up");
            System.out.println("3 Go back");
            System.out.print(">> ");
            int option;

            //test input option
            try{
                option = scanner.nextInt();
                if(option<1||option>3){
                    throw new InputMismatchException();
                }
            }catch(InputMismatchException e){
                System.out.println("Please enter a valid integer in the range 1-3 as your option.");
                continue;
            }

            //execute options
            switch (option){
                case 1://login into account
                {
                    String employeeId;
                    String password;
                    scanner.nextLine();

                    while(true){//check user input
                        try{
                            System.out.print("Please enter your employee ID: ");
                            employeeId = scanner.nextLine();
                            if(employeeId.length()!=5)//throw exception for wrong format of employeeID
                                throw new InputMismatchException();

                            System.out.print("Please enter your password: ");
                            password = scanner.nextLine();
                            if(password.length()<1||password.length()>20)//throw exception for wrong format of password
                                throw new InputMismatchException();

                            break;
                        }catch(InputMismatchException e){
                            System.out.println("Please enter a valid employee ID (5 digits) and a valid password (1-20 characters).");
                        }
                    }

                    employeeId = "'" + employeeId +"'";
                    password = "'" + password + "'";
                    String sql = " Select * FROM StaffAccount WHERE eid = " + employeeId + " AND password = " + password;
                    ResultSet rs;
                    try {
                        rs = stmt.executeQuery(sql);
                    }catch (SQLException e){
                        System.out.println("Database error. Want to try again?");
                        int response = navigate();
                        if(response == 1){
                            continue;
                        }else{
                            break;
                        }
                    }
                    if(rs.next()){//there is matching records
                        System.out.println("Login success!");
                        rs.close();
                        return true;//return true
                    }else{//There is no matching records
                        rs.close();
                        System.out.println("There is no matching records for the specified employee ID and the password.");
                        System.out.println("If you do not have an account yet please sign up first with your employee ID");
                    }
                    break;
                }
                case 2://sign up for an account
                {
                    String employeeId;
                    String password;
                    scanner.nextLine();
                    boolean correctID; //check the result of employeeID authentication
                    while(true){//get the correct input employee ID
                        try{
                            System.out.print("Please enter your employee ID: ");
                            employeeId = scanner.nextLine();//get employee ID from user input
                            if(employeeId.length()!=5)
                                throw new InputMismatchException();

                            //user input is of correct format

                            employeeId = "'" + employeeId + "'"; //format the employeeid

                            String checkEid = "SELECT * FROM Employee WHERE eid = " + employeeId;//check if eid exists in employee table
                            ResultSet rs;
                            try {
                                rs = stmt.executeQuery(checkEid);//run check id
                            }catch (SQLException e){
                                System.out.println(e.getMessage());
                                System.out.println("Database error. Want to try again?");
                                int response = navigate();
                                if(response == 1)
                                    continue;
                                else{
                                    correctID = false;
                                    break;
                                }
                            }
                            //The sql returns without error

                            if(!rs.next()){//if it does not exist
                                System.out.println("Input employee ID cannot be found in the database!");
                                System.out.println("Please confirm the correctness of your input.");
                                rs.close();
                                //check the intention of the user to keep trying or go back
                                int choice = navigate();
                                if(choice == 1) {//keep trying
                                    scanner.nextLine();
                                    continue;
                                } else {
                                    correctID = false;//give up
                                    break;
                                }
                            }

                            //At this point it is verified that eid exists in the eid table
                            String checkDuplicate = "SELECT * FROM StaffAccount WHERE eid = " + employeeId;//check if eid exists in the account table already
                            try {
                                rs = stmt.executeQuery(checkDuplicate);
                            }catch (SQLException e){
                                System.out.println(e.getMessage());
                                System.out.println("Database error. Want to try again?");
                                int response = navigate();
                                if(response == 1)
                                    continue;
                                else{
                                    correctID = false;
                                    break;
                                }
                            }
                            //The sql returns without errors
                            if(rs.next()){//it already exists
                                System.out.println("Account already exists. Please login instead.");
                                rs.close();
                                correctID = false;
                                break;
                            }
                            rs.close();

                            //At this point it is verified that the eid exists in employee table but not present in account table
                            correctID = true;
                            break;
                        }catch (InputMismatchException e){//catch input error of employeeId
                            System.out.println("Please enter a valid employee ID (5 digits).");
                        }
                    }

                    if(!correctID)// true if the user wants to go back
                        break;

                    while(true){//get valid password
                        try{
                            System.out.print("Please enter your password (1-20 characters): ");
                            password = scanner.nextLine();//receive user input
                            if(password.length()<1||password.length()>20)
                                throw new InputMismatchException();
                            password = "'" + password + "'";//format the password
                            break;
                        }catch(InputMismatchException e){
                            System.out.println("Please enter a valid password.");
                        }
                    }

                    //Up to this point, there is a valid employee id and a valid password
                    //Insert into the database the new account
                    String update = "INSERT INTO StaffAccount " + "VALUES" + "(" + employeeId + "," + password + ")";

                    try {
                        stmt.executeUpdate(update);
                    }catch (SQLException e){
                        System.out.println(e.getMessage());
                        System.out.println("Database error. Want to try again?");
                        int response = navigate();
                        if(response == 1)
                            continue;
                        else{
                            break;
                        }
                    }

                    System.out.println("Sign up successful!");
                    return true;
                }
                case 3://go back
                    return false;
                default:
                    break;
            }
        }
    }
    //print operation options for customers
    public static void printOptionCustomer(){
        System.out.println("Please choose from the following options:");
        System.out.println("1 Book a Ticket");
        System.out.println("2 Recent Shows");
        System.out.println("3 View Your Transaction Records");
    }

    //main loop
    public static void main(String[] args) throws SQLException {
    	
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());//register driver
            System.out.println("Found driver.");
            
            if(!start()){//if failed to establish connection then exit the program with an error message
                System.out.println("Failed to connect to the database.");
                System.exit(1);
            }
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
				    //authentication
				    if(!authenticate())//if authentication fails the user cannot access administration page
				        continue;

				    administration:
                    while (true) {
						printOptionAdministrator();
						int option_adm = scanner.nextInt();
						switch (option_adm) {
							case 1:
								break;
							case 2:
								break;
							case 3:
								break;
                            case 4:
                                break administration;
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
