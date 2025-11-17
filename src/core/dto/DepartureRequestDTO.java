package dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class DepartureRequestDTO {

    private String fromStop;
    private String toStop;
    private LocalDate travelDate;
    private LocalTime travelTime;

    // --- Constructor ---
    public DepartureRequestDTO(String fromStop, String toStop, LocalDate travelDate, LocalTime travelTime) {
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.travelDate = travelDate;
        this.travelTime = travelTime;
    }

    // --- Getters ---
    public String getFromStop() {
        return fromStop;
    }

    public String getToStop() {
        return toStop;
    }

    public LocalDate getTravelDate() {
        return travelDate;
    }

    public LocalTime getTravelTime() {
        return travelTime;
    }

    // --- Setters ---
    public void setFromStop(String fromStop) {
        this.fromStop = fromStop;
    }

    public void setToStop(String toStop) {
        this.toStop = toStop;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public void setTravelTime(LocalTime travelTime) {
        this.travelTime = travelTime;
    }

    // --- Override toString() ---
    @Override
    public String toString() {
        return "DepartureRequestDTO{" +
                "fromStop='" + fromStop + '\'' +
                ", toStop='" + toStop + '\'' +
                ", travelDate=" + travelDate +
                ", travelTime=" + travelTime +
                '}';
    }

    // --- Override equals() and hashCode() ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DepartureRequestDTO that = (DepartureRequestDTO) o;
        return Objects.equals(fromStop, that.fromStop) &&
                Objects.equals(toStop, that.toStop) &&
                Objects.equals(travelDate, that.travelDate) &&
                Objects.equals(travelTime, that.travelTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromStop, toStop, travelDate, travelTime);
    }
}
