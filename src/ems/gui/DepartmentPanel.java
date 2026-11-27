package ems.gui;
import ems.model.Department;
import ems.service.DepartmentService;
import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class DepartmentPanel extends JPanel implements Refreshable {
    private final DepartmentService service;
    private final DepartmentTableModel tableModel;
    private final JTable table;
    private final JTextField idField;
    private final JTextField nameField;
    public DepartmentPanel(DepartmentService service){
        this.service=service;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        tableModel = new DepartmentTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        idField= new JTextField(10);
        nameField=new JTextField(20);
        idField.setEditable(false);
        buildForm();
        add(new JScrollPane(table),BorderLayout.CENTER);
        registerListeners();
        refreshData();
    }
    private void buildForm(){
        JLabel title = new JLabel("Manage Departments");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Department Details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.weightx = 0;
        formPanel.add(new JLabel("Department ID:"), c);
        c.gridx = 1; c.weightx = 1;
        formPanel.add(idField, c);

        c.gridx = 0; c.gridy = 1; c.weightx = 0;
        formPanel.add(new JLabel("Department Name:"), c);
        c.gridx = 1; c.weightx = 1;
        formPanel.add(nameField, c);

        JButton addButton =new JButton("Add");
        JButton updateButton =new JButton("Update");
        JButton deleteButton =new JButton("Delete");
        JButton clearButton =new JButton("Clear");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        buttonPanel.add(clearButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JPanel northPanel = new JPanel(new BorderLayout(0, 6));
        northPanel.add(title, BorderLayout.NORTH);
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener(e -> clearFields());
    }
    private void registerListeners(){
        table.getSelectionModel().addListSelectionListener(e->{
            if(e.getValueIsAdjusting()){
                return;
            }
            int viewRow = table.getSelectedRow();
            if(viewRow < 0){
                return;
            }
            int modelRow = table.convertRowIndexToModel(viewRow);
            Department department = tableModel.getDepartmentAt(modelRow);
            idField.setText(String.valueOf(department.getDepartmentId()));
            nameField.setText(department.getDepartmentName());
        });
    }
    private String validateDepartmentName(String rawName) {
        String trimmed = rawName == null ? "" : rawName.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be blank.");
        }
        if (trimmed.contains(",") || trimmed.indexOf('\n') >= 0 || trimmed.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("Department name cannot contain commas or line breaks.");
        }
        return trimmed;
    }
    private void addDepartment(){
        try {
            String name = validateDepartmentName(nameField.getText());
            service.addDepartment(name);
            refreshData();
            MessageHelper.showInfo(this, "Department added successfully!");
        } catch (Exception ex) {
            MessageHelper.showError(this, ex.getMessage());
        }
    }
    private void updateDepartment() {
        try {
            if (idField.getText().isBlank()) {
                MessageHelper.showWarning(this, "Please select a department from the table to update.");
                return;
            }
            String name = validateDepartmentName(nameField.getText());
            service.updateDepartment(Integer.parseInt(idField.getText()), name);
            refreshData();
            MessageHelper.showInfo(this, "Department updated successfully!");
        } catch (Exception ex) {
            MessageHelper.showError(this, ex.getMessage());
        }
    }
    private void deleteDepartment() {
        try {
            if (idField.getText().isBlank()) {
                MessageHelper.showWarning(this, "Please select a department from the table to delete.");
                return;
            }
            String question = "Are you sure you want to delete department "
                    + idField.getText() + " - " + nameField.getText().trim() + "?";
            if (!MessageHelper.confirm(this, question, "Confirm Delete")) {
                return;
            }
            service.deleteDepartment(Integer.parseInt(idField.getText()));
            refreshData();
            MessageHelper.showInfo(this, "Department deleted successfully!");
        } catch (Exception ex) {
            MessageHelper.showError(this, ex.getMessage());
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
        clearFields();
    }
}
