package com.library.ui.BookFrames;

import com.library.service.BookService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeleteBookFrame extends JFrame {
    private JTextField bookIdField;
    private JButton deleteButton, cancelButton;
    private BookService bookService;

    public DeleteBookFrame() {
        bookService = new BookService();
        setTitle("Delete Book");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        add(new JLabel("Enter Book ID:"));
        bookIdField = new JTextField();
        add(bookIdField);

        deleteButton = new JButton("Delete");
        cancelButton = new JButton("Cancel");
        add(deleteButton);
        add(cancelButton);

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteBook();
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

    private void deleteBook() {
        String idText = bookIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Book ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int bookId = Integer.parseInt(idText);

            // Disable delete button to prevent duplicate submissions
            deleteButton.setEnabled(false);

            // Execute deletion in background thread
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return bookService.deleteBook(bookId);
                }

                @Override
                protected void done() {
                    // Re-enable delete button after processing
                    deleteButton.setEnabled(true);
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(DeleteBookFrame.this, "Book deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(DeleteBookFrame.this, "Failed to delete book. Please check if the Book ID is correct.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DeleteBookFrame.this, "An error occurred while deleting the book.", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
