package vn.edu.ute.languagecenter.model;

public enum EnrollmentStatus {

    PENDING,
    CONFIRMED,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
}

