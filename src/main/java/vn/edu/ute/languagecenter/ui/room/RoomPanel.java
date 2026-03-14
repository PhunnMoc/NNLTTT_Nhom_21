package vn.edu.ute.languagecenter.ui.room;

import vn.edu.ute.languagecenter.model.Room;
import vn.edu.ute.languagecenter.service.RoomService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class RoomPanel extends JPanel {

    private final String role;
    private final RoomService roomService = new RoomService();
    private final RoomTableModel tableModel = new RoomTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblSelected = new JLabel("Selected: (none)");
    private final JTextField txtKeyword = new JTextField(15);
    private final JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Tất cả", "ACTIVE", "INACTIVE"});
    private Room selectedRoom;

    public RoomPanel(String role) {
        this.role = role == null ? "" : role;
        setLayout(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.add(new JLabel("Từ khóa"));
        top.add(txtKeyword);
        top.add(new JLabel("Trạng thái"));
        top.add(comboStatus);
        JButton btnRefresh = new JButton("Làm mới");
        top.add(btnRefresh);
        if (canEditRoom()) {
            JButton btnAdd = new JButton("Thêm phòng");
            JButton btnEdit = new JButton("Sửa");
            JButton btnDelete = new JButton("Xóa");
            top.add(btnAdd);
            top.add(btnEdit);
            top.add(btnDelete);
            btnAdd.addActionListener(e -> {
                new AddRoomDialog(getFrame(), this::loadRooms).setVisible(true);
            });
            btnEdit.addActionListener(e -> {
                if (selectedRoom == null) {
                    JOptionPane.showMessageDialog(this, "Chọn một phòng.");
                    return;
                }
                new EditRoomDialog(getFrame(), selectedRoom, this::loadRooms).setVisible(true);
            });
            btnDelete.addActionListener(e -> deleteSelected());
        }

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> onRowSelected());
        JScrollPane scroll = new JScrollPane(table);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        btnRefresh.addActionListener(e -> loadRooms());
        txtKeyword.addActionListener(e -> loadRooms());
        comboStatus.addActionListener(e -> loadRooms());
        loadRooms();
    }

    private java.awt.Frame getFrame() {
        java.awt.Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
        return w instanceof java.awt.Frame ? (java.awt.Frame) w : null;
    }

    private boolean canEditRoom() {
        String r = this.role.toUpperCase();
        return r.equals("ADMIN") || r.equals("SUPER_ADMIN");
    }

    private void loadRooms() {
        try {
            String keyword = txtKeyword.getText();
            String status = (String) comboStatus.getSelectedItem();
            if ("Tất cả".equals(status)) {
                status = "";
            }
            tableModel.setData(roomService.search(keyword, status));
            selectedRoom = null;
            lblSelected.setText("Selected: (none)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        selectedRoom = tableModel.getAt(row);
        lblSelected.setText(selectedRoom != null ? "ID=" + selectedRoom.getId() + " | " + selectedRoom.getRoomName() : "Selected: (none)");
    }

    private void deleteSelected() {
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "Chọn một phòng.");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Xóa phòng \"" + selectedRoom.getRoomName() + "\"?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            roomService.deleteById(selectedRoom.getId());
            loadRooms();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
