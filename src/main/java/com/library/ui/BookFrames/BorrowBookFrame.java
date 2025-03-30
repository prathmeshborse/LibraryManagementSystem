package com.library.ui.BookFrames;

import com.library.model.Loan;
import com.library.service.LoanService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

public class BorrowBookFrame extends JFrame {
    private JTextField bookIdField;
    private JButton borrowButton, cancelButton;
    private LoanService loanService;

    public BorrowBookFrame(int userId) {
        loanService = new LoanService();
        setTitle("Borrow Book");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Book ID:"));
        bookIdField = new JTextField();
        add(bookIdField);

        borrowButton = new JButton("Borrow");
        cancelButton = new JButton("Cancel");
        add(borrowButton);
        add(cancelButton);

        borrowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrowBook(userId);
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

    private void borrowBook(int userId) {
        String bookIdStr = bookIdField.getText().trim();
        if (bookIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Book ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int bookId = Integer.parseInt(bookIdStr);
            Date issueDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(issueDate);
            cal.add(Calendar.DAY_OF_MONTH, 14); // 14-day loan period
            Date dueDate = cal.getTime();

            // Create a new Loan object with id=0, userId, bookId, issueDate, dueDate, and null returnDate
            Loan loan = new Loan(0, userId, bookId, issueDate, dueDate, null);

            // Disable borrow button to prevent duplicate submissions
            borrowButton.setEnabled(false);

            // Run the loan issuance in a background thread
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return loanService.issueBook(loan);
                }

                @Override
                protected void done() {
                    borrowButton.setEnabled(true);
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(BorrowBookFrame.this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(BorrowBookFrame.this, "Failed to borrow the book. It might be unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(BorrowBookFrame.this, "An error occurred while borrowing the book.", "Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            }.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Book ID must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
