/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Map;
/**
 *
 * @author Sky Hawk
 */
public abstract class Employee {
    private int employeeId;
    private String fullName;
    private int departmentId;
    private int age;
    private String position;
    protected Employee(int employeeId, String fullName,
            int departmentId, int age, String position){
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.departmentId = departmentId;
        this.age = age;
        this.position = position;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public int getAge() {
        return age;
    }

    public String getPosition() {
        return position;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    
    public abstract double calculateSalary();
    public abstract Map<String, Double> getSalaryComponents();
    public abstract EmployeeType getType();
    public abstract String getSalaryDescription();
}
