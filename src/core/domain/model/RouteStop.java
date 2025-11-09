package domain.model;

import domain.model.util.MathUtil;

// representerer en stopp i en rute, inneholder info om rekkefølge, tid fra start og avstand fra forrige stopp.
public class RouteStop {
    private int id;
    private Route route;
    private Stop stop;
    private int routeOrder;
    private int timeFromStart;
    private double distanceFromPrevious;

    // Constructors
    // brukes for et eksisterende rutestopp med ID i systemet.
    public RouteStop(int id, Route route, Stop stop, int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        setDistanceFromPrevious(distanceFromPrevious);
        validateRouteStop();
    }

    // brukes for et nytt rutestopp uten ID.
    public RouteStop(Route route, Stop stop, int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.route = route;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        setDistanceFromPrevious(distanceFromPrevious);
        validateRouteStop();
    }

    // Getters
    public Route getRoute() {
        return route;
    }

    public Stop getStop() {
        return stop;
    }

    public int getRouteOrder() {
        return routeOrder;
    }

    public int getTimeFromStart() {
        return timeFromStart;
    }

    public double getDistanceFromPrevious() {
        return distanceFromPrevious;
    }

    // Setters
    public void setRoute(Route route) {
        this.route = route;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    public void setRouteOrder(int routeOrder) {
        this.routeOrder = routeOrder;
        validateRouteStop();
    }

    public void setTimeFromStart(int timeFromStart) {
        this.timeFromStart = timeFromStart;
        validateRouteStop();
    }

    // setter avstand fra forrige stopp, og avrunder til 1 desimal.
    public void setDistanceFromPrevious(double distanceFromPrevious) {
        this.distanceFromPrevious = MathUtil.round(distanceFromPrevious, 1);
        validateRouteStop();
    }

    // Validation
    // en valideringsmetode for å sjekke at dataene er logiske
    private void validateRouteStop() {
        if (routeOrder < 1) throw new IllegalArgumentException("routeOrder must be >= 1");
        if (timeFromStart < 0) throw new IllegalArgumentException("timeFromStart must be >= 0");
        if (distanceFromPrevious < 0) throw new IllegalArgumentException("distanceFromPrevious must be >= 0");
    }

    // Overrides
    // returnerer en lettleselig streng som beskriver rutestoppet.
    @Override
    public String toString() {
        return String.format("Route %s – Stop %s – #%d – +%d min – +%.2f km",
                route != null ? route.getRouteNum() : "null",
                stop != null ? stop.getName() : "null",
                routeOrder, timeFromStart, distanceFromPrevious);
    }

    // sammenligner rutestopp basert på ID hvis begge har det, ellers sjekkes rute, stopp og rekkefølge.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStop)) return false;
        RouteStop other = (RouteStop) o;

        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        return routeOrder == other.routeOrder
                && (route == null ? other.route == null : route.equals(other.route))
                && (stop == null ? other.stop == null : stop.equals(other.stop));
    }

    // generer en hash kode basert på id hvis satt, ellers rute, stopp og rekkefølge.
    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        int result = 17;
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (stop != null ? stop.hashCode() : 0);
        result = 31 * result + routeOrder;
        return result;
    }
}
