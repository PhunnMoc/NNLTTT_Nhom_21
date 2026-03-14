package vn.edu.ute.languagecenter.ui.frame;

import vn.edu.ute.languagecenter.model.UserAccount;
import vn.edu.ute.languagecenter.model.UserRole;
import vn.edu.ute.languagecenter.service.StudentService;
import vn.edu.ute.languagecenter.service.TeacherService;
import vn.edu.ute.languagecenter.service.UserAccountService;
import vn.edu.ute.languagecenter.ui.student.StudentRegistrationDialog;
import vn.edu.ute.languagecenter.ui.teacher.TeacherRegistrationDialog;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginFrame extends JFrame {

    private final UserAccountService userAccountService = new UserAccountService();
    private final TeacherService teacherService = new TeacherService();
    private final StudentService studentService = new StudentService();

    private final JTextField adminUsername = new JTextField(20);
    private final JPasswordField adminPassword = new JPasswordField(20);
    private final JCheckBox adminRemember = new JCheckBox("Nhớ đăng nhập");

    private final JTextField teacherUsername = new JTextField(20);
    private final JPasswordField teacherPassword = new JPasswordField(20);

    private final JTextField studentUsername = new JTextField(20);
    private final JPasswordField studentPassword = new JPasswordField(20);

    private final JTextField staffUsername = new JTextField(20);
    private final JPasswordField staffPassword = new JPasswordField(20);

    public LoginFrame() {
        super("Đăng nhập hệ thống");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildUI();
        pack();
        setMinimumSize(new Dimension(500, 350));
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JLabel lblTitle = new JLabel("Language Center MIS");
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Admin", buildAdminTab());
        tabs.addTab("Staff", buildStaffTab());
        tabs.addTab("Teacher", buildTeacherTab());
        tabs.addTab("Student", buildStudentTab());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildAdminTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        panel.add(adminUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(adminPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(adminRemember, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> onAdminLogin());
        return panel;
    }

    private JPanel buildTeacherTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        panel.add(teacherUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(teacherPassword, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        JButton btnRegister = new JButton("Đăng ký tài khoản Teacher");
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnRegister, gbc);

        btnLogin.addActionListener(e -> onTeacherLogin());
        btnRegister.addActionListener(e -> openTeacherRegistration());
        return panel;
    }

    private JPanel buildStaffTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        panel.add(staffUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(staffPassword, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> onStaffLogin());
        return panel;
    }

    private JPanel buildStudentTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        panel.add(studentUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(studentPassword, gbc);

        JButton btnLogin = new JButton("Đăng nhập");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(btnLogin, gbc);

        JButton btnRegister = new JButton("Đăng ký tài khoản Student");
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(btnRegister, gbc);

        btnLogin.addActionListener(e -> onStudentLogin());
        btnRegister.addActionListener(e -> openStudentRegistration());
        return panel;
    }

    private void onAdminLogin() {
        String username = adminUsername.getText().trim();
        char[] password = adminPassword.getPassword();
        if (username.isEmpty() || password.length == 0) {
            return;
        }
        UserAccount account = userAccountService.findByUsername(username);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        if (!isAdminRole(account.getRole())) {
            JOptionPane.showMessageDialog(this, "Tài khoản không có quyền Admin");
            return;
        }
        if (userAccountService.isLocked(account)) {
            JOptionPane.showMessageDialog(this, "Tài khoản đang bị khóa tạm thời");
            return;
        }
        if (!userAccountService.verifyPassword(account, new String(password))) {
            userAccountService.recordFailedLogin(account, 5, 15);
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        userAccountService.recordSuccessfulLogin(account);
        MainFrame main = new MainFrame(account.getUsername(), account.getRole(), MainFrameContentFactory.create(account));
        main.setVisible(true);
        dispose();
    }

    private void onTeacherLogin() {
        String username = teacherUsername.getText().trim();
        char[] password = teacherPassword.getPassword();
        if (username.isEmpty() || password.length == 0) {
            return;
        }
        UserAccount account = userAccountService.findByUsername(username);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        if (!"TEACHER".equalsIgnoreCase(account.getRole())) {
            JOptionPane.showMessageDialog(this, "Tài khoản không phải Teacher");
            return;
        }
        if (userAccountService.isLocked(account)) {
            JOptionPane.showMessageDialog(this, "Tài khoản đang bị khóa tạm thời");
            return;
        }
        if (!userAccountService.verifyPassword(account, new String(password))) {
            userAccountService.recordFailedLogin(account, 5, 15);
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        userAccountService.recordSuccessfulLogin(account);
        MainFrame main = new MainFrame(account.getUsername(), account.getRole(), MainFrameContentFactory.create(account));
        main.setVisible(true);
        dispose();
    }

    private void onStaffLogin() {
        String username = staffUsername.getText().trim();
        char[] password = staffPassword.getPassword();
        if (username.isEmpty() || password.length == 0) {
            return;
        }
        UserAccount account = userAccountService.findByUsername(username);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        if (!UserRole.isStaffTabRole(account.getRole())) {
            JOptionPane.showMessageDialog(this, "Tài khoản không thuộc nhóm Staff");
            return;
        }
        if (userAccountService.isLocked(account)) {
            JOptionPane.showMessageDialog(this, "Tài khoản đang bị khóa tạm thời");
            return;
        }
        if (!userAccountService.verifyPassword(account, new String(password))) {
            userAccountService.recordFailedLogin(account, 5, 15);
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        userAccountService.recordSuccessfulLogin(account);
        MainFrame main = new MainFrame(account.getUsername(), account.getRole(), MainFrameContentFactory.create(account));
        main.setVisible(true);
        dispose();
    }

    private void onStudentLogin() {
        String username = studentUsername.getText().trim();
        char[] password = studentPassword.getPassword();
        if (username.isEmpty() || password.length == 0) {
            return;
        }
        UserAccount account = userAccountService.findByUsername(username);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        if (!"STUDENT".equalsIgnoreCase(account.getRole())) {
            JOptionPane.showMessageDialog(this, "Tài khoản không phải Student");
            return;
        }
        if (userAccountService.isLocked(account)) {
            JOptionPane.showMessageDialog(this, "Tài khoản đang bị khóa tạm thời");
            return;
        }
        if (!userAccountService.verifyPassword(account, new String(password))) {
            userAccountService.recordFailedLogin(account, 5, 15);
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu");
            return;
        }
        userAccountService.recordSuccessfulLogin(account);
        MainFrame main = new MainFrame(account.getUsername(), account.getRole(), MainFrameContentFactory.create(account));
        main.setVisible(true);
        dispose();
    }

    private void openTeacherRegistration() {
        TeacherRegistrationDialog dlg = new TeacherRegistrationDialog(this, teacherService, userAccountService);
        dlg.setVisible(true);
    }

    private void openStudentRegistration() {
        StudentRegistrationDialog dlg = new StudentRegistrationDialog(this, studentService, userAccountService);
        dlg.setVisible(true);
    }

    private boolean isAdminRole(String r) {
        if (r == null) return false;
        String v = r.toUpperCase();
        return v.equals("ADMIN") || v.equals("SUPER_ADMIN");
    }
}
