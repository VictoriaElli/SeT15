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
    private EntityManager em; // EntityManager for JPA-spørringer

    // --- Finn alle frekvenser for en gitt rute ---
    @Override
    public List<Frequency> findByRoute(Route route) {
        if (route == null) return List.of(); // returner tom liste hvis null
        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.route = :route",
                        Frequency.class)
                .setParameter("route", route)
                .getResultList();
    }

    // --- Finn frekvenser for en rute på en spesifikk dato ---
    @Override
    public List<Frequency> findByRouteAndDate(Route route, LocalDate date) {
        if (route == null || date == null) return List.of(); // trenger begge
        Weekday weekday = Weekday.fromLocalDate(date);
        return findByRouteAndWeekday(route, weekday); // map dato til ukedag
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
    @Override
    public List<Frequency> findActiveForRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        Weekday weekday = Weekday.fromLocalDate(date);

        String query = "SELECT f FROM Frequency f WHERE f.weekday = :weekday AND f.route.isActive = true";

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

    // --- Finn inaktive frekvenser for en rute på en spesifikk dato ---
    @Override
    public List<Frequency> findInactiveForRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        Weekday weekday = Weekday.fromLocalDate(date);

        String query = "SELECT f FROM Frequency f WHERE f.weekday = :weekday AND f.route.isActive = false";

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

    // --- Finn alle frekvenser for en sesong ---
    @Override
    public List<Frequency> findBySeason(Season season) {
        if (season == null) return List.of();
        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.season = :season",
                        Frequency.class)
                .setParameter("season", season)
                .getResultList();
    }

    // --- Finn alle frekvenser på en spesifikk dato ---
    @Override
    public List<Frequency> findAllOnDate(LocalDate date) {
        if (date == null) return List.of();
        Weekday weekday = Weekday.fromLocalDate(date);
        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.weekday = :weekday",
                        Frequency.class)
                .setParameter("weekday", weekday)
                .getResultList();
    }

    @Override
    public List<Frequency> findAllActiveOnDate(LocalDate date) {
        return findActiveForRouteAndDate(null, date);
    }

    @Override
    public List<Frequency> findAllInactiveOnDate(LocalDate date) {
        return findInactiveForRouteAndDate(null, date);
    }

    // --- Finn alle frekvenser for en ukedag ---
    @Override
    public List<Frequency> findAllOnWeekday(Weekday weekday) {
        if (weekday == null) return List.of();
        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.weekday = :weekday",
                        Frequency.class)
                .setParameter("weekday", weekday)
                .getResultList();
    }

    @Override
    public List<Frequency> findAllActiveOnWeekday(Weekday weekday) {
        if (weekday == null) return List.of();
        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.weekday = :weekday AND f.route.isActive = true",
                        Frequency.class)
                .setParameter("weekday", weekday)
                .getResultList();
    }

    @Override
    public List<Frequency> findAllInactiveOnWeekday(Weekday weekday) {
        if (weekday == null) return List.of();
        return em.createQuery(
                        "SELECT f FROM Frequency f WHERE f.weekday = :weekday AND f.route.isActive = false",
                        Frequency.class)
                .setParameter("weekday", weekday)
                .getResultList();
    }
}
