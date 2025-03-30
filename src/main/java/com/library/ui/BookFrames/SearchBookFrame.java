package com.library.ui.BookFrames;

import com.library.model.Book;
import com.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SearchBookFrame extends JFrame {
    private JTextField searchField;
    private JButton searchButton;
    private JTable resultTable;
    private BookService bookService;

    public SearchBookFrame() {
        bookService = new BookService();
        setTitle("Search Books");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel for search input
        JPanel topPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        topPanel.add(new JLabel("Enter keyword:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Table to display search results
        resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        // Action listener for search button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        setVisible(true);
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a keyword!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Disable the search button to prevent duplicate requests
        searchButton.setEnabled(false);

        new SwingWorker<List<Book>, Void>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                return bookService.searchBooks(keyword);
            }

            @Override
            protected void done() {
                // Re-enable the search button after processing
                searchButton.setEnabled(true);
                try {
                    List<Book> books = get();
                    if (books.isEmpty()) {
                        JOptionPane.showMessageDialog(SearchBookFrame.this, "No books found!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                    // Create table model with columns for book attributes
                    DefaultTableModel model = getModel(books);
                    resultTable.setModel(model);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(SearchBookFrame.this, "An error occurred during search.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private static DefaultTableModel getModel(List<Book> books) {
        String[] columns = {"ID", "Title", "Author", "Category", "Quantity", "Available"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        for (Book book : books) {
            Object[] row = {
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getQuantity(),
                    book.isAvailable() ? "Yes" : "No"
            };
            model.addRow(row);
        }
        return model;
    }
}
