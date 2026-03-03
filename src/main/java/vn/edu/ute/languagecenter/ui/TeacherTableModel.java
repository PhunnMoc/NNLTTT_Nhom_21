package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Teacher;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TeacherTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Tên giáo viên", "Số điện thoại", "Email", "Chuyên môn", "Trạng thái"};
    private final List<Teacher> data = new ArrayList<>();

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
        Teacher t = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> t.getId();
            case 1 -> t.getFullName();
            case 2 -> t.getPhone();
            case 3 -> t.getEmail();
            case 4 -> t.getSpecialty();
            case 5 -> t.getStatus();
            default -> "";
        };
    }

    public void setData(List<Teacher> teachers) {
        data.clear();
        data.addAll(teachers);
        fireTableDataChanged();
    }

    public Teacher getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }
}

