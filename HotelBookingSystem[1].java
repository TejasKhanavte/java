
import java.util.Scanner;

public class HotelBookingSystem {
    static final int FLOORS = 5;     
    
    static final int ROOMS_PER_FLOOR = 5; 
    static boolean[][] rooms = new boolean[FLOORS][ROOMS_PER_FLOOR]; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n=== Hotel Booking System ===");
            System.out.println("1. View Room Status");
            System.out.println("2. Book a Room");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewRooms();
                    break;
                case 2:
                    bookRoom(scanner);
                    break;
                case 3:
                    System.out.println("Thank you for using the Hotel Booking System.");
                    break;
                default:
                    System.out.println("Invalid choice. Please choose again.");
            }
        } while (choice != 3);

        scanner.close();
    }

    public static void viewRooms() {
        System.out.println("\nRoom Status (F = Floor, R = Room):");
        for (int i = 0; i < FLOORS; i++) {
            for (int j = 0; j < ROOMS_PER_FLOOR; j++) {
                String status = rooms[i][j] ? "[X]" : "[ ]";
                System.out.print("F" + (i + 1) + "R" + (j + 1) + status + " ");
            }
            System.out.println();
        }
    }

    public static void bookRoom(Scanner scanner) {
        System.out.print("Enter floor number (1 to " + FLOORS + "): ");
        int floor = scanner.nextInt();
        System.out.print("Enter room number (1 to " + ROOMS_PER_FLOOR + "): ");
        int room = scanner.nextInt();

        if (floor < 1 || floor > FLOORS || room < 1 || room > ROOMS_PER_FLOOR) {
            System.out.println("Invalid floor or room number.");
            return;
        }

        if (rooms[floor - 1][room - 1]) {
            System.out.println("Sorry, that room is already booked.");
        } else {
            rooms[floor - 1][room - 1] = true;
            System.out.println("Room F" + floor + "R" + room + " has been successfully booked.");
        }
    }
}

