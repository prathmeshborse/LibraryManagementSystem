package com.library.service;

import com.library.model.Loan;
import com.library.networking.NetworkClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class LoanService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = Logger.getLogger(LoanService.class.getName());



    // Assuming sdf is defined for pipe-delimited parsing, e.g., "yyyy-MM-dd HH:mm:ss"
    // For parsing date-only fields in toString() output, use a simpler format:
    private static final SimpleDateFormat sdfSimple = new SimpleDateFormat("yyyy-MM-dd");

    // Issue a book
    public boolean issueBook(Loan loan) {
        String request = String.format("ISSUE_BOOK|%d|%d|%s|%s",
                loan.getUserId(), loan.getBookId(), sdf.format(loan.getIssueDate()), sdf.format(loan.getDueDate()));
        String response = NetworkClient.sendRequest(request);
        return response != null && response.startsWith("SUCCESS");
    }

    // Return a book
    public boolean returnBook(int loanId, Date returnDate) {
        String request = String.format("RETURN_BOOK|%d|%s", loanId, sdf.format(returnDate));
        String response = NetworkClient.sendRequest(request);
        return response != null && response.startsWith("SUCCESS");
    }

    // Get all active loans
    public List<Loan> getActiveLoans() {
        String response = NetworkClient.sendRequest("GET_ACTIVE_LOANS");
        return parseLoansResponse(response);
    }

    // Get loans by user ID
    public List<Loan> getLoansByUser(int userId) {
        String request = "GET_LOANS_BY_USER|" + userId;
        String response = NetworkClient.sendRequest(request);
        return parseLoansResponse(response);
    }

    // Check if a book is currently loaned
    public boolean isBookLoaned(int bookId) {
        String request = "IS_BOOK_LOANED|" + bookId;
        String response = NetworkClient.sendRequest(request);
        return response != null && response.equalsIgnoreCase("TRUE");
    }

    // Calculate fine for overdue books
    public int calculateFine(int loanId) {
        String request = "CALCULATE_FINE|" + loanId;
        String response = NetworkClient.sendRequest(request);
        try {
            if (response != null && response.startsWith("SUCCESS|")) {
                String[] parts = response.split("\\|");
                if(parts.length >= 2)
                    return Integer.parseInt(parts[1]);
            }
            return 0;
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "Invalid fine response: " + response, e);
            return 0;
        }
    }

    public static boolean payFine(int userId, double amount){
        // TODO: Implement payFine when the server-side is ready
        return true;
    }

    public static double getUserFines(int userId){
        // TODO: Implement getUserFines when the server-side is ready
        return 10.00;
    }

    // Helper method to parse loan responses from the server
    private List<Loan> parseLoansResponse(String response) {
        List<Loan> loans = new ArrayList<>();
        if (response == null || response.isEmpty() || response.startsWith("ERROR") || response.equalsIgnoreCase("NO_LOANS_FOUND")) {
            return loans;
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
                break; // Termination marker encountered
            }
            if (record.isEmpty()) continue;
            if (record.startsWith("Loan{")) {
                Loan l = parseLoan(record);
                if (l != null) {
                    loans.add(l);
                }
            } else {
                // Fallback: try parsing as pipe-delimited format
                String[] parts = record.split("\\|");
                if (parts.length >= 6) {
                    try {
                        int id = Integer.parseInt(parts[0]);
                        int userId = Integer.parseInt(parts[1]);
                        int bookId = Integer.parseInt(parts[2]);
                        Date issueDate = sdf.parse(parts[3]);
                        Date dueDate = sdf.parse(parts[4]);
                        Date returnDate = parts[5].equalsIgnoreCase("null") ? null : sdf.parse(parts[5]);
                        loans.add(new Loan(id, userId, bookId, issueDate, dueDate, returnDate));
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Error parsing loan record (pipe delimited): " + record, e);
                    }
                }
            }
        }
        return loans;
    }

    private Loan parseLoan(String record) {
        // Expected format:
        // Loan{id=2, userId=1, bookId=3, issueDate=2025-03-29, dueDate=2025-04-12, returnDate=null}
        try {
            int startIndex = record.indexOf("{");
            int endIndex = record.lastIndexOf("}");
            if (startIndex < 0 || endIndex < 0) {
                return null;
            }
            String content = record.substring(startIndex + 1, endIndex);
            // Split by comma and space (assuming no commas in the values)
            String[] parts = content.split(", ");
            int id = 0, userId = 0, bookId = 0;
            Date issueDate = null, dueDate = null, returnDate = null;
            for (String part : parts) {
                String[] keyValue = part.split("=");
                if (keyValue.length < 2) {
                    continue;
                }
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                switch (key) {
                    case "id":
                        id = Integer.parseInt(value);
                        break;
                    case "userId":
                        userId = Integer.parseInt(value);
                        break;
                    case "bookId":
                        bookId = Integer.parseInt(value);
                        break;
                    case "issueDate":
                        issueDate = sdfSimple.parse(value);
                        break;
                    case "dueDate":
                        dueDate = sdfSimple.parse(value);
                        break;
                    case "returnDate":
                        if (!value.equalsIgnoreCase("null")) {
                            returnDate = sdfSimple.parse(value);
                        }
                        break;
                }
            }
            return new Loan(id, userId, bookId, issueDate, dueDate, returnDate);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing loan record (toString format): " + record, e);
            return null;
        }
    }
}
