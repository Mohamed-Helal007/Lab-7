/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Sky Hawk
 */
public class FullTimeEmployee extends Employee {
    public static final String KEY_BASIC_SALARY="Basic-Salary";
    public static final String KEY_BONUS="Bonus";
    private double basicSalary;
    private double bonus;
    
    public FullTimeEmployee(int employeeId, String fullName, int departmentId,
            int age, String position, double basicSalary, double bonus){
        super(employeeId,fullName,departmentId,age,position);
        this.basicSalary=basicSalary;
        this.bonus=bonus;
    }

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBasicSalary(double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }
    @Override
    public double calculateSalary(){
        return basicSalary+bonus;
    }
    @Override
    public Map<String, Double> getSalaryComponents(){
        Map<String,Double>map=new LinkedHashMap<>();
        map.put(KEY_BASIC_SALARY, basicSalary);
        map.put(KEY_BONUS, bonus);
        return map;
    }
    @Override
    public EmployeeType getType(){
        return EmployeeType.FULL_TIME;
    }
    @Override
    public String getSalaryDescription(){
        return String.format("Basic : %.2f +Bonus : %.2f",basicSalary,bonus);
    }
}
