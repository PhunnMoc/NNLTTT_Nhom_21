package vn.edu.ute.languagecenter.ui.room;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class RoomScheduleAndListPanel extends JPanel {

    public RoomScheduleAndListPanel(String role) {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Lịch phòng", new RoomSchedulePanel());
        tabs.addTab("Danh sách phòng", new RoomPanel(role));
        add(tabs, BorderLayout.CENTER);
    }
}

