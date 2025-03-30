package com.library.ui.LoanFram;

import com.library.model.Loan;
import com.library.service.LoanService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoanViewFrame extends JFrame {
    private JTable loansTable;
    private LoanService loanService;

    public LoanViewFrame(int userId) {
        // For demonstration, using a dummy user ID. In a complete system, pass the actual logged-in user's ID.
        loanService = new LoanService();
        setTitle("View Borrowed Books");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Table model with columns for loan attributes
        String[] columns = {"Loan ID", "Book ID", "Issue Date", "Due Date", "Return Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        loansTable = new JTable(model);
        loansTable.setFillsViewportHeight(true);
        loansTable.setRowHeight(30);
        loansTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Fetch loans for the user and add them to the table model
        List<Loan> loans = loanService.getLoansByUser(userId);
        for (Loan loan : loans) {
            Object[] row = {
                    loan.getId(),
                    loan.getBookId(),
                    loan.getIssueDate(),
                    loan.getDueDate(),
                    loan.getReturnDate() != null ? loan.getReturnDate() : "Not Returned"
            };
            model.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(loansTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }
}

