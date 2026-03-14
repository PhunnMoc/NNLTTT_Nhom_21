package vn.edu.ute.languagecenter.ui.account;

import vn.edu.ute.languagecenter.model.UserAccount;
import vn.edu.ute.languagecenter.service.UserAccountService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class UserAccountPanel extends JPanel {

    private final UserAccountService userAccountService = new UserAccountService();
    private final UserAccountTableModel tableModel = new UserAccountTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private UserAccount selectedAccount;

    public UserAccountPanel() {
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");

        top.add(btnRefresh);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadAccounts());

        loadAccounts();
    }

    private void loadAccounts() {
        try {
            tableModel.setData(userAccountService.findAll());
            selectedAccount = null;
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        selectedAccount = tableModel.getAt(row);
        if (selectedAccount != null) {
            lblSelected.setText("Selected: ID=" + selectedAccount.getId() + " | " + selectedAccount.getUsername());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
