package ems.exception;

public class DuplicateIdException extends EmsException {
    public DuplicateIdException(int employeeId) {
        super("Employee ID " + employeeId + " already exists.");
    }
}
