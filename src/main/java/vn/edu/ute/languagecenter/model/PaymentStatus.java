package vn.edu.ute.languagecenter.model;

public enum PaymentStatus {

    PENDING,
    PAID,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
}

