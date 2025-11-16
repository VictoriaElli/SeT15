package domain.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Representerer en rute med:
 * - Rutenummer
 * - Start- og sluttstopp
 * - Liste over rutestopp (RouteStop)
 *
 * Stoppene mellom start og slutt genereres fra en ekstern liste med alle RouteStop-objekter.
 */
@Entity
public class Route {

    // --- Felt ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                         // Unik ID generert av databasen

    @Column(name = "num")
    private int routeNum;                   // Rutenummer

    @ManyToOne
    @JoinColumn(name = "fromStopId")
    private Stops fromStop;                  // Startstopp

    @ManyToOne
    @JoinColumn(name = "toStopId")
    private Stops toStop;                    // Sluttstopp

    @Column(name = "isActive")
    private boolean isActive = true;        // Om ruten er aktiv

    @Transient
    private List<RouteStops> stops;          // Generert liste over stopp (ikke lagret i DB)

    @Transient
    private List<RouteStops> allRouteStops;  // Tilgjengelige stopp for alle ruter (settes eksternt)

    // --- Konstruktører ---

    public Route(int id, int routeNum, Stops fromStop, Stops toStop) {
        this.id = id;
        this.routeNum = routeNum;
        this.fromStop = fromStop;
        this.toStop = toStop;
    }

    public Route(int routeNum, Stops fromStop, Stops toStop) {
        this.routeNum = routeNum;
        this.fromStop = fromStop;
        this.toStop = toStop;
    }

    // --- Metoder ---

    /**
     * Returnerer rutenavnet som "<rutenummer> <fraStop> - <tilStop>".
     */
    public String getName() {
        if (fromStop == null || toStop == null) {
            throw new IllegalStateException("Route must have a valid fromStop and toStop.");
        }
        return String.format("%d %s - %s", routeNum, fromStop.getName(), toStop.getName());
    }

    /**
     * Bygger og returnerer listen over RouteStop:
     * - Første stopp: fromStop
     * - Deretter mellomliggende stopp fra allRouteStops
     * - Til slutt toStop
     */
    public List<RouteStops> getStops() {
        if (stops == null) {
            stops = new ArrayList<>();

            // Startstopp
            stops.add(new RouteStops(this, fromStop, 0, 0, 0));

            // Mellomliggende stopp
            for (RouteStops rs : getRouteStopsForRoute(this)) {
                if (!rs.getStop().equals(fromStop) && !rs.getStop().equals(toStop)) {
                    stops.add(rs);
                }
            }

            // Sluttstopp (standardverdier for tid og avstand)
            if (!stops.isEmpty()) {
                RouteStops last = stops.get(stops.size() - 1);
                stops.add(new RouteStops(
                        this,
                        toStop,
                        stops.size(),
                        last.getTimeFromStart() + 5,
                        20
                ));
            }
        }
        return stops;
    }

    /**
     * Henter alle RouteStop-objekter som tilhører denne ruten.
     */
    public List<RouteStops> getRouteStopsForRoute(Route route) {
        List<RouteStops> list = new ArrayList<>();
        if (allRouteStops == null) return list;

        for (RouteStops rs : allRouteStops) {
            if (rs.getRoute().equals(route)) {
                list.add(rs);
            }
        }
        return list;
    }

    /**
     * Setter listen over alle tilgjengelige RouteStop (typisk hentet fra repository).
     */
    public void setAllRouteStops(List<RouteStops> allRouteStops) {
        this.allRouteStops = allRouteStops;
    }

    // --- Gettere ---

    public int getId() { return id; }
    public int getRouteNum() { return routeNum; }
    public Stops getFromStop() { return fromStop; }
    public Stops getToStop() { return toStop; }
    public boolean isActive() { return isActive; }

    // --- Settere ---

    public void setFromStop(Stops fromStop) { this.fromStop = fromStop; }
    public void setToStop(Stops toStop) { this.toStop = toStop; }
    public void setActive(boolean isActive) { this.isActive = isActive; }

    // --- Overrides ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route other = (Route) o;

        return routeNum == other.routeNum
                && fromStop != null && fromStop.equals(other.fromStop)
                && toStop != null && toStop.equals(other.toStop);
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(routeNum);
        result = 31 * result + (fromStop != null ? fromStop.hashCode() : 0);
        result = 31 * result + (toStop != null ? toStop.hashCode() : 0);
        return result;
    }
}
