/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */


package ems.gui;

import ems.exception.EmsException;
import ems.exception.ValidationException;
import ems.model.Department;
import ems.model.Employee;
import ems.service.DepartmentService;
import ems.service.EmployeeSearchService;
import ems.service.EmployeeService;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;


public class SearchUpdatePanel extends JPanel implements Refreshable {

    private static final long serialVersionUID = 1L;

    private static final String SEARCH_BY_ID = "Employee ID";
    private static final String SEARCH_BY_NAME = "Name";
    private static final String SEARCH_BY_DEPARTMENT = "Department";

    private final JComboBox<String> searchByCombo;
    private final JTextField searchField;
    private final JButton searchButton;
    private final JComboBox<Department> filterCombo;
    private final JButton filterButton;
    private final JButton showAllButton;
    private final JTable resultsTable;
    private final EmployeeTableModel tableModel;
    private final EmployeeFormPanel updateForm;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton clearButton;
    private final JLabel statusLabel;
    private Runnable lastQuery;
    private final EmployeeService employeeService;
    private final EmployeeSearchService searchService;
    private final DepartmentService departmentService;

    public SearchUpdatePanel(EmployeeService employeeService, EmployeeSearchService searchService,
                             DepartmentService departmentService) {
        this.employeeService = Objects.requireNonNull(employeeService, "employeeService");
        this.searchService = Objects.requireNonNull(searchService, "searchService");
        this.departmentService = Objects.requireNonNull(departmentService, "departmentService");

        searchByCombo = new JComboBox<>(new String[] {SEARCH_BY_ID, SEARCH_BY_NAME, SEARCH_BY_DEPARTMENT});
        searchField = new JTextField(18);
        searchButton = new JButton("Search");
        filterCombo = new JComboBox<>();
        filterButton = new JButton("Filter");
        showAllButton = new JButton("Show All");
        tableModel = new EmployeeTableModel(departmentService);
        resultsTable = new JTable(tableModel);
        updateForm = new EmployeeFormPanel(departmentService, false);
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");
        statusLabel = new JLabel("0 result(s)");
        lastQuery = this::showAllEmployees;

        configureTable();
        buildLayout();
        registerListeners();

        reloadFilterCombo();
        showAllEmployees();
        disableEditForm();
    }

    @Override
    public void refreshData() {
        try {
            searchByCombo.setSelectedIndex(0);
            searchField.setText("");
            reloadFilterCombo();
            updateForm.reloadDepartments();
            lastQuery = this::showAllEmployees;
            showAllEmployees();
            resetEditForm();
        } catch (RuntimeException e) {
            MessageHelper.showError(this, "Could not refresh the screen: " + e.getMessage());
        }
    }
    private void configureTable() {
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setRowSorter(new TableRowSorter<>(tableModel));
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(24);
        resultsTable.setPreferredScrollableViewportSize(new Dimension(700, 150));
    }

