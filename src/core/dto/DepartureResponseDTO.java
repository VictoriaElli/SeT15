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
    private int delayMinutes;
    private String operationMessage;
    private EnvironmentDTO environmentSavings;


    public DepartureResponseDTO() {}

    public DepartureResponseDTO(DepartureDTO dto, TimeMode timeMode) {
        this.routeNumber = dto.getRouteNumber();
        this.fromStopName = dto.getFromStopName();
        this.toStopName = dto.getToStopName();
        this.travelDate = dto.getTravelDate();
        this.plannedDeparture = dto.getPlannedDeparture();
        this.arrivalTime = dto.getArrivalTime();
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

    public static DepartureResponseDTO fromDepartureDTO(DepartureDTO dto) {
        DepartureResponseDTO resp = new DepartureResponseDTO();
        resp.setRouteNumber(dto.getRouteNumber());
        resp.setFromStopName(dto.getFromStopName());
        resp.setToStopName(dto.getToStopName());
        resp.setTravelDate(dto.getTravelDate());
        resp.setPlannedDeparture(dto.getPlannedDeparture());
        resp.setArrivalTime(dto.getArrivalTime());
        resp.setDelayMinutes(dto.getDelayMinutes());
        resp.setOperationMessage(dto.getOperationMessage());
        return resp;
    }



    // --- Gettere ---
    public int getRouteNumber() {return routeNumber;}
    public String getFromStopName() { return fromStopName; }
    public String getToStopName() { return toStopName; }
    public LocalDate getTravelDate() { return travelDate; }
    public LocalTime getRelevantTime() { return relevantTime; }
    public LocalTime getPlannedDeparture() { return plannedDeparture; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public int getDelayMinutes() { return delayMinutes; }
    public String getOperationMessage() { return operationMessage; }
    public EnvironmentDTO getEnvironmentSavings() {
        return environmentSavings;
    }


    // --- Settere ---
    public void setRouteNumber(int routeNumber) {this.routeNumber = routeNumber;}
    public void setFromStopName(String fromStopName) { this.fromStopName = fromStopName; }
    public void setToStopName(String toStopName) { this.toStopName = toStopName; }
    public void setTravelDate(LocalDate travelDate) { this.travelDate = travelDate; }
    public void setRelevantTime(LocalTime relevantTime) { this.relevantTime = relevantTime; }
    public void setPlannedDeparture(LocalTime plannedDeparture) { this.plannedDeparture = plannedDeparture; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setDelayMinutes(int delayMinutes) { this.delayMinutes = delayMinutes; }
    public void setOperationMessage(String operationMessage) { this.operationMessage = operationMessage; }

    public void setEnvironmentSavings(EnvironmentDTO environmentSavings) {
        this.environmentSavings = environmentSavings;
    }
}
