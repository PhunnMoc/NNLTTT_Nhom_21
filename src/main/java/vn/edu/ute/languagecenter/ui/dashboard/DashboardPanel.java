package vn.edu.ute.languagecenter.ui.dashboard;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Enrollment;
import vn.edu.ute.languagecenter.model.Payment;
import vn.edu.ute.languagecenter.model.PaymentStatus;
import vn.edu.ute.languagecenter.model.Schedule;
import vn.edu.ute.languagecenter.model.TimeSlot;
import vn.edu.ute.languagecenter.service.ClassService;
import vn.edu.ute.languagecenter.service.EnrollmentService;
import vn.edu.ute.languagecenter.service.PaymentService;
import vn.edu.ute.languagecenter.service.ScheduleService;
import vn.edu.ute.languagecenter.service.StudentService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DashboardPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final StudentService studentService = new StudentService();
    private final ClassService classService = new ClassService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final PaymentService paymentService = new PaymentService();
    private final ScheduleService scheduleService = new ScheduleService();

    private final JLabel lblStatStudents = new JLabel("0");
    private final JLabel lblStatClasses = new JLabel("0");
    private final JLabel lblStatRevenue = new JLabel("0");
    private final JLabel lblStatAttendance = new JLabel("N/A");
    private final JLabel lblStatNewEnrollments = new JLabel("0");
    private final JLabel lblStatDebt = new JLabel("0");

    private final DefaultTableModel scheduleModel = new DefaultTableModel(
            new String[]{"Ngày", "Giờ", "Lớp", "Phòng", "Giáo viên"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel endingClassesModel = new DefaultTableModel(
            new String[]{"Lớp", "Khóa học", "Ngày kết thúc"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.add(createStatCard("Học viên đang học", lblStatStudents));
        statsPanel.add(createStatCard("Lớp đang mở", lblStatClasses));
        statsPanel.add(createStatCard("Doanh thu tháng này", lblStatRevenue));
        statsPanel.add(createStatCard("Tỉ lệ chuyên cần", lblStatAttendance));
        statsPanel.add(createStatCard("Đăng ký mới tháng này", lblStatNewEnrollments));
        statsPanel.add(createStatCard("Công nợ hiện tại", lblStatDebt));

        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Lịch học tuần này"));
        JTable scheduleTable = new JTable(scheduleModel);
        schedulePanel.add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        JPanel endingPanel = new JPanel(new BorderLayout());
        endingPanel.setBorder(BorderFactory.createTitledBorder("Các lớp sắp kết thúc"));
        endingPanel.add(new JScrollPane(new JTable(endingClassesModel)), BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.add(schedulePanel);
        center.add(endingPanel);

        JPanel refreshBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        refreshBar.add(btnRefresh);

        JPanel north = new JPanel(new BorderLayout());
        north.add(refreshBar, BorderLayout.NORTH);
        north.add(statsPanel, BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        SwingUtilities.invokeLater(this::loadData);
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblTitle = new JLabel(title);
        valueLabel.setHorizontalAlignment(JLabel.RIGHT);
        p.add(lblTitle, BorderLayout.WEST);
        p.add(valueLabel, BorderLayout.EAST);
        p.setPreferredSize(new Dimension(0, 80));
        return p;
    }

    private void loadData() {
        try {
            int totalStudents = studentService.findAll().size();
            List<CourseClass> classes = classService.findAll();
            long activeClasses = classes.stream()
                    .filter(cc -> cc.getStatus() != null && "ACTIVE".equalsIgnoreCase(cc.getStatus()))
                    .count();

            List<Enrollment> enrollments = enrollmentService.findAll();
            long confirmedEnrollments = enrollments.stream()
                    .filter(e -> e.getStatus() != null && "CONFIRMED".equalsIgnoreCase(e.getStatus()))
                    .count();
            long distinctStudentsEnrolled = enrollments.stream()
                    .filter(e -> e.getStatus() != null && "CONFIRMED".equalsIgnoreCase(e.getStatus()))
                    .map(e -> e.getStudent() != null ? e.getStudent().getId() : null)
                    .distinct()
                    .count();

            YearMonth now = YearMonth.now();
            LocalDate monthStart = now.atDay(1);
            LocalDate monthEnd = now.atEndOfMonth();
            long newEnrollmentsThisMonth = enrollments.stream()
                    .filter(e -> e.getEnrollmentDate() != null
                            && !e.getEnrollmentDate().isBefore(monthStart)
                            && !e.getEnrollmentDate().isAfter(monthEnd))
                    .count();

            List<Payment> payments = paymentService.findAll();
            BigDecimal revenueThisMonth = BigDecimal.ZERO;
            BigDecimal totalDebt = BigDecimal.ZERO;
            for (Payment p : payments) {
                if (p.getAmount() == null) continue;
                if (PaymentStatus.PAID.toString().equals(p.getStatus())
                        && p.getPaymentDate() != null
                        && !p.getPaymentDate().isBefore(monthStart)
                        && !p.getPaymentDate().isAfter(monthEnd)) {
                    revenueThisMonth = revenueThisMonth.add(p.getAmount());
                }
                if (PaymentStatus.PENDING.toString().equals(p.getStatus())) {
                    totalDebt = totalDebt.add(p.getAmount());
                }
            }

            lblStatStudents.setText(String.valueOf(distinctStudentsEnrolled));
            lblStatClasses.setText(String.valueOf(activeClasses));
            lblStatRevenue.setText(revenueThisMonth.toPlainString());
            lblStatAttendance.setText("N/A");
            lblStatNewEnrollments.setText(String.valueOf(newEnrollmentsThisMonth));
            lblStatDebt.setText(totalDebt.toPlainString());

            LocalDate today = LocalDate.now();
            DayOfWeek dow = today.getDayOfWeek();
            int toMonday = dow.getValue() - DayOfWeek.MONDAY.getValue();
            if (toMonday < 0) toMonday += 7;
            LocalDate weekStart = today.minusDays(toMonday);
            LocalDate weekEnd = weekStart.plusDays(6);
            List<Schedule> weekSchedules = scheduleService.findByDateRange(weekStart, weekEnd);
            scheduleModel.setRowCount(0);
            for (Schedule s : weekSchedules) {
                String dateStr = s.getDate() != null ? s.getDate().format(DATE_FMT) : "";
                String timeStr = "";
                if (s.getStartTime() != null && s.getEndTime() != null) {
                    TimeSlot slot = TimeSlot.fromTimes(s.getStartTime(), s.getEndTime());
                    timeStr = slot != null ? slot.getLabel() : s.getStartTime().format(TIME_FMT) + "-" + s.getEndTime().format(TIME_FMT);
                }
                String className = s.getCourseClass() != null ? s.getCourseClass().getClassName() : "";
                String roomName = s.getRoom() != null ? s.getRoom().getRoomName() : "";
                String teacherName = (s.getCourseClass() != null && s.getCourseClass().getTeacher() != null)
                        ? s.getCourseClass().getTeacher().getFullName() : "";
                scheduleModel.addRow(new Object[]{dateStr, timeStr, className, roomName, teacherName});
            }

            LocalDate endLimit = today.plusDays(30);
            List<CourseClass> endingSoon = new ArrayList<>();
            for (CourseClass cc : classes) {
                if (cc.getEndDate() == null) continue;
                if (!cc.getEndDate().isBefore(today) && !cc.getEndDate().isAfter(endLimit)) {
                    endingSoon.add(cc);
                }
            }
            endingSoon.sort((a, b) -> {
                if (a.getEndDate() == null || b.getEndDate() == null) return 0;
                return a.getEndDate().compareTo(b.getEndDate());
            });
            endingClassesModel.setRowCount(0);
            for (int i = 0; i < Math.min(10, endingSoon.size()); i++) {
                CourseClass cc = endingSoon.get(i);
                String courseName = cc.getCourse() != null ? cc.getCourse().getCourseName() : "";
                String endStr = cc.getEndDate() != null ? cc.getEndDate().format(DATE_FMT) : "";
                endingClassesModel.addRow(new Object[]{cc.getClassName(), courseName, endStr});
            }
        } catch (Exception e) {
            lblStatStudents.setText("0");
            lblStatClasses.setText("0");
            lblStatRevenue.setText("0");
            lblStatNewEnrollments.setText("0");
            lblStatDebt.setText("0");
        }
    }
}
