package ems.repository;

import ems.exception.DataAccessException;
import ems.model.*;
import ems.service.EmployeeService;

import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;

public class CsvEmployeeRepository implements EmployeeRepository {
    public static final String HEADER = "EmployeeId,FullName,EmployeeType,DepartmentId,Age,Position,BasicSalary,Bonus,HourlyRate,HoursWorked";
    private static final int COLUMN_COUNT = 10;
    private static final int FIRST_SALARY_COLUMN = 6;
    private final Path filePath;
    private final List<String> loadWarnings = new ArrayList<>();
    private static final String[] SALARY_KEYS = {
            FullTimeEmployee.KEY_BASIC_SALARY,
            FullTimeEmployee.KEY_BONUS,
            PartTimeEmployee.KEY_HOURLY_RATE,
            PartTimeEmployee.KEY_HOURS_WORKED
    };

    public CsvEmployeeRepository(String filePath) {
        this.filePath = Path.of(filePath);
    }

    public List<Employee> loadAll() throws DataAccessException {
        loadWarnings.clear();
        List<Employee> result = new ArrayList<>();

        if (!Files.exists(filePath))
            return null;

        List<String> lines = CsvUtil.readLines(filePath);
        if (lines.isEmpty() || !withoutBom(lines.get(0)).trim().equals(HEADER))
            throw new DataAccessException ("The file " + filePath + " is empty or does not have the expected header.", null);

        Set<integers> seenIds = new HashSet<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;
            if (line.isBlank())
                continue;
            try {
                List<String> fields = CsvUtil.parseLine(Line);
                if (fields.size() != COLUMN_COUNT) {
                    loadWarnings.add("Line " + lineNumber + ": expected " + COLUMN_COUNT + " columns, found " + fields.size());
                    continue;
                }
                Employee employee = parseRow(fields, lineNumber);
                if (seenIds.add(employee.getEmployeeId()))
                    result.add(employee);
                else
                    loadWarnings.add("Line " + lineNumber + ": duplicate employee ID " + employee.getEmployeeId() + " (first row kept)");
            }
            catch (IllegalArgumentException e) {
                loadWarnings.add("Line " + lineNumber + ": " + e.getMessage());
            }
        }
        return result;
    }

    public void saveAll(List<Employee> employees) throws DataAccessException {
        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort(Comparator.comparingInt(Employee::getEmployeeId));

        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Employee employee : sorted) {
            lines.add(CsvUtil.toLine(toRow(employee)));
        }
        CsvUtil.writeLinesAtomically(filePath, lines);
    }

    public List<String> getLastLoadWarnings() {
        return new ArrayList<>(loadWarnings);
    }

    private Employee parseRow(List<String> fields, int lineNumber) {
        int employeeId = parseWholeNumber("Employee ID", fields.get(0));
        String fullName = fields.get(1);
        EmployeeType type = EmployeeType.fromText(fields.get(2));
        int departmentId = parseWholeNumber("Department ID", fields.get(3));
        int age = parseWholeNumber("Age", fields.get(4));
        String position = fields.get(5);

        Map<String, Double> components = new LinkedHashMap<>();
        for (int i = 0; i < SALARY_KEYS.length; i++) {
            String text = fields.get(FIRST_SALARY_COLUMN + i).trim();
            if (!text.isEmpty())
                components.put(SALARY_KEYS[i], parseDecimal(SALARY_KEYS[i], text));
        }
        return EmployeeFactory.createEmployee(type, employeeId, fullName, departmentId, age, position, components);
    }

    private static int parseWholeNumber(String label, String text) {
        try {
            return Integer.parseInt(text.trim());
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " is not a whole number: '" + text + "'");
        }
    }

    private static double parseDecimal(String label, String text) {
        try {
            return Double.parseDouble(text);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " is not a number: '" + text + "'");
        }
    }

    private static String withoutBom(String text) {
        return text.startsWith("\uFEFF") ? text.substring(1) : text;
    }

    private List<String> toRow(Employee employee) {
        Map<String, Double> components = employee.getSalaryComponents();
        List<String> row = new ArrayList<>();
        row.add(String.valueOf(employee.getEmployeeId()));
        row.add(employee.getFullName());
        row.add(employee.getEmployeeType().name());
        row.add(String.valueOf(employee.getDepartmentId()));
        row.add(String.valueOf(employee.getAge()));
        row.add(employee.getPosition());
        for (String key : SALARY_KEYS) {
            Double value = components.get(key);
            row.add(value == null ? "" : BigDecimal.valueOf(value).toPlainString());
        }
        return row;
    }
}
