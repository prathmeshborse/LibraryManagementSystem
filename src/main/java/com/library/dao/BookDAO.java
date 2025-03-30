package com.library.dao;

import com.library.model.Book;
import com.library.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookDAO {

    // Add a new book to the database
    public boolean addBook(Book book) {
        String checkQuery = "SELECT quantity FROM books WHERE title = ? AND author = ?";
        String insertQuery = "INSERT INTO books (title, author, category, quantity) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE books SET quantity = quantity + ? WHERE title = ? AND author = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, book.getTitle());
            checkStmt.setString(2, book.getAuthor());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Book exists, update quantity
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, book.getQuantity());
                    updateStmt.setString(2, book.getTitle());
                    updateStmt.setString(3, book.getAuthor());
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                // Book does not exist, insert new entry
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, book.getTitle());
                    insertStmt.setString(2, book.getAuthor());
                    insertStmt.setString(3, book.getCategory());
                    insertStmt.setInt(4, book.getQuantity());
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
        }
        return false;
    }


    // Delete a book by ID
    public boolean deleteBook(int bookId) {
        String checkQuery = "SELECT id FROM books WHERE id = ?";
        String deleteQuery = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Book with ID " + bookId + " does not exist.");
                return false; // Book not found
            }

            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, bookId);
                return deleteStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
            return false;
        }
    }

    // Update book details
    public boolean updateBook(Book book) {
        String checkQuery = "SELECT id FROM books WHERE id = ?";
        String updateQuery = "UPDATE books SET title = ?, author = ?, category = ?, quantity = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, book.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Book with ID " + book.getId() + " does not exist.");
                return false; // Book not found
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, book.getTitle());
                updateStmt.setString(2, book.getAuthor());
                updateStmt.setString(3, book.getCategory());
                updateStmt.setInt(4, book.getQuantity());
                updateStmt.setInt(5, book.getId());
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
        }
        return false;
    }

    // Search books by title, author, or category
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchKeyword = "%" + keyword + "%";
            stmt.setString(1, searchKeyword);
            stmt.setString(2, searchKeyword);
            stmt.setString(3, searchKeyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
        }
        return books;
    }

    // search book by bookId
    public Book searchBookById(int bookId){
        Book book =  null;
        String query = "SELECT * FROM books WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getBoolean("available")
                );
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
        }
        return book;
    }

    // Get all books
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getBoolean("available")
                ));
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
        }
        return books;
    }

    // Check if a book is available by ID
    public boolean checkAvailability(int bookId) {
        String query = "SELECT available FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("available");
            }
        } catch (SQLException e) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, "Database error", e);
        }
        return false;
    }
}
