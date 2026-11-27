package ems.gui;

import ems.model.Department;
import ems.model.Employee;
import ems.model.EmployeeFormData;
import ems.model.EmployeeType;
import ems.model.FullTimeEmployee;
import ems.model.PartTimeEmployee;
import ems.service.DepartmentService;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import javax.swing.*;
import java.util.Map;
import java.util.List;

public class EmployeeFormPanel extends JPanel {
    private final JTextField idField=new JTextField(12);
    private final JTextField nameField=new JTextField(12);
    private final JTextField ageField=new JTextField(12);
    private final JTextField positionField=new JTextField(12);
    private final JComboBox<Department> departmentCombo=new JComboBox<>();
    private final JComboBox<EmployeeType> typeCombo=new JComboBox<>(EmployeeType.values());
    private final CardLayout salaryLayout=new CardLayout();
    private final JPanel salaryCards=new JPanel(salaryLayout);
    private final JTextField basicSalaryField=new JTextField(12);
    private final JTextField bonusField=new JTextField(12);
    private final JTextField hourlyRateField=new JTextField(12);
    private final JTextField hoursWorkedField=new JTextField(12);
    private final DepartmentService departmentService;

    public EmployeeFormPanel(DepartmentService departmentService, boolean idEditable){
        if (departmentService == null) {
            throw new IllegalArgumentException("DepartmentService must not be null.");
        }
        this.departmentService = departmentService;

        setLayout(new BorderLayout(8, 8));
        add(buildCommonPanel(), BorderLayout.NORTH);
        add(buildSalaryCards(), BorderLayout.CENTER);

        idField.setEditable(idEditable);
        typeCombo.setSelectedIndex(-1);
        departmentCombo.setSelectedIndex(-1);
        typeCombo.addItemListener(e -> showSalaryCard());
        showSalaryCard();

        reloadDepartments();
    }

