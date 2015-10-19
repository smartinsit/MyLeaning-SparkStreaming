package chapter.two;

import java.net.*;
import java.io.*;

/**
 * This client I copied from https://www.cs.uic.edu/~troy/spring05/cs450/sockets/EchoServer.java
 * because the book example was not working, so I used it
 * to understand how socket worked and then fix the book version
 */
public class ServerSocketDataGen {
    public static void main(String[] args) throws IOException {
        System.out.println("Defining new Socket");
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(9087);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 9087.");
            System.exit(1);
        }

        System.out.println("Waiting for connection.....");
        Socket clientSocket = null;

        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        System.out.println("Connection successful");
        System.out.println("Waiting for input.....");

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            System.out.println("Server: " + inputLine);
            out.println(inputLine);

            if (inputLine.equals("Bye."))
                break;
        }

        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}