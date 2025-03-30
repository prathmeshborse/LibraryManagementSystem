package com.library.service;

import com.library.model.Book;
import com.library.networking.NetworkClient;

import java.util.ArrayList;
import java.util.List;

public class BookService {
    // Add a new book using the networking layer
    public boolean addBook(Book book) {
        String request = "ADD_BOOK|"
                + book.getTitle() + "|"
                + book.getAuthor() + "|"
                + book.getCategory() + "|"
                + book.getQuantity() + "|"
                + (book.isAvailable() ? "1" : "0");
        String response = NetworkClient.sendRequest(request);
        return response != null && response.startsWith("SUCCESS");
    }

    // Get a book by its ID using the networking layer
    public Book getBookById(int bookId) {
        String request = "GET_BOOK|" + bookId;
        String response = NetworkClient.sendRequest(request);
        if (response != null && !response.isEmpty()) {
            if (response.startsWith("ERROR")) {
                return null;
            }
            // Expected response format: id|title|author|category|quantity|available
            String[] parts = response.split("\\|");
            if (parts.length < 6) return null;
            int id = Integer.parseInt(parts[0]);
            String title = parts[1];
            String author = parts[2];
            String category = parts[3];
            int quantity = Integer.parseInt(parts[4]);
            boolean available = parts[5].equals("1") || parts[5].equalsIgnoreCase("true");
            return new Book(id, title, author, category, quantity, available);
        }
        return null;
    }

    // Update book details via networking
    public boolean updateBook(Book book) {
        String request = "UPDATE_BOOK|"
                + book.getId() + "|"
                + book.getTitle() + "|"
                + book.getAuthor() + "|"
                + book.getCategory() + "|"
                + book.getQuantity() + "|"
                + (book.isAvailable() ? "1" : "0");
        String response = NetworkClient.sendRequest(request);
        return response != null && response.startsWith("SUCCESS");
    }

    // Delete a book via networking
    public boolean deleteBook(int bookId) {
        String request = "DELETE_BOOK|" + bookId;
        String response = NetworkClient.sendRequest(request);
        return response != null && response.startsWith("SUCCESS");
    }

    // Search books by keyword using networking
    public List<Book> searchBooks(String keyword) {
        String request = "SEARCH_BOOKS|" + keyword;
        String response = NetworkClient.sendRequest(request);
        List<Book> books = new ArrayList<>();
        if (response == null || response.isEmpty() || response.startsWith("ERROR")) {
            return books;
        }
        // Expected response: SUCCESS\n<record1>\n<record2>\n... \nEND_OF_RESPONSE
        String[] lines = response.split("\n");
        int start = 0;
        if (lines.length > 0 && lines[0].startsWith("SUCCESS")) {
            start = 1;
        }
        for (int i = start; i < lines.length; i++) {
            String record = lines[i].trim();
            if (record.equals("END_OF_RESPONSE")) {
                break; // Stop processing when termination marker is encountered
            }
            if (record.isEmpty()) continue;
            if (record.startsWith("Book{")) {
                Book b = parseBook(record);
                if (b != null) {
                    books.add(b);
                }
            } else {
                // Fallback: try parsing as pipe-delimited format
                String[] parts = record.split("\\|");
                if (parts.length >= 6) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        String title = parts[1];
                        String author = parts[2];
                        String category = parts[3];
                        int quantity = Integer.parseInt(parts[4]);
                        boolean available = parts[5].equals("1") || parts[5].equalsIgnoreCase("true");
                        books.add(new Book(id, title, author, category, quantity, available));
                    } catch (NumberFormatException e) {
                        // Skip invalid entry
                    }
                }
            }
        }
        return books;
    }


    // Get all books via networking
    public List<Book> getAllBooks() {
        String request = "GET_ALL_BOOKS";
        String response = NetworkClient.sendRequest(request);
        List<Book> books = new ArrayList<>();
        if (response != null && !response.isEmpty() && !response.startsWith("ERROR")) {
            // Expecting response: SUCCESS\n<record1>\n<record2>...
            String[] lines = response.split("\n");
            int start = 0;
            if (lines[0].startsWith("SUCCESS")) {
                start = 1;
            }
            for (int i = start; i < lines.length; i++) {
                String record = lines[i].trim();
                if (record.isEmpty()) continue;
                if (record.startsWith("Book{")) {
                    books.add(parseBook(record));
                } else {
                    String[] parts = record.split("\\|");
                    if (parts.length >= 6) {
                        try {
                            int id = Integer.parseInt(parts[0]);
                            String title = parts[1];
                            String author = parts[2];
                            String category = parts[3];
                            int quantity = Integer.parseInt(parts[4]);
                            boolean available = parts[5].equals("1") || parts[5].equalsIgnoreCase("true");
                            books.add(new Book(id, title, author, category, quantity, available));
                        } catch (NumberFormatException e) {
                            // Skip invalid entry
                        }
                    }
                }
            }
        }
        return books;
    }

    private Book parseBook(String record) {
        // Expected record: Book{id=1, title='The Catcher in the Rye', author='J.D. Salinger', category='Fiction', quantity=15, available=true}
        try {
            int startIndex = record.indexOf("{");
            int endIndex = record.lastIndexOf("}");
            if (startIndex < 0 || endIndex < 0) {
                return null;
            }
            String content = record.substring(startIndex + 1, endIndex);
            // Split by comma, but note that values might contain commas inside quotes.
            // For simplicity, we assume the format is fixed.
            String[] parts = content.split(", ");
            int id = 0;
            String title = "";
            String author = "";
            String category = "";
            int quantity = 0;
            boolean available = false;
            for (String part : parts) {
                String[] keyValue = part.split("=");
                if (keyValue.length < 2) continue;
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                switch (key) {
                    case "id":
                        id = Integer.parseInt(value);
                        break;
                    case "title":
                        title = value.substring(1, value.length() - 1); // remove surrounding quotes
                        break;
                    case "author":
                        author = value.substring(1, value.length() - 1);
                        break;
                    case "category":
                        category = value.substring(1, value.length() - 1);
                        break;
                    case "quantity":
                        quantity = Integer.parseInt(value);
                        break;
                    case "available":
                        available = Boolean.parseBoolean(value);
                        break;
                }
            }
            return new Book(id, title, author, category, quantity, available);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
