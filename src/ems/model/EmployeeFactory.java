/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ems.model;

import java.util.Map;

/**
 *
 * @author Sky Hawk
 */
final class EmployeeFactory {
    private EmployeeFactory(){}
    public static Employee createEmployee(EmployeeType type, int employeeId,
            String fullName, int departmentId, int age,String position,
            Map<String, Double> components){
        if (type == null) throw new IllegalArgumentException("Employee type must not be null.");
        if (components == null) throw new IllegalArgumentException("Salary components must not be null.");
        switch (type) {
            case FULL_TIME:
                return new FullTimeEmployee(employeeId, fullName,  departmentId,age, position,
                        require(components, FullTimeEmployee.KEY_BASIC_SALARY, "Basic-Salary"),
                        require(components, FullTimeEmployee.KEY_BONUS, "Bonus"));
            case PART_TIME:
                return new PartTimeEmployee(employeeId, fullName, departmentId,age, position,
                        require(components, PartTimeEmployee.KEY_HOURLY_RATE, "Hourly-Rate"),
                        require(components, PartTimeEmployee.KEY_HOURS_WORKED, "Hours-Worked"));
            default:
                throw new IllegalArgumentException("Unsupported employee type: " + type);
        }
    }
    private static double require(Map<String, Double> components, String key, String label) {
        Double value = components.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing " + label + " ('" + key + "').");
        }
        return value;
    }
}
