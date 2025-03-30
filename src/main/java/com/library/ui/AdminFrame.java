package com.library.ui;

import com.library.ui.LoanFram.LoanManagementFrame;
import com.library.ui.LoanFram.LoanViewFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminFrame extends JFrame {
    public AdminFrame(String name) {
        setTitle("Library Management System - Admin Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(45, 140, 200));
        JLabel welcomeLabel = new JLabel("Welcome, " + name + " (Admin)", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Button Panel (4 buttons: Book Management, Loan Management, Fine Management, Logout)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton bookManagementButton = createStyledButton("Book Management");
        JButton loanManagementButton = createStyledButton("Loan Management");
        JButton logoutButton = createStyledButton("Logout");

        buttonPanel.add(bookManagementButton);
        buttonPanel.add(loanManagementButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);

        // Book Management: Opens BookFrame with admin privileges
        bookManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BookFrame(true);
            }
        });

        // For now, this opens a placeholder Loan Management frame that allows viewing and returning loans.
        loanManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoanManagementFrame();
            }
        });

        // Logout: Returns to LoginFrame
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
