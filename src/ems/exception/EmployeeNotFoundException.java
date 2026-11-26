package ems.exception;

public class EmployeeNotFoundException extends EmsException {
    public EmployeeNotFoundException(int employeeId) {
        super("Employee " + employeeId + " was not found.");
    }
}
