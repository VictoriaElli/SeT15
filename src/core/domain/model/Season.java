package domain.model;

import java.time.LocalDate;
import java.util.Objects;

// representerer en sesong i ruteplanleggingen, eksempel "Vinter 2025"
public class Season {
    private int id;
    private String seasonType;
    private int year;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructors
    // brukes når sesongen allerede finnes i systemet.
    public Season(int id, String seasonType, int year, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.seasonType = seasonType;
        setYear(year);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    // brukes for å opprette en ny sesong uten ID.
    public Season(String seasonType, int year, LocalDate startDate, LocalDate endDate) {
        this.seasonType = seasonType;
        setYear(year);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    // Methods
    // sjekker om dato er innenfor sesong
    public boolean isActiveOn(LocalDate date) {
        if (startDate == null || endDate == null) return false;
        return (!date.isBefore(startDate) && !date.isAfter(endDate));
    }


    // sjekker overlapp med annen sesong
    public boolean overlapsWith(Season other) {
        if (startDate == null || endDate == null ||
                other.startDate == null || other.endDate == null) return false;

        return !endDate.isBefore(other.startDate) && !startDate.isAfter(other.endDate);
    }


    // lesbar ID
    public String getIdentifier() {
        return seasonType + "-" + year;
    }


    // validering av år, startdato og sluttdato. sikrer at startdato tilhører samme år som "year"-feltet,
    // og at startdato er før eller lik sluttdato.
    private void validateDatesMatchYear() {
        if (year == 0) return;

        if (startDate != null && startDate.getYear() != year) {
            throw new IllegalArgumentException("Start Date must be in the same year as the 'year' field");
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start Date must be before or equal to End Date");
        }
    }

    // Getters
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

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSeasonType(String seasonType) {
        this.seasonType = seasonType;
    }

    public void setYear(int year) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2000 and 2100");
        }
        this.year = year;
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

    // Overrides
    //returnerer lesbar representasjon av sesongen.
    @Override
    public String toString() {
        return String.format("Season[id=%d, %s %d, %s - %s]",
                id, seasonType, year,
                startDate != null ? startDate.toString() : "ukjent",
                endDate != null ? endDate.toString() : "ukjent");
    }

    // to sesonger regnes som like om de har samme type og år.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Season)) return false;
        Season season = (Season) o;
        return year == season.year &&
                Objects.equals(seasonType, season.seasonType);
    }

    // genererer hash basert på seasonType og year.
    @Override
    public int hashCode() {
        return Objects.hash(seasonType, year);
    }

}
