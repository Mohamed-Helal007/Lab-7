package ems.model;

import ems.model.Department;
import java.util.Map;

public class EmployeeFormData {
    private String employeeId;
    private String fullName;
    private Department department;
    private String age;
    private String position;
    private EmployeeType employeeType;
    private String basicSalary;
    private String bonus;
    private String hourlyRate;
    private String hoursWorked;
    
    public EmployeeFormData(){
        this.employeeId   = "";
        this.fullName     = "";
        this.department   = null;
        this.age          = "";
        this.position     = "";
        this.employeeType = null;
        this.basicSalary  = "";
        this.bonus        = "";
        this.hourlyRate   = "";
        this.hoursWorked  = "";
    }

    public EmployeeFormData(String idText, String nameText, String ageText, String positionText,
        Department department, EmployeeType type,
        Map<String, String> salaryFieldTexts) {
        this();                          // reuse the no-arg init
        this.employeeId   = idText;
        this.fullName     = nameText;
        this.age          = ageText;
        this.position     = positionText;
        this.department   = department;
        this.employeeType = type;

        if (salaryFieldTexts != null) {
        this.basicSalary = salaryFieldTexts.getOrDefault("basicSalary", "");
        this.bonus       = salaryFieldTexts.getOrDefault("bonus", "");
        this.hourlyRate  = salaryFieldTexts.getOrDefault("hourlyRate", "");
        this.hoursWorked = salaryFieldTexts.getOrDefault("hoursWorked", "");
        }
        }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public Department getDepartment() {
        return department;
    }

    public String getAge() {
        return age;
    }

    public String getPosition() {
        return position;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public String getBasicSalary() {
        return basicSalary;
    }

    public String getBonus() {
        return bonus;
    }

    public String getHourlyRate() {
        return hourlyRate;
    }

    public String getHoursWorked() {
        return hoursWorked;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public void setBasicSalary(String basicSalary) {
        this.basicSalary = basicSalary;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public void setHourlyRate(String hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setHoursWorked(String hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
    
    
}
