package com.library.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderTextFieldUtil {

    // Method to create a placeholder TextField
    public static JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        field.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });

        return field;
    }

    // Method to create a placeholder PasswordField
    public static JPasswordField createPlaceholderPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setText("Enter Password");
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);  // Show text initially

        field.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals("Enter Password")) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('•');  // Mask password when typing
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText("Enter Password");
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });

        return field;
    }
}
