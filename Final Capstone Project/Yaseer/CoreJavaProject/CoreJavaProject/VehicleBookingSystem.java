package CoreJavaProject;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;


public class VehicleBookingSystem {
    private static Connection connection;

    public static List<Vehicle> vehicles = new ArrayList<>();
    public static List<Trip> trips = new ArrayList<>();
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        initializeDatabase();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter 1 for Vehicle Registration");
            System.out.println("Enter 2 for Vehicle Allocation Request");
            System.out.println("Enter 3 for Multiple Allocation Requests in Parallel");
            System.out.println("Enter 4 for Vehicle Usage Report");
            System.out.println("Enter 0 to exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerVehicle(scanner);
                    break;
                case 2:
                    allocateVehicle(scanner);
                    break;
                case 3:
                    parallelAllocation(scanner);
                    break;
                case 4:
                    generateCsvReport();
                    break;
                case 0:
                    executor.shutdown();
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void initializeDatabase() {
        try {
            // Update with your MySQL database credentials
            // Establish a database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/suretrustproject", "root", "yaseer2003@03");
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1); // Exit if a database connection cannot be established
        }
    }

    public static void insertVehicleIntoDatabase(Vehicle vehicle) {
        try {
            String insertVehicleSQL = "INSERT INTO vehicles (registration_number, type, manufacturer, capacity, is_available, distance_travelled, total_trips) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertVehicleSQL);
            preparedStatement.setString(1, vehicle.getRegistrationNumber());
            preparedStatement.setString(2, vehicle.getType());
            preparedStatement.setString(3, vehicle.getManufacturer());
            preparedStatement.setInt(4, vehicle.getCapacity());
            preparedStatement.setBoolean(5, vehicle.isAvailable());
            preparedStatement.setDouble(6,vehicle.getTotalDistance());
            preparedStatement.setInt(7, vehicle.getTotalTrips());

            // Execute the INSERT statement
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertTripIntoDatabase(Trip trip) {
        try {
            String insertTripSQL = "INSERT INTO trips (trip_id, vehicle_registration_number, start_kilometer, end_kilometer, start_time, status) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertTripSQL);
            preparedStatement.setInt(1, trip.tripId);
            preparedStatement.setString(2, trip.vehicleRegistrationNumber);
            preparedStatement.setDouble(3, trip.startKilometer);
            preparedStatement.setDouble(4, trip.endKilometer);

            // Parse the start time and set it as a Timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date parsedDate = dateFormat.parse(trip.startTime);
            java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
            preparedStatement.setTimestamp(5, timestamp);
            preparedStatement.setString(6, trip.status);

            // Print or log the SQL statement before executing it
            System.out.println("SQL Statement: " + preparedStatement.toString());

            // Execute the INSERT statement
            preparedStatement.executeUpdate();
            preparedStatement.close();

            System.out.println("Trip inserted into the database successfully.");
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            System.err.println("Error inserting trip into the database: " + e.getMessage());
        }
    }


    public static void registerVehicle(Scanner scanner) {
        System.out.print("Enter Registration Number: ");
        String registrationNumber = scanner.nextLine();
        System.out.print("Enter Vehicle Type: ");
        String type = scanner.nextLine();
        System.out.print("Enter Vehicle Manufacturer: ");
        String manufacturer = scanner.nextLine();
        System.out.print("Enter Vehicle Capacity: ");
        int capacity = scanner.nextInt();
        scanner.nextLine();

        Vehicle vehicle1 = new Vehicle(registrationNumber, type, manufacturer, capacity);
        vehicles.add(vehicle1);
        insertVehicleIntoDatabase(vehicle1);

        System.out.println("Vehicle registered successfully.");
    }

    public static void allocateVehicle(Scanner scanner) {
        System.out.print("Enter Trip ID: ");
        int tripId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Start Kilometer: ");
        double startKilometer = scanner.nextDouble();
        System.out.print("Enter End Kilometer: ");
        double endKilometer = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter Start Time: ");
        String startTime = scanner.nextLine();
        System.out.print("Enter Required Vehicle Capacity: ");
        int requiredCapacity = scanner.nextInt();
        scanner.nextLine();

        boolean allocated = false;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.isAvailable() && vehicle.getCapacity() >= requiredCapacity) {
                double distance = endKilometer - startKilometer;
                vehicle.allocate(distance);
                Trip trip = new Trip(tripId, vehicle.getRegistrationNumber(), startKilometer, endKilometer, startTime);
                trips.add(trip);

                // Insert trip data into the database
                insertTripIntoDatabase(trip);

                allocated = true;
                System.out.println("Trip " + tripId + " allocated to vehicle " + vehicle.getRegistrationNumber());
                break;
            }
        }

        if (!allocated) {
            System.out.println("No available vehicles for trip " + tripId + ". Retrying after 30 seconds...");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            allocateVehicle(scanner);
        }
    }

    public static void parallelAllocation(Scanner scanner) {
        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(() -> {
                allocateVehicle(scanner);
                return null; // Callable requires a return value
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void generateCsvReport() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date now = new Date(0);
        String currentTime = dateFormat.format(now);

        System.out.println("Generating CSV report...");
        System.out.println("Registration Number, Vehicle Type, Total Trips Completed, Total Distance Traveled");

        for (Vehicle vehicle : vehicles) {
            System.out.println(vehicle.getRegistrationNumber() + "," + vehicle.getType() + ","
                    + vehicle.getTotalTrips() + "," + vehicle.getTotalDistance());
        }

        for (Trip trip : trips) {
            if ("In Progress".equals(trip.getStatus())) {
                // Complete ongoing trips
                trip.completeTrip(currentTime);
                System.out.println("Trip " + trip.getTripId() + " completed.");
            }
        }
    }
}