package com.library.ui.LoanFram;

import com.library.model.Loan;
import com.library.model.User;
import com.library.service.LoanService;
import com.library.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class LoanManagementFrame extends JFrame {
    private JTable loanTable;
    private LoanService loanService;
    private UserService userService;
    private DefaultTableModel model;
    private JButton returnButton, refreshButton, cancelButton;

    public LoanManagementFrame() {
        loanService = new LoanService();
        userService = new UserService();

        setTitle("Loan Management - View/Return Loans");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Table setup
        model = new DefaultTableModel(new String[]{"Loan ID", "Book ID", "Borrower Name", "Issue Date", "Due Date", "Return Date"}, 0);
        loanTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPane, gbc);

        // Buttons
        returnButton = new JButton("Mark as Returned");
        refreshButton = new JButton("Refresh");
        cancelButton = new JButton("Close");

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        add(returnButton, gbc);
        gbc.gridx = 1;
        add(refreshButton, gbc);
        gbc.gridx = 2;
        add(cancelButton, gbc);

        // Load loans in background
        loadLoanData();

        // Button actions
        returnButton.addActionListener(e -> markLoanAsReturned());
        refreshButton.addActionListener(e -> loadLoanData());
        cancelButton.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadLoanData() {
        new SwingWorker<List<Loan>, Void>() {
            @Override
            protected List<Loan> doInBackground() {
                return loanService.getActiveLoans();
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<Loan> loans = get();
                    for (Loan loan : loans) {
                        User borrower = userService.getUserById(loan.getUserId());
                        model.addRow(new Object[]{
                                loan.getId(),
                                loan.getBookId(),
                                borrower != null ? borrower.getName() : "Unknown",
                                loan.getIssueDate(),
                                loan.getDueDate(),
                                loan.getReturnDate() != null ? loan.getReturnDate() : "Not Returned"
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error loading loans: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void markLoanAsReturned() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a loan to mark as returned.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int loanId = (int) model.getValueAt(selectedRow, 0);
        Date currentDate = new Date();

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return loanService.returnBook(loanId, currentDate);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(null, "Loan marked as returned successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadLoanData();
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to mark the loan as returned. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error processing request: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
