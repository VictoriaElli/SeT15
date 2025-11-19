package dto;

public class RouteStopDTO {
    private int id;
    private StopDTO stop;
    private int routeOrder;
    private int timeFromStart;
    private double distanceFromPrevious;

    public RouteStopDTO() {}

    public RouteStopDTO(int id, StopDTO stop, int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.id = id;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        this.distanceFromPrevious = distanceFromPrevious;
    }

    // --- Gettere / Settere ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public StopDTO getStop() { return stop; }
    public void setStop(StopDTO stop) { this.stop = stop; }

    public int getRouteOrder() { return routeOrder; }
    public void setRouteOrder(int routeOrder) { this.routeOrder = routeOrder; }

    public int getTimeFromStart() { return timeFromStart; }
    public void setTimeFromStart(int timeFromStart) { this.timeFromStart = timeFromStart; }

    public double getDistanceFromPrevious() { return distanceFromPrevious; }
    public void setDistanceFromPrevious(double distanceFromPrevious) { this.distanceFromPrevious = distanceFromPrevious; }
}
