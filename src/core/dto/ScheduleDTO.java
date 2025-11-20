package dto;

import java.time.LocalTime;
import java.util.List;

public class ScheduleDTO {
    private int routeNumber;
    private String fromStopName;
    private String toStopName;
    private List<LocalTime> plannedDepartures;

    public ScheduleDTO(int routeNumber, String fromStopName, String toStopName,  List<LocalTime> plannedDepartures) {
        this.routeNumber = routeNumber;
        this.fromStopName = fromStopName;
        this.toStopName = toStopName;
        this.plannedDepartures = plannedDepartures;
    }

    public int getRouteNumber() { return routeNumber; }
    public String getFromStopName() { return fromStopName; }
    public String getToStopName() { return toStopName; }
    public List<LocalTime> getPlannedDepartures() { return plannedDepartures; }

    public void setRouteNumber(int routeNumber) { this.routeNumber = routeNumber; }
    public void setFromStopName(String fromStopName) { this.fromStopName = fromStopName; }
    public void setToStopName(String toStopName) { this.toStopName = toStopName; }
}
