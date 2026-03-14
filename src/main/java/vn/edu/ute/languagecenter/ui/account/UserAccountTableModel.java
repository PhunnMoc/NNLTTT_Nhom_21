package vn.edu.ute.languagecenter.ui.account;

import vn.edu.ute.languagecenter.model.UserAccount;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserAccountTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Username", "Role", "Status", "Last login", "Failed count", "Lockout until"};
    private final List<UserAccount> data = new ArrayList<>();
    private final DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        UserAccount u = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> u.getId();
            case 1 -> u.getUsername();
            case 2 -> u.getRole();
            case 3 -> u.getStatus();
            case 4 -> u.getLastLogin() != null ? u.getLastLogin().format(dtFmt) : "";
            case 5 -> u.getFailedLoginCount();
            case 6 -> u.getLockoutUntil() != null ? u.getLockoutUntil().format(dtFmt) : "";
            default -> "";
        };
    }

    public void setData(List<UserAccount> accounts) {
        data.clear();
        data.addAll(accounts);
        fireTableDataChanged();
    }

    public UserAccount getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }

    public List<UserAccount> getAllData() {
        return new ArrayList<>(data);
    }
}
