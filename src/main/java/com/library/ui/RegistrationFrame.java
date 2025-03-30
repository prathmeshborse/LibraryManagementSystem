package com.library.ui;

import com.library.model.User;
import com.library.networking.NetworkClient;
import com.library.service.UserService;
import com.library.utils.PlaceholderTextFieldUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationFrame extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton registerButton, cancelButton;
    private UserService userService;

    public RegistrationFrame() {
        userService = new UserService();
        setTitle("Library Management System - User Registration");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Label & Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        nameField = PlaceholderTextFieldUtil.createPlaceholderField("Enter Your Name");
        add(nameField, gbc);

        // Email Label & Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = PlaceholderTextFieldUtil.createPlaceholderField("Enter Email");
        add(emailField, gbc);

        // Password Label & Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = PlaceholderTextFieldUtil.createPlaceholderPasswordField();
        add(passwordField, gbc);

        // Show Password Checkbox
        gbc.gridx = 1;
        gbc.gridy = 3;
        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('*');
                }
            }
        });
        add(showPasswordCheckBox, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        // Style buttons
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.BLACK);
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        cancelButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        // Button Actions
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable the register button to prevent duplicate submissions
        registerButton.setEnabled(false);

        // Run registration request in a background thread
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return userService.registerUser(name, email, password);
            }

            @Override
            protected void done() {
                // Re-enable the register button after processing
                registerButton.setEnabled(true);
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginFrame(); // Open login frame after successful registration
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed! Email may already be in use.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An error occurred during registration.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
