package ems.gui;

import ems.service.DepartmentService;
import ems.service.EmployeeService;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class DashboardPanel extends JPanel implements Refreshable {
    private final Navigator navigator;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private JLabel employeeCountValue;
    private JLabel departmentCountValue;
    private JLabel payrollValue;

    public DashboardPanel(Navigator navigator) {
        this(navigator, null, null);
    }

    public DashboardPanel(Navigator navigator, EmployeeService employeeService, DepartmentService departmentService) {
        this.navigator = navigator;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        initializeUI();
        refreshData();
    }

    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setPreferredSize(new Dimension(560, 400));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        JLabel title = new JLabel("Employee Management System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        JLabel subtitle = new JLabel("Choose a section below to manage employees and departments", SwingConstants.CENTER);
        subtitle.setForeground(new Color(100, 108, 120));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        container.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18));

        if (employeeService != null && departmentService != null) {
            JPanel statsPanel = new JPanel(new GridLayout(1, 3, 12, 0));
            employeeCountValue = createStatValueLabel();
            departmentCountValue = createStatValueLabel();
            payrollValue = createStatValueLabel();
            statsPanel.add(createStatCard("Total Employees", employeeCountValue));
            statsPanel.add(createStatCard("Departments", departmentCountValue));
            statsPanel.add(createStatCard("Monthly Payroll", payrollValue));
            center.add(statsPanel, BorderLayout.NORTH);
        }

        JPanel buttonGrid = new JPanel(new GridLayout(2, 2, 14, 14));
        JButton addEmployeeButton = createMenuButton("Add Employee");
        JButton viewEmployeesButton = createMenuButton("View Employees");
        JButton searchButton = createMenuButton("Search / Update");
        JButton departmentsButton = createMenuButton("Departments");

        addEmployeeButton.addActionListener(e -> navigator.showScreen(Navigator.SCREEN_ADD_EMPLOYEE));
        viewEmployeesButton.addActionListener(e -> navigator.showScreen(Navigator.SCREEN_VIEW_EMPLOYEES));
        searchButton.addActionListener(e -> navigator.showScreen(Navigator.SCREEN_SEARCH_UPDATE));
        departmentsButton.addActionListener(e -> navigator.showScreen(Navigator.SCREEN_DEPARTMENTS));

        buttonGrid.add(addEmployeeButton);
        buttonGrid.add(viewEmployeesButton);
        buttonGrid.add(searchButton);
        buttonGrid.add(departmentsButton);
        center.add(buttonGrid, BorderLayout.CENTER);

        container.add(center, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(140, 38));
        logoutButton.addActionListener(e -> navigator.logout());
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.add(logoutButton);
        container.add(footer, BorderLayout.SOUTH);

        add(container, new GridBagConstraints());
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setPreferredSize(new Dimension(220, 64));
        button.setFocusPainted(false);
        return button;
    }

    private JLabel createStatValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));
        return label;
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 222), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(new Color(100, 108, 120));
        titleLabel.setFont(titleLabel.getFont().deriveFont(12f));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    @Override
    public void refreshData() {
        if (employeeService != null && employeeCountValue != null && payrollValue != null) {
            employeeCountValue.setText(String.valueOf(employeeService.getAllEmployees().size()));
            payrollValue.setText(String.format(Locale.ROOT, "%.2f EGP", employeeService.calculateTotalPayroll()));
        }
        if (departmentService != null && departmentCountValue != null) {
            departmentCountValue.setText(String.valueOf(departmentService.getDepartments().size()));
        }
    }
}
