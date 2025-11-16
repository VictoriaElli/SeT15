package port.outbound;

import domain.model.ExceptionEntry;
import domain.model.Route;
import domain.model.Season;
import domain.model.Weekday;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepositoryPort<T> {

    // Finn alle forekomster for en gitt rute
    List<T> findByRoute(Route route);

    // Finn alle forekomster for en gitt rute på en spesifikk dato
    List<T> findByRouteAndDate(Route route, LocalDate date);

    // Finn alle forekomster for en gitt rute på en spesifikk ukedag
    List<T> findByRouteAndWeekday(Route route, Weekday weekday);

    List<T> findActiveForRouteAndDate(Route route, LocalDate date);

    // Finn alle forekomster for en gitt sesong
    List<T> findBySeason(Season season);

    // Finn alle aktive forekomster på en spesifikk dato
    List<T> findAllActiveOn(LocalDate date);
}
