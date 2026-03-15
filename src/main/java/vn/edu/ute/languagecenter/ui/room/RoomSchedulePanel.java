package vn.edu.ute.languagecenter.ui.room;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.model.Schedule;
import vn.edu.ute.languagecenter.model.TimeSlot;
import vn.edu.ute.languagecenter.service.EnrollmentService;
import vn.edu.ute.languagecenter.service.RoomService;
import vn.edu.ute.languagecenter.service.ScheduleService;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomSchedulePanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLUMN_NAMES = {"Thứ", "Tiết 1", "Tiết 2", "Tiết 3", "Tiết 4", "Tiết 5", "Tiết 6"};
    private static final int ROWS = 7;
    private static final int SLOT_COLUMNS = 6;

    private final RoomService roomService = new RoomService();
    private final ScheduleService scheduleService = new ScheduleService();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    private final Long relatedId;
    private final boolean isTeacher;

    private final JComboBox<Room> comboRoom = new JComboBox<>();
    private final JLabel lblWeek = new JLabel();
    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMN_NAMES, ROWS) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private LocalDate weekStart;

    public RoomSchedulePanel() {
        this.relatedId = null;
        this.isTeacher = false;
        setLayout(new BorderLayout(5, 5));
        buildTop();
        buildTable();
        loadRooms();
        initWeek(LocalDate.now());
        reloadSchedules();
    }

    public RoomSchedulePanel(Long relatedId, boolean isTeacher) {
        this.relatedId = relatedId;
        this.isTeacher = isTeacher;
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
        table.setRowHeight(72);
        for (int r = 0; r < ROWS; r++) {
            tableModel.setValueAt(dayLabel(DayOfWeek.values()[r]), r, 0);
        }
        table.setDefaultRenderer(Object.class, new WrapCellRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadRooms() {
        try {
            comboRoom.removeAllItems();
            List<Room> rooms;
            if (relatedId != null) {
                rooms = isTeacher ? roomService.findRoomsByTeacherId(relatedId) : roomService.findRoomsByStudentId(relatedId);
            } else {
                rooms = roomService.findAll();
            }
            for (Room r : rooms) {
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

    private Map<LocalDate, Map<TimeSlot, List<Schedule>>> buildScheduleMap(List<Schedule> list) {
        Map<LocalDate, Map<TimeSlot, List<Schedule>>> byDateAndSlot = new java.util.HashMap<>();
        for (Schedule s : list) {
            if (s.getDate() == null) continue;
            TimeSlot slot = TimeSlot.fromTimes(s.getStartTime(), s.getEndTime());
            if (slot == null) continue;
            byDateAndSlot
                    .computeIfAbsent(s.getDate(), k -> new EnumMap<>(TimeSlot.class))
                    .computeIfAbsent(slot, k -> new ArrayList<>())
                    .add(s);
        }
        return byDateAndSlot;
    }

    private String formatSchedule(Schedule s) {
        CourseClass cc = s.getCourseClass();
        if (cc == null) return "";
        String name = cc.getClassName();
        if (cc.getCourse() != null) {
            name = cc.getCourse().getCourseName() + " - " + name;
        }
        if (cc.getTeacher() != null) {
            name = name + " (" + cc.getTeacher().getFullName() + ")";
        }
        return name;
    }

    private String formatCell(List<Schedule> schedules) {
        if (schedules == null || schedules.isEmpty()) return "";
        return schedules.stream()
                .map(this::formatSchedule)
                .filter(t -> !t.isEmpty())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    private void reloadSchedules() {
        Room room = (Room) comboRoom.getSelectedItem();
        if (room == null || weekStart == null) {
            clearSlotCells();
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
        if (relatedId != null) {
            if (isTeacher) {
                list = list.stream()
                        .filter(s -> s.getCourseClass() != null && s.getCourseClass().getTeacher() != null && relatedId.equals(s.getCourseClass().getTeacher().getId()))
                        .collect(Collectors.toList());
            } else {
                Set<Long> enrolledClassIds = enrollmentService.findByStudentId(relatedId).stream()
                        .map(e -> e.getCourseClass() != null ? e.getCourseClass().getId() : null)
                        .filter(id -> id != null)
                        .collect(Collectors.toSet());
                list = list.stream()
                        .filter(s -> s.getCourseClass() != null && enrolledClassIds.contains(s.getCourseClass().getId()))
                        .collect(Collectors.toList());
            }
        }
        Map<LocalDate, Map<TimeSlot, List<Schedule>>> byDateAndSlot = buildScheduleMap(list);
        for (int r = 0; r < ROWS; r++) {
            tableModel.setValueAt(dayLabel(weekStart.plusDays(r).getDayOfWeek()), r, 0);
            LocalDate d = weekStart.plusDays(r);
            Map<TimeSlot, List<Schedule>> dayMap = byDateAndSlot.get(d);
            for (int c = 0; c < SLOT_COLUMNS; c++) {
                TimeSlot slot = TimeSlot.values()[c];
                List<Schedule> schedules = (dayMap != null) ? dayMap.get(slot) : null;
                tableModel.setValueAt(formatCell(schedules), r, c + 1);
            }
        }
    }

    private void clearSlotCells() {
        for (int r = 0; r < ROWS; r++) {
            if (weekStart != null) {
                tableModel.setValueAt(dayLabel(weekStart.plusDays(r).getDayOfWeek()), r, 0);
            }
            for (int c = 0; c < SLOT_COLUMNS; c++) {
                tableModel.setValueAt("", r, c + 1);
            }
        }
    }

    private static class WrapCellRenderer extends JTextArea implements TableCellRenderer {

        WrapCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
            setBorder(new EmptyBorder(4, 4, 4, 4));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            int w = table.getColumnModel().getColumn(column).getWidth();
            setSize(w, table.getRowHeight(row));
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            setFont(table.getFont());
            return this;
        }
    }
}
