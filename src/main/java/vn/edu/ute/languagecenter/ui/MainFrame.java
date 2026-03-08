package vn.edu.ute.languagecenter.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {

    private static final String CARD_DASHBOARD = "dashboard";
    private static final String CARD_STUDENT = "student";
    private static final String CARD_TEACHER = "teacher";
    private static final String CARD_COURSE = "course";
    private static final String CARD_STAFF = "staff";
    private static final String CARD_ACCOUNT = "account";
    private static final String CARD_ACCOUNT_MGMT = "account_mgmt";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final String displayName;
    private final String role;

    public MainFrame(String displayName, String role) {
        super("Language Center Management (Swing + JPA + MySQL)");
        this.displayName = displayName;
        this.role = role;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setJMenuBar(buildMenuBar());
        add(buildToolBar(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem miLogout = new JMenuItem("Đăng xuất");
        JMenuItem miExit = new JMenuItem("Thoát");
        miLogout.addActionListener(e -> doLogout());
        miExit.addActionListener(e -> doExit());
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
        JButton btnAddStudent = new JButton("Thêm học viên");
        JButton btnOpenClass = new JButton("Mở lớp");
        JButton btnQuickAttendance = new JButton("Điểm danh nhanh");
        JTextField txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm");
        JButton btnToggleTheme = new JButton("Dark/Light");

        btnHome.addActionListener(e -> showCard(CARD_DASHBOARD));
        btnAddStudent.addActionListener(e -> showCard(CARD_STUDENT));

        tb.add(btnHome);
        tb.addSeparator();
        tb.add(btnAddStudent);
        tb.add(btnOpenClass);
        tb.add(btnQuickAttendance);
        tb.addSeparator();
        tb.add(txtSearch);
        tb.add(btnSearch);
        tb.addSeparator();
        tb.add(btnToggleTheme);

        return tb;
    }

    private JPanel buildSidebar() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblLogo = new JLabel("Language Center");
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);
        lblLogo.setFont(lblLogo.getFont().deriveFont(Font.BOLD, 16f));

        JLabel lblUser = new JLabel(displayName + " - " + role);
        lblUser.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(lblLogo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createNavButton("Dashboard", CARD_DASHBOARD));
        panel.add(createNavButton("Học viên", CARD_STUDENT));
        panel.add(createNavButton("Giáo viên", CARD_TEACHER));
        panel.add(createNavButton("Khóa học & Lớp học", CARD_COURSE));
        if (isAdminRole()) {
            panel.add(createNavButton("Nhân viên & Tài khoản", CARD_STAFF));
        }
        if (isSuperAdmin()) {
            panel.add(createNavButton("Quản lý tài khoản", CARD_ACCOUNT_MGMT));
        }
        panel.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setAlignmentX(CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> doLogout());
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));
        panel.add(btnLogout);

        return panel;
    }

    private JPanel buildMainContent() {
        DashboardPanel dashboard = new DashboardPanel();
        StudentPanel studentPanel = new StudentPanel();
        TeacherPanel teacherPanel = new TeacherPanel();
        CoursePanel coursePanel = new CoursePanel();
        StaffPanel staffPanel = new StaffPanel();
        UserAccountPanel accountPanel = new UserAccountPanel();
        AccountManagementPanel accountMgmtPanel = new AccountManagementPanel();

        cardPanel.add(dashboard, CARD_DASHBOARD);
        cardPanel.add(studentPanel, CARD_STUDENT);
        cardPanel.add(teacherPanel, CARD_TEACHER);
        cardPanel.add(coursePanel, CARD_COURSE);
        cardPanel.add(staffPanel, CARD_STAFF);
        cardPanel.add(accountPanel, CARD_ACCOUNT);
        cardPanel.add(accountMgmtPanel, CARD_ACCOUNT_MGMT);

        showCard(CARD_DASHBOARD);
        return cardPanel;
    }

    private void showCard(String name) {
        cardLayout.show(cardPanel, name);
    }

    private JButton createNavButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.addActionListener(e -> showCard(card));
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

    private boolean isSuperAdmin() {
        if (role == null) return false;
        return "SUPER_ADMIN".equalsIgnoreCase(role);
    }

    private void doLogout() {
        int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void doExit() {
        int c = JOptionPane.showConfirmDialog(this, "Thoát ứng dụng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}

