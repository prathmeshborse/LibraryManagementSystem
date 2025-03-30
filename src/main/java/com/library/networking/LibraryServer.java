package com.library.networking;

import com.library.dao.BookDAO;
import com.library.dao.LoanDAO;
import com.library.dao.UserDAO;
import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryServer {
    private static final int PORT = 12345;
    private static final int THREAD_POOL_SIZE = 10;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final LoanDAO loanDAO;
    private final ExecutorService executorService;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // Termination marker for multi-line responses
    private static final String TERMINATION_MARKER = "END_OF_RESPONSE";

    public LibraryServer() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.loanDAO = new LoanDAO();
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void startServer() {
        System.out.println("[SERVER] Starting Library Server on port " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Error starting server: " + e.getMessage());
        } finally {
            shutdownServer();
        }
    }

    public void shutdownServer() {
        System.out.println("[SERVER] Shutting down...");
        executorService.shutdownNow();
    }

    public String handleRequest(String request) {
        System.out.println("[SERVER DEBUG] Received request: " + request);
        String[] parts = request.split("\\|");
        if (parts.length == 0) return "ERROR|Invalid request";

        String command = parts[0];

        try {
            switch (command) {
                case "ADD_BOOK":
                    if (parts.length < 6) return "ERROR|Missing parameters";
                    Book newBook = new Book(0, parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5].equals("1"));
                    return bookDAO.addBook(newBook) ? "SUCCESS|Book added" : "ERROR|Failed to add book";

                case "GET_BOOK":
                    if (parts.length < 2) return "ERROR|Missing book ID";
                    Book book = bookDAO.searchBookById(Integer.parseInt(parts[1]));
                    return (book != null) ? formatBookResponse(book) : "ERROR|Book not found";

                case "UPDATE_BOOK":
                    if (parts.length < 7) return "ERROR|Missing book details";
                    Book updatedBook = new Book(Integer.parseInt(parts[1]), parts[2], parts[3], parts[4], Integer.parseInt(parts[5]), parts[6].equals("1"));
                    return bookDAO.updateBook(updatedBook) ? "SUCCESS|Book updated" : "ERROR|Failed to update book";

                case "DELETE_BOOK":
                    if (parts.length < 2) return "ERROR|Missing book ID";
                    return bookDAO.deleteBook(Integer.parseInt(parts[1])) ? "SUCCESS|Book deleted" : "ERROR|Failed to delete book";

                case "SEARCH_BOOKS":
                    if (parts.length < 2) return "ERROR|Missing keyword";
                    List<Book> searchResults = bookDAO.searchBooks(parts[1]);
                    return formatListResponse(searchResults);

                case "GET_ALL_BOOKS":
                    List<Book> allBooks = bookDAO.getAllBooks();
                    return formatListResponse(allBooks);

                case "REGISTER_USER":
                    if (parts.length < 4) return "ERROR|Missing user details";
                    User newUser = new User(0, parts[1], parts[2], parts[3], "member");
                    return userDAO.addUser(newUser) ? "SUCCESS|User registered" : "ERROR|Registration failed";

                case "LOGIN":
                    if (parts.length < 3) return "ERROR|Missing credentials";
                    User user = userDAO.validateLogin(parts[1], parts[2]);
                    return (user != null) ? "LOGIN_SUCCESS|" + user.getId() + "|" + user.getName() + "|" + user.getEmail() + "|" + user.getRole() : "ERROR|Invalid credentials";

                case "GET_USER":
                    if (parts.length < 2) return "ERROR|Missing user ID";
                    User retrievedUser = userDAO.getUserById(Integer.parseInt(parts[1]));
                    return (retrievedUser != null) ? formatUserResponse(retrievedUser) : "ERROR|User not found";

                case "SEARCH_USERS":
                    if (parts.length < 2) return "ERROR|Missing keyword";
                    List<User> foundUsers = userDAO.searchUsers(parts[1]);
                    return formatListResponse(foundUsers);

                case "GET_ALL_USERS":
                    List<User> allUsers = userDAO.getAllUsers();
                    return formatListResponse(allUsers);

                case "ISSUE_BOOK":
                    if (parts.length < 5) return "ERROR|Missing loan details";
                    try {
                        int userId = Integer.parseInt(parts[1]);
                        int bookId = Integer.parseInt(parts[2]);
                        Date issueDate = sdf.parse(parts[3]);
                        Date dueDate = sdf.parse(parts[4]);
                        Loan loan = new Loan(0, userId, bookId, issueDate, dueDate, null);
                        return loanDAO.issueBook(loan) ? "SUCCESS|Book issued" : "ERROR|Failed to issue book";
                    } catch (ParseException e) {
                        return "ERROR|Invalid date format";
                    }

                case "RETURN_BOOK":
                    if (parts.length < 3) return "ERROR|Missing loan details";
                    try {
                        int loanId = Integer.parseInt(parts[1]);
                        Date returnDate = sdf.parse(parts[2]);
                        return loanDAO.returnBook(loanId, returnDate) ? "SUCCESS|Book returned" : "ERROR|Failed to return book";
                    } catch (ParseException e) {
                        return "ERROR|Invalid date format";
                    }

                case "IS_BOOK_LOANED":
                    if (parts.length < 2) return "ERROR|Missing book ID";
                    try {
                        boolean isLoaned = loanDAO.isBookLoaned(Integer.parseInt(parts[1]));
                        return isLoaned ? "TRUE" : "FALSE";
                    } catch (NumberFormatException e) {
                        return "ERROR|Invalid book ID";
                    }

                case "CALCULATE_FINE":
                    if (parts.length < 2) return "ERROR|Missing loan ID";
                    try {
                        int fine = loanDAO.calculateFine(Integer.parseInt(parts[1]));
                        return "SUCCESS|" + fine;
                    } catch (NumberFormatException e) {
                        return "ERROR|Invalid loan ID";
                    }

                case "GET_ACTIVE_LOANS":
                    List<Loan> activeLoans = loanDAO.getActiveLoans();
                    return formatListResponse(activeLoans);

                case "GET_LOANS_BY_USER":
                    if (parts.length < 2) return "ERROR|Missing user ID";
                    List<Loan> userLoans = loanDAO.getLoansByUser(Integer.parseInt(parts[1]));
                    return formatListResponse(userLoans);

                default:
                    return "ERROR|Unknown command";
            }
        } catch (Exception e) {
            return "ERROR|Server exception: " + e.getMessage();
        }
    }

    // Updated method to return newline-separated records with termination marker
    private String formatListResponse(List<?> list) {
        if (list.isEmpty()) return "ERROR|No records found";
        StringBuilder response = new StringBuilder("SUCCESS\n");
        for (Object obj : list) {
            response.append(obj.toString()).append("\n");
        }
        response.append(TERMINATION_MARKER);
        return response.toString();
    }

    private String formatUserResponse(User user) {
        return user.getId() + "|" + user.getName() + "|" + user.getEmail() + "|" + user.getRole();
    }

    private String formatBookResponse(Book book) {
        return book.getId() + "|" + book.getTitle() + "|" + book.getAuthor() + "|" +
                book.getCategory() + "|" + book.getQuantity() + "|" + (book.isAvailable() ? "1" : "0");
    }
}
