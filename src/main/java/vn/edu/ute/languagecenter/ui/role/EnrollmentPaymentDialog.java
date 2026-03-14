package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Enrollment;
import vn.edu.ute.languagecenter.model.Payment;
import vn.edu.ute.languagecenter.model.PaymentMethod;
import vn.edu.ute.languagecenter.model.PaymentStatus;
import vn.edu.ute.languagecenter.model.Student;
import vn.edu.ute.languagecenter.service.EnrollmentService;
import vn.edu.ute.languagecenter.service.PaymentService;
import vn.edu.ute.languagecenter.service.StudentService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EnrollmentPaymentDialog extends JDialog {

    private final Long studentId;
    private final CourseClass courseClass;
    private final EnrollmentService enrollmentService;
    private final PaymentService paymentService = new PaymentService();
    private final StudentService studentService = new StudentService();

    private JTextField txtStudent;
    private JTextField txtCourse;
    private JTextField txtClass;
    private JTextField txtAmount;
    private JComboBox<PaymentMethod> comboMethod;

    public EnrollmentPaymentDialog(Frame owner, Long studentId, CourseClass courseClass, EnrollmentService enrollmentService) {
        super(owner, "Hóa đơn đăng ký lớp học", true);
        this.studentId = studentId;
        this.courseClass = courseClass;
        this.enrollmentService = enrollmentService;
        initUi();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUi() {
        setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new GridLayout(0, 2, 5, 5));

        txtStudent = createReadOnlyField();
        txtCourse = createReadOnlyField();
        txtClass = createReadOnlyField();
        txtAmount = createReadOnlyField();
        comboMethod = new JComboBox<>(PaymentMethod.values());

        center.add(new JLabel("Học viên"));
        center.add(txtStudent);
        center.add(new JLabel("Khóa học"));
        center.add(txtCourse);
        center.add(new JLabel("Lớp học"));
        center.add(txtClass);
        center.add(new JLabel("Số tiền"));
        center.add(txtAmount);
        center.add(new JLabel("Phương thức thanh toán"));
        center.add(comboMethod);

        add(center, BorderLayout.CENTER);

        JButton btnConfirm = new JButton("Xác nhận đăng ký");
        JButton btnCancel = new JButton("Hủy");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnConfirm);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);

        loadData();

        btnCancel.addActionListener(e -> dispose());
        btnConfirm.addActionListener(e -> onConfirm());
    }

    private JTextField createReadOnlyField() {
        JTextField txt = new JTextField();
        txt.setEditable(false);
        return txt;
    }

    private void loadData() {
        Student s = null;
        if (studentId != null) {
            for (Student st : studentService.findAll()) {
                if (studentId.equals(st.getId())) {
                    s = st;
                    break;
                }
            }
        }
        String studentName = s != null ? s.getFullName() : ("ID=" + studentId);
        String courseName = courseClass.getCourse() != null ? courseClass.getCourse().getCourseName() : "";
        String className = courseClass.getClassName();
        BigDecimal amount = courseClass.getCourse() != null ? courseClass.getCourse().getFee() : BigDecimal.ZERO;

        txtStudent.setText(studentName);
        txtCourse.setText(courseName);
        txtClass.setText(className);
        txtAmount.setText(amount != null ? amount.toPlainString() : "0");
    }

    private void onConfirm() {
        PaymentMethod method = (PaymentMethod) comboMethod.getSelectedItem();
        if (method == null) {
            JOptionPane.showMessageDialog(this, "Chọn phương thức thanh toán.");
            return;
        }
        try {
            Enrollment enrollment = enrollmentService.enroll(studentId, courseClass.getId());
            Payment p = new Payment();
            p.setStudent(enrollment.getStudent());
            p.setEnrollment(enrollment);
            BigDecimal amount = courseClass.getCourse() != null ? courseClass.getCourse().getFee() : BigDecimal.ZERO;
            p.setAmount(amount);
            p.setPaymentMethod(method.name());
            p.setPaymentDate(LocalDate.now());
            p.setStatus(PaymentStatus.PENDING.toString());
            paymentService.create(p);
            JOptionPane.showMessageDialog(this, "Đã tạo phiếu đăng ký và hóa đơn ở trạng thái chờ xác nhận.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

