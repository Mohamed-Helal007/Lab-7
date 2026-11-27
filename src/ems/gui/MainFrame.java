package ems.gui;
import ems.service.DepartmentService;
import javax.swing.*;
import java.awt.*;
public class MainFrame extends JFrame implements Navigator {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;
    public MainFrame(DepartmentService departmentService){
        setTitle("Employee Managment System");
        setSize(900,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout =new CardLayout();
        cardPanel =new JPanel(cardLayout);
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        DepartmentPanel departmentPanel= new DepartmentPanel(departmentService);
        cardPanel.add(dashboardPanel,SCREEN_DASHBOARD);
        cardPanel.add(departmentPanel,SCREEN_DEPARTMENTS);
        add(cardPanel);
        showScreen(SCREEN_DASHBOARD);     
    }
    @Override 
    public void showScreen(String screenName){
        Component[] components=cardPanel.getComponents();
        for(Component component:components){
            if(component instanceof Refreshable){
                ((Refreshable)component).refreshData();
            }
        }
        cardLayout.show(cardPanel,screenName);
    }
    @Override 
    public void logout(){
        JOptionPane.showMessageDialog(this, "Logout clicked");
    }


}
