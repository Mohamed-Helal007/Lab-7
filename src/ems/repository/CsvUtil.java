package ems.repository;

import ems.exception.DataAccessException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class CsvUtil {
    private CsvUtil() {
    }

    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    boolean doubledQuote = i + 1 < line.length() && line.charAt(i + 1) == '"';
                    if (doubledQuote) {
                        current.append('"');
                        i++;
                    }
                    else
                        inQuotes = false;
                }
                else
                    current.append(c);
            }
            else if (c == '"')
                inQuotes = true;
            else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            }
            else 
                current.append(c);
        }
        if (inQuotes)
            throw new IllegalArgumentException("Unterminated quote");
        fields.add(current.toString());
        return fields;
    }

    public static String toLine(List<String> fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0)
                line.append(',');
            line.append(escape(fields.get(i)));
        }
        return line.toString();
    }

    private static String escape(String field) {
        if (field == null)
            return "";

        boolean needsQuotes = field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r");

        if (!needsQuotes)
            return field;
        return "\"" + field.replace("\"", "\"\"") + "\"";
    }

    public static List<String> readLines(Path file) throws DataAccessException {
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new DataAccessException("Could not read " + file + ": " + e.getMessage(), e);
        }
    }

    public static void writeLinesAtomically(Path file, List<String> lines) throws DataAccessException {
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        try {
            Path folder = file.toAbsolutePath().getParent();
            if (folder != null)
                Files.createDirectories(folder);
            if (Files.exists(file))
                Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            try {
                Files.deleteIfExists(temp);
            }
            catch (IOException ignored) {
            }
            throw new DataAccessException("Could not save " + file + ": " + e.getMessage(), e);
        }
    }
}
