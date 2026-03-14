package vn.edu.ute.languagecenter.ui.courseclass;

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
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class AddClassDialog extends JDialog {

    private final ClassService classService = new ClassService();
    private final CourseService courseService = new CourseService();
    private final TeacherService teacherService = new TeacherService();
    private final RoomService roomService = new RoomService();

    private final JTextField txtClassName = new JTextField(25);
    private final JComboBox<Course> comboCourse = new JComboBox<>();
    private final JComboBox<Teacher> comboTeacher = new JComboBox<>();
    private final JSpinner dateStart = createDateSpinner();
    private final JSpinner dateEnd = createDateSpinner();
    private final JSpinner spinMaxStudent = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JComboBox<Room> comboRoom = new JComboBox<>();
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"OPEN", "ONGOING", "CLOSED", "CANCELLED"});

    public AddClassDialog(Frame owner, Runnable onSaved) {
        super(owner, "Mở lớp mới", true);
        buildUI(onSaved);
        loadCombos();
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
        panel.add(comboTeacher, gbc(1, y++));
        panel.add(new JLabel("Từ ngày"), gbc(0, y));
        panel.add(dateStart, gbc(1, y++));
        panel.add(new JLabel("Đến ngày"), gbc(0, y));
        panel.add(dateEnd, gbc(1, y++));
        panel.add(new JLabel("Sĩ số tối đa"), gbc(0, y));
        panel.add(spinMaxStudent, gbc(1, y++));
        panel.add(new JLabel("Phòng"), gbc(0, y));
        panel.add(comboRoom, gbc(1, y++));
        panel.add(new JLabel("Trạng thái"), gbc(0, y));
        panel.add(comboStatus, gbc(1, y++));

        JButton btnSave = new JButton("Tạo lớp");
        panel.add(btnSave, gbc(1, y));
        add(panel, BorderLayout.CENTER);
        dateEnd.setEnabled(false);
        ((JSpinner.DateEditor) dateEnd.getEditor()).getTextField().setEditable(false);

        btnSave.addActionListener(e -> {
            try {
                Course course = (Course) comboCourse.getSelectedItem();
                Teacher teacher = (Teacher) comboTeacher.getSelectedItem();
                if (course == null || teacher == null) {
                    JOptionPane.showMessageDialog(this, "Chọn khóa học và giáo viên.");
                    return;
                }
                CourseClass cc = new CourseClass();
                cc.setClassName(txtClassName.getText().trim());
                cc.setCourse(course);
                cc.setTeacher(teacher);
                cc.setStartDate(toLocalDate((Date) dateStart.getValue()));
                cc.setEndDate(toLocalDate((Date) dateEnd.getValue()));
                cc.setMaxStudent(((Number) spinMaxStudent.getValue()).intValue());
                Room room = (Room) comboRoom.getSelectedItem();
                cc.setRoom(room);
                cc.setStatus((String) comboStatus.getSelectedItem());
                classService.create(cc);
                JOptionPane.showMessageDialog(this, "Đã tạo lớp.");
                if (onSaved != null) onSaved.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    private static JSpinner createDateSpinner() {
        JSpinner s = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(s, "dd/MM/yy");
        s.setEditor(editor);
        return s;
    }

    private static LocalDate toLocalDate(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void loadCombos() {
        try {
            comboCourse.removeAllItems();
            comboTeacher.removeAllItems();
            comboRoom.removeAllItems();
            for (Course c : courseService.findAll()) comboCourse.addItem(c);
            for (Teacher t : teacherService.findAll()) comboTeacher.addItem(t);
            for (Room r : roomService.findAll()) comboRoom.addItem(r);
            comboCourse.addActionListener(e -> updateEndDateFromCourse());
            dateStart.addChangeListener(e -> updateEndDateFromCourse());
            updateEndDateFromCourse();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void updateEndDateFromCourse() {
        Course c = (Course) comboCourse.getSelectedItem();
        if (c == null) return;
        Integer dur = c.getDuration();
        if (dur == null || dur <= 0) return;
        LocalDate start = toLocalDate((Date) dateStart.getValue());
        if (start == null) return;
        LocalDate end = start.plusDays(dur - 1);
        dateEnd.setValue(toDate(end));
    }

    private static Date toDate(LocalDate d) {
        return d == null ? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
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
