package vn.edu.ute.languagecenter.ui.role;

import vn.edu.ute.languagecenter.model.Payment;
import vn.edu.ute.languagecenter.service.PaymentService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.util.List;

public class AdminInvoicesPanel extends JPanel {

    private final PaymentService paymentService = new PaymentService();
    private final InvoiceTableModel tableModel = new InvoiceTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblInfo = new JLabel("Tổng số hóa đơn: 0 | Tổng tiền: 0");

    public AdminInvoicesPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        top.add(btnRefresh);
        add(top, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblInfo, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        try {
            List<Payment> list = paymentService.findAll();
            tableModel.setData(list);
            BigDecimal total = BigDecimal.ZERO;
            for (Payment p : list) {
                if (p.getAmount() != null) {
                    total = total.add(p.getAmount());
                }
            }
            lblInfo.setText("Tổng số hóa đơn: " + list.size() + " | Tổng tiền: " + total.toPlainString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

