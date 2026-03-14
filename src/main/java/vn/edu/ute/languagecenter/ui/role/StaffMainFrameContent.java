package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.ui.frame.MainFrameContent;
import vn.edu.ute.languagecenter.ui.dashboard.DashboardPanel;
import vn.edu.ute.languagecenter.ui.student.StudentPanel;
import vn.edu.ute.languagecenter.ui.teacher.TeacherPanel;
import vn.edu.ute.languagecenter.ui.course.CourseAndClassPanel;
import vn.edu.ute.languagecenter.ui.room.RoomPanel;
import vn.edu.ute.languagecenter.ui.room.RoomSchedulePanel;
import vn.edu.ute.languagecenter.ui.room.RoomScheduleAndListPanel;
import vn.edu.ute.languagecenter.ui.staff.StaffPanel;
import vn.edu.ute.languagecenter.ui.account.UserAccountPanel;
import vn.edu.ute.languagecenter.ui.account.AccountManagementPanel;

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
import javax.swing.JTextField;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StaffMainFrameContent implements MainFrameContent {

    private static final String CARD_DASHBOARD = "dashboard";
    private static final String CARD_STUDENT = "student";
    private static final String CARD_TEACHER = "teacher";
    private static final String CARD_COURSE = "course";
    private static final String CARD_ROOM = "room";
    private static final String CARD_ROOM_SCHEDULE = "room_schedule";
    private static final String CARD_ENROLLMENT = "enrollment";
    private static final String CARD_INVOICE = "invoice";
    private static final String CARD_STAFF = "staff";
    private static final String CARD_ACCOUNT = "account";
    private static final String CARD_ACCOUNT_MGMT = "account_mgmt";

    private final String displayName;
    private final String role;
    private final boolean includeAccountMgmt;
    private Runnable logoutCallback;
    private Runnable exitCallback;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel sidebar;

    public StaffMainFrameContent(String displayName, String role, boolean includeAccountMgmt) {
        this.displayName = displayName;
        this.role = role;
        this.includeAccountMgmt = includeAccountMgmt;
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
        if (cardPanel.getComponentCount() == 0) {
            buildCards();
        }
        return cardPanel;
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
        miLogout.addActionListener(e -> runLogout());
        miExit.addActionListener(e -> {
            if (exitCallback != null) exitCallback.run();
        });
        file.add(miLogout);
        file.add(miExit);

        JMenu manage = new JMenu("Quản lý");
        manage.add(new JMenuItem("Học viên"));
        manage.add(new JMenuItem("Giáo viên"));
        manage.add(new JMenuItem("Khóa học"));
        manage.add(new JMenuItem("Lớp học"));
        manage.add(new JMenuItem("Phòng học"));

        JMenu finance = new JMenu("Tài chính");
        finance.add(new JMenuItem("Thanh toán"));
        finance.add(new JMenuItem("Hóa đơn"));
        finance.add(new JMenuItem("Báo cáo doanh thu"));

        JMenu academic = new JMenu("Học thuật");
        academic.add(new JMenuItem("Điểm danh"));
        academic.add(new JMenuItem("Kết quả"));
        academic.add(new JMenuItem("Chứng chỉ"));

        JMenu system = new JMenu("Hệ thống");
        system.add(new JMenuItem("Nhân viên"));
        system.add(new JMenuItem("Tài khoản"));
        system.add(new JMenuItem("Cài đặt"));
        system.add(new JMenuItem("Backup"));

        JMenu help = new JMenu("Trợ giúp");
        help.add(new JMenuItem("Hướng dẫn"));
        help.add(new JMenuItem("Về phần mềm"));

        bar.add(file);
        bar.add(manage);
        bar.add(finance);
        bar.add(academic);
        if (isAdminRole()) {
            bar.add(system);
        }
        bar.add(help);
        return bar;
    }

    private JToolBar buildToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        JButton btnHome = new JButton("Dashboard");
        JButton btnOpenClass = new JButton("Mở lớp");
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm");
        JButton btnToggleTheme = new JButton("Dark/Light");
        btnHome.addActionListener(e -> {
            setSidebarVisible(true);
            showCard(CARD_DASHBOARD);
        });
        btnOpenClass.addActionListener(e -> {
            showCard(CARD_COURSE);
            setSidebarVisible(false);
        });
        tb.add(btnHome);
        tb.addSeparator();
        tb.add(btnOpenClass);
        tb.addSeparator();
        tb.add(txtSearch);
        tb.add(btnSearch);
        tb.addSeparator();
        tb.add(btnToggleTheme);
        return tb;
    }

    private void setSidebarVisible(boolean visible) {
        if (sidebar != null) {
            sidebar.setVisible(visible);
            if (sidebar.getParent() != null) {
                sidebar.getParent().revalidate();
                sidebar.getParent().repaint();
            }
        }
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

        panel.add(createNavButton("Dashboard", CARD_DASHBOARD));
        panel.add(createNavButton("Học viên", CARD_STUDENT));
        panel.add(createNavButton("Giáo viên", CARD_TEACHER));
        panel.add(createNavButton("Khóa học & Lớp học", CARD_COURSE));
        panel.add(createNavButton("Đăng ký lớp", CARD_ENROLLMENT));
        panel.add(createNavButton("Hóa đơn", CARD_INVOICE));
        panel.add(createNavButton("Lịch phòng", CARD_ROOM_SCHEDULE));
        if (isAdminRole()) {
            panel.add(createNavButton("Phòng học", CARD_ROOM));
        }
        if (isAdminRole()) {
            panel.add(createNavButton("Nhân viên & Tài khoản", CARD_STAFF));
        }
        if (includeAccountMgmt) {
            panel.add(createNavButton("Quản lý tài khoản", CARD_ACCOUNT_MGMT));
        }
        panel.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setAlignmentX(JButton.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> runLogout());
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLogout);
        return panel;
    }

    private void buildCards() {
        cardPanel.add(new DashboardPanel(), CARD_DASHBOARD);
        cardPanel.add(new StudentPanel(), CARD_STUDENT);
        cardPanel.add(new TeacherPanel(), CARD_TEACHER);
        cardPanel.add(new CourseAndClassPanel(role), CARD_COURSE);
        cardPanel.add(new EnrollmentManagementPanel(), CARD_ENROLLMENT);
        cardPanel.add(new AdminInvoicesPanel(), CARD_INVOICE);
        cardPanel.add(new RoomScheduleAndListPanel(role), CARD_ROOM_SCHEDULE);
        cardPanel.add(new RoomPanel(role), CARD_ROOM);
        cardPanel.add(new StaffPanel(), CARD_STAFF);
        cardPanel.add(new UserAccountPanel(), CARD_ACCOUNT);
        cardPanel.add(new AccountManagementPanel(), CARD_ACCOUNT_MGMT);
        showCard(CARD_DASHBOARD);
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    private JButton createNavButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(JButton.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.addActionListener(e -> {
            setSidebarVisible(true);
            showCard(card);
        });
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(230, 230, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    private boolean isAdminRole() {
        if (role == null) return false;
        String v = role.toUpperCase();
        return v.equals("ADMIN") || v.equals("SUPER_ADMIN");
    }

    private void runLogout() {
        if (logoutCallback != null) {
            logoutCallback.run();
        }
    }
}
