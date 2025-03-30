package com.library.ui.BookFrames;

import com.library.model.Book;
import com.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ViewBooksFrame extends JFrame {
    private JTable booksTable;
    private BookService bookService;
    private DefaultTableModel model;

    public ViewBooksFrame() {
        bookService = new BookService();
        setTitle("View All Books");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Table model with columns for book attributes
        String[] columns = {"ID", "Title", "Author", "Category", "Quantity", "Available"};
        model = new DefaultTableModel(columns, 0);
        booksTable = new JTable(model);

        // Customize table header
        JTableHeader header = booksTable.getTableHeader();
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        // Set table properties
        booksTable.setRowHeight(30);
        booksTable.setFont(new Font("Arial", Font.PLAIN, 14));
        booksTable.setFillsViewportHeight(true);

        // Alternating row colors for better readability
        booksTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);

        // Fetch books in the background
        fetchBooksInBackground();
    }

    private void fetchBooksInBackground() {
        new SwingWorker<List<Book>, Void>() {
            @Override
            protected List<Book> doInBackground() throws Exception {
                return bookService.getAllBooks();
            }

            @Override
            protected void done() {
                try {
                    List<Book> books = get();
                    // Clear existing rows
                    model.setRowCount(0);
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
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ViewBooksFrame.this, "An error occurred while fetching books.", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
