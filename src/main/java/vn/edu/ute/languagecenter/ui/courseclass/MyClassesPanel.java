package vn.edu.ute.languagecenter.ui.courseclass;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.service.ClassService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

public class MyClassesPanel extends JPanel {

    private final Long teacherId;
    private final ClassService classService = new ClassService();
    private final ClassTableModel tableModel = new ClassTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");

    public MyClassesPanel(Long teacherId) {
        this.teacherId = teacherId;
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnSchedule = new JButton("Xem thời khóa biểu");
        top.add(btnRefresh);
        top.add(btnSchedule);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(table);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            CourseClass cc = tableModel.getAt(row);
            lblSelected.setText(cc != null ? "ID=" + cc.getId() + " | " + cc.getClassName() : "Selected: (none)");
        });

        btnRefresh.addActionListener(e -> loadClasses());
        btnSchedule.addActionListener(e -> {
            int row = table.getSelectedRow();
            CourseClass cc = tableModel.getAt(row);
            if (cc == null) {
                JOptionPane.showMessageDialog(this, "Chọn một lớp.");
                return;
            }
            CourseClass fresh = classService.findById(cc.getId());
            if (fresh != null) {
                new ScheduleDialog(getFrame(), fresh).setVisible(true);
            }
        });
        loadClasses();
    }

    private java.awt.Frame getFrame() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return w instanceof java.awt.Frame ? (java.awt.Frame) w : null;
    }

    private void loadClasses() {
        try {
            List<CourseClass> list = teacherId != null ? classService.findByTeacherId(teacherId) : List.of();
            tableModel.setData(list);
            List<Integer> counts = new ArrayList<>();
            for (CourseClass cc : list) {
                counts.add(classService.countEnrolled(cc.getId()));
            }
            tableModel.setEnrolledCounts(counts);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
