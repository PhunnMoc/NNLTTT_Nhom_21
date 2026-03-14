package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.service.CourseService;
import vn.edu.ute.languagecenter.ui.course.CourseTableModel;

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

public class MyCoursesPanel extends JPanel {

    private final CourseService courseService = new CourseService();
    private final CourseTableModel tableModel = new CourseTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private final Long relatedId;
    private final boolean isTeacher;

    public MyCoursesPanel(Long relatedId, boolean isTeacher) {
        this.relatedId = relatedId;
        this.isTeacher = isTeacher;
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");
        top.add(btnRefresh);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        btnRefresh.addActionListener(e -> loadCourses());
        loadCourses();
    }

    private void loadCourses() {
        try {
            List<Course> courses = isTeacher
                    ? courseService.findCoursesByTeacherId(relatedId)
                    : courseService.findCoursesByStudentId(relatedId);
            tableModel.setData(courses);
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        Course c = tableModel.getAt(row);
        if (c != null) {
            lblSelected.setText("Selected: ID=" + c.getId() + " | " + c.getCourseName());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
