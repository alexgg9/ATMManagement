package PSP.client;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 8080;

        try (
                Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter clientOutput = new PrintWriter(socket.getOutputStream(), true)
        ) {
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            Thread serverListener = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = clientInput.readLine()) != null) {
                        System.out.println("Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            serverListener.start();
            String userInputLine;
            while ((userInputLine = userInput.readLine()) != null) {
                clientOutput.println(userInputLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

