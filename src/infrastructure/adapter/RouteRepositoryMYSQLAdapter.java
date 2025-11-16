package adapter;

import domain.model.Route;
import domain.model.Stops;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import port.outbound.FerryRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * JPA-adapter/repository for Route.
 * Henter data fra MySQL via JPA og implementerer FerryRepositoryPort
 * for generisk bruk i applikasjonen.
 */
@Repository
@Transactional
public class RouteRepositoryMYSQLAdapter implements FerryRepositoryPort<Route> {

    @PersistenceContext
    private EntityManager em; // Håndterer JPA-operasjoner

    /**
     * Finn en Route basert på rutenavn.
     * Rutenavn må ha format "<rutenummer> <fromStop> - <toStop>", f.eks. "12 Oslo - Bergen".
     *
     * @param routeName Rutenavn som streng
     * @return Optional<Route> som matcher; tom hvis ingen match
     */
    @Override
    public Optional<Route> findByName(String routeName) {
        return em.createQuery(
                        "SELECT r FROM Route r " +
                                "WHERE CONCAT(r.routeNum, ' ', r.fromStop.name, ' - ', r.toStop.name) = :routeName",
                        Route.class)
                .setParameter("routeName", routeName)
                .getResultStream()
                .findFirst();
    }

    /**
     * Hent alle aktive ruter.
     * En rute regnes som aktiv hvis isActive = true.
     *
     * @return Liste over aktive ruter
     */
    @Override
    public List<Route> findAllActive() {
        return em.createQuery("SELECT r FROM Route r WHERE r.isActive = true", Route.class)
                .getResultList();
    }

    /**
     * Finn ruter som starter fra et spesifikt stoppested.
     *
     * @param fromStop Stoppested som ruten starter fra
     * @return Liste over aktive ruter som starter fra stoppestedet
     */
    public List<Route> findByFromStop(Stops fromStop) {
        return em.createQuery(
                        "SELECT r FROM Route r WHERE r.fromStop = :fromStop AND r.isActive = true",
                        Route.class)
                .setParameter("fromStop", fromStop)
                .getResultList();
    }

    /**
     * Finn ruter som slutter på et spesifikt stoppested.
     *
     * @param toStop Stoppested som ruten slutter på
     * @return Liste over aktive ruter som slutter på stoppestedet
     */
    public List<Route> findByToStop(Stops toStop) {
        return em.createQuery(
                        "SELECT r FROM Route r WHERE r.toStop = :toStop AND r.isActive = true",
                        Route.class)
                .setParameter("toStop", toStop)
                .getResultList();
    }
}
