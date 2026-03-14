package vn.edu.ute.languagecenter.ui.frame;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    private final MainFrameContent content;

    public MainFrame(String displayName, String role, MainFrameContent content) {
        super("Language Center Management (Swing + JPA + MySQL)");
        this.content = content;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setJMenuBar(content.getMenuBar());
        add(content.getToolBar(), BorderLayout.NORTH);
        add(content.getSidebar(), BorderLayout.WEST);
        add(content.getMainContent(), BorderLayout.CENTER);

        content.setLogoutCallback(this::doLogout);
        content.setExitCallback(this::doExit);

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void doLogout() {
        int c = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Đăng xuất", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        SwingUtilities.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void doExit() {
        int c = JOptionPane.showConfirmDialog(this, "Thoát ứng dụng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
}
