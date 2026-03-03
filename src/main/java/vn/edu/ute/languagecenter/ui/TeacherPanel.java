package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Teacher;
import vn.edu.ute.languagecenter.service.TeacherService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class TeacherPanel extends JPanel {

    private final TeacherService teacherService = new TeacherService();
    private final TeacherTableModel tableModel = new TeacherTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private Teacher selectedTeacher;

    public TeacherPanel() {
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");

        top.add(btnRefresh);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadTeachers());

        loadTeachers();
    }

    private void loadTeachers() {
        try {
            tableModel.setData(teacherService.findAll());
            selectedTeacher = null;
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        selectedTeacher = tableModel.getAt(row);
        if (selectedTeacher != null) {
            lblSelected.setText("Selected: ID=" + selectedTeacher.getId() + " | " + selectedTeacher.getFullName());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

