package domain.model;

import java.time.LocalDate;

public enum Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static Weekday fromLocalDate(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return MONDAY;
            case TUESDAY: return TUESDAY;
            case WEDNESDAY: return WEDNESDAY;
            case THURSDAY: return THURSDAY;
            case FRIDAY: return FRIDAY;
            case SATURDAY: return SATURDAY;
            case SUNDAY: return SUNDAY;
            default: throw new IllegalArgumentException("Unknown day: " + date.getDayOfWeek());
        }
    }
}

