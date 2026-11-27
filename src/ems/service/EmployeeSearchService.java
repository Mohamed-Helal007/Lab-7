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
import ems.model.Department;
import ems.model.Employee;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EmployeeSearchService {
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final InputValidator validator;

public EmployeeSearchService(EmployeeService employeeService, DepartmentService departmentService, InputValidator validator) {
        this.employeeService = Objects.requireNonNull(employeeService, "employeeService");
        this.departmentService = Objects.requireNonNull(departmentService, "departmentService");
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    public List<Employee> searchById(String rawId) throws ValidationException {
        int id = validator.parseEmployeeId(rawId);
        List<Employee> result = new ArrayList<>();
        Employee found = employeeService.findEmployeeById(id);
        if (found != null) {
            result.add(found);
        }
        return result;
    }

    public List<Employee> searchByName(String keyword) throws ValidationException {
        String needle = validator.requireText("Full Name", keyword).toLowerCase(Locale.ROOT);
        List<Employee> result = new ArrayList<>();
        for (Employee employee : employeeService.getAllEmployees()) {
            if (employee.getFullName().toLowerCase(Locale.ROOT).contains(needle)) {
                result.add(employee);
            }
        }
        sortById(result);
        return result;
    }

    public List<Employee> searchByDepartmentName(String departmentName) throws ValidationException {
        String name = validator.requireText("Department Name", departmentName);
        Department department = departmentService.findDepartmentByName(name);
        if (department == null) {
            return new ArrayList<>();
        }
        return filterByDepartment(department.getDepartmentId());
    }

    public List<Employee> filterByDepartment(int departmentId) {
        List<Employee> result = new ArrayList<>();
        for (Employee employee : employeeService.getAllEmployees()) {
            if (employee.getDepartmentId() == departmentId) {
                result.add(employee);
            }
        }
        sortById(result);
        return result;
    }

    private static void sortById(List<Employee> employees) {
        employees.sort(Comparator.comparingInt(Employee::getEmployeeId));
    }
}
