package com.library.ui.BookFrames;

import com.library.service.LoanService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class ReturnBookFrame extends JFrame {
    private JTextField loanIdField;
    private JButton returnButton, cancelButton;
    private LoanService loanService;

    public ReturnBookFrame() {
        loanService = new LoanService();
        setTitle("Return Book");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Enter Loan ID:"));
        loanIdField = new JTextField();
        add(loanIdField);

        returnButton = new JButton("Return Book");
        cancelButton = new JButton("Cancel");
        add(returnButton);
        add(cancelButton);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnBook();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void returnBook() {
        String loanIdStr = loanIdField.getText().trim();
        if (loanIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Loan ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int loanId = Integer.parseInt(loanIdStr);
            Date returnDate = new Date(); // current date

            // Disable the return button to prevent duplicate submissions
            returnButton.setEnabled(false);

            // Run the return process in a background thread
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return loanService.returnBook(loanId, returnDate);
                }

                @Override
                protected void done() {
                    // Re-enable the return button after processing
                    returnButton.setEnabled(true);
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ReturnBookFrame.this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(ReturnBookFrame.this, "Failed to return book. Please check the Loan ID.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ReturnBookFrame.this, "An error occurred while returning the book.", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Loan ID must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
