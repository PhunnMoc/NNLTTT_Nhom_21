package vn.edu.ute.languagecenter.ui.account;

import vn.edu.ute.languagecenter.model.UserAccount;
import vn.edu.ute.languagecenter.model.UserRole;
import vn.edu.ute.languagecenter.service.UserAccountService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountManagementPanel extends JPanel {

    private final UserAccountService userAccountService = new UserAccountService();
    private final UserAccountTableModel modelStudent = new UserAccountTableModel();
    private final UserAccountTableModel modelTeacher = new UserAccountTableModel();
    private final UserAccountTableModel modelStaff = new UserAccountTableModel();

    private final JTable tableStudent = new JTable(modelStudent);
    private final JTable tableTeacher = new JTable(modelTeacher);
    private final JTable tableStaff = new JTable(modelStaff);

    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private final JTextField searchField = new JTextField(25);
    private JTable currentTable;
    private UserAccountTableModel currentModel;

    private List<UserAccount> fullStudentList = new ArrayList<>();
    private List<UserAccount> fullTeacherList = new ArrayList<>();
    private List<UserAccount> fullStaffList = new ArrayList<>();

    public AccountManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Tìm (ID hoặc tên):"));
        topBar.add(searchField);
        JButton btnSearch = new JButton("Tìm");
        btnSearch.addActionListener(e -> applySearch());
        topBar.add(btnSearch);
        JButton btnClearSearch = new JButton("Xóa");
        btnClearSearch.addActionListener(e -> {
            searchField.setText("");
            applySearch();
        });
        topBar.add(btnClearSearch);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Student", buildRolePanel(tableStudent, modelStudent, UserRole.STUDENT));
        tabs.addTab("Teacher", buildRolePanel(tableTeacher, modelTeacher, UserRole.TEACHER));
        tabs.addTab("Staff", buildRolePanel(tableStaff, modelStaff, UserRole.STAFF));

        tabs.addChangeListener(e -> {
            int idx = tabs.getSelectedIndex();
            if (idx == 0) {
                currentTable = tableStudent;
                currentModel = modelStudent;
            } else if (idx == 1) {
                currentTable = tableTeacher;
                currentModel = modelTeacher;
            } else {
                currentTable = tableStaff;
                currentModel = modelStaff;
            }
            onRowSelected();
        });

        currentTable = tableStudent;
        currentModel = modelStudent;

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadAll();
    }

    private void applySearch() {
        String q = searchField.getText().trim();
        List<UserAccount> filteredStudent = filterAccounts(fullStudentList, q);
        List<UserAccount> filteredTeacher = filterAccounts(fullTeacherList, q);
        List<UserAccount> filteredStaff = filterAccounts(fullStaffList, q);
        modelStudent.setData(filteredStudent);
        modelTeacher.setData(filteredTeacher);
        modelStaff.setData(filteredStaff);
    }

    private List<UserAccount> filterAccounts(List<UserAccount> list, String query) {
        if (query.isEmpty()) return new ArrayList<>(list);
        String lower = query.toLowerCase(Locale.ROOT);
        List<UserAccount> out = new ArrayList<>();
        for (UserAccount u : list) {
            if (u.getId() != null && String.valueOf(u.getId()).contains(query)) {
                out.add(u);
                continue;
            }
            if (u.getUsername() != null && u.getUsername().toLowerCase(Locale.ROOT).contains(lower)) {
                out.add(u);
            }
        }
        return out;
    }

    private JPanel buildRolePanel(JTable table, UserAccountTableModel model, UserRole role) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnRefresh = new JButton("Refresh");
        JButton btnEdit = new JButton("Chỉnh sửa");
        JButton btnToggleStatus = new JButton("Active/Inactive");
        JButton btnAdd = new JButton("Thêm tài khoản Staff");

        btnRefresh.addActionListener(e -> loadByRole(role, model));
        btnEdit.addActionListener(e -> doEdit(role, table, model));
        btnToggleStatus.addActionListener(e -> doToggleStatus(table, model));
        btnAdd.addActionListener(e -> doAddStaffAccount(model));

        toolbar.add(btnRefresh);
        toolbar.add(btnEdit);
        toolbar.add(btnToggleStatus);
        if (role == UserRole.STAFF) {
            toolbar.add(btnAdd);
        }

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (currentTable == table) {
                onRowSelected();
            }
        });

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private void loadAll() {
        loadByRole(UserRole.STUDENT, modelStudent);
        loadByRole(UserRole.TEACHER, modelTeacher);
        loadByRole(UserRole.STAFF, modelStaff);
        applySearch();
    }

    private void loadByRole(UserRole role, UserAccountTableModel model) {
        try {
            List<UserAccount> list;
            if (role == UserRole.STAFF) {
                list = userAccountService.findByRoles(UserRole.getStaffTabRoles());
                fullStaffList = new ArrayList<>(list);
            } else if (role == UserRole.STUDENT) {
                list = userAccountService.findByRole(role);
                fullStudentList = new ArrayList<>(list);
            } else {
                list = userAccountService.findByRole(role);
                fullTeacherList = new ArrayList<>(list);
            }
            model.setData(list);
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        if (currentTable == null || currentModel == null) {
            return;
        }
        int row = currentTable.getSelectedRow();
        UserAccount u = currentModel.getAt(row);
        if (u != null) {
            lblSelected.setText("ID=" + u.getId() + " | " + u.getUsername() + " | " + u.getRole() + " | " + u.getStatus());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private void doEdit(UserRole role, JTable table, UserAccountTableModel model) {
        int row = table.getSelectedRow();
        UserAccount u = model.getAt(row);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Chọn một tài khoản để chỉnh sửa.");
            return;
        }
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(AccountManagementPanel.this);
        EditAccountDialog dlg = new EditAccountDialog(frame, userAccountService, u);
        dlg.setVisible(true);
        loadByRole(role, model);
    }

    private void doToggleStatus(JTable table, UserAccountTableModel model) {
        int row = table.getSelectedRow();
        UserAccount u = model.getAt(row);
        if (u == null) {
            JOptionPane.showMessageDialog(this, "Chọn một tài khoản.");
            return;
        }
        try {
            String newStatus = "ACTIVE".equalsIgnoreCase(u.getStatus()) ? "INACTIVE" : "ACTIVE";
            u.setStatus(newStatus);
            userAccountService.update(u);
            JOptionPane.showMessageDialog(this, "Đã đổi trạng thái thành " + newStatus);
            loadAll();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void doAddStaffAccount(UserAccountTableModel model) {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(AccountManagementPanel.this);
        AddStaffAccountDialog dlg = new AddStaffAccountDialog(frame, userAccountService);
        dlg.setVisible(true);
        loadByRole(UserRole.STAFF, model);
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
