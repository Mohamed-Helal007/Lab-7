package ems.gui;
import ems.model.Department;
import ems.service.DepartmentService;
import javax.swing.*;
import java.awt.*;

public class DepartmentPanel extends JPanel implements Refreshable {
    private final DepartmentService service;
    private final DepartmentTableModel tableModel;
    private final JTable table;
    private final JTextField idField;
    private final JTextField nameField;
    public DepartmentPanel(DepartmentService service){
        this.service=service;
        setLayout(new BorderLayout());
        tableModel = new DepartmentTableModel();
        table = new JTable(tableModel);
        idField= new JTextField(10);
        nameField=new JTextField(20);
        idField.setEditable(false);
        buildForm();
        add(new JScrollPane(table),BorderLayout.CENTER);
        registerListeners();
        refreshData();
    }
    private void buildForm(){
        JPanel formPanel=new JPanel(new GridLayout(3,2,5,5));
        
        JButton addButton =new JButton("Add");
        JButton updateButton =new JButton("Update");
        JButton deleteButton =new JButton("Delete");
        JButton clearButton =new JButton("Clear");
        
        formPanel.add(new JLabel("ID"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Name"));
        formPanel.add(nameField);
        
        JPanel buttonPanel =new JPanel();
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel northPanel =new JPanel(new BorderLayout());

        northPanel.add(formPanel,BorderLayout.CENTER);
        northPanel.add(buttonPanel,BorderLayout.SOUTH);
        
        add(northPanel,BorderLayout.NORTH);

        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener( e -> clearFields());
    }
    private void registerListeners(){
        table.getSelectionModel().addListSelectionListener(e->{
            if(e.getValueIsAdjusting()){
                return;
            }
            int row=table.getSelectedRow();
            if(row<0){
                return;
            }
            Department department =tableModel.getDepartmentAt(row);
            idField.setText(String.valueOf(department.getDepartmentId()));
            nameField.setText(department.getDepartmentName());
        });
    }
    private void addDepartment(){
        try {
            service.addDepartment(nameField.getText().trim());
            refreshData();
            clearFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage());
        }
    }
    private void updateDepartment() {

        try {
            if (idField.getText().isBlank()) {
                return;
            }
            service.updateDepartment(Integer.parseInt(idField.getText()),nameField.getText().trim());
            refreshData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage());
        }
    }
    private void deleteDepartment() {
        try {

            if (idField.getText().isBlank()) {
                return;
            }

            service.deleteDepartment(Integer.parseInt(idField.getText()));
            refreshData();
            clearFields();
        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this,ex.getMessage());
        }
    }

    private void clearFields() {

        idField.setText("");
        nameField.setText("");
        table.clearSelection();
    }
    @Override
    public void refreshData() {

        tableModel.setDepartments(
                service.getDepartments()
        );
    }


}
