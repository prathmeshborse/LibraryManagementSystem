package com.library.networking;

import java.io.*;
import java.net.*;

/**
 * NetworkClient handles communication with the server.
 * It sends requests and retrieves responses for processing.
 */
public class NetworkClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    /**
     * Sends a request string to the server and returns the server's response.
     * @param request The request string to send.
     * @return The response from the server, or null if an error occurred.
     */
    public static String sendRequest(String request) {
        System.out.println("[CLIENT DEBUG] Connecting to server...");

        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("[CLIENT DEBUG] Connected to server. Sending request: " + request);
            out.println(request);
            out.flush(); // Ensure data is sent immediately

            // Read the response from the server
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                if ("END_OF_RESPONSE".equals(line)) {
                    break;
                }
                responseBuilder.append(line).append("\n");
            }
            String response = responseBuilder.toString().trim();
            System.out.println("[CLIENT DEBUG] Received response: " + response);

            return (response != null) ? response.trim() : "ERROR|No response from server";
        } catch (IOException e) {
            System.err.println("[NetworkClient] Communication error: " + e.getMessage());
            return "ERROR|Communication failure";
        }
    }
}
