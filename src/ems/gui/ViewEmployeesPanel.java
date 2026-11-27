package ems.gui;

import ems.model.Employee;
import ems.service.*;

import javax.security.auth.Refreshable;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import javax.swing.text.View;

import java.awt.*;
import java.util.*;

public class ViewEmployeesPanel extends JPanel implements Refreshable {
    private static final String NO_SELECTION_TEXT = "Select an employee to see the salary breakdown.";
    private final JTable employeeTable;
    private final EmployeeTableModel tableModel;
    private final JButton refreshButton;
    private final JLabel countLabel;
    private final JLabel payrollLabel;
    private final JLabel detailsLabel;
    private final EmployeeService employeeService;

    public ViewEmployeesPanel(EmployeeService employeeService, DepartmentService departmentService) {
        this.employeeService = employeeService;
        this.tableModel = new EmployeeTableModel(departmentService);
        this.employeeTable = new JTable(tableModel);
        this.refreshButton = new JButton("Refresh");
        this.countLabel = new JLabel();
        this.payrollLabel = new JLabel();
        this.detailsLabel = new JLabel(NO_SELECTION_TEXT);

        configureTable();
        buildLayout();
        addListeners();
        refreshData();
    }

    public void refreshData() {
        tableModel.setEmployees(employeeService.getAllEmployees());
        countLabel.setText("Total employees: " + tableModel.getRowCount());
        payrollLabel.setText(String.format(Locale.ROOT, "Total monthly payroll: %.2f EGP", employeeService.calculateTotalPayroll()));
        detailsLabel.setText(NO_SELECTION_TEXT);
    }

    public Employee getSelectedEmployee() {
        int viewRow = employeeTable.getSelectedRow();
        if (viewRow < 0)
            return null;
        int modelRow = employeeTable.convertRowIndexToModel(viewRow);
        return tableModel.getEmployeeAt(modelRow);
    }

    private void configureTable() {
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(24);

        TableRowSorter<EmployeeTableModel> sorter = new TableRowSorter<>(tableModel);

        sorter.setComparator(EmployeeTableModel.SALARY_COLUMN, Comparator.comparingDouble((Object value) -> Double.parseDouble((String) value)));
        employeeTable.setRowSorter(sorter);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("View Employees");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));

        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.WEST);
        header.add(refreshButton, BorderLayout.EAST);

        JPanel statusStrip = new JPanel();
        statusStrip.setLayout(new BoxLayout(statusStrip, BoxLayout.Y_AXIS));
        statusStrip.add(countLabel);
        statusStrip.add(payrollLabel);
        statusStrip.add(detailsLabel);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(employeeTable), BorderLayout.CENTER);
        add(statusStrip, BorderLayout.SOUTH);
    }

    private void addListeners() {
        refreshButton.addActionListener(e -> refreshData());

        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting())
                return;
            showSelectedDetails();
        });
    }

    private void showSelectedDetails() {
        Employee employee = getSelectedEmployee();
        if (employee == null) {
            detailsLabel.setText(NO_SELECTION_TEXT);
            return;
        }
        detailsLabel.setText(String.format(Locale.ROOT, "ID %d - %s: %s = %.2f EGP", employee.getEmployeeId(), employee.getFullName(), employee.getSalaryDescription(), employee.calculateSalary()));
    }
}
