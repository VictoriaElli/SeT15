package domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representerer en sesong i ruteplanleggingen.
 * Eksempler på sesonger kan være "Vinter 2025", "Sommer 2024", osv.
 */
public class Season {
    // --- Felt ---
    private int id;                     // unik identifikator for sesongen
    private String seasonType;          // type sesong (f.eks. "Vinter", "Sommer")
    private int year;                   // år for sesongen (f.eks. 2025)
    private LocalDate startDate;        // startdato for sesongen
    private LocalDate endDate;          // sluttdato for sesongen

    // --- Konstruktører ---

    /**
     * Konstruktør som brukes når sesongen allerede finnes i systemet (med ID).
     *
     * @param id ID for sesongen
     * @param seasonType type av sesongen (f.eks. "Vinter")
     * @param year år for sesongen (f.eks. 2025)
     * @param startDate startdato for sesongen
     * @param endDate sluttdato for sesongen
     */
    public Season(int id, String seasonType, int year, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.seasonType = seasonType;
        setYear(year);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    /**
     * Konstruktør for å opprette en ny sesong uten ID.
     *
     * @param seasonType type av sesongen (f.eks. "Vinter")
     * @param year år for sesongen (f.eks. 2025)
     * @param startDate startdato for sesongen
     * @param endDate sluttdato for sesongen
     */
    public Season(String seasonType, int year, LocalDate startDate, LocalDate endDate) {
        this.seasonType = seasonType;
        setYear(year);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    // --- Metoder ---

    /**
     * Sjekker om en gitt dato faller innenfor sesongen.
     *
     * @param date datoen som skal sjekkes
     * @return true hvis datoen er innenfor sesongen, ellers false
     */
    public boolean isActiveOn(LocalDate date) {
        if (startDate == null || endDate == null) return false;
        return (!date.isBefore(startDate) && !date.isAfter(endDate));
    }

    /**
     * Sjekker om denne sesongen overlapper med en annen sesong.
     *
     * @param other den andre sesongen som skal sjekkes mot
     * @return true hvis sesongene overlapper, ellers false
     */
    public boolean overlapsWith(Season other) {
        if (startDate == null || endDate == null ||
                other.startDate == null || other.endDate == null) return false;

        // Sjekker om sesongene overlapper, dvs. at sluttdatoen for én sesong ikke er før startdatoen til den andre,
        // og at startdatoen for én sesong ikke er etter sluttdatoen til den andre.
        return !endDate.isBefore(other.startDate) && !startDate.isAfter(other.endDate);
    }

    /**
     * Returnerer en lettleselig ID for sesongen i formatet "Type-År".
     *
     * @return en streng som representerer sesongens identifikator (f.eks. "Vinter-2025")
     */
    public String getIdentifier() {
        return seasonType + "-" + year;
    }

    // --- Validering ---

    /**
     * Validerer at startdatoen tilhører samme år som "year"-feltet,
     * og at startdatoen er før eller lik sluttdatoen.
     */
    private void validateDatesMatchYear() {
        if (year == 0) return;  // Hvis year er 0, gjør ingenting (unngår feil ved første opprettelse av sesong)

        if (startDate != null && startDate.getYear() != year) {
            throw new IllegalArgumentException("Start Date must be in the same year as the 'year' field");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start Date must be before or equal to End Date");
        }
    }

    // --- Getters ---

    public int getId() {
        return id;
    }

    public String getSeasonType() {
        return seasonType;
    }

    public int getYear() {
        return year;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    // --- Setters ---

    /**
     * Setter ID for sesongen.
     *
     * @param id ID for sesongen
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter type for sesongen (f.eks. "Vinter", "Sommer").
     *
     * @param seasonType type av sesongen
     */
    public void setSeasonType(String seasonType) {
        this.seasonType = seasonType;
    }

    /**
     * Setter år for sesongen, og validerer at det er et gyldig år (mellom 2000 og 2100).
     *
     * @param year år for sesongen
     */
    public void setYear(int year) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2000 and 2100");
        }
        this.year = year;
        validateDatesMatchYear();
    }

    /**
     * Setter startdatoen for sesongen og validerer at startdatoen er korrekt i forhold til år og sluttdato.
     *
     * @param startDate startdato for sesongen
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        validateDatesMatchYear();
    }

    /**
     * Setter sluttdatoen for sesongen og validerer at sluttdatoen er korrekt i forhold til år og startdato.
     *
     * @param endDate sluttdato for sesongen
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        validateDatesMatchYear();
    }

    // --- Overrides ---

    /**
     * Returnerer en lettleselig representasjon av sesongen som kan brukes til logging og visning.
     * Format: "Season[id={id}, {seasonType} {year}, {startDate} - {endDate}]"
     *
     * @return en lesbar streng som representerer sesongen
     */
    @Override
    public String toString() {
        return String.format("Season[id=%d, %s %d, %s - %s]",
                id, seasonType, year,
                startDate != null ? startDate.toString() : "ukjent",
                endDate != null ? endDate.toString() : "ukjent");
    }

    /**
     * To sesonger regnes som like hvis de har samme sesongtype og år.
     *
     * @param o objektet som skal sammenlignes
     * @return true hvis sesongene er like, ellers false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season)) return false;
        Season season = (Season) o;
        return year == season.year &&
                Objects.equals(seasonType, season.seasonType);
    }

    /**
     * Genererer en hash-kode basert på sesongtype og år.
     *
     * @return hash-koden for sesongen
     */
    @Override
    public int hashCode() {
        return Objects.hash(seasonType, year);
    }
}
