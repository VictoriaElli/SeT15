package domain.model;

import java.time.LocalDate;
import java.time.DayOfWeek;

/**
 * Representerer ukedager som enum.
 *
 * Brukes for å knytte frekvenser eller unntak til spesifikke ukedager.
 * Inkluderer hjelpefunksjon for å konvertere fra {@link LocalDate}.
 */
public enum Weekday {
    MONDAY,     // Mandag
    TUESDAY,    // Tirsdag
    WEDNESDAY,  // Onsdag
    THURSDAY,   // Torsdag
    FRIDAY,     // Fredag
    SATURDAY,   // Lørdag
    SUNDAY;     // Søndag

    /**
     * Konverterer en {@link LocalDate} til tilsvarende {@link Weekday}.
     *
     * @param date Dato som skal konverteres
     * @return Ukedag som matcher datoen
     */
    public static Weekday fromLocalDate(LocalDate date) {
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
