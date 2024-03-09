package PSP.model;

public class BankAccount {

    private int accountId;
    private int clientId;
    private String accountType;
    private double balance;


    public BankAccount() {
        this(0,0, "", 0.0 );
    }

    public BankAccount(int accountId, int clientId, String accountType, double balance) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.accountType = accountType;
        this.balance = balance;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "accountId=" + accountId +
                ", clientId=" + clientId +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                '}';
    }
}
