package vn.edu.ute.languagecenter.ui.course;

import vn.edu.ute.languagecenter.model.Course;
import vn.edu.ute.languagecenter.model.Level;
import vn.edu.ute.languagecenter.service.CourseService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;

public class AddCourseDialog extends JDialog {

    private final CourseService courseService = new CourseService();
    private final JTextField txtName = new JTextField(25);
    private final JTextArea txtDescription = new JTextArea(3, 25);
    private final JComboBox<Level> comboLevel = new JComboBox<>(Level.values());
    private final JSpinner spinDuration = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
    private final JTextField txtFee = new JTextField(15);
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"ACTIVE", "DRAFT", "INACTIVE"});

    public AddCourseDialog(Frame owner, Runnable onSaved) {
        super(owner, "Thêm khóa học", true);
        buildUI(onSaved);
        pack();
        setLocationRelativeTo(owner);
    }

    private void buildUI(Runnable onSaved) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        panel.add(new JLabel("Tên khóa học"), gbc(0, y));
        panel.add(txtName, gbc(1, y++));
        panel.add(new JLabel("Mô tả"), gbc(0, y));
        panel.add(new javax.swing.JScrollPane(txtDescription), gbc(1, y++));
        panel.add(new JLabel("Trình độ"), gbc(0, y));
        panel.add(comboLevel, gbc(1, y++));
        panel.add(new JLabel("Thời lượng (buổi)"), gbc(0, y));
        panel.add(spinDuration, gbc(1, y++));
        panel.add(new JLabel("Học phí"), gbc(0, y));
        panel.add(txtFee, gbc(1, y++));
        panel.add(new JLabel("Trạng thái"), gbc(0, y));
        panel.add(comboStatus, gbc(1, y++));

        JButton btnSave = new JButton("Thêm");
        panel.add(btnSave, gbc(1, y));
        add(panel, BorderLayout.CENTER);

        btnSave.addActionListener(e -> {
            try {
                Course c = new Course();
                c.setCourseName(txtName.getText().trim());
                c.setDescription(txtDescription.getText().trim());
                Level lv = (Level) comboLevel.getSelectedItem();
                c.setLevel(lv != null ? lv.name() : null);
                c.setDuration(((Number) spinDuration.getValue()).intValue());
                String feeStr = txtFee.getText().trim();
                c.setFee(feeStr.isEmpty() ? null : new BigDecimal(feeStr));
                c.setStatus((String) comboStatus.getSelectedItem());
                courseService.create(c);
                JOptionPane.showMessageDialog(this, "Đã thêm khóa học.");
                if (onSaved != null) onSaved.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = x;
        g.gridy = y;
        return g;
    }
}
