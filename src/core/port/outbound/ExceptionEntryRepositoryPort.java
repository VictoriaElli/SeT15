package port.outbound;

import domain.model.ExceptionEntry;
import domain.model.Route;
import domain.model.Season;
import domain.model.Stops;
import domain.model.Weekday;

import java.time.LocalDate;
import java.util.List;

public interface ExceptionEntryRepositoryPort extends CRUDRepositoryPort<ExceptionEntry> {

    // --- Finn etter route ---
    List<ExceptionEntry> findByRoute(Route route);
    List<ExceptionEntry> findByRouteAndDate(Route route, LocalDate date);
    List<ExceptionEntry> findByRouteAndWeekday(Route route, Weekday weekday);

    // --- Finn etter stop ---
    List<ExceptionEntry> findByStop(Stops stop);
    List<ExceptionEntry> findByRouteAndStop(Route route, Stops stop);
    List<ExceptionEntry> findByStopAndDate(Stops stop, LocalDate date);
    List<ExceptionEntry> findByStopAndWeekday(Stops stop, Weekday weekday);

    // --- Finn alle på dato ---
    List<ExceptionEntry> findAllOnDate(LocalDate date);
    List<ExceptionEntry> findAllActiveOnDate(LocalDate date);
    List<ExceptionEntry> findAllInactiveOnDate(LocalDate date);

    // --- Finn alle på ukedag ---
    List<ExceptionEntry> findAllOnWeekday(Weekday weekday);
    List<ExceptionEntry> findAllActiveOnWeekday(Weekday weekday);
    List<ExceptionEntry> findAllInactiveOnWeekday(Weekday weekday);

    // --- Aktive/inaktive for route + dato ---
    List<ExceptionEntry> findActiveForRouteAndDate(Route route, LocalDate date);
    List<ExceptionEntry> findInactiveForRouteAndDate(Route route, LocalDate date);

    // --- Aktive/inaktive for stop + dato ---
    List<ExceptionEntry> findActiveForStopAndDate(Stops stop, LocalDate date);
    List<ExceptionEntry> findInactiveForStopAndDate(Stops stop, LocalDate date);

    // --- Aktive/inaktive for route + ukedag ---
    List<ExceptionEntry> findActiveForRouteAndWeekday(Route route, Weekday weekday);
    List<ExceptionEntry> findInactiveForRouteAndWeekday(Route route, Weekday weekday);

    // --- Aktive/inaktive for stop + ukedag ---
    List<ExceptionEntry> findActiveForStopAndWeekday(Stops stop, Weekday weekday);
    List<ExceptionEntry> findInactiveForStopAndWeekday(Stops stop, Weekday weekday);


    // --- Finn etter season ---
    List<ExceptionEntry> findBySeason(Season season);
}