    private JPanel buildCommonPanel() {
        JPanel body = new JPanel(new GridBagLayout());
        body.setBorder(BorderFactory.createTitledBorder("Employee data"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;

        int row = 0;
        addRow(body, c, row++, "Employee ID:", idField);
        addRow(body, c, row++, "Full Name:",   nameField);
        addRow(body, c, row++, "Age:",         ageField);
        addRow(body, c, row++, "Position:",    positionField);
        addRow(body, c, row++, "Department:",  departmentCombo);
        addRow(body, c, row++, "Type:",        typeCombo);
        return body;
    }

    private void addRow(JPanel body, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        body.add(new JLabel(label), c);
        c.gridx = 1; c.gridy = row; c.weightx = 1;
        body.add(field, c);
    }

    private JPanel buildSalaryCards() {
        JPanel noneCard = new JPanel(new GridBagLayout());
        noneCard.setPreferredSize(new Dimension(1, 56));
        JLabel hintLabel = new JLabel("Select an employee type above to enter salary details.");
        hintLabel.setForeground(Color.GRAY);
        noneCard.add(hintLabel);

        JPanel fullTime = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        addRow(fullTime, c, 0, "Basic Salary (EGP):", basicSalaryField);
        addRow(fullTime, c, 1, "Bonus (EGP):",        bonusField);

        JPanel partTime = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.insets = new Insets(4, 6, 4, 6);
        c2.anchor = GridBagConstraints.WEST;
        c2.fill = GridBagConstraints.HORIZONTAL;
        addRow(partTime, c2, 0, "Hourly Rate (EGP):", hourlyRateField);
        addRow(partTime, c2, 1, "Hours Worked:",      hoursWorkedField);

        salaryCards.setBorder(BorderFactory.createTitledBorder("Salary"));
        salaryCards.add(noneCard,    "NONE");
        salaryCards.add(fullTime,    EmployeeType.FULL_TIME.name());
        salaryCards.add(partTime,    EmployeeType.PART_TIME.name());
        return salaryCards;
    }

    private void showSalaryCard() {
        EmployeeType t = (EmployeeType) typeCombo.getSelectedItem();
        if (t == null) {
            salaryLayout.show(salaryCards, "NONE");
            setSalaryFieldsEnabled(false);
        } else {
            salaryLayout.show(salaryCards, t.name());
            setSalaryFieldsEnabled(true);
        }
    }

    private void setSalaryFieldsEnabled(boolean enabled) {
        basicSalaryField.setEnabled(enabled);
        bonusField.setEnabled(enabled);
        hourlyRateField.setEnabled(enabled);
        hoursWorkedField.setEnabled(enabled);
    }

    public void reloadDepartments() {
        Department previous = (Department) departmentCombo.getSelectedItem();
        int previousId = (previous == null) ? -1 : previous.getDepartmentId();

        departmentCombo.removeAllItems();
        List<Department> departments = (departmentService == null)
                ? Collections.emptyList()
                : departmentService.getDepartments();
        for (Department d : departments) {
            departmentCombo.addItem(d);
        }
        if (previousId > 0) {
            selectDepartmentById(previousId);
        }else {
            departmentCombo.setSelectedIndex(-1); // nothing selected by default
        }
    }

    private void selectDepartmentById(int departmentId) {
        for (int i = 0; i < departmentCombo.getItemCount(); i++) {
            Department d = departmentCombo.getItemAt(i);
            if (d != null && d.getDepartmentId() == departmentId) {
                departmentCombo.setSelectedIndex(i);
                return;
            }
        }
        departmentCombo.setSelectedIndex(-1);
    }

    public void populate(Employee employee) {
        if (employee == null){
            clearForm();
            return;
        }

        reloadDepartments();
        idField.setText(String.valueOf(employee.getEmployeeId()));
        nameField.setText(employee.getFullName());
        ageField.setText(String.valueOf(employee.getAge()));
        positionField.setText(employee.getPosition());
        selectDepartmentById(employee.getDepartmentId());

        basicSalaryField.setText("");
        bonusField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");

        if (employee instanceof FullTimeEmployee f) {
            typeCombo.setSelectedItem(EmployeeType.FULL_TIME);
            basicSalaryField.setText(String.valueOf(f.getBasicSalary()));
            bonusField.setText(String.valueOf(f.getBonus()));
        } else if (employee instanceof PartTimeEmployee p) {
            typeCombo.setSelectedItem(EmployeeType.PART_TIME);
            hourlyRateField.setText(String.valueOf(p.getHourlyRate()));
            hoursWorkedField.setText(String.valueOf(p.getHoursWorked()));
        }
        showSalaryCard();
    }

    public void clearForm() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        positionField.setText("");
        basicSalaryField.setText("");
        bonusField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");
        typeCombo.setSelectedIndex(-1);
        departmentCombo.setSelectedIndex(-1);
        showSalaryCard();
    }

    public void setIdFieldText(String text) {
        idField.setText(text);
    }

    public void setIdEditable(boolean editable) {
        idField.setEditable(editable);
    }

    public void setFormEnabled(boolean enabled) {
        idField.setEnabled(enabled);
        nameField.setEnabled(enabled);
        ageField.setEnabled(enabled);
        positionField.setEnabled(enabled);
        departmentCombo.setEnabled(enabled);
        typeCombo.setEnabled(enabled);
        basicSalaryField.setEnabled(enabled);
        bonusField.setEnabled(enabled);
        hourlyRateField.setEnabled(enabled);
        hoursWorkedField.setEnabled(enabled);
        salaryCards.setEnabled(enabled);
        if (enabled){
            showSalaryCard();
        }else{
            setSalaryFieldsEnabled(false);
        }
    }

    public EmployeeFormData readFormData() {
        Department selected = (Department) departmentCombo.getSelectedItem();
        EmployeeType type = (EmployeeType) typeCombo.getSelectedItem();

        EmployeeFormData data = new EmployeeFormData(
                idField.getText(),
                nameField.getText(),
                ageField.getText(),
                positionField.getText(),
                selected,
                type,
                Collections.emptyMap());

        if (type == EmployeeType.FULL_TIME) {
            data.setBasicSalary(basicSalaryField.getText());
            data.setBonus(bonusField.getText());
        } else if (type == EmployeeType.PART_TIME) {
            data.setHourlyRate(hourlyRateField.getText());
            data.setHoursWorked(hoursWorkedField.getText());
        }

        return data;
    }
}
