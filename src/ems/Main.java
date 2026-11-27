package ems;

import ems.gui.MainFrame;
import ems.repository.CsvDepartmentRepository;
import ems.repository.DepartmentRepository;
import ems.service.DepartmentService;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                DepartmentRepository repository =
                        new CsvDepartmentRepository(
                                "data/departments.csv"
                        );

                DepartmentService departmentService =
                        new DepartmentService(
                                repository
                        );

                departmentService.loadDepartments();

                MainFrame frame =
                        new MainFrame(
                                departmentService
                        );

                frame.setVisible(true);

            } catch (Exception e) {

                e.printStackTrace();
            }
        });
    }
}