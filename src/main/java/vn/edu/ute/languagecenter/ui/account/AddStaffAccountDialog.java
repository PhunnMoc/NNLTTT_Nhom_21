package vn.edu.ute.languagecenter.ui.account;

import vn.edu.ute.languagecenter.model.Staff;
import vn.edu.ute.languagecenter.model.UserRole;
import vn.edu.ute.languagecenter.service.StaffService;
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
import java.util.List;

public class AddStaffAccountDialog extends JDialog {

    private final UserAccountService userAccountService;
    private final StaffService staffService = new StaffService();

    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JComboBox<UserRole> comboRole = new JComboBox<>(new UserRole[]{UserRole.STAFF, UserRole.CONSULTANT, UserRole.ACCOUNTANT, UserRole.ADMIN});
    private final JComboBox<Staff> comboStaff = new JComboBox<>();

    public AddStaffAccountDialog(Frame owner, UserAccountService userAccountService) {
        super(owner, "Thêm tài khoản Staff", true);
        this.userAccountService = userAccountService;
        buildUI();
        loadStaffList();
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
        panel.add(new JLabel("Nhân viên"), gbc);
        gbc.gridx = 1;
        panel.add(comboStaff, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Role"), gbc);
        gbc.gridx = 1;
        panel.add(comboRole, gbc);

        JButton btnCreate = new JButton("Tạo tài khoản");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(btnCreate, gbc);

        add(panel, BorderLayout.CENTER);
        btnCreate.addActionListener(e -> onCreate());
    }

    private void loadStaffList() {
        comboStaff.removeAllItems();
        try {
            List<Staff> list = staffService.findAll();
            for (Staff s : list) {
                comboStaff.addItem(s);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onCreate() {
        String username = txtUsername.getText().trim();
        char[] pw = txtPassword.getPassword();
        if (username.isEmpty() || pw == null || pw.length == 0) {
            JOptionPane.showMessageDialog(this, "Nhập username và password.");
            return;
        }
        Staff staff = (Staff) comboStaff.getSelectedItem();
        Long relatedId = staff != null ? staff.getId() : null;
        UserRole role = (UserRole) comboRole.getSelectedItem();
        try {
            userAccountService.createWithRawPassword(username, new String(pw), role != null ? role.name() : null, relatedId);
            JOptionPane.showMessageDialog(this, "Đã tạo tài khoản.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
