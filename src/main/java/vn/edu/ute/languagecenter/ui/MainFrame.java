package vn.edu.ute.languagecenter.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        super("Language Center Management (Swing + JPA + MySQL)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Học viên", new StudentPanel());
        tabs.addTab("Giáo viên", new TeacherPanel());
        tabs.addTab("Khóa học", new CoursePanel());

        add(tabs, BorderLayout.CENTER);

        setSize(900, 500);
        setLocationRelativeTo(null);
    }
}

