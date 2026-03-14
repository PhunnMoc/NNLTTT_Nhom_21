package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Enrollment;
import vn.edu.ute.languagecenter.service.EnrollmentService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentEnrollmentsPanel extends JPanel {

    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final StudentEnrollmentsTableModel tableModel = new StudentEnrollmentsTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblInfo = new JLabel("Chọn một dòng để xem chi tiết.");
    private final Long studentId;

    public StudentEnrollmentsPanel(Long studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblInfo, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        try {
            List<Enrollment> list = enrollmentService.findByStudentId(studentId);
            tableModel.setData(list);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        Enrollment e = tableModel.getAt(row);
        if (e == null) {
            lblInfo.setText("Chọn một dòng để xem chi tiết.");
            return;
        }
        String date = e.getEnrollmentDate() != null
                ? e.getEnrollmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "";
        String studentName = e.getStudent() != null ? e.getStudent().getFullName() : "";
        String className = e.getCourseClass() != null ? e.getCourseClass().getClassName() : "";
        String courseName = (e.getCourseClass() != null && e.getCourseClass().getCourse() != null)
                ? e.getCourseClass().getCourse().getCourseName()
                : "";
        lblInfo.setText("Ngày ĐK: " + date + " | Học viên: " + studentName +
                " | Lớp: " + className + " | Khóa: " + courseName +
                " | Trạng thái: " + e.getStatus());
    }
}

