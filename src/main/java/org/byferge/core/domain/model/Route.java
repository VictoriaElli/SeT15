package org.byferge.core.domain.model;

import org.byferge.core.domain.model.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

// representerer en rute med et rutenummer og en liste av rutestopp, og status. beregner total distansen for ruten.
public class Route {
    private int id;
    private int routeNum;
    private List<RouteStop> stops;
    private boolean isActive = true;

    // Constructors
    // brukes for rute med eksisterende id
    public Route(int id, int routeNum) {
        this.id = id;
        this.routeNum = routeNum;
        this.stops = new ArrayList<>();
    }

    // brukes for ny rute med uten id
    public Route(int routeNum) {
        this.routeNum = routeNum;
        this.stops = new ArrayList<>();
    }

    // brukes for rute med full informasjon, inkludert liste over rutestopp. setter stopp til tom liste hvis null.
    public Route(int id, int routeNum, List<RouteStop> stops) {
        this.id = id;
        this.routeNum = routeNum;
        this.stops = stops != null ? stops : new ArrayList<>();
    }

    // Methods
    // legge til et nytt stoppested hvis den ikke allerede finnes
    public void addStop(RouteStop stop) {
        if (stop != null && !stops.contains(stop)) {
            stops.add(stop);
        }
    }

    // generer et navn basert på første og siste stopp
    public String getName() {
        if (stops.size() < 2) {
            throw new IllegalStateException("Route must have at least two stops to generate a name.");
        }
        String from = stops.get(0).getStop().getName();
        String to = stops.get(stops.size() - 1).getStop().getName();
        return String.format("%d %s - %s", routeNum, from, to);
    }

    // kalkulerer total distansen for ruten, og avrunder til 2 desimaler
    public double calculateTotalDistance() {
        if (stops.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (RouteStop stop : stops) {
            total += stop.getDistanceFromPrevious();
        }
        return MathUtil.round(total, 2);
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getRouteNum() {
        return routeNum;
    }

    public List<RouteStop> getStops() {
        return stops;
    }

    public boolean isActive() {
        return isActive;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setRouteNum(int routeNum) {
        this.routeNum = routeNum;
    }

    // setter listen over stopp. setter til tom liste hvis null.
    public void setStops(List<RouteStop> stops) {
        this.stops = stops != null ? stops : new ArrayList<>();
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    // Overrides
    // returnerer rutenavn + total distanse
    @Override
    public String toString() {
        return getName() + " (" + calculateTotalDistance() + " km)";
    }

    // sammenligner ruter først på ID hvis begge har det, ellers på rutenummer og første/siste stopp
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route other = (Route) o;

        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        if (this.routeNum != other.routeNum) return false;
        if (this.stops.size() < 2 || other.stops.size() < 2) {
            return false;
        }

        var thisFrom = this.stops.get(0).getStop();
        var thisTo = this.stops.get(this.stops.size() - 1).getStop();

        var otherFrom = other.stops.get(0).getStop();
        var otherTo = other.stops.get(other.stops.size() - 1).getStop();

        return thisFrom.equals(otherFrom) && thisTo.equals(otherTo);
    }

    // generarer hash kode basert på id hvis satt, ellers rutenummer + fra/til stopp
    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }

        int result = Integer.hashCode(routeNum);
        if (stops.size() >= 2) {
            var from = stops.get(0).getStop();
            var to = stops.get(stops.size() - 1).getStop();
            result = 31 * result + from.hashCode();
            result = 31 * result + to.hashCode();
        }
        return result;
    }
}
