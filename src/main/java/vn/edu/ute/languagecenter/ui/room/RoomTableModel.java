package vn.edu.ute.languagecenter.ui.room;

import vn.edu.ute.languagecenter.model.Room;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class RoomTableModel extends AbstractTableModel {

    private final String[] columns = {"ID", "Tên phòng", "Sức chứa", "Vị trí", "Trạng thái"};
    private final List<Room> data = new ArrayList<>();

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Room r = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> r.getId();
            case 1 -> r.getRoomName();
            case 2 -> r.getCapacity();
            case 3 -> r.getLocation();
            case 4 -> r.getStatus();
            default -> "";
        };
    }

    public void setData(List<Room> list) {
        data.clear();
        data.addAll(list);
        fireTableDataChanged();
    }

    public Room getAt(int row) {
        if (row < 0 || row >= data.size()) return null;
        return data.get(row);
    }
}
