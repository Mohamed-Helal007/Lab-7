package ems.gui;

public interface Navigator {
    String SCREEN_LOGIN = "login";

    String SCREEN_DASHBOARD = "dashboard";

    String SCREEN_ADD_EMPLOYEE ="addEmployee";

    String SCREEN_VIEW_EMPLOYEES ="viewEmployees";

    String SCREEN_SEARCH_UPDATE ="searchUpdate";

    String SCREEN_DEPARTMENTS ="departments";

    void showScreen(String screenName);

    void logout();

}
