package ems.gui;

import ems.exception.EmsException;
import ems.model.EmployeeFormData;
import ems.service.DepartmentService;
import ems.service.EmployeeService;

import javax.swing.*;
import java.awt.*;

public class AddEmployeePanel extends JPanel implements Refreshable{
    private final EmployeeFormPanel form;
    private final JButton addButton;
    private final JButton clearButton;
    private final EmployeeService employeeService;

    public AddEmployeePanel(EmployeeService employeeService, DepartmentService departmentService) {
        if (employeeService == null) {
            throw new IllegalArgumentException("EmployeeService must not be null.");
        }
        this.employeeService = employeeService;
        this.form = new EmployeeFormPanel(departmentService, true);

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Add Employee");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        this.clearButton = new JButton("Clear");
        this.addButton = new JButton("Add Employee");
        buttons.add(clearButton);
        buttons.add(addButton);
        add(buttons, BorderLayout.SOUTH);

        addButton.addActionListener(e -> onAddClicked());
        clearButton.addActionListener(e -> {
            form.clearForm();
            prefillNextId();
        });

        refreshData();
    }

    @Override
    public void refreshData() {
        form.reloadDepartments();
        prefillNextId();
    }

    private void prefillNextId() {
        try {
            form.setIdFieldText(String.valueOf(employeeService.generateNextEmployeeId()));
        } catch (RuntimeException ex) {
            form.setIdFieldText("");
        }
    }

    private void onAddClicked() {
        EmployeeFormData data = form.readFormData();
        try {
            employeeService.addEmployee(data);
            MessageHelper.showInfo(this, "Employee added successfully!");
            form.clearForm();
            prefillNextId();
        } catch (EmsException ex) {
            MessageHelper.showError(this, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            MessageHelper.showError(this, ex.getMessage());
        }
    }
}