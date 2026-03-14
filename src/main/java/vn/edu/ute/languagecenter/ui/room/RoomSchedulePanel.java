package vn.edu.ute.languagecenter.ui.room;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.model.Schedule;
import vn.edu.ute.languagecenter.service.RoomService;
import vn.edu.ute.languagecenter.service.ScheduleService;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RoomSchedulePanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final RoomService roomService = new RoomService();
    private final ScheduleService scheduleService = new ScheduleService();

    private final JComboBox<Room> comboRoom = new JComboBox<>();
    private final JLabel lblWeek = new JLabel();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Ngày", "Thứ", "Từ giờ", "Đến giờ", "Môn", "Lớp", "GV"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private LocalDate weekStart;

    public RoomSchedulePanel() {
        setLayout(new BorderLayout(5, 5));
        buildTop();
        buildTable();
        loadRooms();
        initWeek(LocalDate.now());
        reloadSchedules();
    }

    private void buildTop() {
        JPanel top = new JPanel(new BorderLayout(5, 5));
        JPanel left = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        JPanel right = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        left.add(new JLabel("Phòng"));
        left.add(comboRoom);
        JButton btnPrev = new JButton("Tuần trước");
        JButton btnNext = new JButton("Tuần sau");
        right.add(btnPrev);
        right.add(lblWeek);
        right.add(btnNext);
        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);
        comboRoom.addActionListener(e -> reloadSchedules());
        btnPrev.addActionListener(e -> changeWeek(-7));
        btnNext.addActionListener(e -> changeWeek(7));
    }

    private void buildTable() {
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadRooms() {
        try {
            comboRoom.removeAllItems();
            for (Room r : roomService.findAll()) {
                comboRoom.addItem(r);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void initWeek(LocalDate anyDate) {
        DayOfWeek dow = anyDate.getDayOfWeek();
        int diff = dow.getValue() - DayOfWeek.MONDAY.getValue();
        if (diff < 0) diff += 7;
        weekStart = anyDate.minusDays(diff);
        updateWeekLabel();
    }

    private void changeWeek(int days) {
        weekStart = weekStart.plusDays(days);
        updateWeekLabel();
        reloadSchedules();
    }

    private void updateWeekLabel() {
        LocalDate end = weekStart.plusDays(6);
        lblWeek.setText(weekStart.format(DATE_FMT) + " - " + end.format(DATE_FMT));
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

    private void reloadSchedules() {
        Room room = (Room) comboRoom.getSelectedItem();
        if (room == null || weekStart == null) {
            tableModel.setRowCount(0);
            return;
        }
        LocalDate end = weekStart.plusDays(6);
        List<Schedule> list;
        try {
            list = scheduleService.findByRoomAndRange(room.getId(), weekStart, end);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            return;
        }
        tableModel.setRowCount(0);
        for (Schedule s : list) {
            LocalDate d = s.getDate();
            String dayText = d != null ? d.format(DATE_FMT) : "";
            String dowText = d != null ? dayLabel(d.getDayOfWeek()) : "";
            String startText = s.getStartTime() != null ? s.getStartTime().toString() : "";
            String endText = s.getEndTime() != null ? s.getEndTime().toString() : "";
            CourseClass cc = s.getCourseClass();
            String className = cc != null ? cc.getClassName() : "";
            String courseName = "";
            String teacherName = "";
            if (cc != null && cc.getCourse() != null) {
                courseName = cc.getCourse().getCourseName();
            }
            if (cc != null && cc.getTeacher() != null) {
                teacherName = cc.getTeacher().getFullName();
            }
            tableModel.addRow(new Object[]{
                    dayText,
                    dowText,
                    startText,
                    endText,
                    courseName,
                    className,
                    teacherName
            });
        }
    }
}

