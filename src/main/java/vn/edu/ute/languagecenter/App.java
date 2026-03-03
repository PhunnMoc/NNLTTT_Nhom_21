package vn.edu.ute.languagecenter;

import vn.edu.ute.languagecenter.ui.MainFrame;
import vn.edu.ute.languagecenter.ui.UI;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        UI.initLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

