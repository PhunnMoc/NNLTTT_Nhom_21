package vn.edu.ute.languagecenter.ui.courseclass;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.service.ClassService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class ClassPanel extends JPanel {

    private final String role;
    private final ClassService classService = new ClassService();
    private final ClassTableModel tableModel = new ClassTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private CourseClass selectedClass;

    public ClassPanel(String role) {
        this.role = role == null ? "" : role;
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        top.add(btnRefresh);
        if (canEditClass()) {
            JButton btnAdd = new JButton("Mở lớp");
            JButton btnEdit = new JButton("Sửa");
            JButton btnSchedule = new JButton("Thời khóa biểu");
            top.add(btnAdd);
            top.add(btnEdit);
            top.add(btnSchedule);
            btnAdd.addActionListener(e -> {
                new AddClassDialog(getFrame(), this::loadClasses).setVisible(true);
            });
            btnEdit.addActionListener(e -> {
                if (selectedClass == null) {
                    JOptionPane.showMessageDialog(this, "Chọn một lớp.");
                    return;
                }
                CourseClass fresh = classService.findById(selectedClass.getId());
                if (fresh != null) {
                    new EditClassDialog(getFrame(), fresh, this::loadClasses).setVisible(true);
                }
            });
            btnSchedule.addActionListener(e -> {
                if (selectedClass == null) {
                    JOptionPane.showMessageDialog(this, "Chọn một lớp.");
                    return;
                }
                CourseClass fresh = classService.findById(selectedClass.getId());
                if (fresh != null) {
                    new ScheduleDialog(getFrame(), fresh).setVisible(true);
                }
            });
        }
        if (canDeleteClass()) {
            JButton btnDelete = new JButton("Xóa");
            top.add(btnDelete);
            btnDelete.addActionListener(e -> deleteSelected());
        }

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadClasses());
        loadClasses();
    }

    private java.awt.Frame getFrame() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return w instanceof java.awt.Frame ? (java.awt.Frame) w : null;
    }

    private boolean canEditClass() {
        String r = this.role.toUpperCase();
        return r.equals("ADMIN") || r.equals("SUPER_ADMIN") || r.equals("CONSULTANT") || r.equals("STAFF");
    }

    private boolean canDeleteClass() {
        String r = this.role.toUpperCase();
        return r.equals("ADMIN") || r.equals("SUPER_ADMIN");
    }

    private void loadClasses() {
        try {
            List<CourseClass> list = classService.findAll();
            tableModel.setData(list);
            List<Integer> counts = new ArrayList<>();
            for (CourseClass cc : list) {
                counts.add(classService.countEnrolled(cc.getId()));
            }
            tableModel.setEnrolledCounts(counts);
            selectedClass = null;
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        selectedClass = tableModel.getAt(row);
        if (selectedClass != null) {
            lblSelected.setText("Selected: ID=" + selectedClass.getId() + " | " + selectedClass.getClassName());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private void deleteSelected() {
        if (selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Chọn một lớp.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa lớp \"" + selectedClass.getClassName() + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            classService.deleteById(selectedClass.getId());
            loadClasses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
