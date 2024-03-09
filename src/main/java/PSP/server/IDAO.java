package PSP.server;

public interface IDAO {
    boolean verifyCredentials(String username, String password);
    boolean addUser(String username, String password);
    boolean createBankAccount(String username);
    double getBalance(String username);
    boolean withdraw(String username, double amount);
    void deposit(String username, double amount);
    String getAccountDetails(int accountNumber);
    String getClientDetails(String username);
    boolean deleteBankAccount(int accountNumber);
}
