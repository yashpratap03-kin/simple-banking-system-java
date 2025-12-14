import java.io.*;
import java.util.Scanner;

class BankAccount {
    private String accountHolder;
    private int accountNumber;
    private double balance;

    public BankAccount(String accountHolder, int accountNumber, double balance) {
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return accountHolder + "," + accountNumber + "," + balance;
    }

    public static BankAccount fromString(String data) {
        String[] parts = data.split(",");
        return new BankAccount(parts[0],
                Integer.parseInt(parts[1]),
                Double.parseDouble(parts[2]));
    }
}

public class BankingSystem {

    static Scanner sc = new Scanner(System.in);
    static final String FILE_NAME = "accounts.txt";

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== SIMPLE BANKING SYSTEM =====");
            System.out.println("1. Create Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Check Balance");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> depositMoney();
                case 3 -> withdrawMoney();
                case 4 -> checkBalance();
                case 5 -> System.out.println("Thank you for using Banking System!");
                default -> System.out.println("Invalid choice!");
            }

        } while (choice != 5);

        sc.close();
    }

    // ---------------- METHODS ----------------

    static void createAccount() {
        sc.nextLine();
        System.out.print("Enter Account Holder Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        System.out.print("Enter Initial Balance: ");
        double bal = sc.nextDouble();

        BankAccount account = new BankAccount(name, accNo, bal);

        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(account.toString() + "\n");
            System.out.println("Account created successfully!");
        } catch (IOException e) {
            System.out.println("Error saving account.");
        }
    }

    static BankAccount findAccount(int accNo) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                BankAccount acc = BankAccount.fromString(line);
                if (acc.getAccountNumber() == accNo) {
                    return acc;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    static void updateAccount(BankAccount updatedAcc) {
        File tempFile = new File("temp.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
             FileWriter fw = new FileWriter(tempFile)) {

            String line;
            while ((line = br.readLine()) != null) {
                BankAccount acc = BankAccount.fromString(line);
                if (acc.getAccountNumber() == updatedAcc.getAccountNumber()) {
                    fw.write(updatedAcc.toString() + "\n");
                } else {
                    fw.write(line + "\n");
                }
            }

        } catch (IOException e) {
            System.out.println("Error updating account.");
        }

        new File(FILE_NAME).delete();
        tempFile.renameTo(new File(FILE_NAME));
    }

    static void depositMoney() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter amount to deposit: ");
        double amt = sc.nextDouble();

        acc.deposit(amt);
        updateAccount(acc);
        System.out.println("Deposit successful!");
    }

    static void withdrawMoney() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        BankAccount acc = findAccount(accNo);
        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter amount to withdraw: ");
        double amt = sc.nextDouble();

        if (acc.withdraw(amt)) {
            updateAccount(acc);
            System.out.println("Withdrawal successful!");
        } else {
            System.out.println("Insufficient balance!");
        }
    }

    static void checkBalance() {
        System.out.print("Enter Account Number: ");
        int accNo = sc.nextInt();

        BankAccount acc = findAccount(accNo);
        if (acc != null) {
            System.out.println("Current Balance: ₹" + acc.getBalance());
        } else {
            System.out.println("Account not found!");
        }
    }
}