    private void buildLayout() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        searchRow.add(new JLabel("Search by:"));
        searchRow.add(searchByCombo);
        searchRow.add(searchField);
        searchRow.add(searchButton);
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        filterRow.add(new JLabel("Filter by department:"));
        filterRow.add(filterCombo);
        filterRow.add(filterButton);
        filterRow.add(showAllButton);
        JPanel top = new JPanel(new BorderLayout());
        top.add(searchRow, BorderLayout.NORTH);
        top.add(filterRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 4));
        center.add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        center.add(statusLabel, BorderLayout.SOUTH);
        add(center, BorderLayout.CENTER);
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBorder(BorderFactory.createTitledBorder("Edit selected employee"));
        formWrapper.add(updateForm, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(formWrapper, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private void registerListeners() {
        searchButton.addActionListener(e -> onSearchClicked());
        searchField.addActionListener(e -> onSearchClicked());
        filterButton.addActionListener(e -> onFilterClicked());
        showAllButton.addActionListener(e -> onShowAllClicked());
        updateButton.addActionListener(e -> onUpdateClicked());
        deleteButton.addActionListener(e -> onDeleteClicked());
        clearButton.addActionListener(e -> resetEditForm());
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRowSelected();
            }
        });
    }

    private void onSearchClicked() {
        final String searchBy = (String) searchByCombo.getSelectedItem();
        final String text = searchField.getText();
        if (runSearch(searchBy, text, true)) {
            lastQuery = () -> runSearch(searchBy, text, false);
        }
    }

    private void onFilterClicked() {
        Department department = (Department) filterCombo.getSelectedItem();
        if (department == null) {
            MessageHelper.showWarning(this, "Please select a department to filter.");
            return;
        }
        final int departmentId = department.getDepartmentId();
        runFilter(departmentId, true);
        lastQuery = () -> runFilter(departmentId, false);
    }

    private void onShowAllClicked() {
        lastQuery = this::showAllEmployees;
        showAllEmployees();
        resetEditForm();
    }

    private void onRowSelected() {
        int viewRow = resultsTable.getSelectedRow();
        if (viewRow < 0) {
            disableEditForm();
            return;
        }
        Employee employee = tableModel.getEmployeeAt(resultsTable.convertRowIndexToModel(viewRow));
        updateForm.setFormEnabled(true);
        updateForm.populate(employee);
        updateForm.setIdEditable(false); 
    }

    private void onUpdateClicked() {
        Employee selected = getSelectedEmployee();
        if (selected == null) {
            MessageHelper.showWarning(this, "Please select an employee to update.");
            return;
        }
        try {
            employeeService.updateEmployee(selected.getEmployeeId(), updateForm.readFormData());
            MessageHelper.showInfo(this, "Employee updated successfully!");
            lastQuery.run();
        } catch (EmsException e) {
            MessageHelper.showError(this, e.getMessage());
        }
    }

    private void onDeleteClicked() {
        Employee selected = getSelectedEmployee();
        if (selected == null) {
            MessageHelper.showWarning(this, "Please select an employee to delete.");
            return;
        }
        String question = "Are you sure you want to delete employee "
                + selected.getEmployeeId() + " - " + selected.getFullName() + "?";
        if (!MessageHelper.confirm(this, question, "Confirm Delete")) {
            return; // cancelled: nothing changes
        }
        try {
            employeeService.deleteEmployee(selected.getEmployeeId());
            MessageHelper.showInfo(this, "Employee deleted successfully!");
            lastQuery.run();
        } catch (EmsException e) {
            MessageHelper.showError(this, e.getMessage());
        }
    }

    private boolean runSearch(String searchBy, String text, boolean announceEmpty) {
        try {
            List<Employee> results;
            if (SEARCH_BY_ID.equals(searchBy)) {
                results = searchService.searchById(text);
            } else if (SEARCH_BY_NAME.equals(searchBy)) {
                results = searchService.searchByName(text);
            } else {
                results = searchService.searchByDepartmentName(text);
            }
            showResults(results);
            if (results.isEmpty() && announceEmpty) {
                MessageHelper.showInfo(this, "No employees found matching your search.");
            }
            return true;
        } catch (ValidationException e) {
            MessageHelper.showError(this, e.getMessage());
            return false;
        }
    }

    private void runFilter(int departmentId, boolean announceEmpty) {
        List<Employee> results = searchService.filterByDepartment(departmentId);
        showResults(results);
        if (results.isEmpty() && announceEmpty) {
            MessageHelper.showInfo(this, "No employees found in this department.");
        }
    }

    private void showAllEmployees() {
        showResults(employeeService.getAllEmployees());
    }

    private void showResults(List<Employee> employees) {
        tableModel.setEmployees(employees);
        statusLabel.setText(employees.size() + " result(s)");
        disableEditForm();
    }

    private Employee getSelectedEmployee() {
        int viewRow = resultsTable.getSelectedRow();
        if (viewRow < 0) {
            return null;
        }
        return tableModel.getEmployeeAt(resultsTable.convertRowIndexToModel(viewRow));
    }

    private void reloadFilterCombo() {
        DefaultComboBoxModel<Department> model = new DefaultComboBoxModel<>();
        for (Department department : departmentService.getDepartments()) {
            model.addElement(department);
        }
        filterCombo.setModel(model);
        filterCombo.setSelectedItem(null); 
    }

    private void resetEditForm() {
        resultsTable.clearSelection();
        disableEditForm();
    }

    private void disableEditForm() {
        updateForm.clearForm();
        updateForm.setFormEnabled(false);
        updateForm.setIdEditable(false);
    }
}
