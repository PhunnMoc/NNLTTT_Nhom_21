package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.service.ClassService;
import vn.edu.ute.languagecenter.service.CourseService;
import vn.edu.ute.languagecenter.ui.course.CourseTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseRegistrationPanel extends JPanel {

    private final Long studentId;
    private final CourseService courseService = new CourseService();
    private final ClassService classService = new ClassService();

    private final CourseTableModel courseTableModel = new CourseTableModel();
    private final JTable tblCourses = new JTable(courseTableModel);

    private final JTextField txtSearch = new JTextField(25);

    private List<Course> allActiveCourses = new ArrayList<>();

    public CourseRegistrationPanel(Long studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));
        initUi();
        loadCourses();
    }

    private void initUi() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        top.add(new JLabel("Tìm khóa học:"));
        top.add(txtSearch);
        JButton btnSearch = new JButton("Tìm");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnRegister = new JButton("Đăng ký");
        top.add(btnSearch);
        top.add(btnRefresh);
        top.add(btnRegister);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        tblCourses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        center.add(new JScrollPane(tblCourses), BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        btnSearch.addActionListener(e -> applySearch());
        txtSearch.addActionListener(e -> applySearch());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadCourses();
        });

        btnRegister.addActionListener(e -> onRegisterForCourse());
    }

    private void loadCourses() {
        try {
            List<Course> all = courseService.findAll();
            List<Course> active = new ArrayList<>();
            for (Course c : all) {
                if (c.getStatus() != null && c.getStatus().toUpperCase(Locale.ROOT).equals("ACTIVE")) {
                    active.add(c);
                }
            }
            allActiveCourses = active;
            courseTableModel.setData(active);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applySearch() {
        String keyword = txtSearch.getText();
        if (keyword == null) {
            keyword = "";
        }
        String kw = keyword.trim().toLowerCase(Locale.ROOT);
        if (kw.isEmpty()) {
            courseTableModel.setData(allActiveCourses);
            return;
        }
        List<Course> filtered = new ArrayList<>();
        for (Course c : allActiveCourses) {
            String name = c.getCourseName() != null ? c.getCourseName().toLowerCase(Locale.ROOT) : "";
            String level = c.getLevel() != null ? c.getLevel().toLowerCase(Locale.ROOT) : "";
            if (name.contains(kw) || level.contains(kw)) {
                filtered.add(c);
            }
        }
        courseTableModel.setData(filtered);
    }

    private void onRegisterForCourse() {
        int row = tblCourses.getSelectedRow();
        Course course = courseTableModel.getAt(row);
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khóa học.");
            return;
        }
        List<CourseClass> classes;
        try {
            classes = classService.findByCourseId(course.getId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (classes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Khóa học này hiện chưa có lớp để đăng ký.");
            return;
        }
        Frame frame = getFrame();
        ClassSelectionDialog dlg = new ClassSelectionDialog(frame, classes);
        dlg.setVisible(true);
        CourseClass selectedClass = dlg.getSelectedClass();
        if (selectedClass != null) {
            ClassRegistrationDialog regDlg = new ClassRegistrationDialog(frame, studentId, selectedClass);
            regDlg.setVisible(true);
        }
    }

    private Frame getFrame() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return w instanceof Frame ? (Frame) w : null;
    }
}

