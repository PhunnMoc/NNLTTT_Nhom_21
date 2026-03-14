package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.model.Teacher;
import vn.edu.ute.languagecenter.service.ClassService;
import vn.edu.ute.languagecenter.service.CourseService;
import vn.edu.ute.languagecenter.service.RoomService;
import vn.edu.ute.languagecenter.service.TeacherService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditClassDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ClassService classService = new ClassService();
    private final CourseService courseService = new CourseService();
    private final TeacherService teacherService = new TeacherService();
    private final RoomService roomService = new RoomService();

    private final CourseClass courseClass;
    private final JTextField txtClassName = new JTextField(25);
    private final JComboBox<Course> comboCourse = new JComboBox<>();
    private final JComboBox<Teacher> comboTeacher = new JComboBox<>();
    private final JTextField txtStartDate = new JTextField(12);
    private final JTextField txtEndDate = new JTextField(12);
    private final JSpinner spinMaxStudent = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JComboBox<Room> comboRoom = new JComboBox<>();
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"OPEN", "ONGOING", "CLOSED", "CANCELLED"});

    public EditClassDialog(Frame owner, CourseClass courseClass, Runnable onSaved) {
        super(owner, "Sửa lớp học", true);
        this.courseClass = courseClass;
        buildUI(onSaved);
        loadCombos();
        loadTeachersByAvailability();
        loadClass();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI(Runnable onSaved) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        panel.add(new JLabel("Tên lớp"), gbc(0, y));
        panel.add(txtClassName, gbc(1, y++));
        panel.add(new JLabel("Khóa học"), gbc(0, y));
        panel.add(comboCourse, gbc(1, y++));
        panel.add(new JLabel("Giáo viên"), gbc(0, y));
        JPanel pTeacher = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
        pTeacher.add(comboTeacher);
        JButton btnRefreshTeacher = new JButton("Chỉ GV trống lịch");
        btnRefreshTeacher.addActionListener(e -> loadTeachersByAvailability());
        pTeacher.add(btnRefreshTeacher);
        panel.add(pTeacher, gbc(1, y++));
        panel.add(new JLabel("Từ ngày (dd/MM/yyyy)"), gbc(0, y));
        panel.add(txtStartDate, gbc(1, y++));
        panel.add(new JLabel("Đến ngày (dd/MM/yyyy)"), gbc(0, y));
        panel.add(txtEndDate, gbc(1, y++));
        panel.add(new JLabel("Sĩ số tối đa"), gbc(0, y));
        panel.add(spinMaxStudent, gbc(1, y++));
        panel.add(new JLabel("Phòng"), gbc(0, y));
        panel.add(comboRoom, gbc(1, y++));
        panel.add(new JLabel("Trạng thái"), gbc(0, y));
        panel.add(comboStatus, gbc(1, y++));

        JButton btnSave = new JButton("Lưu");
        panel.add(btnSave, gbc(1, y));
        add(panel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> {
            try {
                Course course = (Course) comboCourse.getSelectedItem();
                Teacher teacher = (Teacher) comboTeacher.getSelectedItem();
                if (course == null || teacher == null) {
                    JOptionPane.showMessageDialog(this, "Chọn khóa học và giáo viên.");
                    return;
                }
                LocalDate start = parseDate(txtStartDate.getText());
                LocalDate end = parseDate(txtEndDate.getText());
                if (start != null && end != null && !classService.isTeacherAvailable(teacher.getId(), start, end, courseClass.getId())) {
                    JOptionPane.showMessageDialog(this, "Giáo viên đã có lịch dạy trùng trong khoảng thời gian này. Chọn giáo viên khác hoặc nhấn \"Chỉ GV trống lịch\" để lọc.");
                    return;
                }
                courseClass.setClassName(txtClassName.getText().trim());
                courseClass.setCourse(course);
                courseClass.setTeacher(teacher);
                courseClass.setStartDate(parseDate(txtStartDate.getText()));
                courseClass.setEndDate(parseDate(txtEndDate.getText()));
                courseClass.setMaxStudent(((Number) spinMaxStudent.getValue()).intValue());
                Room room = (Room) comboRoom.getSelectedItem();
                courseClass.setRoom(room);
                courseClass.setStatus((String) comboStatus.getSelectedItem());
                classService.update(courseClass);
                JOptionPane.showMessageDialog(this, "Đã lưu.");
                if (onSaved != null) onSaved.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }

    private void loadCombos() {
        try {
            comboCourse.removeAllItems();
            comboRoom.removeAllItems();
            for (Course c : courseService.findAll()) comboCourse.addItem(c);
            for (Room r : roomService.findAll()) comboRoom.addItem(r);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadTeachersByAvailability() {
        LocalDate start = parseDate(txtStartDate.getText());
        LocalDate end = parseDate(txtEndDate.getText());
        try {
            comboTeacher.removeAllItems();
            java.util.List<Teacher> list = (start != null && end != null)
                    ? classService.findTeachersAvailableForPeriod(start, end, courseClass.getId())
                    : teacherService.findAll();
            for (Teacher t : list) comboTeacher.addItem(t);
            if (courseClass.getTeacher() != null) comboTeacher.setSelectedItem(courseClass.getTeacher());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadClass() {
        txtClassName.setText(courseClass.getClassName());
        if (courseClass.getCourse() != null) comboCourse.setSelectedItem(courseClass.getCourse());
        if (courseClass.getTeacher() != null) comboTeacher.setSelectedItem(courseClass.getTeacher());
        txtStartDate.setText(courseClass.getStartDate() != null ? courseClass.getStartDate().format(DATE_FMT) : "");
        txtEndDate.setText(courseClass.getEndDate() != null ? courseClass.getEndDate().format(DATE_FMT) : "");
        spinMaxStudent.setValue(courseClass.getMaxStudent() != null ? courseClass.getMaxStudent() : 1);
        if (courseClass.getRoom() != null) comboRoom.setSelectedItem(courseClass.getRoom());
        comboStatus.setSelectedItem(courseClass.getStatus() != null ? courseClass.getStatus() : "OPEN");
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = x;
        g.gridy = y;
        return g;
    }
}
