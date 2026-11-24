package ems.model;

public class Department {
    private final int departmentId;
    private String departmentName;
    public Department(int departmentId, String departmentName) {

        if (departmentId <= 0) {
            throw new IllegalArgumentException("Department ID must be greater than zero.");
        }
        setDepartmentName(departmentName);
        this.departmentId = departmentId;
    }
    public int getDepartmentId()
    {
        return departmentId;
    }
    public String getDepartmentName()
    {
        return departmentName;
    }
   public void setDepartmentName(String departmentName) {
        if (departmentName == null ||departmentName.trim().isEmpty()) {
                throw new IllegalArgumentException("Department name cannot be blank.");
        }
        this.departmentName = departmentName.trim();
    }
     @Override
    public String toString() {
        return departmentName;
    }


}
