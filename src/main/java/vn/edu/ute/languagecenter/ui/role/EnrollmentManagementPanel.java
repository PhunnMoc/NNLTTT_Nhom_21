package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Enrollment;
import vn.edu.ute.languagecenter.model.EnrollmentStatus;
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
import java.util.List;

public class EnrollmentManagementPanel extends JPanel {

    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final EnrollmentTableModel tableModel = new EnrollmentTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblStatus = new JLabel("Chọn một dòng để xem trạng thái.");

    public EnrollmentManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnConfirm = new JButton("Xác nhận đăng ký");
        JButton btnCancel = new JButton("Hủy đăng ký");
        top.add(btnRefresh);
        top.add(btnConfirm);
        top.add(btnCancel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblStatus, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadData());
        btnConfirm.addActionListener(e -> updateSelectedStatus(EnrollmentStatus.CONFIRMED));
        btnCancel.addActionListener(e -> updateSelectedStatus(EnrollmentStatus.CANCELLED));
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());

        loadData();
    }

    private void loadData() {
        try {
            List<Enrollment> list = enrollmentService.findAll();
            tableModel.setData(list);
            lblStatus.setText("Tổng số đăng ký: " + list.size());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        Enrollment e = tableModel.getAt(row);
        if (e == null) {
            lblStatus.setText("Chọn một dòng để xem trạng thái.");
            return;
        }
        lblStatus.setText("ID=" + e.getId() + " | Học viên: " +
                (e.getStudent() != null ? e.getStudent().getFullName() : "") +
                " | Lớp: " +
                (e.getCourseClass() != null ? e.getCourseClass().getClassName() : "") +
                " | Trạng thái: " + e.getStatus());
    }

    private void updateSelectedStatus(EnrollmentStatus status) {
        int row = table.getSelectedRow();
        Enrollment e = tableModel.getAt(row);
        if (e == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đăng ký.");
            return;
        }
        try {
            enrollmentService.updateStatus(e.getId(), status);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

