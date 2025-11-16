package adapter;

import domain.model.ExceptionEntry;
import domain.model.Route;
import domain.model.Season;
import domain.model.Weekday;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import port.outbound.ScheduleRepositoryPort;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class ExceptionEntryRepositoryMYSQLAdapter implements ScheduleRepositoryPort<ExceptionEntry> {

    @PersistenceContext
    private EntityManager em;

    // --- Finn alle exception entries for en gitt rute ---
    @Override
    public List<ExceptionEntry> findByRoute(Route route) {
        if (route == null) return List.of();

        return em.createQuery(
                        "SELECT e FROM ExceptionEntry e WHERE e.route = :route",
                        ExceptionEntry.class)
                .setParameter("route", route)
                .getResultList();
    }

    // --- Finn entries for en rute på en spesifikk dato ---
    @Override
    public List<ExceptionEntry> findByRouteAndDate(Route route, LocalDate date) {
        if (route == null || date == null) return List.of();

        return em.createQuery(
                        "SELECT e FROM ExceptionEntry e WHERE e.route = :route AND e.date = :date",
                        ExceptionEntry.class)
                .setParameter("route", route)
                .setParameter("date", date)
                .getResultList();
    }

    // --- Finn entries for en rute på en spesifikk ukedag ---
    @Override
    public List<ExceptionEntry> findByRouteAndWeekday(Route route, Weekday weekday) {
        if (weekday == null) return List.of();

        List<ExceptionEntry> result = new ArrayList<>();

        // Entries med eksplisitt ukedag
        if (route != null) {
            result.addAll(em.createQuery(
                            "SELECT e FROM ExceptionEntry e WHERE e.route = :route AND e.weekday = :weekday",
                            ExceptionEntry.class)
                    .setParameter("route", route)
                    .setParameter("weekday", weekday)
                    .getResultList());
        } else {
            result.addAll(em.createQuery(
                            "SELECT e FROM ExceptionEntry e WHERE e.weekday = :weekday",
                            ExceptionEntry.class)
                    .setParameter("weekday", weekday)
                    .getResultList());
        }

        // Entries med spesifikk dato som tilsvarer ukedagen
        List<ExceptionEntry> byDate = em.createQuery(
                        "SELECT e FROM ExceptionEntry e WHERE e.date IS NOT NULL" +
                                (route != null ? " AND e.route = :route" : ""),
                        ExceptionEntry.class)
                .setParameter("route", route)
                .getResultList();

        byDate.removeIf(e -> Weekday.fromLocalDate(e.getValidDate()) != weekday);

        result.addAll(byDate);

        return result;
    }

    // --- Finn aktive entries for en rute på en spesifikk dato ---
    // Hvis route er null, returner alle aktive entries for datoen
    @Override
    public List<ExceptionEntry> findActiveForRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        List<ExceptionEntry> result = new ArrayList<>();

        // Hvis route er spesifisert, sjekk at den er aktiv
        if (route != null && !route.isActive()) return List.of();

        // Entries med spesifikk dato
        if (route != null) {
            result.addAll(findByRouteAndDate(route, date));
        } else {
            result.addAll(em.createQuery(
                            "SELECT e FROM ExceptionEntry e WHERE e.date = :date AND e.route.isActive = true",
                            ExceptionEntry.class)
                    .setParameter("date", date)
                    .getResultList());
        }

        // Entries som matcher ukedag
        Weekday weekday = Weekday.fromLocalDate(date);
        result.addAll(findByRouteAndWeekday(route, weekday));

        return result;
    }

    // --- Finn entries for en spesifikk sesong ---
    @Override
    public List<ExceptionEntry> findBySeason(Season season) {
        if (season == null) return List.of();

        return em.createQuery(
                        "SELECT e FROM ExceptionEntry e WHERE e.season = :season",
                        ExceptionEntry.class)
                .setParameter("season", season)
                .getResultList();
    }

    // --- Finn alle aktive entries på en spesifikk dato ---
    @Override
    public List<ExceptionEntry> findAllActiveOn(LocalDate date) {
        if (date == null) return List.of();
        return findActiveForRouteAndDate(null, date); // null route = alle ruter
    }
}
