package adapter;

import domain.model.Route;
import domain.model.RouteStops;
import domain.model.Stops;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import port.outbound.FerryRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * JPA-adapter/repository for RouteStops.
 * Henter data fra MySQL via JPA og implementerer FerryRepositoryPort
 * for generisk bruk i applikasjonen.
 */
@Repository
@Transactional // Alle metoder kjøres innenfor en transaksjon
public class RouteStopsRepositoryMYSQLAdapter implements FerryRepositoryPort<RouteStops> {

    @PersistenceContext
    private EntityManager em; // Håndterer JPA-operasjoner

    /**
     * Finn et RouteStops basert på rutenummer og stopp.
     *
     * @param routeName Rutenavn i format "<rutenummer> <fromStop> - <toStop>", f.eks. "12 Oslo - Bergen"
     * @return Optional<RouteStops> som matcher; tom hvis ingen match eller formatet er ugyldig
     */
    @Override
    public Optional<RouteStops> findByName(String routeName) {
        // Splitter rutenummer fra fra-til stopp
        String[] parts = routeName.split(" ", 2);
        if (parts.length < 2) return Optional.empty();

        // Parse rutenummer
        int routeNum;
        try {
            routeNum = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        // Splitter fra- og til-stopp
        String[] stops = parts[1].split(" - ");
        if (stops.length < 2) return Optional.empty();

        String fromStopName = stops[0].trim();
        String toStopName = stops[1].trim();

        // JPQL-spørring: filtrer direkte i databasen
        return em.createQuery(
                        "SELECT rs FROM RouteStops rs " +
                                "WHERE rs.route.routeNum = :routeNum " +
                                "AND rs.route.fromStop.name = :fromStop " +
                                "AND rs.route.toStop.name = :toStop",
                        RouteStops.class)
                .setParameter("routeNum", routeNum)
                .setParameter("fromStop", fromStopName)
                .setParameter("toStop", toStopName)
                .getResultStream()
                .findFirst();
    }

    /**
     * Hent alle aktive RouteStops.
     * Et stopp regnes som aktivt dersom både ruten og selve stoppet er aktive.
     *
     * @return Liste over alle aktive RouteStops
     */
    @Override
    public List<RouteStops> findAllActive() {
        return em.createQuery(
                        "SELECT rs FROM RouteStops rs " +
                                "WHERE rs.route.isActive = true AND rs.stop.isActive = true",
                        RouteStops.class)
                .getResultList();
    }

    /**
     * Finn alle RouteStops som tilhører en spesifikk rute.
     * Resultatet sorteres etter rekkefølgen i ruten.
     *
     * @param route Ruten som skal hentes RouteStops for
     * @return Liste over RouteStops tilhørende ruten, sortert etter routeOrder
     */
    public List<RouteStops> findByRoute(Route route) {
        return em.createQuery(
                        "SELECT rs FROM RouteStops rs WHERE rs.route = :route ORDER BY rs.routeOrder",
                        RouteStops.class)
                .setParameter("route", route)
                .getResultList();
    }

    /**
     * Finn alle RouteStops som refererer til et gitt stoppested.
     *
     * @param stop Stoppested som RouteStops skal hentes for
     * @return Liste over RouteStops som inkluderer stoppestedet
     */
    public List<RouteStops> findByStop(Stops stop) {
        return em.createQuery(
                        "SELECT rs FROM RouteStops rs WHERE rs.stop = :stop",
                        RouteStops.class)
                .setParameter("stop", stop)
                .getResultList();
    }
}
