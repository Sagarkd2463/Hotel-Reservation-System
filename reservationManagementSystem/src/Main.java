import java.sql.*;
import java.util.Scanner;

public class Main {
    //default parameters to connect to the database
    private static final String url = "";
    private static final String username  = "";
    private static final String password = "";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); //connecting jdbc driver
                System.out.println("Driver Loaded Successfully!!!");
            } catch (ClassNotFoundException e){
                System.out.println(e.getMessage());
            }

            try { //establishing connection with the driver manager class by passing default parameters above
                Connection connection = DriverManager.getConnection(url, username, password);
                while(true){ //looping until user selects 0 option
                    System.out.println();
                    System.out.println("HOTEL MANAGEMENT SYSTEM");
                    Scanner scanner = new Scanner(System.in); //input from user for selecting anyone from them
                    System.out.println("1. Reserve a room");
                    System.out.println("2. View Reservations");
                    System.out.println("3. Get Room Number");
                    System.out.println("4. Update Reservations");
                    System.out.println("5. Delete Reservations");
                    System.out.println("0. Exit");
                    System.out.print("Choose an option: ");

                    int option = scanner.nextInt();
                    switch (option) {
                        case 1 ->
                                createReserveRoom(connection, scanner); //passing connection & scanner for creating a new reservation for new user
                        case 2 ->
                                checkReservations(connection); //passing connection for reading reservations from the reservation table
                        case 3 ->
                                getRoomNumber(connection, scanner); //passing connection & scanner for getting specific room number of the existing user
                        case 4 ->
                                updateReservation(connection, scanner); //passing connection & scanner for updating data of the existing user
                        case 5 ->
                                deleteReservations(connection, scanner); //passing connection & scanner for deleting data of any existing user
                        case 0 -> {
                            exit(); //exiting from the loop of hotel reservation system
                            scanner.close();
                            return;
                        }
                        default -> System.out.println("Invalid choice. Try Again.");
                    }
                }
            } catch (SQLException e){
                System.out.println(e.getMessage());
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
    }

    private static void createReserveRoom(Connection connection, Scanner scanner) {
        //scanner for taking input from user
        System.out.print("Enter guest name: ");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter room number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter contact number: ");
        String contactNumber = scanner.next();

        //insert query for passing data into reservation table
        String query = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                "VALUES ('" + guestName + "', " + roomNumber + ", '" + contactNumber + "')";

        try{
            Statement statement = connection.createStatement();  //a statement for executing SQL query of insert
            int affectedRows = statement.executeUpdate(query);  //checking how many rows into table are affected

            if(affectedRows > 0 ){
                System.out.println("Reservation Done Successful!!");
            } else {
                System.out.println("Reservation Failed!!");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void checkReservations(Connection connection) {
        //retrieving a query for getting data from the table
        String query = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        try{
            Statement statement = connection.createStatement(); //SQl query statement
            ResultSet resultSet = statement.executeQuery(query); //a pointer used for fetching related data from the database

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while(resultSet.next()){ //next method until data is present in the table
                //fetching all the details required from the table
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try{
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            //retrieving data from reservation table
            String query = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try{
                Statement statement = connection.createStatement(); //create SQL query statement
                ResultSet resultSet = statement.executeQuery(query); //a pointer for fetching data required for the query

                if(resultSet.next()){ //method until all the data is retrieved
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            } finally {
                System.out.println("------------------------------");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationId)) { // a method to check whether the room is reserved or not
                System.out.println("Reservation not found for the given ID.");
                return;
            }
            //update the information for that user using scanner class
            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            //update query for getting required data for modifying details
            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;
            try {
                Statement statement = connection.createStatement(); //SQL query statement
                int affectedRows = statement.executeUpdate(sql); //checking rows affected into the table

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            } finally {
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void deleteReservations(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");  //reservation_id for deleting any user from table
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) { // a method for checking if reservation is present already or not
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            //delete query for deleting data from the table
            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try {
                Statement statement = connection.createStatement(); //SQL query
                int affectedRows = statement.executeUpdate(sql);  //checking rows affected

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            } finally {
                System.out.println("---------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            //select query for identifying id of the user is present into table or not
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try {
                Statement statement = connection.createStatement(); //SQL query statement
                ResultSet resultSet = statement.executeQuery(sql); // a pointer for iterating through table

                return resultSet.next(); // If there's a result, the reservation exists
            } finally {
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }

    private static void exit() throws InterruptedException{
        System.out.print("Exiting System"); //an animated method for exiting through the system
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(500);
            i--;
        }
        System.out.println();
        System.out.println("Thank You for using Hotel Reservation System");
    }
}