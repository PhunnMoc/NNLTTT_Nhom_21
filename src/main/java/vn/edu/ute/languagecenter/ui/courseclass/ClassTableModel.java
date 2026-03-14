package vn.edu.ute.languagecenter.ui.courseclass;

import vn.edu.ute.languagecenter.model.CourseClass;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClassTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String[] columns = {"ID", "Tên lớp", "Khóa học", "Giáo viên", "Từ ngày", "Đến ngày", "Đã ĐK", "Sĩ số", "Phòng", "Trạng thái"};
    private final List<CourseClass> data = new ArrayList<>();
    private final List<Integer> enrolledCounts = new ArrayList<>();

    public void setEnrolledCounts(List<Integer> counts) {
        enrolledCounts.clear();
        if (counts != null) enrolledCounts.addAll(counts);
        fireTableDataChanged();
    }

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
        CourseClass cc = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> cc.getId();
            case 1 -> cc.getClassName();
            case 2 -> cc.getCourse() != null ? cc.getCourse().getCourseName() : "";
            case 3 -> cc.getTeacher() != null ? cc.getTeacher().getFullName() : "";
            case 4 -> cc.getStartDate() != null ? cc.getStartDate().format(DATE_FMT) : "";
            case 5 -> cc.getEndDate() != null ? cc.getEndDate().format(DATE_FMT) : "";
            case 6 -> rowIndex < enrolledCounts.size() ? enrolledCounts.get(rowIndex) : "";
            case 7 -> cc.getMaxStudent() != null ? cc.getMaxStudent() : "";
            case 8 -> cc.getRoom() != null ? cc.getRoom().getRoomName() : "";
            case 9 -> cc.getStatus();
            default -> "";
        };
    }

    public void setData(List<CourseClass> list) {
        data.clear();
        data.addAll(list);
        fireTableDataChanged();
    }

    public CourseClass getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }
}
