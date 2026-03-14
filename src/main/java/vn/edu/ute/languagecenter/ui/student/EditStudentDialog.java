package vn.edu.ute.languagecenter.ui.student;

import vn.edu.ute.languagecenter.model.Level;
import vn.edu.ute.languagecenter.model.Student;
import vn.edu.ute.languagecenter.service.StudentService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class EditStudentDialog extends JDialog {

    private final StudentService studentService;
    private final Student student;

    private final JTextField txtFullName = new JTextField(20);
    private final JTextField txtPhone = new JTextField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JComboBox<Level> cmbLevel = new JComboBox<>(Level.values());
    private final JTextField txtStatus = new JTextField(20);

    public EditStudentDialog(Frame owner, StudentService studentService, Student student) {
        super(owner, "Chỉnh sửa học viên", true);
        this.studentService = studentService;
        this.student = student;
        fillForm();
        buildUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void fillForm() {
        txtFullName.setText(student.getFullName());
        txtPhone.setText(student.getPhone() != null ? student.getPhone() : "");
        txtEmail.setText(student.getEmail() != null ? student.getEmail() : "");
        txtStatus.setText(student.getStatus() != null ? student.getStatus() : "");
        String levelStr = student.getLevel();
        if (levelStr != null && !levelStr.isBlank()) {
            try {
                cmbLevel.setSelectedItem(Level.valueOf(levelStr.trim()));
            } catch (IllegalArgumentException ignored) {
            }
        }
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
        panel.add(new JLabel("Trạng thái"), gbc);
        gbc.gridx = 1;
        panel.add(txtStatus, gbc);

        JButton btnSave = new JButton("Lưu");
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(btnSave, gbc);

        add(panel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> onSave());
    }

    private void onSave() {
        String fullName = txtFullName.getText().trim();
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên.");
            return;
        }
        try {
            student.setFullName(fullName);
            student.setPhone(txtPhone.getText().trim());
            student.setEmail(txtEmail.getText().trim());
            student.setLevel(((Level) cmbLevel.getSelectedItem()).name());
            student.setStatus(txtStatus.getText().trim());
            studentService.update(student);
            JOptionPane.showMessageDialog(this, "Đã lưu.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
