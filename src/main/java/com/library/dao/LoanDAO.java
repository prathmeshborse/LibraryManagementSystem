package com.library.dao;

import com.library.database.DatabaseConnection;
import com.library.model.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanDAO {
    private static final Logger LOGGER = Logger.getLogger(LoanDAO.class.getName());
    private static final int DAILY_FINE = 1; // Fine per day

    // Issue a book
    public boolean issueBook(Loan loan) {
        String query = "INSERT INTO loans (user_id, book_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, loan.getUserId());
            stmt.setInt(2, loan.getBookId());
            stmt.setDate(3, new java.sql.Date(loan.getIssueDate().getTime()));
            stmt.setDate(4, new java.sql.Date(loan.getDueDate().getTime()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while issuing book", e);
        }
        return false;
    }

    // Return a book
    public boolean returnBook(int loanId, Date returnDate) {
        String query = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, new java.sql.Date(returnDate.getTime()));
            stmt.setInt(2, loanId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while returning book", e);
        }
        return false;
    }

    // Get all active loans
    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE return_date IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while fetching active loans", e);
        }
        return loans;
    }

    // Get loans by user ID
    public List<Loan> getLoansByUser(int userId) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while fetching loans for user", e);
        }
        return loans;
    }

    // Check if a book is loaned
    public boolean isBookLoaned(int bookId) {
        String query = "SELECT 1 FROM loans WHERE book_id = ? AND return_date IS NULL LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while checking if book is loaned", e);
        }
        return false;
    }

    // Calculate fine for overdue books
    public int calculateFine(int loanId) {
        String query = "SELECT due_date, return_date FROM loans WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, loanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Date dueDate = rs.getDate("due_date");
                Date returnDate = rs.getDate("return_date");

                // If book isn't returned yet, use current date
                if (returnDate == null) {
                    returnDate = new Date(System.currentTimeMillis());
                }

                // Calculate fine if overdue
                if (returnDate.after(dueDate)) {
                    long daysLate = (returnDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
                    return (int) daysLate * DAILY_FINE;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while calculating fine", e);
        }
        return 0;
    }

    // Helper method to map ResultSet to Loan object
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        return new Loan(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("book_id"),
                rs.getDate("issue_date"),
                rs.getDate("due_date"),
                rs.getDate("return_date")
        );
    }
}
