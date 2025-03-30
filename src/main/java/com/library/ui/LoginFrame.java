package com.library.ui;

import com.library.model.User;
import com.library.service.UserService;
import com.library.utils.PlaceholderTextFieldUtil;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton loginButton;
    private UserService userService; // Use UserService

    public LoginFrame() {
        userService = new UserService(); // Initialize UserService

        setTitle("Library Management System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Email Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = PlaceholderTextFieldUtil.createPlaceholderField("Enter Email");
        add(emailField, gbc);

        // Password Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = PlaceholderTextFieldUtil.createPlaceholderPasswordField();
        add(passwordField, gbc);

        // Show Password Checkbox
        gbc.gridx = 1;
        gbc.gridy = 2;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0); // Show password
            } else {
                passwordField.setEchoChar('*'); // Hide password
            }
        });
        add(showPasswordCheckBox, gbc);

        // Login Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        loginButton = new JButton("Login");
        add(loginButton, gbc);

        loginButton.addActionListener(e -> handleLogin());

        setVisible(true);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        // Disable login button to prevent duplicate clicks
        loginButton.setEnabled(false);

        // Run login process in a background thread
        new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return userService.loginUser(email, password);
            }

            @Override
            protected void done() {
                // Re-enable login button after task completion
                loginButton.setEnabled(true);
                try {
                    User user = get();
                    if (user != null) {
                        JOptionPane.showMessageDialog(null, "Login Successful");

                        dispose(); // Close LoginFrame

                        // Open the correct frame based on user role
                        SwingUtilities.invokeLater(() -> {
                            if (user.getRole().equalsIgnoreCase("ADMIN")) {
                                new AdminFrame(user.getName());
                            } else {
                                new MemberFrame(user.getName(), user.getId());
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Login failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
