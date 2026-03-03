package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Course;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Tên khóa học", "Level", "Thời lượng", "Học phí", "Trạng thái"};
    private final List<Course> data = new ArrayList<>();

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
        Course c = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> c.getId();
            case 1 -> c.getCourseName();
            case 2 -> c.getLevel();
            case 3 -> c.getDuration();
            case 4 -> c.getFee();
            case 5 -> c.getStatus();
            default -> "";
        };
    }

    public void setData(List<Course> courses) {
        data.clear();
        data.addAll(courses);
        fireTableDataChanged();
    }

    public Course getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }
}

