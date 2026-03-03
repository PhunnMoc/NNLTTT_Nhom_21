package vn.edu.ute.languagecenter.ui;

import vn.edu.ute.languagecenter.model.Student;
import vn.edu.ute.languagecenter.service.StudentService;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class MainFrame extends JFrame {

    private final StudentService studentService = new StudentService();
    private final StudentTableModel tableModel = new StudentTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private Student selectedStudent;

    public MainFrame() {
        super("Language Center Management (Swing + JPA + MySQL)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buildUI();

        loadStudents();

        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");

        top.add(btnAdd);
        top.add(btnEdit);
        top.add(btnDelete);
        top.add(btnRefresh);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setContentPane(root);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> onRefresh());
    }

    private void loadStudents() {
        try {
            tableModel.setData(studentService.findAll());
            selectedStudent = null;
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        selectedStudent = tableModel.getAt(row);
        if (selectedStudent != null) {
            lblSelected.setText("Selected: ID=" + selectedStudent.getId() + " | " + selectedStudent.getFullName());
        } else {
            lblSelected.setText("Selected: (none)");
        }
    }

    private void onAdd() {
        JOptionPane.showMessageDialog(this, "TODO: Implement add student dialog.");
    }

    private void onEdit() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên trước.");
            return;
        }
        JOptionPane.showMessageDialog(this, "TODO: Implement edit student dialog.");
    }

    private void onDelete() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn học viên trước.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Xóa học viên ID=" + selectedStudent.getId() + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            studentService.deleteById(selectedStudent.getId());
            JOptionPane.showMessageDialog(this, "Đã xóa!");
            loadStudents();
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void onRefresh() {
        loadStudents();
    }

    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

