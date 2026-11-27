/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

package ems.service;

import ems.exception.ValidationException;
import java.util.regex.Pattern;

public class InputValidator {

    private static final int MIN_AGE = 20;
    private static final int MAX_AGE = 100;
    private static final Pattern WHOLE_NUMBER = Pattern.compile("[+-]?[0-9]+");
    private static final Pattern DECIMAL_NUMBER =  Pattern.compile("[+-]?([0-9]+(\\.[0-9]*)?|\\.[0-9]+)([eE][+-]?[0-9]+)?");

    public String requireText(String fieldLabel, String rawValue) throws ValidationException {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " is required.");
        }
        if (rawValue.indexOf('\n') >= 0 || rawValue.indexOf('\r') >= 0) {
            throw new ValidationException(fieldLabel + " must not contain line breaks");
        }
        return rawValue.trim();
    }

    public int parsePositiveInt(String fieldLabel, String rawValue) throws ValidationException {
        long value = parseWholeNumber(fieldLabel, rawValue);
        if (value <= 0) {
            throw new ValidationException(fieldLabel + " must be greater than zero");
        }
        if (value > Integer.MAX_VALUE) {
            throw new ValidationException(fieldLabel + " is too large");
        }
        return (int) value;
    }

    public int parseEmployeeId(String rawValue) throws ValidationException {
        return parsePositiveInt("Employee ID", rawValue);
    }

    public int parseAge(String rawValue) throws ValidationException {
        long value = parseWholeNumber("Age", rawValue);
        if (value < MIN_AGE || value > MAX_AGE) {
            throw new ValidationException("Age must be between " + MIN_AGE + " and " + MAX_AGE);
        }
        return (int) value;
    }

    public double parseNonNegativeAmount(String fieldLabel, String rawValue) throws ValidationException {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " is required");
        }
        String text = rawValue.trim();
        if (!DECIMAL_NUMBER.matcher(text).matches()) {
            throw new ValidationException(fieldLabel + " must be a number");
        }
        double value;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldLabel + " must be a number");
        }
        if (Double.isNaN(value)) {
            throw new ValidationException(fieldLabel + " must be a number");
        }
        if (Double.isInfinite(value)) {
            throw new ValidationException(fieldLabel + " is too large");
        }
        if (value < 0) {
            throw new ValidationException(fieldLabel + " must not be negative");
        }
        return value == 0 ? 0.0 : value;
    }

    public void requireSelection(String fieldLabel, Object selectedValue) throws ValidationException {
        if (selectedValue == null) {
            throw new ValidationException("Please select the " + fieldLabel);
        }
    }

    public void validateLoginInput(String username, String password) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
    }

    private long parseWholeNumber(String fieldLabel, String rawValue) throws ValidationException {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            throw new ValidationException(fieldLabel + " is required");
        }
        String text = rawValue.trim();
        if (!WHOLE_NUMBER.matcher(text).matches()) {
            throw new ValidationException(fieldLabel + " must be a whole number");
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException overflow) {
            return text.startsWith("-") ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
    }
}
