package ems.service;

import ems.exception.*;
import ems.model.*;
import ems.repository.EmployeeRepository;
import java.util.*;

public class EmployeeService implements DepartmentUsageChecker {
    private final EmployeeRepository repository;
    private final DepartmentService departmentService;
    private final InputValidator validator;
    private final List<Employee> employees = new ArrayList<>();

    public EmployeeService(EmployeeRepository repository, DepartmentService departmentService, InputValidator validator) {
        this.repository = repository;
        this.departmentService = departmentService;
        this.validator = validator;
    }

    public void loadEmployees() throws DataAccessException {
        List<Employee> loaded = repository.loadAll();
        employees.clear();
        employees.addAll(loaded);
    }

    public List<String> getLoadWarnings() {
        return repository.getLastLoadWarnings();
    }

    public List<Employee> getAllEmployees() {
        List<Employee> copy = new ArrayList<>(employees);
        copy.sort(Comparator.comparingInt(Employee::getEmployeeId));
        return Collections.unmodifiableList(copy);
    }

    public Employee findEmployeeById(int employeeId) {
        for (Employee employee : employees) {
            if (employee.getEmployeeId() == employeeId) {
                return employee;
            }
        }
        return null;
    }

    public boolean employeeIdExists(int employeeId) {
        return findEmployeeById(employeeId) != null;
    }

    public int generateNextEmployeeId() {
        int max = 0;
        for (Employee employee : employees) {
            max = Math.max(max, employee.getEmployeeId());
        }
        return max + 1;
    }

    public Employee addEmployee(EmployeeFormData data) throws ValidationException, DuplicateIdException, DataAccessException {
        int employeeId = validator.parseEmployeeId(data.getEmployeeId());
        if (employeeIdExists(employeeId))
            throw new DuplicateIdException(employeeId);

        Employee employee = buildEmployee(employeeId, data);
        employees.add(employee);

        try {
            repository.saveAll(employees);
        }
        catch (DataAccessException e) {
            employees.remove(employee);
            throw e;
        }
        return employee;
    }

    public Employee updateEmployee(int employeeId, EmployeeFormData data) throws ValidationException, DuplicateIdException, DataAccessException {
        int index = indexOf(employeeId);
        if (index < 0)
            throw new EmployeeNotFoundException(employeeId);

        Employee replacement = buildEmployee(employeeId, data);
        Employee old = employees.set(index, replacement);

        try {
            repository.saveAll(employees);
        }
        catch (DataAccessException e) {
            employees.set(index, old);
            throw e;
        }
        return replacement;
    }

    public void deleteEmployee(int employeeId) throws EmployeeNotFoundException, DataAccessException {
        int index = indexOf(employeeId);
        if (index < 0)
            throw new EmployeeNotFoundException(employeeId);

        Employee removed = employees.remove(index);

        try {
            repository.saveAll(employees);
        }
        catch (DataAccessException e) {
            employees.add(index, removed);
            throw e;
        }
    }

    public int countEmployeesInDepartment(int departmentId) {
        int count = 0;
        for (Employee employee : employees) {
            if (employee.getDepartmentId() == departmentId)
                count++;
        }
        return count;
    }

    public double calculateTotalPayroll() {
        double total = 0;
        for (Employee employee : employees)
            total += employee.calculateSalary();

        return total;
    }

    public boolean isDepartmentInUse(int departmentId) {
        return countEmployeesInDepartment(departmentId) > 0;
    }

    private int indexOf(int employeeId) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmployeeId() == employeeId) {
                return i;
            }
        }
        return -1;
    }

    private Employee buildEmployee(int employeeId, EmployeeFormData data) throws ValidationException {
        String fullName = validator.requireText("Full name", data.getFullName());
        int age = validator.parseAge(data.getAge());
        String position = validator.requireText("Position", data.getPosition());

        validator.requireSelection("department", data.getDepartment());
        int departmentId = data.getDepartment().getDepartmentId();
        if (!departmentService.departmentExists(departmentId))
            throw new ValidationException("The selected department does not exist.");

        validator.requireSelection("employee type", data.getEmployeeType());
        EmployeeType type = data.getEmployeeType();

        Map<String, Double> components = readSalaryComponents(type, data);
        try {
            return EmployeeFactory.createEmployee(type, employeeId, fullName, departmentId, age, position, components);
        }
        catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }

    private Map<String, Double> readSalaryComponents(EmployeeType type, EmployeeFormData data) throws ValidationException {
        Map<String, Double> components = new LinkedHashMap<>();
        switch (type) {
            case FULL_TIME -> {
                components.put(FullTimeEmployee.KEY_BASIC_SALARY, validator.parseNonNegativeAmount("Basic salary", data.getBasicSalary()));
                components.put(FullTimeEmployee.KEY_BONUS, validator.parseNonNegativeAmount("Bonus", data.getBonus()));
            }
            case PART_TIME -> {
                components.put(PartTimeEmployee.KEY_HOURLY_RATE, validator.parseNonNegativeAmount("Hourly rate", data.getHourlyRate()));
                components.put(PartTimeEmployee.KEY_HOURS_WORKED, validator.parseNonNegativeAmount("Hours worked", data.getHoursWorked()));
            }
        }
        return components;
    }
}
