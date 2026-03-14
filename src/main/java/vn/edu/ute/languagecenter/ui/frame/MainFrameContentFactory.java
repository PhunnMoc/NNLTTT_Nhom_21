package vn.edu.ute.languagecenter.ui.frame;

import vn.edu.ute.languagecenter.model.UserAccount;
import vn.edu.ute.languagecenter.ui.role.StaffMainFrameContent;
import vn.edu.ute.languagecenter.ui.role.TeacherStudentMainFrameContent;

public final class MainFrameContentFactory {

    private MainFrameContentFactory() {
    }

    public static MainFrameContent create(UserAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("account is null");
        }
        String role = account.getRole() == null ? "" : account.getRole().toUpperCase();
        String displayName = account.getUsername();
        switch (role) {
            case "STUDENT":
                return new TeacherStudentMainFrameContent(
                        displayName, account.getRole(), account.getRelatedId(), false);
            case "TEACHER":
                return new TeacherStudentMainFrameContent(
                        displayName, account.getRole(), account.getRelatedId(), true);
            case "SUPER_ADMIN":
                return new StaffMainFrameContent(displayName, account.getRole(), true);
            default:
                return new StaffMainFrameContent(displayName, account.getRole(), false);
        }
    }
}
