package PSP.server;

import java.util.HashMap;
import java.util.Map;

public class DAO implements IDAO {
    // Simulación de una base de datos en memoria utilizando un mapa
    private Map<String, String> users;
    private Map<String, Double> accountBalances;

    public DAO() {
        users = new HashMap<>();
        accountBalances = new HashMap<>();
    }

    @Override
    public boolean verifyCredentials(String username, String password) {
        // Verificar si el usuario y la contraseña coinciden
        return users.containsKey(username) && users.get(username).equals(password);
    }

    @Override
    public boolean addUser(String username, String password) {
        // Agregar un nuevo usuario
        if (!users.containsKey(username)) {
            users.put(username, password);
            return true;
        }
        return false; // Usuario ya existe
    }

    @Override
    public boolean createBankAccount(String username) {
        // Crear una nueva cuenta bancaria para el usuario
        if (!accountBalances.containsKey(username)) {
            accountBalances.put(username, 0.0); // Saldo inicial de 0
            return true;
        }
        return false; // El usuario ya tiene una cuenta bancaria
    }

    @Override
    public double getBalance(String username) {
        // Obtener el saldo de la cuenta del usuario
        return accountBalances.getOrDefault(username, 0.0);
    }

    @Override
    public boolean withdraw(String username, double amount) {
        // Retirar dinero de la cuenta del usuario
        if (accountBalances.containsKey(username)) {
            double balance = accountBalances.get(username);
            if (balance >= amount) {
                accountBalances.put(username, balance - amount);
                return true; // Retiro exitoso
            }
        }
        return false; // Fondos insuficientes o cuenta inexistente
    }

    @Override
    public void deposit(String username, double amount) {
        // Depositar dinero en la cuenta del usuario
        if (accountBalances.containsKey(username)) {
            double balance = accountBalances.get(username);
            accountBalances.put(username, balance + amount);
        }
    }

    @Override
    public String getAccountDetails(int accountNumber) {
        return null;
    }

    @Override
    public String getClientDetails(String username) {
        return null;
    }

    @Override
    public boolean deleteBankAccount(int accountNumber) {
        return false;
    }
}
