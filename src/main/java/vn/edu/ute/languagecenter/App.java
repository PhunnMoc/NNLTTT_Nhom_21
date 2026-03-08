package vn.edu.ute.languagecenter;

import vn.edu.ute.languagecenter.ui.LoginFrame;
import vn.edu.ute.languagecenter.ui.UI;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        UI.initLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}

