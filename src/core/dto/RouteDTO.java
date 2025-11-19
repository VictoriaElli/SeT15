package dto;

import java.util.List;

public class RouteDTO {
    private int id;
    private int routeNum;
    private StopDTO fromStop;
    private StopDTO toStop;
    private boolean isActive;
    private List<RouteStopDTO> stops;

    public RouteDTO() {}

    public RouteDTO(int id, int routeNum, StopDTO fromStop, StopDTO toStop, boolean isActive, List<RouteStopDTO> stops) {
        this.id = id;
        this.routeNum = routeNum;
        this.fromStop = fromStop;
        this.toStop = toStop;
        this.isActive = isActive;
        this.stops = stops;
    }

    // --- Gettere / Settere ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRouteNum() { return routeNum; }
    public void setRouteNum(int routeNum) { this.routeNum = routeNum; }

    public StopDTO getFromStop() { return fromStop; }
    public void setFromStop(StopDTO fromStop) { this.fromStop = fromStop; }

    public StopDTO getToStop() { return toStop; }
    public void setToStop(StopDTO toStop) { this.toStop = toStop; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<RouteStopDTO> getStops() { return stops; }
    public void setStops(List<RouteStopDTO> stops) { this.stops = stops; }
}

