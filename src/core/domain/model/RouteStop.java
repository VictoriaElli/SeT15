package domain.model;

import domain.model.util.MathUtil;

/**
 * Representerer et stopp i en rute.
 *
 * Et rutestopp inneholder informasjon om rekkefølge på stoppet, tid fra startpunktet
 * og avstand fra forrige stopp.
 *
 * Funksjonaliteten inkluderer:
 * - Sette og validere stoppets data (rekkefølge, tid, avstand)
 * - Beregne en lettleselig beskrivelse
 * - Sammenligne rutestopp
 */
public class RouteStop {
    // --- Felt ---
    private int id;                     // unik identifikator for rutestoppet
    private Route route;                // ruten som stoppet tilhører
    private Stop stop;                  // stoppestedet for dette stoppet
    private int routeOrder;             // rekkefølge av stoppet på ruten
    private int timeFromStart;          // tid fra startpunktet (i minutter)
    private double distanceFromPrevious; // avstand fra forrige stopp (i km)

    // --- Konstruktører ---

    /**
     * Konstruktør for eksisterende rutestopp med ID.
     *
     * @param id unik identifikator for stoppet
     * @param route ruten som stoppet tilhører
     * @param stop stoppestedet for dette stoppet
     * @param routeOrder rekkefølgen av stoppet på ruten
     * @param timeFromStart tid fra startpunktet (i minutter)
     * @param distanceFromPrevious avstand fra forrige stopp (i km)
     */
    public RouteStop(int id, Route route, Stop stop, int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        setDistanceFromPrevious(distanceFromPrevious);  // setter og validerer avstanden
        validateRouteStop();  // validerer at stoppet er logisk
    }

    /**
     * Konstruktør for nytt rutestopp uten ID.
     *
     * @param route ruten som stoppet tilhører
     * @param stop stoppestedet for dette stoppet
     * @param routeOrder rekkefølgen av stoppet på ruten
     * @param timeFromStart tid fra startpunktet (i minutter)
     * @param distanceFromPrevious avstand fra forrige stopp (i km)
     */
    public RouteStop(Route route, Stop stop, int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.route = route;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        setDistanceFromPrevious(distanceFromPrevious);  // setter og validerer avstanden
        validateRouteStop();  // validerer at stoppet er logisk
    }

    // --- Getters ---
    public Route getRoute() { return route; }
    public Stop getStop() { return stop; }
    public int getRouteOrder() { return routeOrder; }
    public int getTimeFromStart() { return timeFromStart; }
    public double getDistanceFromPrevious() { return distanceFromPrevious; }

    // --- Setters ---
    public void setRoute(Route route) { this.route = route; }
    public void setStop(Stop stop) { this.stop = stop; }

    /**
     * Setter rekkefølgen av stoppet og validerer at den er logisk (>= 1).
     * @param routeOrder rekkefølgen av stoppet på ruten
     */
    public void setRouteOrder(int routeOrder) {
        this.routeOrder = routeOrder;
        validateRouteStop();  // validerer at rekkefølgen er korrekt
    }

    /**
     * Setter tid fra startpunktet og validerer at den er logisk (>= 0).
     * @param timeFromStart tid fra startpunktet (i minutter)
     */
    public void setTimeFromStart(int timeFromStart) {
        this.timeFromStart = timeFromStart;
        validateRouteStop();  // validerer at tiden er korrekt
    }

    /**
     * Setter avstanden fra forrige stopp og runder den til 1 desimal.
     * Validerer at avstanden er logisk (>= 0).
     * @param distanceFromPrevious avstand fra forrige stopp (i km)
     */
    public void setDistanceFromPrevious(double distanceFromPrevious) {
        this.distanceFromPrevious = MathUtil.round(distanceFromPrevious, 1);
        validateRouteStop();  // validerer at avstanden er korrekt
    }

    // --- Validering ---

    /**
     * Validerer at dataene for rutestoppet er logiske:
     * - routeOrder >= 1
     * - timeFromStart >= 0
     * - distanceFromPrevious >= 0
     */
    private void validateRouteStop() {
        if (routeOrder < 1) throw new IllegalArgumentException("routeOrder must be >= 1");
        if (timeFromStart < 0) throw new IllegalArgumentException("timeFromStart must be >= 0");
        if (distanceFromPrevious < 0) throw new IllegalArgumentException("distanceFromPrevious must be >= 0");
    }

    // --- Overrides ---

    /**
     * Returnerer en lettleselig beskrivelse av rutestoppet i formatet:
     * "Route {rutenummer} – Stop {stoppnavn} – #{rekkefølge} – +{tid} min – +{avstand} km"
     * @return beskrivelse av rutestoppet
     */
    @Override
    public String toString() {
        return String.format("Route %s – Stop %s – #%d – +%d min – +%.2f km",
                route != null ? route.getRouteNum() : "null",
                stop != null ? stop.getName() : "null",
                routeOrder, timeFromStart, distanceFromPrevious);
    }

    /**
     * Sammenligner rutestopp basert på ID hvis begge har ID.
     * Hvis ikke, sammenlignes ruten, stoppet og rekkefølgen.
     * @param o objektet som skal sammenlignes
     * @return true hvis objektene er like, ellers false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStop)) return false;
        RouteStop other = (RouteStop) o;

        // Sammenligner ID hvis begge har ID
        if (this.id != 0 && other.id != 0) {
            return this.id == other.id;
        }

        // Hvis ID ikke er satt, sammenlignes ruten, stopp og rekkefølge
        return routeOrder == other.routeOrder
                && (route == null ? other.route == null : route.equals(other.route))
                && (stop == null ? other.stop == null : stop.equals(other.stop));
    }

    /**
     * Genererer en hash-kode basert på ID hvis satt, ellers rute, stopp og rekkefølge.
     * @return hash-kode
     */
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
