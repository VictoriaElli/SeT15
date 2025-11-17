package dto;

import domain.model.TimeMode;

import java.time.LocalDate;
import java.time.LocalTime;

public class DepartureResponseDTO {

    private int routeNumber;
    private String fromStopName;
    private String toStopName;
    private LocalDate travelDate;
    private LocalTime relevantTime; // avhengig av TimeMode
    private LocalTime plannedDeparture;
    private LocalTime arrivalTime;
    private boolean isExtra;
    private boolean isDelayed;
    private boolean isCancelled;
    private boolean isOmitted;
    private int delayMinutes;
    private String operationMessage;

    public DepartureResponseDTO() {}

    public DepartureResponseDTO(DepartureDTO dto, TimeMode timeMode) {
        this.routeNumber = dto.getRouteNumber();
        this.fromStopName = dto.getFromStopName();
        this.toStopName = dto.getToStopName();
        this.travelDate = dto.getTravelDate();
        this.plannedDeparture = dto.getPlannedDeparture();
        this.arrivalTime = dto.getArrivalTime();
        this.isExtra = dto.isExtra();
        this.isDelayed = dto.isDelayed();
        this.isCancelled = dto.isCancelled();
        this.isOmitted = dto.isOmitted();
        this.delayMinutes = dto.getDelayMinutes();
        this.operationMessage = dto.getOperationMessage();

        // Sett relevant tid basert på TimeMode
        switch (timeMode) {
            case DEPART -> this.relevantTime = plannedDeparture;
            case ARRIVAL -> this.relevantTime = arrivalTime;
            case NOW -> this.relevantTime = LocalTime.now();
            default -> this.relevantTime = plannedDeparture;
        }
    }

    // --- Gettere ---
    public String getFromStopName() { return fromStopName; }
    public String getToStopName() { return toStopName; }
    public LocalDate getTravelDate() { return travelDate; }
    public LocalTime getRelevantTime() { return relevantTime; }
    public LocalTime getPlannedDeparture() { return plannedDeparture; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public boolean isExtra() { return isExtra; }
    public boolean isDelayed() { return isDelayed; }
    public boolean isCancelled() { return isCancelled; }
    public boolean isOmitted() { return isOmitted; }
    public int getDelayMinutes() { return delayMinutes; }
    public String getOperationMessage() { return operationMessage; }

    // --- Settere (valgfritt) ---
    public void setFromStopName(String fromStopName) { this.fromStopName = fromStopName; }
    public void setToStopName(String toStopName) { this.toStopName = toStopName; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public void setRelevantTime(LocalTime relevantTime) { this.relevantTime = relevantTime; }
    public void setPlannedDeparture(LocalTime plannedDeparture) { this.plannedDeparture = plannedDeparture; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setExtra(boolean extra) { isExtra = extra; }
    public void setDelayed(boolean delayed) { isDelayed = delayed; }
    public void setCancelled(boolean cancelled) { isCancelled = cancelled; }
    public void setOmitted(boolean omitted) { isOmitted = omitted; }
    public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }
    public void setOperationMessage(String operationMessage) { this.operationMessage = operationMessage; }
}
