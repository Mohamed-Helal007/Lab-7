/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

package ems.service;

public class AuthService {

    public static final String DEFAULT_USERNAME = "admin";
    public static final String DEFAULT_PASSWORD = "admin123";
    private final String validUsername;
    private final String validPassword;
    private String currentUsername;

    public AuthService() {
        this(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public AuthService(String validUsername, String validPassword) {
        if (validUsername == null || validPassword == null) {
            throw new IllegalArgumentException("Credentials must not be null");
        }
        this.validUsername = validUsername;
        this.validPassword = validPassword;
    }

    public boolean authenticate(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        boolean ok = validUsername.equals(username.trim()) && validPassword.equals(password);
        if (ok) {
            currentUsername = validUsername;
        }
        return ok;
    }

    public boolean isLoggedIn() {
        return currentUsername != null;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void logout() {
        currentUsername = null;
    }
}
