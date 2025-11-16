package domain.model;

import domain.model.util.MathUtil;
import jakarta.persistence.*;

/**
 * Representerer et stopp på en rute, inkludert:
 * - Rekkefølge i ruten (routeOrder)
 * - Tid fra ruten starter (timeFromStart)
 * - Avstand fra forrige stopp (distanceFromPrevious)
 *
 * Hver RouteStop er koblet til både en Route og et Stop.
 */
@Entity
public class RouteStops {

    // --- Felt ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                         // Unik ID generert av databasen

    @ManyToOne
    @JoinColumn(name = "routeId")
    private Route route;                    // Ruten stoppet tilhører

    @ManyToOne
    @JoinColumn(name = "stopId")
    private Stops stop;                      // Selve stoppet

    @Column(name = "routeOrder")
    private int routeOrder;                 // Rekkefølge i ruten (1 = første stopp)

    @Column(name = "timeFromStart")
    private int timeFromStart;              // Tid i minutter fra ruten starter

    @Column(name = "distanceFromPrevious")
    private double distanceFromPrevious;    // Avstand fra forrige stopp i km (avrundet til 1 desimal)

    // --- Konstruktører ---

    /**
     * Full konstruktør med ID (brukes typisk ved lasting fra DB)
     */
    public RouteStops(int id, Route route, Stops stop,
                      int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        setDistanceFromPrevious(distanceFromPrevious);
        validateRouteStop();
    }

    /**
     * Konstruktør uten ID (nytt objekt som ennå ikke er lagret i DB)
     */
    public RouteStops(Route route, Stops stop,
                      int routeOrder, int timeFromStart, double distanceFromPrevious) {
        this.route = route;
        this.stop = stop;
        this.routeOrder = routeOrder;
        this.timeFromStart = timeFromStart;
        setDistanceFromPrevious(distanceFromPrevious);
        validateRouteStop();
    }

    // --- Validering ---

    /**
     * Sjekker at routeOrder >= 1, timeFromStart >= 0 og distanceFromPrevious >= 0
     */
    private void validateRouteStop() {
        if (routeOrder < 1) throw new IllegalArgumentException("routeOrder must be >= 1");
        if (timeFromStart < 0) throw new IllegalArgumentException("timeFromStart must be >= 0");
        if (distanceFromPrevious < 0) throw new IllegalArgumentException("distanceFromPrevious must be >= 0");
    }

    // --- Gettere ---

    public int getId() { return id; }
    public Route getRoute() { return route; }
    public Stops getStop() { return stop; }
    public int getRouteOrder() { return routeOrder; }
    public int getTimeFromStart() { return timeFromStart; }
    public double getDistanceFromPrevious() { return distanceFromPrevious; }

    // --- Settere ---

    public void setRoute(Route route) { this.route = route; }
    public void setStop(Stops stop) { this.stop = stop; }

    public void setRouteOrder(int routeOrder) {
        this.routeOrder = routeOrder;
        validateRouteStop();
    }

    public void setTimeFromStart(int timeFromStart) {
        this.timeFromStart = timeFromStart;
        validateRouteStop();
    }

    /**
     * Setter avstand fra forrige stopp og runder til 1 desimal.
     */
    public void setDistanceFromPrevious(double distanceFromPrevious) {
        this.distanceFromPrevious = MathUtil.round(distanceFromPrevious, 1);
        validateRouteStop();
    }

    // --- Overrides ---

    @Override
    public String toString() {
        return String.format("Route %s – Stop %s – #%d – +%d min – +%.2f km",
                route != null ? route.getRouteNum() : "null",
                stop != null ? stop.getName() : "null",
                routeOrder, timeFromStart, distanceFromPrevious);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteStops)) return false;
        RouteStops other = (RouteStops) o;

        // Hvis begge objekter har database-ID: sammenlign ID
        if (id != 0 && other.id != 0) return id == other.id;

        // Ellers sammenlign rute, stopp og rekkefølge
        return routeOrder == other.routeOrder &&
                (route == null ? other.route == null : route.equals(other.route)) &&
                (stop == null ? other.stop == null : stop.equals(other.stop));
    }

    @Override
    public int hashCode() {
        if (id != 0) return Integer.hashCode(id);
        int result = 17;
        result = 31 * result + (route != null ? route.hashCode() : 0);
        result = 31 * result + (stop != null ? stop.hashCode() : 0);
        result = 31 * result + routeOrder;
        return result;
    }
}
