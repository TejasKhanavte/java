package dsa_and_basics.java;

 class a1{
    protected String accountNumber;
    protected String accountHolderName;
    protected double balance;

    public a1 (String accountNumber, String accountHolderName, double initialBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.balance = initialBalance;
    }
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount + ". New balance: " + balance);
        } else {
            System.out.println("Deposit amount must be positive.");
        }
    }
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            System.out.println("Withdrew: " + amount + ". New balance: " + balance);
        } else {
            System.out.println("Insufficient balance or invalid amount.");
        }
    }
    public void checkBalance() {
        System.out.println("YOUR BALANCE IS : "+balance);
    }
}

class gstaccount extends a1 {
    protected double interestRate;
    public gstaccount(String accountNumber, String accountHolderName, double initialBalance, double interestRate) {
        super(accountNumber, accountHolderName, initialBalance);
        this.interestRate = interestRate;
    }
    public void gst() {
        double interest = balance * interestRate / 100;
        deposit(interest);

        System.out.println("Interest applied: " + interest + ". New balance: " + balance);
    }
    public double getInterestRate() {
        return interestRate;
    }
    public void setInterestRate(double rate) {
        if (rate > 0) {
            interestRate = rate;
        } else {
            System.out.println("Interest rate must be positive.");
        }
    }
}

public class Bankaccount {
    public static void main(String[] args) {
        gstaccount savAcc = new gstaccount("102", "Bob", 2000, 4.0);
        savAcc.checkBalance();
        savAcc.deposit(1000);
        savAcc.withdraw(500);
        savAcc.gst();
        gstaccount savAcc2= new gstaccount("103", "pepper ", 1999999, 3.0);
        savAcc2.checkBalance();
        savAcc2.deposit(10000);
        savAcc2.withdraw(50000);
        savAcc2.gst();
    }
}