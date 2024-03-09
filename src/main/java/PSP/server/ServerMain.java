package PSP.server;

import PSP.SQLConnection.ConnectionMySQL;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;

public class ServerMain {
    private static final int PORT = 8080;
    private static Connection con;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server listening on port " + PORT);
            con = ConnectionMySQL.getConnect();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);
                GesConect gesConect = new GesConect(clientSocket, con);
                Thread thread = new Thread(gesConect);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
