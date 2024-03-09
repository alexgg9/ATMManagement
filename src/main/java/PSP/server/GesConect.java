package PSP.server;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GesConect implements Runnable {
    private Socket clientSocket;
    private Connection con;

    public GesConect(Socket clientSocket, Connection con) {
        this.clientSocket = clientSocket;
        this.con = con;
    }


    @Override
    public void run() {
        try (
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter clientOutput = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            try {
                sendMenu(clientOutput);
                String choice = clientInput.readLine();
                if (choice.equals("1")) {
                    handleATM(clientInput, clientOutput);
                } else if (choice.equals("2")) {
                    handleBankOperator(clientInput, clientOutput);
                } else if (choice.equals("3")) {
                    clientSocket.close();
                }
            } catch (SocketException e) {
                System.err.println("Error reading from socket: " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMenu(PrintWriter clientOutput) {
        clientOutput.println("Welcome! Please select an option:");
        clientOutput.println("1. ATM");
        clientOutput.println("2. Bank Operator");
        clientOutput.println("3. Exit");
    }



    private void handleATM(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Enter your username:");
        String username = clientInput.readLine();
        clientOutput.println("Enter your password:");
        String password = clientInput.readLine();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT balance FROM bankaccount JOIN client ON bankaccount.id_client = client.id_client WHERE client.username = ? AND client.password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                clientOutput.println("Your current balance is: " + balance);

                boolean exitRequested = false;
                do {
                    clientOutput.println("Please select an option:");
                    clientOutput.println("1. Check balance");
                    clientOutput.println("2. Withdraw");
                    clientOutput.println("3. Deposit");

                    String option = clientInput.readLine();
                    switch (option) {
                        case "1":
                            handleCheckBalance(username, clientOutput);
                            break;
                        case "2":
                            handleWithdrawal(username, clientInput, clientOutput);
                            break;
                        case "3":
                            handleDeposit(username, clientInput, clientOutput);
                            break;
                        default:
                            clientOutput.println("Invalid option.");
                            break;
                    }

                    clientOutput.println("Do you want to perform another operation? (yes/no)");
                    String continueOption = clientInput.readLine();
                    if (!continueOption.equalsIgnoreCase("yes")) {
                        exitRequested = true;
                        clientOutput.println("Exiting ATM menu.");
                        sendMenu(clientOutput);
                    }
                } while (!exitRequested);

            } else {
                clientOutput.println("Incorrect credentials. Closing connection.");
                clientSocket.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void handleCheckBalance(String username, PrintWriter clientOutput) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT balance FROM bankaccount JOIN client ON bankaccount.id_client = client.id_client WHERE client.username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                clientOutput.println("Your current balance is: " + balance);
            } else {
                clientOutput.println("Client not found.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error fetching balance.");
            return;
        }
    }






    private void handleWithdrawal(String username, BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Enter the amount to withdraw:");
        double amountToWithdraw = Double.parseDouble(clientInput.readLine());

        try {
            int idClient = getIdClient(username);

            if (idClient != -1) {
                PreparedStatement withdrawStatement = con.prepareStatement("UPDATE bankaccount SET balance = balance - ? WHERE id_client = ?");
                withdrawStatement.setDouble(1, amountToWithdraw);
                withdrawStatement.setInt(2, idClient);
                int rowsAffected = withdrawStatement.executeUpdate();

                if (rowsAffected > 0) {
                    clientOutput.println("Withdrawal successful.");
                } else {
                    clientOutput.println("Insufficient funds.");
                }
            } else {
                clientOutput.println("Client not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error processing withdrawal.");
        }
    }


    private int getIdClient(String username) throws SQLException {
        int idClient = -1;

        PreparedStatement statement = con.prepareStatement("SELECT id_client FROM client WHERE username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            idClient = resultSet.getInt("id_client");
        }

        return idClient;
    }





    private void handleDeposit(String username, BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Enter the amount to deposit:");
        double amountToDeposit = Double.parseDouble(clientInput.readLine());
        try {
            int idClient = getIdClient(username);
            PreparedStatement depositStatement = con.prepareStatement("UPDATE bankaccount SET balance = balance + ? WHERE id_client = ?");
            depositStatement.setDouble(1, amountToDeposit);
            depositStatement.setInt(2, idClient);
            int rowsAffected = depositStatement.executeUpdate();
            if (rowsAffected > 0) {
                clientOutput.println("Deposit successful.");
            } else {
                clientOutput.println("Error processing deposit.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error processing deposit.");
        }
    }

    private boolean authenticateUser(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Ingrese su nombre de usuario:");
        String username;
        String password;
        try {
            username = clientInput.readLine();
            clientOutput.println("Ingrese su contraseña:");
            password = clientInput.readLine();
        } catch (IOException e) {
            clientOutput.println("Error al leer la entrada. Cerrando la conexión.");
            clientSocket.close();
            return false;
        }

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM client WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return true;
            } else {
                clientOutput.println("Credenciales incorrectas.");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
            return false;
        }
    }


    private void handleBankOperator(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        try {
            if (!authenticateUser(clientInput, clientOutput)) {
                clientOutput.println("Cerrando la conexión.");
                return;
            }

            while (true) {
                clientOutput.println("¡Bienvenido! Por favor seleccione una opción:");
                clientOutput.println("1. Agregar un nuevo usuario");
                clientOutput.println("2. Crear una nueva cuenta bancaria");
                clientOutput.println("3. Ver detalles de la cuenta");
                clientOutput.println("4. Ver detalles del cliente");
                clientOutput.println("5. Eliminar una cuenta bancaria");
                clientOutput.println("6. Salir");

                String option = clientInput.readLine();
                switch (option) {
                    case "1":
                        addNewUser(clientInput, clientOutput);
                        break;
                    case "2":
                        createNewAccount(clientInput, clientOutput);
                        break;
                    case "3":
                        viewAccountDetails(clientInput, clientOutput);
                        break;
                    case "4":
                        viewClientDetails(clientInput, clientOutput);
                        break;
                    case "5":
                        deleteAccount(clientInput, clientOutput);
                        break;
                    case "6":
                        clientOutput.println("Cerrando la sesión.");
                        return;
                    default:
                        clientOutput.println("Opción inválida.");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
            return;
        }
    }



    private void addNewUser(BufferedReader clientInput, PrintWriter clientOutput) throws IOException, SQLException {
        clientOutput.println("Ingrese el nombre de usuario del nuevo usuario:");
        String newUsername = clientInput.readLine();
        PreparedStatement checkUserStatement = con.prepareStatement("SELECT COUNT(*) AS count FROM client WHERE username = ?");
        checkUserStatement.setString(1, newUsername);
        ResultSet userCheckResultSet = checkUserStatement.executeQuery();
        if (userCheckResultSet.next()) {
            int count = userCheckResultSet.getInt("count");
            if (count > 0) {
                clientOutput.println("El nombre de usuario ya está en uso. Por favor, elija otro.");
                return;
            }
        }
        clientOutput.println("Ingrese el nombre del usuario:");
        String firstName = clientInput.readLine();
        clientOutput.println("Ingrese el apellido del usuario:");
        String lastName = clientInput.readLine();
        clientOutput.println("Ingrese la contraseña del nuevo usuario:");
        String newPassword = clientInput.readLine();
        try {
            PreparedStatement addUserStatement = con.prepareStatement("INSERT INTO client (firstname, lastname, username, password) VALUES (?, ?, ?, ?)");
            addUserStatement.setString(1, firstName);
            addUserStatement.setString(2, lastName);
            addUserStatement.setString(3, newUsername);
            addUserStatement.setString(4, newPassword);

            int addUserRowsAffected = addUserStatement.executeUpdate();
            if (addUserRowsAffected > 0) {
                clientOutput.println("Nuevo usuario agregado exitosamente.");
            } else {
                clientOutput.println("Error al agregar nuevo usuario.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
        }
    }

    public void createNewAccount(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Ingrese el nombre de usuarioal que quieres crear la cuenta:");
        String username = clientInput.readLine();
        clientOutput.println("Ingrese el tipo de cuenta (Cuenta Corriente o Cuenta Ahorro):");
        String accountType = clientInput.readLine();
        clientOutput.println("Ingrese el saldo inicial de la cuenta:");
        double initialBalance = Double.parseDouble(clientInput.readLine());
        try {
            int idClient = getIdClient(username);

            if (idClient != -1) {
                PreparedStatement addAccountStatement = con.prepareStatement("INSERT INTO bankaccount (id_client, account_type, balance) VALUES (?, ?, ?)");
                addAccountStatement.setInt(1, idClient);
                addAccountStatement.setString(2, accountType);
                addAccountStatement.setDouble(3, initialBalance);
                int addAccountRowsAffected = addAccountStatement.executeUpdate();
                if (addAccountRowsAffected > 0) {
                    clientOutput.println("Nueva cuenta bancaria creada exitosamente.");
                } else {
                    clientOutput.println("Error al crear la nueva cuenta bancaria.");
                }
            } else {
                clientOutput.println("Cliente no encontrado.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
        }
    }

    public void viewAccountDetails(BufferedReader clientInput, PrintWriter clientOutput) {
        try {
            clientOutput.println("Ingresa el nombre de usuario del cliente:");
            String usernameClient = clientInput.readLine();
            int idClient = getIdClient(usernameClient);
            PreparedStatement accountDetailsStatement = con.prepareStatement("SELECT * FROM bankaccount WHERE id_client = ?");
            accountDetailsStatement.setInt(1, idClient);
            ResultSet accountDetailsResultSet = accountDetailsStatement.executeQuery();
            if (accountDetailsResultSet.next()) {
                String aaccounttype = accountDetailsResultSet.getString("account_type");
                double balance = accountDetailsResultSet.getDouble("balance");

                clientOutput.println("Detalles de la cuenta:");
                clientOutput.println("Tipo de cuenta: " + aaccounttype);
                clientOutput.println("Saldo: " + balance);
            } else {
                clientOutput.println("No se encontraron detalles de la cuenta.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
        }
    }

    public void viewClientDetails(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Ingresa el nombre de usuario del cliente:");
        String username = clientInput.readLine();
        try {
            PreparedStatement clientDetailsStatement = con.prepareStatement("SELECT * FROM client WHERE username = ?");
            clientDetailsStatement.setString(1, username);
            ResultSet clientDetailsResultSet = clientDetailsStatement.executeQuery();
            if (clientDetailsResultSet.next()) {
                String name = clientDetailsResultSet.getString("firstname");
                String surname = clientDetailsResultSet.getString("lastname");
                clientOutput.println("Detalles del cliente:");
                clientOutput.println("Nombre: " + name);
                clientOutput.println("Apellido: " + surname);
            } else {
                clientOutput.println("No se encontró el cliente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
        }
    }

    public void deleteAccount(BufferedReader clientInput, PrintWriter clientOutput) throws IOException {
        clientOutput.println("Ingrese el ID de la cuenta bancaria que desea eliminar:");
        int accountIdToDelete = Integer.parseInt(clientInput.readLine());
        try {
            PreparedStatement deleteAccountStatement = con.prepareStatement("DELETE FROM bankaccount WHERE account_id = ?");
            deleteAccountStatement.setInt(1, accountIdToDelete);
            int deleteAccountRowsAffected = deleteAccountStatement.executeUpdate();
            if (deleteAccountRowsAffected > 0) {
                clientOutput.println("Cuenta bancaria eliminada exitosamente.");
            } else {
                clientOutput.println("No se encontró la cuenta bancaria especificada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            clientOutput.println("Error al procesar la operación.");
        }
    }
}
