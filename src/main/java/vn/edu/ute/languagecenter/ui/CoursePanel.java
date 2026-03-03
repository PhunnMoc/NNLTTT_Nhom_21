package vn.edu.ute.languagecenter.ui;

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

    private final CourseService courseService = new CourseService();
    private final CourseTableModel tableModel = new CourseTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private Course selectedCourse;

    public CoursePanel() {
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

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

