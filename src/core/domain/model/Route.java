package domain.model;

import domain.model.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Representerer en rute med et rutenummer og en liste over rutestopp.
 *
 * Inneholder også status for ruten (aktiv/inaktiv) og kan beregne total distanse.
 *
 * Funksjonalitet inkluderer:
 * - Legge til stopp
 * - Beregne navn basert på første og siste stopp
 * - Beregne total distanse
 * - Hente start- og endestopp
 * - Sammenligning av ruter med equals/hashCode
 */
public class Route {

    // --- Felt ---
    private int id;                     // unik identifikator for ruten
    private int routeNum;               // rutenummer
    private List<RouteStop> stops;      // liste over stopp for ruten
    private boolean isActive = true;    // status: aktiv eller ikke

    // --- Konstruktører ---

    /**
     * Fullkonstruktør med ID for eksisterende rute.
     * @param id unik identifikator
     * @param routeNum rutenummer
     */
    public Route(int id, int routeNum) {
        this.id = id;
        this.routeNum = routeNum;
        this.stops = new ArrayList<>();
    }

    /**
     * Konstruktør for ny rute uten ID.
     * @param routeNum rutenummer
     */
    public Route(int routeNum) {
        this.routeNum = routeNum;
        this.stops = new ArrayList<>();
    }

    /**
     * Konstruktør med full informasjon, inkludert liste over stopp.
     * Setter stopp til tom liste hvis null.
     * @param id unik identifikator
     * @param routeNum rutenummer
     * @param stops liste over rutestopp
     */
    public Route(int id, int routeNum, List<RouteStop> stops) {
        this.id = id;
        this.routeNum = routeNum;
        this.stops = stops != null ? stops : new ArrayList<>();
    }

    // --- Metoder ---

    /**
     * Legger til et nytt stoppested hvis det ikke allerede finnes.
     * @param stop stoppsted som skal legges til
     */
    public void addStop(RouteStop stop) {
        if (stop != null && !stops.contains(stop)) {
            stops.add(stop);
        }
    }

    /**
     * Genererer rutenavn basert på første og siste stopp.
     * Format: "rutenummer fra - til"
     * @return rutenavn
     * @throws IllegalStateException hvis ruta har mindre enn to stopp
     */
    public String getName() {
        if (stops.size() < 2) {
            throw new IllegalStateException("Route must have at least two stops to generate a name.");
        }
        String from = stops.get(0).getStop().getName();
        String to = stops.get(stops.size() - 1).getStop().getName();
        return String.format("%d %s - %s", routeNum, from, to);
    }

    /**
     * Kalkulerer total distanse for ruten, og avrunder til 2 desimaler.
     * Summerer alle distanser mellom hvert stopp.
     * @return total distanse i km
     */
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

    /**
     * Henter første stopp (startsted).
     * @return første RouteStop
     * @throws IllegalStateException hvis ruta ikke har stopp
     */
    public RouteStop getStartStop() {
        if (stops == null || stops.isEmpty()) {
            throw new IllegalStateException("Route has no stops");
        }
        return stops.get(0);
    }

    /**
     * Henter siste stopp (sluttsted).
     * @return siste RouteStop
     * @throws IllegalStateException hvis ruta ikke har stopp
     */
    public RouteStop getEndStop() {
        if (stops == null || stops.isEmpty()) {
            throw new IllegalStateException("Route has no stops");
        }
        return stops.get(stops.size() - 1);
    }

    // --- Getters ---
    public int getId() { return id; }
    public int getRouteNum() { return routeNum; }
    public List<RouteStop> getStops() { return stops; }
    public boolean isActive() { return isActive; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setRouteNum(int routeNum) { this.routeNum = routeNum; }

    /**
     * Setter listen over stopp. Setter til tom liste hvis null.
     * @param stops liste over RouteStop
     */
    public void setStops(List<RouteStop> stops) {
        this.stops = stops != null ? stops : new ArrayList<>();
    }

    public void setActive(boolean isActive) { this.isActive = isActive; }

    // --- Overrides ---

    /**
     * Returnerer rutenavn og total distanse.
     * Eksempel: "12 Oslo - Bergen (456.78 km)"
     */
    @Override
    public String toString() {
        return getName() + " (" + calculateTotalDistance() + " km)";
    }

    /**
     * Sammenligner ruter:
     * - Hvis begge har ID, sammenlignes ID
     * - Ellers sammenlignes rutenummer og første/siste stopp
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route other = (Route) o;

        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        if (this.routeNum != other.routeNum) return false;
        if (this.stops.size() < 2 || other.stops.size() < 2) return false;

        var thisFrom = this.stops.get(0).getStop();
        var thisTo = this.stops.get(this.stops.size() - 1).getStop();
        var otherFrom = other.stops.get(0).getStop();
        var otherTo = other.stops.get(other.stops.size() - 1).getStop();

        return thisFrom.equals(otherFrom) && thisTo.equals(otherTo);
    }

    /**
     * Genererer hashkode basert på:
     * - ID hvis satt
     * - Ellers rutenummer + første og siste stopp
     */
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
