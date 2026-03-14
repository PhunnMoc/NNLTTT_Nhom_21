package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.service.ClassService;
import vn.edu.ute.languagecenter.service.EnrollmentService;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

public class ClassRegistrationDialog extends JDialog {

    private final Long studentId;
    private final CourseClass courseClass;
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final ClassService classService = new ClassService();

    private JTextField txtCourseName;
    private JTextField txtClassName;
    private JTextField txtTeacher;
    private JTextField txtDates;
    private JTextField txtRoom;
    private JTextField txtStatus;
    private JTextField txtCapacity;
    private JTextField txtEnrolled;

    public ClassRegistrationDialog(Frame owner, Long studentId, CourseClass courseClass) {
        super(owner, "Đăng ký lớp học", true);
        this.studentId = studentId;
        this.courseClass = courseClass;
        initUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new GridLayout(0, 2, 5, 5));

        txtCourseName = createReadOnlyField();
        txtClassName = createReadOnlyField();
        txtTeacher = createReadOnlyField();
        txtDates = createReadOnlyField();
        txtRoom = createReadOnlyField();
        txtStatus = createReadOnlyField();
        txtCapacity = createReadOnlyField();
        txtEnrolled = createReadOnlyField();

        center.add(new JLabel("Khóa học"));
        center.add(txtCourseName);
        center.add(new JLabel("Lớp học"));
        center.add(txtClassName);
        center.add(new JLabel("Giáo viên"));
        center.add(txtTeacher);
        center.add(new JLabel("Thời gian"));
        center.add(txtDates);
        center.add(new JLabel("Phòng"));
        center.add(txtRoom);
        center.add(new JLabel("Trạng thái lớp"));
        center.add(txtStatus);
        center.add(new JLabel("Sĩ số tối đa"));
        center.add(txtCapacity);
        center.add(new JLabel("Số đã đăng ký"));
        center.add(txtEnrolled);

        add(center, BorderLayout.CENTER);

        JButton btnRegister = new JButton("Đăng ký");
        JButton btnClose = new JButton("Đóng");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnRegister);
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);

        loadData();

        btnClose.addActionListener(e -> dispose());
        btnRegister.addActionListener(e -> onRegister());
    }

    private JTextField createReadOnlyField() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        return txt;
    }

    private void loadData() {
        CourseClass fresh = classService.findById(courseClass.getId());
        CourseClass cc = fresh != null ? fresh : courseClass;
        String courseName = cc.getCourse() != null ? cc.getCourse().getCourseName() : "";
        String teacherName = cc.getTeacher() != null ? cc.getTeacher().getFullName() : "";
        String roomName = cc.getRoom() != null ? cc.getRoom().getRoomName() : "";
        String dates = "";
        if (cc.getStartDate() != null && cc.getEndDate() != null) {
            dates = cc.getStartDate() + " - " + cc.getEndDate();
        }
        txtCourseName.setText(courseName);
        txtClassName.setText(cc.getClassName());
        txtTeacher.setText(teacherName);
        txtDates.setText(dates);
        txtRoom.setText(roomName);
        txtStatus.setText(cc.getStatus());
        txtCapacity.setText(cc.getMaxStudent() != null ? String.valueOf(cc.getMaxStudent()) : "");
        int enrolled = classService.countEnrolled(cc.getId());
        txtEnrolled.setText(String.valueOf(enrolled));
    }

    private void onRegister() {
        if (studentId == null || courseClass.getId() == null) {
            JOptionPane.showMessageDialog(this, "Thiếu thông tin học viên hoặc lớp.");
            return;
        }
        if (enrollmentService.existsByStudentAndClass(studentId, courseClass.getId())) {
            JOptionPane.showMessageDialog(this, "Bạn đã đăng ký lớp học này.");
            return;
        }
        CourseClass fresh = classService.findById(courseClass.getId());
        CourseClass cc = fresh != null ? fresh : courseClass;
        Integer max = cc.getMaxStudent();
        int enrolled = classService.countEnrolled(cc.getId());
        if (max != null && enrolled >= max) {
            JOptionPane.showMessageDialog(this, "Lớp đã đủ sĩ số, không thể đăng ký thêm.");
            return;
        }
        Frame frame = (Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        EnrollmentPaymentDialog dlg = new EnrollmentPaymentDialog(frame, studentId, cc, enrollmentService);
        dlg.setVisible(true);
        dispose();
    }
}

