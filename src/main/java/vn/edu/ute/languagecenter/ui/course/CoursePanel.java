package vn.edu.ute.languagecenter.ui.course;

import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.service.CourseService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class CoursePanel extends JPanel {

    private final String role;
    private final CourseService courseService = new CourseService();
    private final CourseTableModel tableModel = new CourseTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private Course selectedCourse;

    public CoursePanel() {
        this(null);
    }

    public CoursePanel(String role) {
        this.role = role == null ? "" : role;
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm khóa học");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        top.add(btnRefresh);
        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        btnRefresh.addActionListener(e -> loadCourses());
        btnAdd.addActionListener(e -> {
            new AddCourseDialog(getFrame(), this::loadCourses).setVisible(true);
        });
        btnEdit.addActionListener(e -> {
            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Chọn một khóa học.");
                return;
            }
            new EditCourseDialog(getFrame(), selectedCourse, this::loadCourses).setVisible(true);
        });
        btnDelete.addActionListener(e -> deleteSelected());

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        loadCourses();
    }

    private void loadCourses() {
        try {
            tableModel.setData(courseService.findAll());
            selectedCourse = null;
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        selectedCourse = tableModel.getAt(row);
        if (selectedCourse != null) {
            lblSelected.setText("Selected: ID=" + selectedCourse.getId() + " | " + selectedCourse.getCourseName());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private java.awt.Frame getFrame() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return w instanceof java.awt.Frame ? (java.awt.Frame) w : null;
    }

    private void deleteSelected() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Chọn một khóa học.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa khóa học \"" + selectedCourse.getCourseName() + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            courseService.deleteById(selectedCourse.getId());
            loadCourses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
