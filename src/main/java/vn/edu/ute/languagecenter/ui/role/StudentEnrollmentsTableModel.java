package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Enrollment;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StudentEnrollmentsTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String[] columns = {
            "Ngày đăng ký", "Khóa học", "Lớp", "Trạng thái"
    };
    private final List<Enrollment> data = new ArrayList<>();

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
        Enrollment e = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> e.getEnrollmentDate() != null ? e.getEnrollmentDate().format(DATE_FMT) : "";
            case 1 -> (e.getCourseClass() != null && e.getCourseClass().getCourse() != null)
                    ? e.getCourseClass().getCourse().getCourseName()
                    : "";
            case 2 -> e.getCourseClass() != null ? e.getCourseClass().getClassName() : "";
            case 3 -> e.getStatus();
            default -> "";
        };
    }

    public void setData(List<Enrollment> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
        fireTableDataChanged();
    }

    public Enrollment getAt(int row) {
        if (row < 0 || row >= data.size()) {
            return null;
        }
        return data.get(row);
    }
}

