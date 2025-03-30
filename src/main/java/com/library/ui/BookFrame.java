package com.library.ui;

import com.library.ui.BookFrames.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BookFrame extends JFrame {
    private boolean isAdmin;

    public BookFrame(boolean isAdmin) {
        this.isAdmin = isAdmin;
        setTitle("Library Management System - Book Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(isAdmin ? 3 : 2, 2, 10, 10));

        JButton searchBooksButton = new JButton("Search Books");
        JButton viewBooksButton = new JButton("View All Books");

        add(searchBooksButton);
        add(viewBooksButton);

        if (isAdmin) {
            JButton addBookButton = new JButton("Add Book");
            JButton updateBookButton = new JButton("Update Book");
            JButton deleteBookButton = new JButton("Delete Book");

            add(addBookButton);
            add(updateBookButton);
            add(deleteBookButton);

            addBookButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Run addBook frame initialization in a background thread
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // Optionally perform network tasks if needed
                            return null;
                        }
                        @Override
                        protected void done() {
                            new AddBookFrame();
                        }
                    }.execute();
                }
            });

            updateBookButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Run updateBook frame initialization in a background thread
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // Optionally perform network tasks if needed
                            return null;
                        }
                        @Override
                        protected void done() {
                            new UpdateBookFrame();
                        }
                    }.execute();
                }
            });

            deleteBookButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Run deleteBook frame initialization in a background thread
                    new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            // Optionally perform network tasks if needed
                            return null;
                        }
                        @Override
                        protected void done() {
                            new DeleteBookFrame();
                        }
                    }.execute();
                }
            });
        }

        searchBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Run searchBook frame initialization in a background thread
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Optionally perform network tasks if needed
                        return null;
                    }
                    @Override
                    protected void done() {
                        new SearchBookFrame();
                    }
                }.execute();
            }
        });

        viewBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Run viewBooks frame initialization in a background thread
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        // Optionally perform network tasks if needed
                        return null;
                    }
                    @Override
                    protected void done() {
                        new ViewBooksFrame();
                    }
                }.execute();
            }
        });

        setVisible(true);
    }
}
