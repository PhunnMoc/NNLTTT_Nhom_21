package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Staff;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StaffTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Tên nhân viên", "Vai trò", "Điện thoại", "Email", "Trạng thái", "Ngày vào làm"};
    private final List<Staff> data = new ArrayList<>();
    private final DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        Staff s = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getId();
            case 1 -> s.getFullName();
            case 2 -> s.getRole();
            case 3 -> s.getPhone();
            case 4 -> s.getEmail();
            case 5 -> s.getStatus();
            case 6 -> s.getHireDate() != null ? s.getHireDate().format(dateFmt) : "";
            default -> "";
        };
    }

    public void setData(List<Staff> staffList) {
        data.clear();
        data.addAll(staffList);
        fireTableDataChanged();
    }

    public Staff getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }
}

