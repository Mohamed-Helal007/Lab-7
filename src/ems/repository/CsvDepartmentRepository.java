package ems.repository;
import java.nio.file.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ems.model.Department;

public class CsvDepartmentRepository implements DepartmentRepository {
    private final Path filePath;
    private final String HEADER = "DepartmentId,DepartmentName";
    public CsvDepartmentRepository(String filepath){
        this.filePath = Paths.get(filepath);
    }
    @Override 
    public boolean storageExists(){
        return Files.exists(filePath);
    }
    @Override
    public List<Department> loadAll() throws DataAccessException{
        List<Department> departments =new ArrayList<>();
        if (!Files.exists(filePath)) {
            return departments;
        }
        try{
            List<String> lines =Files.readAllLines(filePath);
            for(int i =0;i<lines.size(),i++){
                String[] parts=lines.get(i).split(",");
                int id=Integer.parseInt(parts[0]);
                String name=parts[1];
                departments.add(new Department(id,name));
            }
            return departments;
        }
        catch(IOException e){
            throw new DataAccessException("Error reading department data from file",e);
        }

    }
    @Override
    public void saveAll(List<Department> departments) throws DataAccessException{
        List<String> lines=new ArrayList<>();
        lines.add(HEADER);
        for(Department department:departments)
        {
            lines.add(department.getDepartmentId()+","+department.getDepartmentName());
        }
        try{
            Files.write(filePath,lines);
        }
        catch(IOException e){
            throw new DataAccessException("Failed to save departments",e);
        }
    }

}
