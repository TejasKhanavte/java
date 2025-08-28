//Tejas khanavate
import java.util.InputMismatchException;
import java.util.Scanner;

public class SimpleAtm {
    private int pin;
    private double balance;
    private int accNo;
    private static int accNoCounter = 1000;

    public SimpleAtm(int pin, double balance) {
        this.pin = pin;
        this.balance = balance;
        this.accNo = ++accNoCounter;
    }

    public int getAccNo() {
        return accNo;
    }

    public void showBalance() {
        System.out.printf("Current balance: $%.2f%n", balance);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        balance += amount;
        System.out.printf("Deposited: $%.2f%n", amount);
    }

    public boolean verifyPin(int enteredPin) {
        return this.pin == enteredPin;
    }

    public void withdraw(int enteredPin, double amount) {
        if (this.pin != enteredPin) {
            throw new SecurityException("Incorrect PIN.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > balance) {
            throw new ArithmeticException("Insufficient funds.");
        }
        balance -= amount;
        System.out.printf("Withdrawn: $%.2f%n", amount);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        SimpleAtm myAtm = new SimpleAtm(1234, 5000.00);

        System.out.println("Welcome to SimpleATM!");

        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Exit");

            System.out.print("Your choice: ");

            try {
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        myAtm.showBalance();
                        break;

                    case 2:
                        System.out.print("Enter deposit amount: ");
                        double depositAmount = sc.nextDouble();
                        try {
                            myAtm.deposit(depositAmount);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;

                    case 3:
                        boolean pinVerified = false;
                        int attempts = 3;

                        while (attempts > 0 && !pinVerified) {
                            System.out.print("Enter your PIN: ");
                            int enteredPin = sc.nextInt();
                            if (myAtm.verifyPin(enteredPin)) {
                                pinVerified = true;
                            } else {
                                attempts--;
                                System.out.println("Incorrect PIN. Attempts left: " + attempts);
                                if (attempts == 0) {
                                    System.out.println("Too many incorrect attempts. Returning to main menu.");
                                }
                            }
                        }

                        if (pinVerified) {
                            System.out.print("Enter withdrawal amount: ");
                            double withdrawAmount = sc.nextDouble();
                            try {
                                myAtm.withdraw(1234, withdrawAmount); 
                            } catch (IllegalArgumentException | ArithmeticException e) {
                                System.out.println("Error: " + e.getMessage());
                            } catch (SecurityException se) {
                                System.out.println("Error: " + se.getMessage());
                            }
                        }
                        break;

                    case 4:
                        System.out.println("Thank you for using SimpleATM. Goodbye!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid option. Please choose 1-4.");
                }
            } catch (InputMismatchException ime) {
                System.out.println("Invalid input type. Please enter numbers only.");
                sc.nextLine(); 
            } finally {
                System.out.println("Operation completed.");
            }
        }
    }
}
