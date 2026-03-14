package vn.edu.ute.languagecenter.model;

import java.time.LocalTime;

public enum TimeSlot {

    TIET_1("Tiết 1", LocalTime.of(7, 0), LocalTime.of(8, 30)),
    TIET_2("Tiết 2", LocalTime.of(8, 45), LocalTime.of(10, 15)),
    TIET_3("Tiết 3", LocalTime.of(10, 30), LocalTime.of(12, 0)),
    TIET_4("Tiết 4", LocalTime.of(13, 30), LocalTime.of(15, 0)),
    TIET_5("Tiết 5", LocalTime.of(15, 15), LocalTime.of(16, 45)),
    TIET_6("Tiết 6", LocalTime.of(17, 0), LocalTime.of(18, 30));

    private final String label;
    private final LocalTime startTime;
    private final LocalTime endTime;

    TimeSlot(String label, LocalTime startTime, LocalTime endTime) {
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getLabel() {
        return label;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public static TimeSlot fromTimes(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            return null;
        }
        for (TimeSlot slot : values()) {
            if (slot.startTime.equals(start) && slot.endTime.equals(end)) {
                return slot;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return label + " (" + startTime + " - " + endTime + ")";
    }
}

