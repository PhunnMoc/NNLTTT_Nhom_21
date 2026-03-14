package vn.edu.ute.languagecenter.model;

import java.time.DayOfWeek;

public enum StudyDay {

    MON("Thứ 2", DayOfWeek.MONDAY),
    TUE("Thứ 3", DayOfWeek.TUESDAY),
    WED("Thứ 4", DayOfWeek.WEDNESDAY),
    THU("Thứ 5", DayOfWeek.THURSDAY),
    FRI("Thứ 6", DayOfWeek.FRIDAY),
    SAT("Thứ 7", DayOfWeek.SATURDAY),
    SUN("Chủ nhật", DayOfWeek.SUNDAY);

    private final String label;
    private final DayOfWeek dayOfWeek;

    StudyDay(String label, DayOfWeek dayOfWeek) {
        this.label = label;
        this.dayOfWeek = dayOfWeek;
    }

    public String getLabel() {
        return label;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    @Override
    public String toString() {
        return label;
    }
}

