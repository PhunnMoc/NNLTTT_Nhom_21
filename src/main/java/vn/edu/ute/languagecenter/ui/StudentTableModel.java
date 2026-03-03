package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Student;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Tên học viên", "Số điện thoại", "Email", "Trình độ", "Trạng thái"};
    private final List<Student> data = new ArrayList<>();

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
        Student s = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> s.getId();
            case 1 -> s.getFullName();
            case 2 -> s.getPhone();
            case 3 -> s.getEmail();
            case 4 -> s.getLevel();
            case 5 -> s.getStatus();
            default -> "";
        };
    }

    public void setData(List<Student> students) {
        data.clear();
        data.addAll(students);
        fireTableDataChanged();
    }

    public Student getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }
}

