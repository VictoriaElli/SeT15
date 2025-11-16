package adapter;

import domain.model.Frequency;
import domain.model.Route;
import domain.model.Season;
import domain.model.Weekday;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import port.outbound.ScheduleRepositoryPort;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class FrequencyRepositoryMYSQLAdapter implements ScheduleRepositoryPort<Frequency> {

    @PersistenceContext
    private EntityManager em;

    // --- Finn alle frekvenser for en gitt rute ---
    @Override
    public List<Frequency> findByRoute(Route route) {
        if (route == null) return List.of();

        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.route = :route",
                        Frequency.class)
                .setParameter("route", route)
                .getResultList();
    }

    // --- Finn frekvenser for en rute på en spesifikk dato ---
    @Override
    public List<Frequency> findByRouteAndDate(Route route, LocalDate date) {
        if (route == null) return List.of();
        Weekday weekday = Weekday.fromLocalDate(date);
        return findByRouteAndWeekday(route, weekday);
    }

    // --- Finn frekvenser for en rute på en spesifikk ukedag ---
    @Override
    public List<Frequency> findByRouteAndWeekday(Route route, Weekday weekday) {
        if (route == null || weekday == null) return List.of();

        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.route = :route AND f.weekday = :weekday",
                        Frequency.class)
                .setParameter("route", route)
                .setParameter("weekday", weekday)
                .getResultList();
    }

    // --- Finn aktive frekvenser for en rute på en spesifikk dato ---
    // Hvis route er null, returner alle aktive frekvenser for datoen
    @Override
    public List<Frequency> findActiveForRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();

        Weekday weekday = Weekday.fromLocalDate(date);

        String query = "SELECT f FROM Frequency f " +
                "WHERE f.weekday = :weekday " +
                "AND f.route.isActive = true";

        if (route != null) {
            query += " AND f.route = :route";
            return em.createQuery(query, Frequency.class)
                    .setParameter("weekday", weekday)
                    .setParameter("route", route)
                    .getResultList();
        } else {
            return em.createQuery(query, Frequency.class)
                    .setParameter("weekday", weekday)
                    .getResultList();
        }
    }

    // --- Finn frekvenser for en spesifikk sesong ---
    @Override
    public List<Frequency> findBySeason(Season season) {
        if (season == null) return List.of();

        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.season = :season",
                        Frequency.class)
                .setParameter("season", season)
                .getResultList();
    }

    // --- Finn alle aktive frekvenser på en spesifikk dato ---
    // Henter alle ruter hvis route ikke spesifisert
    @Override
    public List<Frequency> findAllActiveOn(LocalDate date) {
        if (date == null) return List.of();

        Weekday weekday = Weekday.fromLocalDate(date);
        return em.createQuery(
                        "SELECT f FROM Frequency f " +
                                "WHERE f.weekday = :weekday " +
                                "AND f.route.isActive = true",
                        Frequency.class)
                .setParameter("weekday", weekday)
                .getResultList();
    }
}
