package inheritance;

public class OverdraftAccount extends BankAccount {

    public OverdraftAccount(double balance) {
        super(balance);
    }

    @Override
    public void withdraw(double amount) {

        if (balance - amount >= -500) {
            balance -= amount;

            System.out.println("Withdrawal successful");
            System.out.println("Transaction Log:");
            System.out.println("Withdrawn: " + amount);
            System.out.println("Current Balance: " + balance);

        } else {
            System.out.println("Overdraft limit exceeded");
        }
    }

    @Override
    public void deposit(double amount) {
        balance += amount;

        System.out.println("Transaction Log:");
        System.out.println("Deposited: " + amount);
        System.out.println("Current Balance: " + balance);
    }
}