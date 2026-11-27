package ems.gui;

import ems.model.Department;
import ems.model.Employee;
import ems.service.DepartmentService;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class EmployeeTableModel extends AbstractTableModel {
    private static final String[] COLUMN_NAMES = {"ID", "Full Name", "Department", "Age", "Position", "Type", "Salary Details", "Monthly Salary (EGP)"};
    public static final int SALARY_COLUMN = 7;
    private List<Employee> employees = new ArrayList<>();
    private final DepartmentService departmentService;

    public EmployeeTableModel(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = new ArrayList<>(employees);
        fireTableDataChanged();
    }

    public Employee getEmployeeAt(int modelRowIndex) {
        return employees.get(modelRowIndex);
    }

    public int getRowCount() {
        return employees.size();
    }

    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == 0 || columnIndex == 3) ? Integer.class : String.class;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Employee employee = employees.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> employee.getEmployeeId();
            case 1 -> employee.getFullName();
            case 2 -> departmentNameOf(employee.getDepartmentId());
            case 3 -> employee.getAge();
            case 4 -> employee.getPosition();
            case 5 -> employee.getType().getDisplayName();
            case 6 -> employee.getSalaryDescription();
            case 7 -> String.format(Locale.ROOT, "%.2f", employee.calculateSalary());
            default -> "";
        };
    }

    private String departmentNameOf(int departmentId) {
    for (Department department : departmentService.getDepartments()) {
        if (department.getDepartmentId() == departmentId) {
            return department.getDepartmentName();
        }
    }
    return "Unknown";
}

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
