package inheritance;

public class TestAccount {

    public static void main(String[] args) {

        OverdraftAccount account = new OverdraftAccount(100);

        account.deposit(50);

        account.withdraw(200);

        account.withdraw(300);

        System.out.println("Final Balance: " + account.getBalance());
    }
}