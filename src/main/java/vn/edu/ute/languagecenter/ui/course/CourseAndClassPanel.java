package vn.edu.ute.languagecenter.ui.course;

import vn.edu.ute.languagecenter.ui.courseclass.ClassPanel;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class CourseAndClassPanel extends JPanel {

    public CourseAndClassPanel(String role) {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Danh mục khóa học", new CoursePanel(role));
        tabs.addTab("Lớp học", new ClassPanel(role));
        add(tabs, BorderLayout.CENTER);
    }
}
