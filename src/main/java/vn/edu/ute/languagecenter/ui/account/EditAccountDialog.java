package vn.edu.ute.languagecenter.ui.account;

import vn.edu.ute.languagecenter.model.UserAccount;
import vn.edu.ute.languagecenter.model.UserRole;
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

public class EditAccountDialog extends JDialog {

    private final UserAccountService userAccountService;
    private final UserAccount account;

    private final JTextField txtUsername = new JTextField(20);
    private final JComboBox<UserRole> comboRole = new JComboBox<>(UserRole.values());
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
    private final JPasswordField txtNewPassword = new JPasswordField(20);

    public EditAccountDialog(Frame owner, UserAccountService userAccountService, UserAccount account) {
        super(owner, "Chỉnh sửa tài khoản", true);
        this.userAccountService = userAccountService;
        this.account = account;
        buildUI();
        loadAccount();
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
        panel.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        txtUsername.setEditable(false);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Role"), gbc);
        gbc.gridx = 1;
        panel.add(comboRole, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Status"), gbc);
        gbc.gridx = 1;
        panel.add(comboStatus, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Mật khẩu mới (để trống = giữ nguyên)"), gbc);
        gbc.gridx = 1;
        panel.add(txtNewPassword, gbc);

        JButton btnSave = new JButton("Lưu");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(btnSave, gbc);

        add(panel, BorderLayout.CENTER);
        btnSave.addActionListener(e -> onSave());
    }

    private void loadAccount() {
        txtUsername.setText(account.getUsername());
        UserRole role = null;
        String value = account.getRole();
        if (value != null) {
            try {
                role = UserRole.valueOf(value);
            } catch (IllegalArgumentException ignored) {
            }
        }
        comboRole.setSelectedItem(role != null ? role : UserRole.STAFF);
        comboStatus.setSelectedItem(account.getStatus() != null ? account.getStatus() : "ACTIVE");
    }

    private void onSave() {
        try {
            UserRole role = (UserRole) comboRole.getSelectedItem();
            account.setRole(role != null ? role.name() : null);
            account.setStatus((String) comboStatus.getSelectedItem());
            userAccountService.update(account);
            char[] pw = txtNewPassword.getPassword();
            if (pw != null && pw.length > 0) {
                userAccountService.setPassword(account.getId(), new String(pw));
            }
            JOptionPane.showMessageDialog(this, "Đã lưu.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
