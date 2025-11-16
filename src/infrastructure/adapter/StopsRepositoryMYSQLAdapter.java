package adapter;

import domain.model.Stops;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import port.outbound.FerryRepositoryPort;

import java.util.List;
import java.util.Optional;

/**
 * Adapter/repository for Stops-objekter i MySQL via JPA.
 * Implementerer FerryRepositoryPort slik at den kan brukes generisk i applikasjonen.
 */
@Repository
@Transactional // Alle metoder kjøres innenfor en transaksjon
public class StopsRepositoryMYSQLAdapter implements FerryRepositoryPort<Stops> {

    @PersistenceContext
    private EntityManager em; // EntityManager håndterer JPA-operasjoner

    /**
     * Finn et stopp basert på navn.
     * Returnerer en Optional, som er tom dersom stoppet ikke finnes.
     */
    @Override
    public Optional<Stops> findByName(String stopName) {
        return em.createQuery("SELECT s FROM Stops s WHERE s.name = :name", Stops.class)
                .setParameter("name", stopName)
                .getResultStream() // Stream gir mulighet til å hente første match
                .findFirst();      // Returnerer Optional<Stops>
    }

    /**
     * Hent alle aktive stopp.
     * Brukes for å filtrere ut inaktive stopp fra ruter, kart osv.
     */
    @Override
    public List<Stops> findAllActive() {
        return em.createQuery("SELECT s FROM Stops s WHERE s.isActive = true", Stops.class)
                .getResultList();
    }

    /**
     * Finn alle stopp som ligger omtrent innenfor en gitt radius (km) fra et punkt.
     * Beregner en enkel "bounding box" for å gjøre søket i databasen effektivt.
     *
     * @param latitude Breddegrad for sentrumspunktet
     * @param longitude Lengdegrad for sentrumspunktet
     * @param radiusKm Radius i kilometer
     * @return Liste med Stops innenfor området
     */
    public List<Stops> findNear(double latitude, double longitude, double radiusKm) {
        // Omtrentlig konvertering fra km til grader
        double latDiff = radiusKm / 111; // 1 grad bredde = ca. 111 km
        double lonDiff = radiusKm / (111 * Math.cos(Math.toRadians(latitude)));
        // Justering for lengdegrad ved forskjellige breddegrader

        return em.createQuery(
                        "SELECT s FROM Stops s WHERE s.isActive = true " +
                                "AND s.latitude BETWEEN :minLat AND :maxLat " +
                                "AND s.longitude BETWEEN :minLon AND :maxLon", Stops.class)
                .setParameter("minLat", latitude - latDiff)
                .setParameter("maxLat", latitude + latDiff)
                .setParameter("minLon", longitude - lonDiff)
                .setParameter("maxLon", longitude + lonDiff)
                .getResultList();
    }

}
