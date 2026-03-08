package vn.edu.ute.languagecenter.model;

public enum UserRole {
    SUPER_ADMIN,
    ADMIN,
    STAFF,
    CONSULTANT,
    ACCOUNTANT,
    TEACHER,
    STUDENT;

    private static final UserRole[] STAFF_TAB_ROLES = {
            ADMIN, STAFF, CONSULTANT, ACCOUNTANT
    };

    public static UserRole[] getStaffTabRoles() {
        return STAFF_TAB_ROLES.clone();
    }

    public static boolean isStaffTabRole(String role) {
        if (role == null || role.isBlank()) return false;
        String v = role.toUpperCase().trim();
        for (UserRole r : STAFF_TAB_ROLES) {
            if (r.name().equals(v)) return true;
        }
        return false;
    }
}

