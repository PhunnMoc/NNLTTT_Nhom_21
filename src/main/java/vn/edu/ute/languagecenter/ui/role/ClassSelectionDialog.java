package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.CourseClass;
import vn.edu.ute.languagecenter.service.ClassService;
import vn.edu.ute.languagecenter.ui.courseclass.ClassTableModel;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

public class ClassSelectionDialog extends JDialog {

    private final ClassService classService = new ClassService();
    private final ClassTableModel tableModel = new ClassTableModel();
    private final JTable table = new JTable(tableModel);
    private CourseClass selectedClass;

    public ClassSelectionDialog(Frame owner, List<CourseClass> classes) {
        super(owner, "Chọn lớp để đăng ký", true);
        setLayout(new BorderLayout(5, 5));

        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblHint = new JLabel("Chọn một lớp và nhấn Đăng ký.");
        JButton btnOk = new JButton("Đăng ký");
        JButton btnCancel = new JButton("Hủy");
        bottom.add(lblHint);
        bottom.add(btnOk);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);

        loadData(classes);

        btnOk.addActionListener(e -> onOk());
        btnCancel.addActionListener(e -> {
            selectedClass = null;
            dispose();
        });

        pack();
        setLocationRelativeTo(owner);
    }

    private void loadData(List<CourseClass> classes) {
        List<CourseClass> list = new ArrayList<>(classes);
        tableModel.setData(list);
        List<Integer> counts = new ArrayList<>();
        for (CourseClass cc : list) {
            counts.add(classService.countEnrolled(cc.getId()));
        }
        tableModel.setEnrolledCounts(counts);
    }

    private void onOk() {
        int row = table.getSelectedRow();
        CourseClass cc = tableModel.getAt(row);
        if (cc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lớp.");
            return;
        }
        selectedClass = cc;
        dispose();
    }

    public CourseClass getSelectedClass() {
        return selectedClass;
    }
}

