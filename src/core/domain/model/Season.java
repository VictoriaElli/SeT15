package domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Representerer en sesong i ruteplanlegging, f.eks. "Vinter 2025".
 * Inneholder informasjon om type sesong, gyldighetsår og start/slutt-datoer.
 */
@Entity
public class Season {

    // --- Felt ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                     // Unik ID generert av databasen

    @Column(name = "seasonType")
    private String seasonType;          // Type sesong, f.eks. "Vinter" eller "Sommer"

    @Column(name = "validYear")
    private int validYear;              // År sesongen gjelder (f.eks. 2025)

    @Column(name = "startDate")
    private LocalDate startDate;        // Startdato for sesongen

    @Column(name = "endDate")
    private LocalDate endDate;          // Sluttdato for sesongen

    // --- Konstruktører ---

    /**
     * Full konstruktør for eksisterende sesjon med ID.
     */
    public Season(int id, String seasonType, int validYear, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.seasonType = seasonType;
        setValidYear(validYear);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    /**
     * Konstruktør for ny sesong uten ID (ID genereres av databasen).
     */
    public Season(String seasonType, int validYear, LocalDate startDate, LocalDate endDate) {
        this.seasonType = seasonType;
        setValidYear(validYear);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    // --- Metoder ---

    /**
     * Returnerer true hvis en gitt dato faller innenfor sesongens periode.
     */
    public boolean isActiveOn(LocalDate date) {
        if (startDate == null || endDate == null) return false;
        return (!date.isBefore(startDate) && !date.isAfter(endDate));
    }

    /**
     * Sjekker om denne sesongen overlapper med en annen sesong.
     */
    public boolean overlapsWith(Season other) {
        if (startDate == null || endDate == null ||
                other.startDate == null || other.endDate == null) return false;

        return !endDate.isBefore(other.startDate) && !startDate.isAfter(other.endDate);
    }

    /**
     * Returnerer en enkel identifikator på format "{seasonType}-{validYear}".
     */
    public String getIdentifier() {
        return seasonType + "-" + validYear;
    }

    // --- Validering ---

    /**
     * Sjekker at startdato og sluttdato matcher året og at startdato ikke er etter sluttdato.
     */
    private void validateDatesMatchYear() {
        if (validYear == 0) return;

        if (startDate != null && startDate.getYear() != validYear) {
            throw new IllegalArgumentException("Start Date must match year");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start Date must be before or equal to End Date");
        }
    }

    // --- Gettere ---

    public int getId() { return id; }

    public String getSeasonType() { return seasonType; }

    public int getValidYear() { return validYear; }

    public LocalDate getStartDate() { return startDate; }

    public LocalDate getEndDate() { return endDate; }

    // --- Settere ---

    public void setId(int id) { this.id = id; }

    public void setSeasonType(String seasonType) { this.seasonType = seasonType; }

    public void setValidYear(int validYear) {
        if (validYear < 2000 || validYear > 2100) {
            throw new IllegalArgumentException("Year must be between 2000 and 2100");
        }
        this.validYear = validYear;
        validateDatesMatchYear();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        validateDatesMatchYear();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        validateDatesMatchYear();
    }

    // --- Overrides ---

    /**
     * Returnerer en lesbar representasjon av sesongen:
     *  "Season[id=1, Vinter 2025, 2025-01-01 - 2025-04-01]"
     */
    @Override
    public String toString() {
        return String.format("Season[id=%d, %s %d, %s - %s]",
                id, seasonType, validYear,
                startDate != null ? startDate : "ukjent",
                endDate != null ? endDate : "ukjent");
    }

    /**
     * To sesonger er like hvis de har samme type og år.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season)) return false;
        Season season = (Season) o;
        return validYear == season.validYear &&
                Objects.equals(seasonType, season.seasonType);
    }

    /**
     * Hash genereres fra sesongtype og år.
     */
    @Override
    public int hashCode() {
        return Objects.hash(seasonType, validYear);
    }
}
