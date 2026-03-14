package vn.edu.ute.languagecenter.ui.student;

import vn.edu.ute.languagecenter.model.Level;
import vn.edu.ute.languagecenter.model.Student;
import vn.edu.ute.languagecenter.service.StudentService;
import vn.edu.ute.languagecenter.service.UserAccountService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class StudentRegistrationDialog extends JDialog {

    private final StudentService studentService;
    private final UserAccountService userAccountService;

    private final JTextField txtFullName = new JTextField(20);
    private final JTextField txtPhone = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JComboBox<Level> cmbLevel = new JComboBox<>(Level.values());
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);

    public StudentRegistrationDialog(Frame owner, StudentService studentService, UserAccountService userAccountService) {
        super(owner, "Đăng ký Student", true);
        this.studentService = studentService;
        this.userAccountService = userAccountService;
        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Họ tên"), gbc);
        gbc.gridx = 1;
        panel.add(txtFullName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Điện thoại"), gbc);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Trình độ"), gbc);
        gbc.gridx = 1;
        panel.add(cmbLevel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        JButton btnRegister = new JButton("Đăng ký");
        gbc.gridx = 1;
        gbc.gridy = 6;
        panel.add(btnRegister, gbc);

        add(panel, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> onRegister());
    }

    private void onRegister() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        char[] password = txtPassword.getPassword();
        if (fullName.isEmpty() || username.isEmpty() || password.length == 0) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên, username, password");
            return;
        }
        try {
            Student s = new Student();
            s.setFullName(fullName);
            s.setPhone(txtPhone.getText().trim());
            s.setEmail(txtEmail.getText().trim());
            s.setLevel(((Level) cmbLevel.getSelectedItem()).name());
            s.setStatus("ACTIVE");
            Student saved = studentService.create(s);
            userAccountService.createWithRawPassword(username, new String(password), "STUDENT", saved.getId());
            JOptionPane.showMessageDialog(this, "Đăng ký Student thành công");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
