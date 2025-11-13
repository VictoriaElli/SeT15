package domain.model;

import java.time.LocalDate;
import java.time.DayOfWeek;

public enum Weekday {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    // Utility method to convert LocalDate to Weekday
    public static Weekday fromLocalDate(LocalDate date) {
        // Get the DayOfWeek enum from LocalDate and map it to the Weekday enum.
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        switch (dayOfWeek) {
            case MONDAY: return MONDAY;
            case TUESDAY: return TUESDAY;
            case WEDNESDAY: return WEDNESDAY;
            case THURSDAY: return THURSDAY;
            case FRIDAY: return FRIDAY;
            case SATURDAY: return SATURDAY;
            case SUNDAY: return SUNDAY;
            default: throw new IllegalArgumentException("Unknown day: " + dayOfWeek);
        }
    }
}
