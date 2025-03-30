package com.library.service;

import com.library.model.User;
import com.library.networking.NetworkClient;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    // Register a new user
    public boolean registerUser(String name, String email, String password) {
        String response = NetworkClient.sendRequest("REGISTER_USER|" + name + "|" + email + "|" + password);
        return response != null && response.startsWith("SUCCESS");
    }

    // Authenticate user login
    public User loginUser(String email, String password) {
        String request = "LOGIN|" + email + "|" + password;
        String response = NetworkClient.sendRequest(request);

        if (response == null || response.isEmpty() || !response.startsWith("LOGIN_SUCCESS")) {
            return null;
        }

        // Expected response format: LOGIN_SUCCESS|userId|name|email|role
        String[] parts = response.split("\\|");
        if (parts.length < 5) return null;

        try {
            int userId = Integer.parseInt(parts[1]);
            String name = parts[2];
            String userEmail = parts[3];
            String role = parts[4];

            return new User(userId, name, userEmail, "", role); // Password not provided by server
        } catch (NumberFormatException e) {
            return null;
        }
    }


    // Get user details by ID
    public User getUserById(int userId) {
        String request = "GET_USER|" + userId;
        String response = NetworkClient.sendRequest(request);

        if (response == null || response.startsWith("ERROR")) {
            return null;
        }

        // Expected response format: id|name|email|role
        String[] parts = response.split("\\|");
        if (parts.length < 4) return null;

        try {
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            String email = parts[2];
            String role = parts[3];

            return new User(id, name, email, "", role); // Password not provided by server
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Get user fines
    public double getUserFines(int userId) {
        String request = "GET_USER_FINES|" + userId;
        String response = NetworkClient.sendRequest(request);

        if (response == null || response.startsWith("ERROR")) {
            return 0.0;
        }

        try {
            // Expected response format: SUCCESS|Fine: <fine>
            String finePart = response.split("\\|")[1];
            // Remove the "Fine: " prefix if present
            finePart = finePart.replace("Fine: ", "");
            return Double.parseDouble(finePart);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // Pay user fine
    public boolean payFine(int userId, double amount) {
        String request = "PAY_FINE|" + userId + "|" + amount;
        String response = NetworkClient.sendRequest(request);
        return response != null && response.equalsIgnoreCase("SUCCESS");
    }

    // Search users by name or email
    public List<User> searchUsers(String keyword) {
        String request = "SEARCH_USERS|" + keyword;
        String response = NetworkClient.sendRequest(request);

        List<User> users = new ArrayList<>();
        if (response == null || response.startsWith("ERROR")) {
            return users;
        }
        // Expected response: SUCCESS\n<record1>\n<record2>...
        String[] records = response.split("\n");
        int startIndex = records[0].startsWith("SUCCESS") ? 1 : 0;
        for (int i = startIndex; i < records.length; i++) {
            String record = records[i];
            String[] parts = record.split("\\|");
            if (parts.length < 4) continue;

            try {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = parts[2];
                String role = parts[3];

                users.add(new User(id, name, email, "", role));
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        return users;
    }

    // Get all users
    public List<User> getAllUsers() {
        String request = "GET_ALL_USERS";
        String response = NetworkClient.sendRequest(request);

        List<User> users = new ArrayList<>();
        if (response == null || response.startsWith("ERROR")) {
            return users;
        }
        // Expected response: SUCCESS\n<record1>\n<record2>...
        String[] records = response.split("\n");
        int startIndex = records[0].startsWith("SUCCESS") ? 1 : 0;
        for (int i = startIndex; i < records.length; i++) {
            String record = records[i];
            String[] parts = record.split("\\|");
            if (parts.length < 4) continue;

            try {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String email = parts[2];
                String role = parts[3];

                users.add(new User(id, name, email, "", role));
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }
        return users;
    }
}
