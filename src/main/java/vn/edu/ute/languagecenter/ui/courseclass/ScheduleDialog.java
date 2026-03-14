package vn.edu.ute.languagecenter.ui.courseclass;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.model.Schedule;
import vn.edu.ute.languagecenter.model.StudyDay;
import vn.edu.ute.languagecenter.model.TimeSlot;
import vn.edu.ute.languagecenter.service.ScheduleService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ScheduleDialog extends JDialog {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ScheduleService scheduleService = new ScheduleService();
    private final CourseClass courseClass;

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Thứ", "Tiết"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JComboBox<StudyDay> comboDay = new JComboBox<>(StudyDay.values());
    private final JComboBox<TimeSlot> comboSlot = new JComboBox<>(TimeSlot.values());

    public ScheduleDialog(Frame owner, CourseClass courseClass) {
        super(owner, "Thời khóa biểu - " + (courseClass != null ? courseClass.getClassName() : ""), true);
        this.courseClass = courseClass;
        buildUI();
        if (courseClass != null) loadSchedules();
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(5, 5));

        table.getTableHeader().setReorderingAllowed(false);
        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;
        form.add(new JLabel("Thứ"), gbc(0, y));
        form.add(comboDay, gbc(1, y++));
        form.add(new JLabel("Tiết"), gbc(0, y));
        form.add(comboSlot, gbc(1, y++));

        JButton btnAdd = new JButton("Thêm lịch cố định");
        JButton btnDelete = new JButton("Xóa buổi");
        form.add(btnAdd, gbc(0, y));
        form.add(btnDelete, gbc(1, y));

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.NORTH);
        main.add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addSchedule());
        btnDelete.addActionListener(e -> deleteSelected());

        add(main, BorderLayout.CENTER);
    }

    private void loadSchedules() {
        tableModel.setRowCount(0);
        try {
            List<Schedule> list = scheduleService.findByClassId(courseClass.getId());
            for (Schedule s : list) {
                String dayText = "";
                if (s.getDate() != null) {
                    dayText = dayLabel(s.getDate().getDayOfWeek());
                }
                TimeSlot slot = TimeSlot.fromTimes(s.getStartTime(), s.getEndTime());
                String slotText = slot != null ? slot.toString() : "";
                tableModel.addRow(new Object[]{
                        s.getId(),
                        dayText,
                        slotText
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void addSchedule() {
        if (courseClass == null) return;
        if (courseClass.getStartDate() == null || courseClass.getEndDate() == null) {
            JOptionPane.showMessageDialog(this, "Lớp phải có ngày bắt đầu và kết thúc.");
            return;
        }
        if (courseClass.getRoom() == null) {
            JOptionPane.showMessageDialog(this, "Lớp chưa có phòng. Vui lòng gán phòng cho lớp trước.");
            return;
        }
        StudyDay studyDay = (StudyDay) comboDay.getSelectedItem();
        TimeSlot slot = (TimeSlot) comboSlot.getSelectedItem();
        if (studyDay == null || slot == null) {
            JOptionPane.showMessageDialog(this, "Chọn thứ và tiết.");
            return;
        }
        Room room = courseClass.getRoom();
        DayOfWeek targetDow = studyDay.getDayOfWeek();
        LocalDate start = courseClass.getStartDate();
        LocalDate end = courseClass.getEndDate();
        if (end.isBefore(start)) {
            JOptionPane.showMessageDialog(this, "Ngày kết thúc lớp nhỏ hơn ngày bắt đầu.");
            return;
        }
        try {
            LocalDate d = start;
            while (!d.isAfter(end)) {
                if (d.getDayOfWeek() == targetDow) {
                    if (scheduleService.existsRoomConflict(room.getId(), d, slot.getStartTime(), slot.getEndTime())) {
                        JOptionPane.showMessageDialog(this, "Phòng " + room.getRoomName() + " đã có lịch vào " + d.format(DATE_FMT) + " tiết này.");
                        return;
                    }
                }
                d = d.plusDays(1);
            }
            d = start;
            while (!d.isAfter(end)) {
                if (d.getDayOfWeek() == targetDow) {
                    Schedule s = new Schedule();
                    s.setCourseClass(courseClass);
                    s.setDate(d);
                    s.setStartTime(slot.getStartTime());
                    s.setEndTime(slot.getEndTime());
                    s.setRoom(room);
                    scheduleService.create(s);
                }
                d = d.plusDays(1);
            }
            loadSchedules();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn một dòng để xóa.");
            return;
        }
        Object idObj = tableModel.getValueAt(row, 0);
        if (idObj instanceof Long id) {
            try {
                scheduleService.deleteById(id);
                loadSchedules();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private String dayLabel(DayOfWeek d) {
        switch (d) {
            case MONDAY:
                return "Thứ 2";
            case TUESDAY:
                return "Thứ 3";
            case WEDNESDAY:
                return "Thứ 4";
            case THURSDAY:
                return "Thứ 5";
            case FRIDAY:
                return "Thứ 6";
            case SATURDAY:
                return "Thứ 7";
            default:
                return "Chủ nhật";
        }
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(3, 3, 3, 3);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = x;
        g.gridy = y;
        return g;
    }
}
