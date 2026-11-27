package ems.gui;

import ems.exception.DataAccessException;
import ems.repository.CsvEmployeeRepository;
import ems.repository.EmployeeRepository;
import ems.service.AuthService;
import ems.service.DepartmentService;
import ems.service.EmployeeSearchService;
import ems.service.EmployeeService;
import ems.service.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainFrame extends JFrame implements Navigator {
    private static final String EMPLOYEES_CSV_PATH = "data/employees.csv";

    private final AuthService authService;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    private final JPanel topNavBar;
    private final JLabel userLabel;
    private final LoginPanel loginPanel;
    private final Map<String, JPanel> screens = new LinkedHashMap<>();
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();

    public MainFrame(DepartmentService departmentService) {
        this(
                new AuthService("mohamed","koko"),
                Objects.requireNonNull(departmentService, "departmentService"),
                createDefaultEmployeeService(departmentService),
                new InputValidator()
        );
    }

    public MainFrame(AuthService authService,
                     DepartmentService departmentService,
                     EmployeeService employeeService,
                     InputValidator validator) {
        this(
                authService,
                departmentService,
                employeeService,
                new EmployeeSearchService(employeeService, departmentService, validator),
                validator
        );
    }

    public MainFrame(AuthService authService,
                     DepartmentService departmentService,
                     EmployeeService employeeService,
                     EmployeeSearchService searchService,
                     InputValidator validator) {
        this.authService = Objects.requireNonNull(authService, "authService");
        Objects.requireNonNull(departmentService, "departmentService");
        Objects.requireNonNull(employeeService, "employeeService");
        Objects.requireNonNull(searchService, "searchService");
        Objects.requireNonNull(validator, "validator");

        departmentService.setUsageChecker(employeeService);

        setTitle("Employee Management System");
        setSize(1000, 720);
        setMinimumSize(new Dimension(880, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(authService, validator, this);
        DashboardPanel dashboardPanel = new DashboardPanel(this, employeeService, departmentService);
        AddEmployeePanel addEmployeePanel = new AddEmployeePanel(employeeService, departmentService);
        ViewEmployeesPanel viewEmployeesPanel = new ViewEmployeesPanel(employeeService, departmentService);
        SearchUpdatePanel searchUpdatePanel = new SearchUpdatePanel(employeeService, searchService, departmentService);
        DepartmentPanel departmentPanel = new DepartmentPanel(departmentService);

        registerScreen(SCREEN_LOGIN, loginPanel);
        registerScreen(SCREEN_DASHBOARD, dashboardPanel);
        registerScreen(SCREEN_ADD_EMPLOYEE, addEmployeePanel);
        registerScreen(SCREEN_VIEW_EMPLOYEES, viewEmployeesPanel);
        registerScreen(SCREEN_SEARCH_UPDATE, searchUpdatePanel);
        registerScreen(SCREEN_DEPARTMENTS, departmentPanel);

        userLabel = new JLabel();
        topNavBar = buildTopNavBar();

        setLayout(new BorderLayout());
        add(topNavBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        showScreen(authService.isLoggedIn() ? SCREEN_DASHBOARD : SCREEN_LOGIN);
    }

    private static EmployeeService createDefaultEmployeeService(DepartmentService departmentService) {
        ensureDataDirectory();
        InputValidator validator = new InputValidator();
        EmployeeRepository employeeRepository = new CsvEmployeeRepository(EMPLOYEES_CSV_PATH);
        EmployeeService employeeService = new EmployeeService(employeeRepository, departmentService, validator);
        Path employeesPath = Path.of(EMPLOYEES_CSV_PATH);
        if (Files.exists(employeesPath)) {
            try {
                employeeService.loadEmployees();
                List<String> warnings = employeeService.getLoadWarnings();
                if (!warnings.isEmpty()) {
                    SwingUtilities.invokeLater(() -> MessageHelper.showWarning(
                            null,
                            "Loaded employees with warnings:\n" + String.join("\n", warnings)
                    ));
                }
            } catch (DataAccessException e) {
                SwingUtilities.invokeLater(() -> MessageHelper.showError(
                        null,
                        "Could not load employees: " + e.getMessage()
                ));
            }
        }
        return employeeService;
    }

    private static void ensureDataDirectory() {
        try {
            Path dataDir = Path.of(EMPLOYEES_CSV_PATH).getParent();
            if (dataDir != null) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException ignored) {
        }
    }

    private void registerScreen(String screenName, JPanel panel) {
        screens.put(screenName, panel);
        cardPanel.add(panel, screenName);
    }

    private JPanel buildTopNavBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 214, 222)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        left.setOpaque(false);
        addNavButton(left, "Dashboard", SCREEN_DASHBOARD);
        addNavButton(left, "Add Employee", SCREEN_ADD_EMPLOYEE);
        addNavButton(left, "View Employees", SCREEN_VIEW_EMPLOYEES);
        addNavButton(left, "Search / Update", SCREEN_SEARCH_UPDATE);
        addNavButton(left, "Departments", SCREEN_DEPARTMENTS);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        userLabel.setForeground(new Color(90, 98, 110));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        right.add(userLabel);
        right.add(logoutButton);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private void addNavButton(JPanel container, String label, String screenName) {
        JButton button = new JButton(label);
        button.setFocusPainted(false);
        button.addActionListener(e -> showScreen(screenName));
        navButtons.put(screenName, button);
        container.add(button);
    }

    @Override
    public void showScreen(String screenName) {
        String targetScreen = screenName;
        if (!SCREEN_LOGIN.equals(targetScreen) && !authService.isLoggedIn()) {
            targetScreen = SCREEN_LOGIN;
        }

        boolean loggedInView = !SCREEN_LOGIN.equals(targetScreen);
        topNavBar.setVisible(loggedInView);
        if (loggedInView) {
            String username = authService.getCurrentUsername();
            userLabel.setText(username == null ? "" : "Signed in as: " + username);
            for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
                entry.getValue().setEnabled(!entry.getKey().equals(targetScreen));
            }
        }

        JPanel targetPanel = screens.get(targetScreen);
        if (targetPanel instanceof Refreshable refreshable) {
            refreshable.refreshData();
        }

        cardLayout.show(cardPanel, targetScreen);
    }

    @Override
    public void logout() {
        authService.logout();
        loginPanel.clearFields();
        showScreen(SCREEN_LOGIN);
    }
}
