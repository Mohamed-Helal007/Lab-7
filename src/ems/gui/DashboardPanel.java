package ems.gui;
import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel{
    private final Navigator navigator;
    public DashboardPanel(Navigator navigator){
        this.navigator=navigator;
        intializeUI();
    }
    private void intializeUI(){
        setLayout(new BorderLayout());
        JLabel title =new JLabel("Employee Managment System");
        title.setFont( new Font("Arial",Font.BOLD,22));
        
        add(title,BorderLayout.NORTH);
        JPanel buttonPanel =new JPanel(new GridLayout(5,1));
        
        JButton addEmployeeButton =new JButton("Add Employee");
        JButton viewEmployeesButton =new JButton("View Employee");
        JButton searchButton =new JButton("Search/Update");
        JButton departmentsButton=new JButton("Departments");
        
        JButton logoutButton =new JButton("Logout");
        addEmployeeButton.addActionListener(e ->navigator.showScreen(Navigator.SCREEN_ADD_EMPLOYEE));
        viewEmployeesButton.addActionListener(e ->navigator.showScreen(Navigator.SCREEN_VIEW_EMPLOYEES));
        searchButton.addActionListener(e ->navigator.showScreen(Navigator.SCREEN_SEARCH_UPDATE));
        departmentsButton.addActionListener(e ->navigator.showScreen(Navigator.SCREEN_DEPARTMENTS));
        logoutButton.addActionListener(e ->navigator.logout());

        buttonPanel.add(addEmployeeButton);
        buttonPanel.add(viewEmployeesButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(departmentsButton);
        buttonPanel.add(logoutButton);
        add(buttonPanel,BorderLayout.CENTER);

    }

}
