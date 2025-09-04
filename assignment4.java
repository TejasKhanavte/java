package dsa_and_basics.java;
import java.util.Scanner;
import java.util.InputMismatchException;

class createAccout {

    String name;
    protected int pin;
    double balance1;
    static int acc_noo = 2000;

    public createAccout(String name, int pin, double balance1) {
        this.name = name;
        this.pin = pin;
        this.balance1 = balance1;
    }

    public void setting_acc_no() {
        acc_noo++;
        System.out.printf("ACCOUNT NUMBER FOR %s is : %d\n", name, acc_noo);
    }
}

class mainprocess extends createAccout {

    mainprocess(String name, int pin, double initialBalance) {
        super(name, pin, initialBalance);
    }

    public void getbaalance() {
        System.out.printf("BALANCE IN ACCOUNT NUMBER %d is %.2f\n", acc_noo, balance1);
    }

    public void  withdraw_1(double amount1) {
        if (amount1 > balance1 || amount1 < 0) {
            throw new IllegalArgumentException(" INSUFFICIENT BALANCE");
        } else {
            balance1 -= amount1;
            System.out.println("AMOUNT WITHDRAWN, NEW BALANCE:\t" + balance1);
        }
    }

    public void creddit(double amount2) {
        if (amount2 <= 0) {
            throw new IllegalArgumentException("INVALID AMOUNT");
        } else {
            balance1 += amount2;
            System.out.println("AMOUNT CREDITED, NEW BALANCE:\t" + balance1);

        }
    }

    public void showPin() {
        System.out.println("PIN for this account is: " + pin);
    }
}

public class assignment4 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                try {

                    System.out.println("\n=== BANK ACCOUNT SYSTEM ===");
                    System.out.println("1. Create new account and perform transactions");
                    System.out.println("2. Exit");
                    System.out.print("Enter your choice: ");
                    int choice = sc.nextInt();

                    sc.nextLine();

                    if (choice == 2) {
                        System.out.println("Exiting program. Goodbye!");
                        break;
                    }
                    if (choice == 1) {
                        System.out.print("Enter account holder name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter pin: ");
                        int pin = sc.nextInt();
                        if(pin <= 0){
                          throw new SecurityException("ERROR : PIN CANT BE NEGATIVE");
                        }
                        System.out.print("Enter initial balance: ");
                        double balance = sc.nextDouble();

                        mainprocess account = new mainprocess(name, pin, balance);
                        account.setting_acc_no();
                        account.getbaalance();

                        while (true) {
                            System.out.println("\n--- Account Menu ---");
                            System.out.println("1. Credit");
                            System.out.println("2. Withdraw");
                            System.out.println("3. View Balance");
                            System.out.println("4. Display PIN");
                            System.out.println("5. Exit to Main Menu");
                            System.out.print(" Choose option: ");
                            int accChoice = sc.nextInt();

                            if (accChoice == 5) {
                                break;
                            }
                            {
                                switch (accChoice) {
                                    case 1 -> {
                                        System.out.print("Enter amount to credit: ");
                                        double creditAmt = sc.nextDouble();
                                        account.creddit(creditAmt);
                                    }
                                    case 2 -> {
                                        System.out.print("Enter amount to withdraw: ");
                                        double withdrawAmt = sc.nextDouble();

                                        account.withdraw_1(withdrawAmt);
                                    }
                                    case 3 -> account.getbaalance();
                                    case 4 -> account.showPin();
                                    default -> throw new IllegalArgumentException(" ERROR : INVALID AMMOUNT ");
                                }
                            }
                        }
                    } else {
                        System.out.println("ENTER A VALID NUMBER OR PRESS 2 TO EXIT ");
                        throw new IllegalArgumentException("INVALID AMMOUNT ");
                    }
                } catch (ArithmeticException | IllegalArgumentException | SecurityException a) {
                    System.out.println("ERROR :" + a.getMessage());
                    sc.nextLine();
                }
            }  catch (InputMismatchException e) {
                System.out.println("ERROR: Please enter a valid number." );
                sc.nextLine();}
        }
    }
}
