package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Payment;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InvoiceTableModel extends AbstractTableModel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final String[] columns = {
            "ID", "Ngày", "Học viên", "Khóa học", "Lớp", "Số tiền", "PT thanh toán", "Trạng thái"
    };
    private final List<Payment> data = new ArrayList<>();

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
        Payment p = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> p.getId();
            case 1 -> p.getPaymentDate() != null ? p.getPaymentDate().format(DATE_FMT) : "";
            case 2 -> p.getStudent() != null ? p.getStudent().getFullName() : "";
            case 3 -> (p.getEnrollment() != null && p.getEnrollment().getCourseClass() != null
                    && p.getEnrollment().getCourseClass().getCourse() != null)
                    ? p.getEnrollment().getCourseClass().getCourse().getCourseName()
                    : "";
            case 4 -> (p.getEnrollment() != null && p.getEnrollment().getCourseClass() != null)
                    ? p.getEnrollment().getCourseClass().getClassName()
                    : "";
            case 5 -> p.getAmount();
            case 6 -> p.getPaymentMethod();
            case 7 -> p.getStatus();
            default -> "";
        };
    }

    public void setData(List<Payment> list) {
        data.clear();
        if (list != null) {
            data.addAll(list);
        }
        fireTableDataChanged();
    }

    public Payment getAt(int row) {
        if (row < 0 || row >= data.size()) {
            return null;
        }
        return data.get(row);
    }
}

