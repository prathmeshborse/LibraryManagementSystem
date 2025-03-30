package com.library.ui.BookFrames;

import com.library.model.Book;
import com.library.service.BookService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateBookFrame extends JFrame {
    private JTextField idField, titleField, authorField, categoryField, quantityField;
    private JButton fetchButton, updateButton, cancelButton;
    private BookService bookService;
    private Book fetchedBook;

    public UpdateBookFrame() {
        bookService = new BookService();
        setTitle("Update Book");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        // Using GridBagLayout for flexibility
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Book ID and Fetch Button
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Book ID:"), gbc);

        gbc.gridx = 1;
        idField = new JTextField();
        add(idField, gbc);

        gbc.gridx = 2;
        fetchButton = new JButton("Fetch");
        add(fetchButton, gbc);

        // Row 1: Title
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        titleField = new JTextField();
        titleField.setEnabled(false); // disabled until fetched
        add(titleField, gbc);
        gbc.gridwidth = 1;

        // Row 2: Author
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Author:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        authorField = new JTextField();
        authorField.setEnabled(false);
        add(authorField, gbc);
        gbc.gridwidth = 1;

        // Row 3: Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        categoryField = new JTextField();
        categoryField.setEnabled(false);
        add(categoryField, gbc);
        gbc.gridwidth = 1;

        // Row 4: Quantity
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        quantityField = new JTextField();
        quantityField.setEnabled(false);
        add(quantityField, gbc);
        gbc.gridwidth = 1;

        // Row 5: Update and Cancel buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        updateButton = new JButton("Update");
        updateButton.setEnabled(false); // disabled until a book is fetched\n
        add(updateButton, gbc);

        gbc.gridx = 1;
        cancelButton = new JButton("Cancel");
        add(cancelButton, gbc);

        // Action for Fetch Button
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchBook();
            }
        });

        // Action for Update Button
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateBook();
            }
        });

        // Action for Cancel Button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void fetchBook() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Book ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int id = Integer.parseInt(idText);
            // Assume BookService has a method getBookById(int id) that returns a Book object.
            fetchedBook = bookService.getBookById(id);
            if (fetchedBook != null) {
                titleField.setText(fetchedBook.getTitle());
                authorField.setText(fetchedBook.getAuthor());
                categoryField.setText(fetchedBook.getCategory());
                quantityField.setText(String.valueOf(fetchedBook.getQuantity()));
                // Enable the fields and update button
                titleField.setEnabled(true);
                authorField.setEnabled(true);
                categoryField.setEnabled(true);
                quantityField.setEnabled(true);
                updateButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Book not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Book ID must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryField.getText().trim();
        String quantityText = quantityField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            // Use the fetched book's ID for updating
            Book book = new Book(fetchedBook.getId(), title, author, category, quantity, true);
            boolean success = bookService.updateBook(book);
            if (success) {
                JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update book. Please check the Book ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
