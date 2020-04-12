//package Java.src;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalDate;

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
    public static String formatWord(String input){
        while(input.length()<25){
            input = input + " ";
        }
        return input;
    }
    public static String longFormatWord(String input){
        while(input.length()<100){
            input = input + " ";
        }
        return input;
    }
    public static ArrayList<String> printTheatres() throws SQLException{//print the theatre info and return a list of addresses
        ArrayList<String> addresses = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT * FROM\n" +
                "Theatre");
        System.out.println(longFormatWord("   Name")+longFormatWord("   Address"));
        int option = 1;
        while(rs.next()){
            System.out.println(""+option+"  " + longFormatWord(rs.getString("theatre_name")) + longFormatWord(rs.getString("addr")));
            addresses.add(rs.getString("addr"));
            option++;
        }
        rs.close();
        return addresses;
    }
    public static ArrayList<Integer> printRooms(String addr) throws SQLException{//print the room numbers and their capacity and return the room numbers
        ArrayList<Integer> roomNums = new ArrayList<>();
        ResultSet rs = stmt.executeQuery("SELECT room,capacity\n" +
                "FROM Room\n" +
                "WHERE addr = " + addr + "\n" +
                "ORDER BY room");
        System.out.println(formatWord("Room Number") + formatWord("Capacity")+"\n");
        while(rs.next()){
            System.out.println(formatWord(""+rs.getInt("room"))+ formatWord(""+rs.getInt("capacity")));
            roomNums.add(rs.getInt("room"));
        }

        return roomNums;
    }
    public static boolean compareCurrentDate(int year,int month, int day){//return true if the input date is later than the current date
        String current = java.time.LocalDate.now().toString();
        String[] split = current.split("-");
        int currentYear = Integer.parseInt(split[0]);
        int currentMonth = Integer.parseInt(split[1]);
        int currentDay = Integer.parseInt(split[2]);
        if(currentYear<year){//the input year is later
            return true;
        }else if(currentYear>year){//the input year is earlier
            return false;
        }else{//same year
            if(currentMonth<month){//the input month is later
                return true;
            }else if(currentMonth>month){//the input month is earlier
                return false;
            }else{//same month
                if(currentDay<day){//the input day is later
                    return true;
                }else{//the input day is same or earlier
                    return false;
                }
            }
        }
    }
    public static boolean containsElement(int[] arr, int element){
        for(int i: arr){
            if(i==element)
                return true;
        }
        return false;
    }
    public static boolean dateRangeCheck(int year, int month, int day){
        if(month<1||month>12){//check the range of the month
            return false;
        }
        int[] longMonth = {1,3,5,7,8,10,12};
        int[] shortMonth = {4,6,9,11};
        if(containsElement(longMonth,month)){//month is long month: 31 days
            if(day>=1&&day<31)
                return true;
            else
                return false;
        }else if(containsElement(shortMonth,month)){//month is short month:30 days
            if(day>=1&&day<=30)
                return true;
            else
                return false;
        }else{//It is February
            if((year%4==0&&year%100!=0)||year%400==0){//leap year: 29 days
                if(day>=1&&day<=29)
                    return true;
                else
                    return false;
            }else{//normal year: 28 days
                if(day>=1&&day<=28)
                    return  true;
                else
                    return false;
            }
        }
    }
    public static boolean verifyDate(String date){
        int inputYear;
        int inputMonth;
        int inputDay;
        //length mismatch
        String[] split = date.split("-");
        if(split.length!=3){
            System.out.println("Format mismatched.");
            return false;
        }
        //format mismatch
        try{
            inputYear = Integer.parseInt(split[0]);
            inputMonth = Integer.parseInt(split[1]);
            inputDay = Integer.parseInt(split[2]);
        }catch(NumberFormatException e){
            System.out.print("Please input digits for year, month and day.");
            return false;
        }
        if(!dateRangeCheck(inputYear,inputMonth,inputDay)) {//not in range of a valid date
            System.out.println("Please input a valid date.");
            return false;
        }
        if(compareCurrentDate(inputYear,inputMonth,inputDay)){
            return true;
        }else{
            System.out.println("You can only schedule a new show for a date later than today.");
            return false;
        }
    }
    public static void addShow(){
        while(true){
            String address;//addr
            int roomNum;//room
            String startDate;//sdate
            String startTime;//start_time
            String title;
            String director;
            String language;//lang
            int price;
            try{
                System.out.println("Please follow the instructions to schedule a new show:\n");

                System.out.println("Please choose from the theatres:\n");
                ArrayList<String> addresses = printTheatres();//get the addresses
                System.out.print(">>  ");
                int theatreOption = scanner.nextInt();//get user input for theatre choice
                if (theatreOption<1||theatreOption>addresses.size()){//The option is invalid
                    System.out.println("Choose from oprions 1 to " + addresses.size());
                    throw new InputMismatchException();
                }else{
                    address = "'" + addresses.get(theatreOption-1) + "'";
                }
                //The address of the theatre is received successfully

                System.out.println("Please choose a room:\n");
                ArrayList<Integer> rooms = printRooms(address);
                System.out.print(">>  ");
                roomNum = scanner.nextInt();
                if(!rooms.contains(roomNum)){
                    System.out.print("Choose from room numbers\t");
                    for(int num: rooms){
                        System.out.print(num+"\t");
                    }
                    System.out.println();
                    throw new InputMismatchException();
                }
                //The room number is received successfully

                System.out.println("Please enter the date that you want to schedule for this show in the YYYY-MM-DD format:\n");
                System.out.print(">>  ");
                scanner.nextLine();
                startDate = scanner.nextLine();
                if(!verifyDate(startDate))
                    throw new InputMismatchException();
                startDate = "'" + startDate + "'";
                //The input date is valid





            }catch(InputMismatchException e){
                System.out.println("Please provide the inputs in proper formats.\n");
            }catch (SQLException e){
                System.out.println("Database Error.");
                System.out.println(e.getMessage());
            }
        }
    }

    public static void printSale(){//print the sale statistics
        try{
            ResultSet rs = stmt.executeQuery("SELECT c.box_eid, e.ename, c.sale_count FROM\n" +
                    "Employee e, (SELECT b.eid as box_eid,COUNT(DISTINCT COALESCE(t.ticket_num)) as sale_count FROM\n" +
                    "\t\t\t BoxofficeClerk b LEFT JOIN tickets t\n" +
                    "\t\t\t ON b.eid = t.eid\n" +
                    "\t\t\t GROUP BY b.eid\n" +
                    "\t\t\t ORDER BY b.eid) c\n" +
                    "WHERE e.eid = c.box_eid\n" +
                    "ORDER BY c.box_eid");
            String head = formatWord("Employee ID") + formatWord("Employee Name") + formatWord("No. of Sales");
            System.out.println(head+"\n");//print the head of table
            while(rs.next()){//print each tuple
                String tuple = formatWord(rs.getString("box_eid")) + formatWord(rs.getString("ename")) + formatWord(""+rs.getInt("sale_count"));
                System.out.println(tuple);
            }
            rs.close();//close the cursor
        }catch(SQLException e){//report error
            System.out.println(e.getMessage());
            System.out.println("Database error.");
        }
    }
    public static void printDuty(){//print the duty of service personnel and their supervisor
        try{
            ResultSet rs = stmt.executeQuery("SELECT s.eid,e.ename,s.addr,s.room_num,a.supervisor_name FROM\n" +
                    "servicepersonnel s,employee e, (SELECT staff_eid, ename AS supervisor_name\n" +
                    "FROM supervises, employee\n" +
                    "WHERE admin_eid = eid) a\n" +
                    "WHERE s.eid = staff_eid AND s.eid = e.eid\n" +
                    "ORDER BY s.eid");
            String head = formatWord("Employee ID") + formatWord("Employee Name") + longFormatWord("Theatre Address")+
                    formatWord("Room Number") + formatWord("Supervisor");
            System.out.println(head+"\n");//print the head of table
            while(rs.next()){//print each tuple
                String tuple = formatWord(rs.getString("eid")) + formatWord(rs.getString("ename"))
                        + longFormatWord(rs.getString("addr")) + formatWord(rs.getString("room_num")) + formatWord(rs.getString("supervisor_name"));
                System.out.println(tuple);
            }
            rs.close();//close the cursor
        }catch(SQLException e){//report error
            System.out.println(e.getMessage());
            System.out.println("Database error.");
        }

    }
    //print operation options for customers
    public static void printOptionCustomer(){
        System.out.println("Please choose from the following options:");
        System.out.println("1 Book a Ticket");
        System.out.println("2 Recent Shows");
        System.out.println("3 View Your Transaction Records");
    }
    
    public static void bookTickets() {
		System.out.println("Please input the show name that you want.");
		System.out.print(">> ");
		Scanner scanner = new Scanner(System.in);
		String name = scanner.nextLine();
		// TODO: Check for user input

		// End check

		try {
			name = "'" + name + "'";
			String sql = "SELECT * FROM shows  WHERE title = " + name;
			int counter = 1;
			HashMap<String, HashSet<Integer>> shows = new HashMap<>();
			ArrayList<String> show_schedules = new ArrayList<>();
			
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println(String.format("%1$d. INFO for Show %2$s:", counter, rs.getString("title")));
				String addr = rs.getString("addr");
				String room = String.format("%1$s", rs.getInt("room"));
				String sdate = String.format("%1$s", rs.getDate("sdate"));
				String start_time = String.format("%1$s", rs.getTime("start_time"));
				System.out.println(String.format(
						"address: %1$s; room: %2$s; date: %3$s; start time: %4$s; end time: %5$s; director: %6$s; language: %7$s",
						addr, room, sdate, start_time,
						rs.getTime("end_time"), rs.getString("director"), rs.getString("lang")));
	
				
				String sql2 = "SELECT capacity FROM room WHERE addr = '" + addr + "' AND room = " + room;
				Statement stmt2 = con.createStatement();
				ResultSet rs2 = stmt2.executeQuery(sql2);
				rs2.next();
				int capacity = rs2.getInt("capacity");	/// For debug
				System.out.println(capacity);
				String key = String.format(
						"%1$s;%2$s;%3$s;%4$s;%5$d",
						addr, room, sdate, start_time, capacity);
				shows.put(key, new HashSet<Integer>());
				show_schedules.add(key);
				
				String sql3 = String.format("SELECT t.seat_num " + 
						"FROM tickets t " + 
						"WHERE t.addr = '%1$s' AND t.room_num = %2$s AND t.start_time = '%3$s' AND t.sdate = '%4$s' ", 
						addr, room, start_time, sdate);
				Statement stmt3 = con.createStatement();
				ResultSet rs3 = stmt3.executeQuery(sql3);
				while(rs3.next()) {
					shows.get(key).add(rs3.getInt("seat_num"));
				}
				System.out.println("Avaliable seats: ");
				for (int i = 1; i <= capacity; i ++) {
					if (shows.get(key).contains(i)) {
						System.out.print(" X");
					} else {
						System.out.print(String.format("%1$2d", i));
					} 
					System.out.print(" ");
					if (i % 10 == 0) {
						System.out.println();
					}
				}
				counter++;
			}
			
			if (counter == 1) {
				System.out.println("The show name is invalid. Action failed, please retry.");
				scanner.close();
				return;
			}
			
			System.out.println(
					"Please enter your social id number, the payment method, show schedule that you pick, and seat numbers you want.");
			System.out.println("Simply specify them seperated by an empty space.");
			System.out.println("Example: 123456789(social id), card(payment method) 1(shedule number) 25(seat number) 26(seat number)");
			// TODO: Check user input
			String[] sarr = scanner.nextLine().split(" ");
			String id = sarr[0];
			int schedule = Integer.parseInt(sarr[1]);
			String method = sarr[2];
			HashSet<Integer> sold_seats = shows.get(show_schedules.get(schedule));
			for (int i = 3; i < sarr.length; i ++ ) {
				if (sold_seats.contains(Integer.parseInt(sarr[i]))) {
					System.out.println("The seat " + sarr[i] + " is sold.");
					System.out.println("Transaction failed. Please try again.");
				}
			}
			

		} catch (SQLException e) {
			System.out.println("Database error.");
			System.err.println("msg: " + e.getMessage() + " code: " + e.getErrorCode() + " state: " + e.getSQLState());
		}

		scanner.close();
	}

    public static void recentShows() {
		System.out.println("Please input a time period for shows that you are looking for.");
		System.out.println("Please respect the form YYYY-MM-DD to YYYY-MM-DD, where the first date "
				+ "is smaller than or equal to the second time and both dates are inclusive.");
		System.out.print(">> ");
		Scanner scanner = new Scanner(System.in);// create a new scanner for user inputs
		String time_period = scanner.nextLine();
		String[] dates = time_period.split("to");
		String date0 = "'" + dates[0].trim() + "'";
		String date1 = "'" + dates[1].trim() + "'";
		// TODO: Check user's input.

		// End TODO: Check user's input.
		// Query for movies
		String sql = "SELECT m.rating, s.title, m.genre, s.addr, s.sdate, s.start_time, s.end_time, s.director, s.lang "
				+ "FROM shows s, movie m " + "WHERE s.sdate >= " + date0 + " AND s.sdate <= " + date1
				+ " AND s.addr = m.addr AND s.room = m.room_num AND s.sdate = m.mdate AND s.start_time = m.start_time "
				+ "ORDER BY m.rating DESC";
		System.out.println("Movies found (ordered by rating):");
		try {
			ResultSet rs = stmt.executeQuery(sql);
			int counter = 1;
			while (rs.next()) {
				System.out.println(String.format("%1$d. INFO for Movie %2$s with rating %3$.2f in genre %4$s:", counter,
						rs.getString("title"), rs.getDouble("rating"), rs.getString("genre")));
				System.out.println(String.format(
						"address: %1$s; date: %2$s; start time: %3$s; end time: %4$s; director: %5$s; language: %6$s",
						rs.getString("addr"), rs.getDate("sdate"), rs.getTime("start_time"), rs.getTime("end_time"),
						rs.getString("director"), rs.getString("lang")));
				System.out.println();
				counter++;
			}
		} catch (SQLException e) {
			System.out.println("Database error.");
			System.err.println("msg: " + e.getMessage() + "code: " + e.getErrorCode() + "state: " + e.getSQLState());
		}

		// Query for stage shows
		sql = "SELECT s.title, t.leading_actor, s.addr, s.sdate, s.start_time, s.end_time, s.director, s.lang "
				+ "FROM shows s, stageshow t " + "WHERE s.sdate >= " + date0 + " AND s.sdate <= " + date1
				+ " AND s.addr = t.addr AND s.room = t.room_num AND s.sdate = t.sdate AND s.start_time = t.start_time ";
		System.out.println("Stage shows found:");
		try {
			ResultSet rs = stmt.executeQuery(sql);
			int counter = 1;
			while (rs.next()) {
				System.out.println(String.format("%1$d. INFO for Stage show %2$s with leading actor %3$s:", counter,
						rs.getString("title"), rs.getString("leading_actor")));
				System.out.println(String.format(
						"address: %1$s; date: %2$s; start time: %3$s; end time: %4$s; director: %5$; language: %6$s",
						rs.getString("addr"), rs.getDate("sdate"), rs.getTime("start_time"), rs.getTime("end_time"),
						rs.getString("director"), rs.getString("lang")));
				System.out.println();
				counter++;
			}
		} catch (SQLException e) {
			System.out.println("Database error.");
			System.err.println("msg: " + e.getMessage() + "code: " + e.getErrorCode() + "state: " + e.getSQLState());
		}
		scanner.close();

	}

	public static void paymentRecord() {
		System.out.println("Please input a your 9 digit social id number.");
		System.out.print(">> ");
		Scanner scanner = new Scanner(System.in);// create a new scanner for user inputs
		String id = scanner.nextLine();
		// TODO: Check user's input.

		// End TODO: Check user's input.
		id = "'" + id + "'";
		// Query for payment records
		String sql = "SELECT p.payment_num, p.payment_method, p.amount, p.ptime, t.price, t.addr, s.title, s.sdate, s.start_time "
				+ "FROM payment p, tickets t, shows s " + "WHERE p.sid = " + id
				+ " AND p.payment_num = t.pid AND s.addr = t.addr AND s.room = t.room_num AND s.sdate = t.sdate AND s.start_time = t.start_time "
				+ "ORDER BY p.ptime, p.payment_num";

		System.out.println("Payment records found:");
		String counter = "empty";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String payment = String.format(
						"Payment number %1$s with amount %2$.2f. payment method: %3$s, payment time: %4$s",
						rs.getString("payment_num"), rs.getDouble("amount"), rs.getString("payment_method"),
						rs.getTimestamp("ptime"));
				String item = String.format(
						"Item INFO: show name: %1$s; show address: %2$s; show date: %3$s; start time: %4$s",
						rs.getString("title"), rs.getString("addr"), rs.getDate("sdate"), rs.getTime("start_time"));
				if (payment.equals(counter)) {
					System.out.println(item);
				} else {
					System.out.println();
					System.out.println(payment);
					System.out.println(item);
					counter = payment;
				}
			}
		} catch (SQLException e) {
			System.out.println("Database error.");
			System.err.println("msg: " + e.getMessage() + "code: " + e.getErrorCode() + "state: " + e.getSQLState());
		}
		System.out.println();
		scanner.close();
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
							case 1://add movie schedule
                                addShow();
								break;
							case 2://sale statistics
                                printSale();
								break;
							case 3://review duty
                                printDuty();
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
								bookTickets();
								break;
							case 2:
								recentShows();
								break;
							case 3:
								paymentRecord();
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
