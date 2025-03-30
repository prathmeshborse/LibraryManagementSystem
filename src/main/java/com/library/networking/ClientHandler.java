package com.library.networking;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    private final Socket clientSocket;
    private final LibraryServer libraryServer;

    public ClientHandler(Socket clientSocket, LibraryServer libraryServer) {
        this.clientSocket = clientSocket;
        this.libraryServer = libraryServer;
        try {
            // Set a reasonable timeout to avoid indefinite blocking
            clientSocket.setSoTimeout(3000);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to set socket timeout", e);
        }
    }

    @Override
    public void run() {
        LOGGER.info("Client connected: " + clientSocket.getRemoteSocketAddress());
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = in.readLine();
            if (request == null) {
                LOGGER.warning("No request received.");
                return;
            }
            String response = libraryServer.handleRequest(request);
            LOGGER.info("Sending response: " + response);
            out.println(response);
            out.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error handling client: " + clientSocket.getRemoteSocketAddress(), e);
        } finally {
            try {
                clientSocket.close();
                LOGGER.info("Client disconnected: " + clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to close client socket", e);
            }
        }
    }
}
