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
    private EntityManager em; // EntityManager for å gjøre JPA-spørringer

    // --- Finn alle entries for en gitt rute ---
    @Override
    public List<ExceptionEntry> findByRoute(Route route) {
        if (route == null) return List.of(); // returner tom liste hvis null
        return em.createQuery(
                        "SELECT e FROM ExceptionEntry e WHERE e.route = :route",
                        ExceptionEntry.class)
                .setParameter("route", route)
                .getResultList();
    }

    // --- Finn entries for en rute på en spesifikk dato ---
    @Override
    public List<ExceptionEntry> findByRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of(); // returner tom liste hvis dato ikke er satt

        // Bygg spørring dynamisk avhengig av om route er null eller ikke
        String query = "SELECT e FROM ExceptionEntry e WHERE e.date = :date";
        if (route != null) query += " AND e.route = :route";

        var q = em.createQuery(query, ExceptionEntry.class)
                .setParameter("date", date);

        if (route != null) q.setParameter("route", route);

        return q.getResultList();
    }

    // --- Finn entries for en rute på en spesifikk ukedag ---
    @Override
    public List<ExceptionEntry> findByRouteAndWeekday(Route route, Weekday weekday) {
        if (weekday == null) return List.of(); // ukedag må være satt

        List<ExceptionEntry> result = new ArrayList<>();

        // 1) Finn entries med eksplisitt ukedag
        String query = "SELECT e FROM ExceptionEntry e WHERE e.weekday = :weekday";
        if (route != null) query += " AND e.route = :route";

        var q = em.createQuery(query, ExceptionEntry.class)
                .setParameter("weekday", weekday);

        if (route != null) q.setParameter("route", route);

        result.addAll(q.getResultList());

        // 2) Finn entries med spesifikk dato som matcher ukedagen
        String dateQuery = "SELECT e FROM ExceptionEntry e WHERE e.date IS NOT NULL";
        if (route != null) dateQuery += " AND e.route = :route";

        var q2 = em.createQuery(dateQuery, ExceptionEntry.class);
        if (route != null) q2.setParameter("route", route);

        List<ExceptionEntry> byDate = q2.getResultList();
        // Fjern alle entries hvor dato ikke matcher ønsket ukedag
        byDate.removeIf(e -> Weekday.fromLocalDate(e.getValidDate()) != weekday);

        result.addAll(byDate);

        return result;
    }

    // --- Finn aktive entries for en rute på en spesifikk dato ---
    @Override
    public List<ExceptionEntry> findActiveForRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        List<ExceptionEntry> result = new ArrayList<>();

        // Hent entries med spesifikk dato
        result.addAll(findByRouteAndDate(route, date));

        // Hent entries som matcher ukedag
        Weekday weekday = Weekday.fromLocalDate(date);
        result.addAll(findByRouteAndWeekday(route, weekday));

        // Filtrer kun aktive ruter
        if (route != null && !route.isActive()) {
            result.clear(); // gitt rute er inaktiv -> returner tom liste
        } else if (route == null) {
            result.removeIf(e -> !e.getRoute().isActive()); // fjern alle inaktive ruter
        }

        return result;
    }

    // --- Finn inaktive entries for en rute på en spesifikk dato ---
    public List<ExceptionEntry> findInactiveForRouteAndDate(Route route, LocalDate date) {
        if (date == null) return List.of();
        List<ExceptionEntry> result = new ArrayList<>();

        // Hent alle entries for dato
        result.addAll(findByRouteAndDate(route, date));
        // Hent alle entries som matcher ukedag
        Weekday weekday = Weekday.fromLocalDate(date);
        result.addAll(findByRouteAndWeekday(route, weekday));

        // Filtrer kun inaktive ruter
        if (route != null && route.isActive()) {
            result.clear(); // gitt rute er aktiv -> ingen inaktive
        } else if (route == null) {
            result.removeIf(e -> e.getRoute().isActive()); // fjern aktive ruter
        }

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

    // --- Finn alle entries på en dato ---
    @Override
    public List<ExceptionEntry> findAllOnDate(LocalDate date) {
        return findByRouteAndDate(null, date); // null = alle ruter
    }

    @Override
    public List<ExceptionEntry> findAllActiveOnDate(LocalDate date) {
        return findActiveForRouteAndDate(null, date); // null = alle ruter
    }

    public List<ExceptionEntry> findAllInactiveOnDate(LocalDate date) {
        return findInactiveForRouteAndDate(null, date); // null = alle ruter
    }

    // --- Finn alle entries på en ukedag ---
    @Override
    public List<ExceptionEntry> findAllOnWeekday(Weekday weekday) {
        return findByRouteAndWeekday(null, weekday); // null = alle ruter
    }

    @Override
    public List<ExceptionEntry> findAllActiveOnWeekday(Weekday weekday) {
        List<ExceptionEntry> all = findAllOnWeekday(weekday);
        all.removeIf(e -> !e.getRoute().isActive()); // fjern inaktive ruter
        return all;
    }

    public List<ExceptionEntry> findAllInactiveOnWeekday(Weekday weekday) {
        List<ExceptionEntry> all = findAllOnWeekday(weekday);
        all.removeIf(e -> e.getRoute().isActive()); // behold kun inaktive ruter
        return all;
    }
}
