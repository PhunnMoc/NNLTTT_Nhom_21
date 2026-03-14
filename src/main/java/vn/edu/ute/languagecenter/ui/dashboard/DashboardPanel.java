package vn.edu.ute.languagecenter.ui.dashboard;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.add(createStatCard("Học viên đang học", "0"));
        statsPanel.add(createStatCard("Lớp đang mở", "0"));
        statsPanel.add(createStatCard("Doanh thu tháng này", "0"));
        statsPanel.add(createStatCard("Tỉ lệ chuyên cần", "0%"));
        statsPanel.add(createStatCard("Học viên mới tháng này", "0"));
        statsPanel.add(createStatCard("Công nợ hiện tại", "0"));

        JPanel middle = new JPanel(new GridLayout(1, 2, 10, 10));
        middle.add(createSchedulePanel());
        middle.add(createChartPlaceholderPanel());

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 10));
        bottom.add(createDebtTablePanel());
        bottom.add(createEndingClassesPanel());

        add(statsPanel, BorderLayout.NORTH);
        add(middle, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblTitle = new JLabel(title);
        JLabel lblValue = new JLabel(value);
        lblValue.setHorizontalAlignment(JLabel.RIGHT);
        p.add(lblTitle, BorderLayout.WEST);
        p.add(lblValue, BorderLayout.EAST);
        p.setPreferredSize(new Dimension(0, 80));
        return p;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Lịch học tuần này"));
        String[] cols = {"Ngày", "Giờ", "Lớp", "Phòng", "Giáo viên"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createChartPlaceholderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel revenue = new JPanel(new BorderLayout());
        revenue.setBorder(BorderFactory.createTitledBorder("Doanh thu theo tháng"));
        revenue.add(new JLabel("Biểu đồ doanh thu (placeholder)"), BorderLayout.CENTER);

        JPanel level = new JPanel(new BorderLayout());
        level.setBorder(BorderFactory.createTitledBorder("Số lượng học viên theo level"));
        level.add(new JLabel("Biểu đồ level (placeholder)"), BorderLayout.CENTER);

        panel.add(revenue);
        panel.add(level);
        return panel;
    }

    private JPanel createDebtTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Top 5 học viên công nợ cao nhất"));
        String[] cols = {"Học viên", "Số điện thoại", "Công nợ"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEndingClassesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Các lớp sắp kết thúc"));
        String[] cols = {"Lớp", "Khóa học", "Ngày kết thúc"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
}
