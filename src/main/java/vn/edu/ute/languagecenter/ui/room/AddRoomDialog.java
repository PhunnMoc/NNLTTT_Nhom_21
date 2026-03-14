package vn.edu.ute.languagecenter.ui.room;

import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.service.RoomService;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class AddRoomDialog extends JDialog {

    private final RoomService roomService = new RoomService();
    private final JTextField txtName = new JTextField(20);
    private final JSpinner spinCapacity = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
    private final JTextField txtLocation = new JTextField(25);
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

    public AddRoomDialog(Frame owner, Runnable onSaved) {
        super(owner, "Thêm phòng học", true);
        JPanel panel = new JPanel(new GridBagLayout());
        int y = 0;
        panel.add(new JLabel("Tên phòng"), gbc(0, y));
        panel.add(txtName, gbc(1, y++));
        panel.add(new JLabel("Sức chứa"), gbc(0, y));
        panel.add(spinCapacity, gbc(1, y++));
        panel.add(new JLabel("Vị trí"), gbc(0, y));
        panel.add(txtLocation, gbc(1, y++));
        panel.add(new JLabel("Trạng thái"), gbc(0, y));
        panel.add(comboStatus, gbc(1, y++));
        JButton btnSave = new JButton("Thêm");
        panel.add(btnSave, gbc(1, y));
        add(panel, BorderLayout.CENTER);
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên phòng không được để trống.");
                    return;
                }
                Room r = new Room();
                r.setRoomName(name);
                r.setCapacity(((Number) spinCapacity.getValue()).intValue());
                r.setLocation(txtLocation.getText().trim());
                r.setStatus((String) comboStatus.getSelectedItem());
                roomService.create(r);
                JOptionPane.showMessageDialog(this, "Đã thêm phòng.");
                if (onSaved != null) onSaved.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });
        pack();
        setLocationRelativeTo(owner);
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
