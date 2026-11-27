/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */


package ems.gui;

import ems.exception.ValidationException;
import ems.service.AuthService;
import ems.service.InputValidator;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton exitButton;
    private final AuthService authService;
    private final InputValidator validator;
    private final Navigator navigator;

    public LoginPanel(AuthService authService, InputValidator validator, Navigator navigator) {
        this.authService = authService;
        this.validator = validator;
        this.navigator = navigator;
        usernameField = new JTextField(18);
        passwordField = new JPasswordField(18);
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
        buildLayout();
        loginButton.addActionListener(e -> onLoginClicked());
        exitButton.addActionListener(e -> System.exit(0));
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                updateDefaultButton();
            }
        });
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocusInWindow();
    }

    private void buildLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(205, 210, 220), 1, true),
                BorderFactory.createEmptyBorder(28, 36, 28, 36)
        ));

        JLabel title = new JLabel("Employee Management System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Please log in to continue", SwingConstants.CENTER);
        subtitle.setForeground(new java.awt.Color(100, 108, 120));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 6, 20, 6);
        card.add(subtitle, gbc);

        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridwidth = 1;

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        card.add(usernameField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        card.add(passwordField, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttons.setOpaque(false);
        buttons.add(loginButton);
        buttons.add(exitButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 6, 6, 6);
        card.add(buttons, gbc);

        add(card, new GridBagConstraints());
    }

    private void updateDefaultButton() {
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane == null) {
            return;
        }
        if (isShowing()) {
            rootPane.setDefaultButton(loginButton);
            usernameField.requestFocusInWindow();
        } else if (rootPane.getDefaultButton() == loginButton) {
            rootPane.setDefaultButton(null);
        }
    }

    private void onLoginClicked() {
        String username = usernameField.getText();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');
        try {
            validator.validateLoginInput(username, password);
            if (authService.authenticate(username, password)) {
                passwordField.setText("");
                navigator.showScreen(Navigator.SCREEN_DASHBOARD);
            } else {
                MessageHelper.showError(this, "Invalid username or password.");
                passwordField.setText("");
                passwordField.requestFocusInWindow();
            }
        } catch (ValidationException e) {
            MessageHelper.showError(this, e.getMessage());
            if (username == null || username.trim().isEmpty()) {
                usernameField.requestFocusInWindow();
            } else {
                passwordField.requestFocusInWindow();
            }
        }
    }
}
