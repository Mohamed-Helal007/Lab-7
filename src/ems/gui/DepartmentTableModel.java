package ems.gui;
import javax.swing.*;
import ems.model.Department;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class DepartmentTableModel extends AbstractTableModel {
    private final String[] columnNames ={"ID","NAME"};
    private List<Department> departments;
    public DepartmentTableModel(){
        departments=new ArrayList<>();
    }
    public void setDepartments(List<Department> departments){
        this.departments=departments;
        fireTableDataChanged();
    }
    public Department getDepartmentAt(int i){
        return departments.get(i);
    }
    @Override 
    public int getRowCount(){
        return departments.size();
    }
    @Override 
    public int getColumnCount(){
        return columnNames.length;
    }
    @Override 
    public String getColumnName(int i){
        return columnNames[i];
    }
    @Override 
    public Object getValueAt(int r,int c){
        Department department = departments.get(r);
        switch(c){
            case 0:
                return department.getDepartmentId();
            case 1:
                return department.getDepartmentName();
            default :
                return null;        
        }
    }

}
