package vn.edu.ute.languagecenter.ui.frame;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

public interface MainFrameContent {

    JMenuBar getMenuBar();

    JToolBar getToolBar();

    JPanel getSidebar();

    JPanel getMainContent();

    void setLogoutCallback(Runnable onLogout);

    void setExitCallback(Runnable onExit);
}
