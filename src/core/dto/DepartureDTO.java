package dto;

import domain.model.Stops;
import java.time.LocalDate;
import java.time.LocalTime;

public class DepartureDTO {

    private int routeId;
    private int routeNumber;
    private int fromStopId;
    private String fromStopName;
    private int toStopId;
    private String toStopName;
    private LocalDate travelDate;
    private LocalTime plannedDeparture;
    private LocalTime arrivalTime;
    private boolean isExtra;
    private boolean isDelayed;
    private boolean isCancelled;
    private boolean isOmitted;
    private int delayMinutes;
    private String operationMessage;
    private EnvironmentDTO environmentSavings;



    // --- Konstruktør ---
    public DepartureDTO(int routeId, int fromStopId, String fromStopName,
                        int toStopId, String toStopName,
                        LocalDate travelDate, LocalTime plannedDeparture, LocalTime arrivalTime,
                        boolean isExtra, boolean isDelayed, boolean isCancelled, boolean isOmitted,
                        int delayMinutes, String operationMessage) {
        this.routeId = routeId;
        this.fromStopId = fromStopId;
        this.fromStopName = fromStopName;
        this.toStopId = toStopId;
        this.toStopName = toStopName;
        this.travelDate = travelDate;
        this.plannedDeparture = plannedDeparture;
        this.arrivalTime = arrivalTime;
        this.isExtra = isExtra;
        this.isDelayed = isDelayed;
        this.isCancelled = isCancelled;
        this.isOmitted = isOmitted;
        this.delayMinutes = delayMinutes;
        this.operationMessage = operationMessage;
    }

    public DepartureDTO() {
        // tom konstruktør for testing
    }

    // --- Gettere ---
    public int getRouteId() { return routeId; }
    public int getRouteNumber() { return routeNumber; }
    public int getFromStopId() { return fromStopId; }
    public String getFromStopName() { return fromStopName; }
    public int getToStopId() { return toStopId; }
    public String getToStopName() { return toStopName; }
    public LocalDate getTravelDate() { return travelDate; }
    public LocalTime getPlannedDeparture() { return plannedDeparture; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public boolean isExtra() { return isExtra; }
    public boolean isDelayed() { return isDelayed; }
    public boolean isCancelled() { return isCancelled; }
    public boolean isOmitted() { return isOmitted; }
    public int getDelayMinutes() { return delayMinutes; }
    public String getOperationMessage() { return operationMessage; }

    public EnvironmentDTO getEnvironmentSavings() {
        return environmentSavings;
    }

    // --- Settere ---
    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public void setFromStopId(int fromStopId) {
        this.fromStopId = fromStopId;
    }

    public void setFromStopName(String fromStopName) {
        this.fromStopName = fromStopName;
    }

    public void setToStopId(int toStopId) {
        this.toStopId = toStopId;
    }

    public void setToStopName(String toStopName) {
        this.toStopName = toStopName;
    }

    public void setTravelDate(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public void setPlannedDeparture(LocalTime plannedDeparture) {
        this.plannedDeparture = plannedDeparture;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setExtra(boolean extra) {
        isExtra = extra;
    }

    public void setDelayed(boolean delayed) {
        isDelayed = delayed;
    }

    public void setOmitted(boolean omitted) {
        isOmitted = omitted;
    }

    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public void setOperationMessage(String operationMessage) {
        this.operationMessage = operationMessage;
    }

    public void setEnvironmentSavings(EnvironmentDTO environmentSavings) {
        this.environmentSavings = environmentSavings;
    }
}
