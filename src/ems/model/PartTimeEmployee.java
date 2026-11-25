/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ems.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Sky Hawk
 */
public class PartTimeEmployee extends Employee {
    public static final String KEY_HOURLY_RATE="Hourly-Rate";
    public static final String KEY_HOURS_WORKED="Hours-Worked";
    private double hourlyRate;
    private double hoursWorked;
    public PartTimeEmployee(int employeeId, String fullName, int departmentId, int age, String position, double
             hourlyRate, double hoursWorked){
        super(employeeId,fullName,departmentId,age,position);
        this.hourlyRate=hourlyRate;
        this.hoursWorked=hoursWorked;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
    @Override
    public double calculateSalary(){
        return hourlyRate*hoursWorked;
    }
    @Override
    public Map<String, Double> getSalaryComponents(){
        Map<String, Double> map = new LinkedHashMap<>();
        map.put(KEY_HOURLY_RATE, hourlyRate);
        map.put(KEY_HOURS_WORKED, hoursWorked);
        return map;
    }
    @Override
    public EmployeeType getType(){
        return EmployeeType.PART_TIME;
    }
    @Override
    public String getSalaryDescription(){
        return String.format("Rate : %.2f +Hours Worked : %.2f",hourlyRate,hoursWorked);
    }
}
