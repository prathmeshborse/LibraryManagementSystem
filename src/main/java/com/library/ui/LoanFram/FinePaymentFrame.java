package com.library.ui.LoanFram;

import com.library.networking.NetworkClient;
import com.library.service.LoanService;

import javax.swing.*;
import java.awt.*;

public class FinePaymentFrame extends JFrame {
    private JLabel currentFineLabel;
    private JTextField paymentField;
    private JButton payButton, cancelButton;
    private int userId = 1; // Dummy user ID for testing

    public FinePaymentFrame() {
        setTitle("Pay Fine");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Current Fine:"), gbc);

        gbc.gridx = 1;
        currentFineLabel = new JLabel("Loading...");
        add(currentFineLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Amount to Pay:"), gbc);

        gbc.gridx = 1;
        paymentField = new JTextField();
        add(paymentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        payButton = new JButton("Pay Fine");
        add(payButton, gbc);

        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        add(cancelButton, gbc);

        payButton.addActionListener(e -> payFine());
        cancelButton.addActionListener(e -> dispose());

        fetchFineAmount(); // Fetch fine amount from server
        setVisible(true);
    }

    private void fetchFineAmount() {
        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                return LoanService.getUserFines(userId);
            }

            @Override
            protected void done() {
                try {
                    currentFineLabel.setText(String.valueOf(get()));
                } catch (Exception e) {
                    currentFineLabel.setText("Error fetching fine");
                }
            }
        }.execute();
    }

    private void payFine() {
        String amountStr = paymentField.getText().trim();
        if (amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount to pay.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double amount = Double.parseDouble(amountStr);
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return LoanService.payFine(userId, amount);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(null, "Fine paid successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Payment failed. Please check the amount.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Payment failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Amount must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
