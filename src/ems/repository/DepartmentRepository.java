package ems.repository;
import ems.exception.DataAccessException;
import ems.model.Department;
import java.util.List;

public interface DepartmentRepository {

    List<Department> loadAll() throws DataAccessException;

    void saveAll(List<Department> departments)
            throws DataAccessException;

    boolean storageExists();
}