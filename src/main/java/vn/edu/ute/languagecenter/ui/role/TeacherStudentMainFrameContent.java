package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.ui.frame.MainFrameContent;
import vn.edu.ute.languagecenter.ui.courseclass.MyClassesPanel;
import vn.edu.ute.languagecenter.ui.room.RoomSchedulePanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import java.awt.Dimension;
import java.awt.Font;

public class TeacherStudentMainFrameContent implements MainFrameContent {

    private final String displayName;
    private final String role;
    private final Long relatedId;
    private final boolean isTeacher;
    private Runnable logoutCallback;
    private Runnable exitCallback;

    private final JPanel mainPanel = new JPanel();
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel sidebar;

    public TeacherStudentMainFrameContent(String displayName, String role, Long relatedId, boolean isTeacher) {
        this.displayName = displayName;
        this.role = role;
        this.relatedId = relatedId;
        this.isTeacher = isTeacher;
    }

    @Override
    public JMenuBar getMenuBar() {
        if (menuBar == null) {
            menuBar = buildMenuBar();
        }
        return menuBar;
    }

    @Override
    public JToolBar getToolBar() {
        if (toolBar == null) {
            toolBar = buildToolBar();
        }
        return toolBar;
    }

    @Override
    public JPanel getSidebar() {
        if (sidebar == null) {
            sidebar = buildSidebar();
        }
        return sidebar;
    }

    @Override
    public JPanel getMainContent() {
        if (mainPanel.getComponentCount() == 0) {
            mainPanel.setLayout(new java.awt.BorderLayout());
            if (isTeacher) {
                javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
                tabs.addTab("Khóa học đang dạy", new MyCoursesPanel(relatedId, true));
                tabs.addTab("Lớp đã phân công", new MyClassesPanel(relatedId));
                tabs.addTab("Lịch phòng", new RoomSchedulePanel());
                mainPanel.add(tabs, java.awt.BorderLayout.CENTER);
            } else {
                javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
                tabs.addTab("Đăng ký khóa học", new CourseRegistrationPanel(relatedId));
                tabs.addTab("Khóa học đã đăng ký", new StudentEnrollmentsPanel(relatedId));
                tabs.addTab("Hóa đơn", new StudentInvoicesPanel(relatedId));
                tabs.addTab("Lịch phòng", new RoomSchedulePanel());
                mainPanel.add(tabs, java.awt.BorderLayout.CENTER);
            }
        }
        return mainPanel;
    }

    @Override
    public void setLogoutCallback(Runnable onLogout) {
        this.logoutCallback = onLogout;
    }

    @Override
    public void setExitCallback(Runnable onExit) {
        this.exitCallback = onExit;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem miLogout = new JMenuItem("Đăng xuất");
        JMenuItem miExit = new JMenuItem("Thoát");
        miLogout.addActionListener(e -> {
            if (logoutCallback != null) logoutCallback.run();
        });
        miExit.addActionListener(e -> {
            if (exitCallback != null) exitCallback.run();
        });
        file.add(miLogout);
        file.add(miExit);
        JMenu help = new JMenu("Trợ giúp");
        help.add(new JMenuItem("Hướng dẫn"));
        help.add(new JMenuItem("Về phần mềm"));
        bar.add(file);
        bar.add(help);
        return bar;
    }

    private JToolBar buildToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        JButton btnMyCourses = new JButton(isTeacher ? "Khóa học đang dạy" : "Khóa học đang học");
        btnMyCourses.addActionListener(e -> {});
        tb.add(btnMyCourses);
        return tb;
    }

    private JPanel buildSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblLogo = new JLabel("Language Center");
        lblLogo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        lblLogo.setFont(lblLogo.getFont().deriveFont(Font.BOLD, 16f));
        JLabel lblUser = new JLabel(displayName + " - " + role);
        lblUser.setAlignmentX(JLabel.CENTER_ALIGNMENT);

        panel.add(lblLogo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(20));
        panel.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> {
            if (logoutCallback != null) logoutCallback.run();
        });
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLogout);
        return panel;
    }
}
