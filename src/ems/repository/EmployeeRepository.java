package ems.repository;

import ems.exception.DataAccessException;
import ems.model.Employee;
import java.util.List;

public interface EmployeeRepository {
    List<Employee> loadAll() throws DataAccessException;
    void saveAll(List<Employee> employees) throws DataAccessException;
    List<String> getLastLoadWarnings();
}
