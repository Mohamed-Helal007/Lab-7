package ems.service;
import ems.exception.*;
import ems.model.Department;
import ems.repository.CsvDepartmentRepository;
import ems.repository.DepartmentRepository;
import java.util.ArrayList;
import java.util.List;
public class DepartmentService {
    private final DepartmentRepository repository;
    private final List<Department> departments;
    private DepartmentUsageChecker usageChecker;
    public DepartmentService(DepartmentRepository repository){//ehna fe elmain bn3ml DepartmentRepository repository =new CsvDepartmentRepository("data/departments.csv");f kda elrepository hya elcsVrepo elhoa 3aml implementation ll repository
        this.repository=repository;
        this.departments=new ArrayList<>();
    }

     public void setUsageChecker(
            DepartmentUsageChecker usageChecker) {

        this.usageChecker = usageChecker;
    }
    public void loadDepartments ()  throws DataAccessException
    {
        departments.clear();
        departments.addAll(repository.loadAll());
    }
    public List<Department> getDepartments(){
        return new ArrayList<>(departments);
    }
    private int generateNextId(){
        int maxId=0;
        for(Department department:departments){
            if(department.getDepartmentId()>maxId){
                maxId=department.getDepartmentId();
            }
        }
        return maxId+1;
    }
    public void addDepartment(String name) throws Exception{
        for (Department department:departments){
            if (department.getDepartmentName().equalsIgnoreCase(name)){
                throw new DuplicateDepartmentException("Department already exists."); 
            }
        }
        Department department =new Department(generateNextId(), name);
        departments.add(department);
        repository.saveAll(departments);
    }
    private Department findDepartmentById(int id){
        for (Department department:departments){
            if(department.getDepartmentId()==id)
            {
                return department;
            }
        }
        return null;
    }
    public void updateDepartment(int id,String newName)throws Exception{
        Department department =findDepartmentById(id);
        if(department ==null){
            throw new DepartmentNotFoundException("Department Not found.");
        }
        for(Department d:departments){
            if(d.getDepartmentId()!=id&&d.getDepartmentName().equalsIgnoreCase(newName)){
                throw new DuplicateDepartmentException("Department already exists");
            }
        }
        department.setDepartmentName(newName);
        repository.saveAll(departments);
    }
    public void deleteDepartment(int id) throws Exception{
       Department department =findDepartmentById(id);
       if(department == null){
        throw new DepartmentNotFoundException("Department not found");
       } 
       if (usageChecker!=null&&usageChecker.isDepartmentInUse(id)){
        throw new DepartmentInUseException("Department is in use");
       }
       departments.remove(department);
       repository.saveAll(departments);
    }
    
}
